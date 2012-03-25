import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * GameModel contains all the relevant game information such as players, points and ball.
 * @author pyry
 *
 */

/*just testing*/
public class GameModel {

	private List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
	private boolean isGameOn;
	private Mode mode;
	private Ball ball;
	private Cube cube;
	private int cam;
	private int cam_mov;
	private boolean cam_dir;
	private int turn;		// index (in list players) of the player whose turn it is currently
	private int hit_count;
	
	public static final int W_MARGIN = 20;
	public static final int H_MARGIN = 60;
	public static final int D_MARGIN = 20;

	public GameModel(PApplet app) {
		this.isGameOn = false;
		this.cam = 0;
		this.cam_mov = 50;
		this.cam_dir = true;
		this.hit_count = 0;
		int areaw = app.width - W_MARGIN;
		int areah = app.height - H_MARGIN;
		this.cube = new Cube(areaw, areah);	
	}

	public void startGame(Mode mode) {
		// Restart ball for a new random staring direction etc
		this.ball = new Ball(this.cube.getW(), this.cube.getH());
		this.isGameOn = true;
		this.mode = mode;
		/*for(Player p : this.players) {
			p.resetPoints();
		}*/
		players.clear();
		if(mode == Mode.MOUSE) {
			Player player1 = new Player(0);
			this.addPlayer(player1, true);	
			Player player2 = new Player(1);
			this.addPlayer(player2, true);			
			Log.debug(this, "No kinect available - using debug player");
		}
	}

	public void endGame() {
		this.isGameOn = false;
	}
	
	public boolean isGame() {
		return this.isGameOn;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Player getPlayer(int id) {
		return players.get(id);
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
	
	public int getTurn(){
		return this.turn;
	}
	
	public void setTurn(){
		this.turn+=1;
		if (this.turn==this.getPlayerCount()){
			this.turn = 0;
		}
	}

	/**
	 * Called for every frame from draw().
	 */
	public void update(App app) {
		if(isGameOn) {
						
			//app.pushMatrix();
			
			String playerStr;
			if (this.getPlayerCount() > 0) {
				playerStr = "Turn: player "+this.players.get(this.getTurn()).getId();
			} else {
				playerStr = "Waiting for calibration";
			}
			
			// Shift overall coordinate system to the centre of the display
			app.translate(app.width/2, app.height/2, -D_MARGIN);
			app.textSize(35);
			app.fill(0xFF000000);
			app.text(this.getPlayerCount() + " players", -app.width, -app.height);
			//System.out.println(app.height);
			app.text(playerStr, -app.width+app.width/10, -app.height, 0);
			app.text("Hits: "+this.hit_count, -app.width+3*app.width/10, -app.height, 0);
			app.text("Score: ", -app.width+5*app.width/10, -app.height, 0);
			int i = 0;
			for(Player p : this.players) {
				app.text(p.getPoints()+" ", i-app.width+6*app.width/10, -app.height, 0);
				i+=app.width/20;
			}
			
			//move cam			
			this.updateCamera(app);

			// Draw things
			this.ball.update(this);
			this.cube.draw(app, ball.getZ(), this.getTurn());
			this.ball.draw(app);
			for(int j = 0; j < this.players.size(); j++) {
				Player player = this.players.get(j);
				player.drawRackets(app, this.getTurn() == j);
			}
			//app.popMatrix();
		}
	}

	private void updateCamera(App app) {
		if (cam>=20000){
			cam_dir = true;
			cam_mov -= 1;	
			cam = cam+cam_mov;				
		}else if(cam<-20000){
			cam_dir = false;
			cam_mov += 1;	
			cam = cam+cam_mov;
		}else{
			if (cam_dir){
				cam = cam-50;
			}else{
				cam = cam+50;
			}
		}
		// System.out.println(cam);
		app.camera(cam/200,0, Cube.DEPTH/2, 0,0,-Cube.DEPTH, 0, 1, 0);
	}

	/**
	 * Transforms screen coordinates (such as mouse position) to the 3D world.
	 * @param x		Screen x coordinate
	 * @param y		Screen y coordinate
	 * @return		Corresponding x and y coordinates in the game's 3D world.
	 */
	public PVector get3DCoordinates(int x, int y, App app) {
		int newx = (x - app.width/2)*2;
		int newy = (y - app.height/2)*2;
		return new PVector(newx, newy);
	}

	/**
	 * Checks if the ball is hit by a Racket in the game.
	 * @param ball	The ball to be hit
	 * @return		Racket that hits the ball if there is a hit, null otherwise.
	 */
	public Racket hitByRacket(Ball ball) {
		this.setTurn();
		for(Player p : this.players) {
			for(Racket r : p.getRackets()) {
				if(r != null) if(r.hits(ball)) {
					this.hit_count+=1;
					return r;
				}
			}
		}
		return null;
	}

	public void ballEscaped(Ball b) {
		Log.debug(this, "Ball at ["+ b.getX() + ", " + b.getY() + ", " + b.getZ() + "] escaped");
		for (Player p : this.getPlayers()) {
			for (Racket r : p.getRackets()) {
				Log.debug(this, "\t racket at " + r.getDimensions()[0] + "; " + r.getDimensions()[1]);
			}
		}
		this.hit_count = 0;
		
		if (this.getPlayerCount() == 0) {
			return;
		}
		
		this.players.get(this.getTurn()).givePoint();
		if (this.players.get(this.getTurn()).getPoints()==3){
		
			this.endGame();
		}
	}
}
