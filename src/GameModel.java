import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * GameModel contains all the relevant game information such as players, points
 * and ball.
 * 
 * @author pyry
 * 
 */

/* just testing */
public class GameModel {

	private List<Player> players = Collections
			.synchronizedList(new ArrayList<Player>(100));
	private boolean isGameOn;
	private Mode mode;
	private Ball ball;
	private Cube cube;
	private int cam;
	private int cam_mov;
	private boolean cam_dir;
	private int turn; // index (in list players) of the player whose turn it is
						// currently
	private int miss_count;

	public static final int W_MARGIN = 20;
	public static final int H_MARGIN = 60;
	public static final int D_MARGIN = 20;

	private Map<Mode, HighScoreTable> highscores;
	public Map<Player, Boolean> scoreChartPlayers;

	public GameModel(PApplet app) {
		this.isGameOn = false;
		this.cam = 0;
		this.cam_mov = 50;
		this.cam_dir = true;
		int areaw = app.width - W_MARGIN;
		int areah = app.height - H_MARGIN;
		this.cube = new Cube(areaw, areah);

		this.highscores = new HashMap<Mode, HighScoreTable>();
		for (Mode m : Mode.values()) {
			highscores
					.put(m, new HighScoreTable("highscores_"
							+ m.toString().toLowerCase().replaceAll(" ", "_")
							+ ".txt"));
		}
	}

	public void removePlayers() {
		this.players.clear();
	}

	public void removePlayer(int id) {
		Player p = this.getPlayer(id);
		this.players.remove(p);
	}

	/**
	 * resets all player properties (score) but does not remove them
	 */
	public void resetPlayers() {
		for (Player p : this.players) {
			p.resetPoints();
		}
	}

	public void startGame(Mode mode) {
		// Restart ball for a new random staring direction etc
		this.ball = new Ball(this.cube.getW(), this.cube.getH());
		this.isGameOn = true;
		this.mode = mode;
		this.miss_count = 0;
		/*
		 * for(Player p : this.players) { p.resetPoints(); }
		 */
		if (mode == Mode.MOUSE) {
			Player player1 = new Player(0);
			this.addPlayer(player1, true);
			Player player2 = new Player(1);
			this.addPlayer(player2, true);
			Log.debug(this, "No kinect available - using debug player");
		}
	}

	public void endGame() {
		this.scoreChartPlayers = new HashMap<Player, Boolean>();
		this.saveScore();
		this.isGameOn = false;
	}

	private void saveScore() {
		Date date = new Date(System.currentTimeMillis());
		HighScoreTable table = this.highscores.get(this.mode);
		for (Player p : this.players) {
			HighScore s = new HighScore(Colour.values()[p.getId()].toString(),
					date, p.getPoints());
			int pos = table.addScore(s);
			boolean highscore = pos < HighScoreTable.SIZE;
			this.scoreChartPlayers.put(p, highscore);
		}
		table.saveScores();
	}

