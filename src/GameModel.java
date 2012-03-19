import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;
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
	private Ball ball;
	private Cube cube;
	private int cam;
	private int cam_mov;
	private boolean cam_dir;
	private int turn;
	private int hit_count;
	
	private Player debugPlayer;
	
	public static final int W_MARGIN = 20;
	public static final int H_MARGIN = 60;
	public static final int D_MARGIN = 20;

	public GameModel(PApplet app) {
		this.isGameOn = false;
		
		int areaw = app.width - W_MARGIN;
		int areah = app.height - H_MARGIN;
		this.cube = new Cube(areaw, areah);
		this.ball = new Ball(new PVector(0, 0, -Cube.DEPTH), areaw, areah);
		this.cam = 0;
		this.cam_mov = 50;
		this.cam_dir = true;
		this.hit_count = 0;
		
	}

	public void startGame() {
		this.isGameOn = true;
	}

	public void endGame() {
		this.isGameOn = false;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Player getPlayer(int id) {
		return players.get(id);
	}

	public int getPlayerCount() {
		return players.size();
	}
	
	public Player getDebugPlayer() {
		return this.debugPlayer;
	}

	public int addPlayer(Player player, boolean debug) {
		players.add(player);
		this.turn = players.indexOf(player);
		
		if (debug) {
			this.debugPlayer = player;
		}

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
						
			app.pushMatrix();
			
			String playerStr;
			if (this.getPlayerCount() > 0) {
				playerStr = "Turn: player "+this.players.get(this.getTurn()).getId();
			} else {
				playerStr = "Waiting for calibration";
			}
			
			// Shift overall coordinate system to the centre of the display
			app.translate(app.width/2, app.height/2, -D_MARGIN);
			app.textSize(32);
			app.text(this.getPlayerCount() + " players", -1200, -1100);
			app.text(playerStr, -1000, -1100, 0);
			app.text("Hits: "+this.hit_count, -700, -1100, 0);
			app.text("Score: ", -500, -1100, 0);
			int i = 0;
			for(Player p : this.players) {
				app.text(p.getPoints()+" ", i-400, -1100, 0);
				i+=100;
			}
			
			//move cam			
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

			this.ball.update(this);
			this.cube.draw(app, ball.getZ());
			this.ball.draw(app);
			for(Player player : this.players) player.drawRackets(app);
			app.popMatrix();			
		}else{
			System.out.println("eerwwer");
			app.fill(0xFFDD1111);
			app.textSize(32);			
			app.noStroke();
			app.text("Score: Score: Score: Score: Score: Score: Score: Score: Score: Score: ",1000,1000,100);
			app.sphere(500);

			app.camera(cam/200,0, Cube.DEPTH/2, 0,0,-Cube.DEPTH, 0, 1, 0);
		}
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

	public void ballEscaped() {
		this.hit_count = 0;
		this.players.get(this.getTurn()).givePoint();
		if (this.players.get(this.getTurn()).getPoints()==3){
			for(Player p : this.players) {
				p.resetPoints();
			}			
			this.endGame();
		}
		// TODO Auto-generated method stub
	}
}
