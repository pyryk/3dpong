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

	public static final int AREA_DEPTH = 1000;

	private List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
	private boolean isGameOn;
	private Ball ball;

	public GameModel() {
		this.isGameOn = false;
		this.ball = new Ball(new PVector(0, 0, -AREA_DEPTH));
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

	public int addPlayer(Player player) {
		players.add(player);

		return players.indexOf(player);
	}

	/**
	 * Called for every frame from draw().
	 */
	public void update(PApplet app) {
		if(isGameOn) {
			this.ball.update();
			this.ball.draw(app);
		}
	}
}
