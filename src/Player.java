import processing.core.PVector;


public class Player {

	
	private int id;
	PVector[] racketPositions;
	
	public Player(int id) {
		this.setId(id);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}
