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
	private Ball ball;
	private Cube cube;
	private int cam;
	private int cam_mov;
	private boolean cam_dir;
	
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
		
		if (debug) {
			this.debugPlayer = player;
		}

		return players.indexOf(player);
	}
	
	public int addPlayer(Player player) {
		return this.addPlayer(player, false);
	}

	/**
	 * Called for every frame from draw().
	 */
	public void update(PApplet app) {
		if(isGameOn) {
			app.pushMatrix();
			
			// Shift overall coordinate system to the centre of the display
			app.translate(app.width/2, app.height/2, -D_MARGIN);
			app.textSize(32);
			app.text(this.getPlayerCount() + " players", -100, -100, 200);
			
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
			System.out.println(cam);
			app.camera(cam/200,0, Cube.DEPTH/2, 0,0,-Cube.DEPTH, 0, 1, 0);

			this.ball.update(this);
			this.cube.draw(app, ball.getZ());
			this.ball.draw(app);
			app.popMatrix();			
		}
	}

	public boolean hitByRacket(Ball ball) {
		// TODO Auto-generated method stub
		return true;
	}

	public void ballEscaped() {
		// TODO Auto-generated method stub
	}
}