	public boolean isGame() {
		return this.isGameOn;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Player getPlayer(int id) {
		for (Player p : players) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}

	public Player getActivePlayer() {
		if (this.getPlayerCount() == 0) {
			return null;
		}

		return this.players.get(this.turn);
	}

	public int getPlayerCount() {
		return players.size();
	}

	public int addPlayer(Player player, boolean debug) {
		players.add(player);
		this.turn = players.indexOf(player);

		return players.indexOf(player);
	}

	public int addPlayer(Player player) {
		this.turn = players.indexOf(player);
		return this.addPlayer(player, false);
	}

	public int getTurn() {
		return this.turn;
	}

	public void setTurn() {
		this.turn += 1;
		if (this.turn == this.getPlayerCount()) {
			this.turn = 0;
		}
	}

	public HighScoreTable getScores(Mode gameMode) {
		HighScoreTable table = this.highscores.get(gameMode);
		return table;
	}

	/**
	 * Called for every frame from draw().
	 */
	public void update(App app) {
		if (isGameOn) {

			// app.pushMatrix();

			// Shift overall coordinate system to the centre of the display
			app.translate(app.width / 2, app.height / 2, -D_MARGIN);

			// Info text
			this.displayInfoText(app);

			// move cam
			this.updateCamera(app);

			// Draw things
			this.ball.update(this);
			this.cube.draw(app, ball.getZ(), this.getActivePlayer().getId());
			this.ball.draw(app);
			for (int j = 0; j < this.players.size(); j++) {
				Player player = this.players.get(j);
				player.drawRackets(app, this.getTurn() == j);
			}
			// app.popMatrix();
		}
	}

	private void displayInfoText(App app) {
		String playerStr;

		if (this.getPlayerCount() > 0) {
			playerStr = "Turn: "
					+ Colour.values()[this.players.get(this.getTurn()).getId()];
		} else {
			playerStr = "Waiting for calibration";
		}
		app.textSize(45);
		app.fill(0xFF000000);

		int margin = 30;
		float textx = -app.width;
		int texty = -app.height;
		String displayString = this.getPlayerCount() + " players";
		app.text(displayString, textx, texty);

		textx += app.textWidth(displayString) + margin;
		displayString = "Misses: " + this.miss_count;
		app.text(displayString, textx, texty);

		textx += app.textWidth(displayString) + margin;
		displayString = "Score: ";
		for (Player p : this.players) {
			displayString += p.getPoints() + " - ";
		}
		displayString = displayString.substring(0, displayString.length() - 2);
		app.text(displayString, textx, texty);

		textx += app.textWidth(displayString) + margin;
		app.text(playerStr, textx, texty);

	}

	private void updateCamera(App app) {
		if (cam >= 35000) {
			cam_dir = true;
			cam_mov -= 1;
			cam = cam + cam_mov;
		} else if (cam < -35000) {
			cam_dir = false;
			cam_mov += 1;
			cam = cam + cam_mov;
		} else {
			if (cam_dir) {
				cam = cam - 100;
			} else {
				cam = cam + 100;
			}
		}
		// System.out.println(cam);
		app.camera(cam / 200, 0, Cube.DEPTH / 2, 0, 0, -Cube.DEPTH, 0, 1, 0);
	}

	/**
	 * Transforms screen coordinates (such as mouse position) to the 3D world.
	 * 
	 * @param x
	 *            Screen x coordinate
	 * @param y
	 *            Screen y coordinate
	 * @return Corresponding x and y coordinates in the game's 3D world.
	 */
	public PVector get3DCoordinates(int x, int y, App app) {
		int newx = (x - app.width / 2) * 2;
		int newy = (y - app.height / 2) * 2;
		return new PVector(newx, newy);
	}

	/**
	 * Checks if the ball is hit by a Racket in the game.
	 * 
	 * @param ball
	 *            The ball to be hit
	 * @return Racket that hits the ball if there is a hit, null otherwise.
	 */
	public Racket hitByRacket(Ball ball) {
		Player p = this.getActivePlayer();
		this.setTurn();
		for (Racket r : p.getRackets()) {
			if (r != null)
				if (r.hits(ball)) {
					p.givePoint();
					return r;
				}
		}
		
		return null;
	}

	public void ballEscaped(Ball b) {
		Log.debug(this,
				"Ball at [" + b.getX() + ", " + b.getY() + ", " + b.getZ()
						+ "] escaped");
		for (Player p : this.getPlayers()) {
			for (Racket r : p.getRackets()) {
				Log.debug(this, "\t racket at " + r.getDimensions()[0] + "; "
						+ r.getDimensions()[1]);
			}
		}
		this.miss_count++;

		if (this.getPlayerCount() == 0) {
			return;
		}

		if (this.isGameOn && this.miss_count >= 3) {
			this.endGame();
		}
	}

	public Map<Player, Boolean> getScoreChartPlayers() {
		return this.scoreChartPlayers;
	}
}
