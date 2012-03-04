import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GameModel contains all the relevant game information such as players, points and ball.
 * @author pyry
 *
 */

/*just testing*/
public class GameModel {

	private List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
	boolean isGameOn;
	
	public GameModel() {
		this.isGameOn = false;
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
}
