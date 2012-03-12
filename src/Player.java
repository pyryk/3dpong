import processing.core.PVector;


public class Player {

	
	private int id;
	private PVector[] racketPositions;
	
	public Player(int id) {
		this.setId(id);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public PVector[] getRacketPositions() {
		/*PVector[] pos = new PVector[racketPositions.length];
		
		for (int i=0; i<racketPositions.length; i++) {
			PVector racket = racketPositions[i];
			pos[i] = new PVector(racket.x, racket.y, 0);
		}
		return pos;*/
		return this.racketPositions;
	}
	
	public void setRacketPositions(PVector[] positions) {
		// adjust the z according to the player number
		for (PVector pos : positions) {
			pos.z = this.id == 0 ? 0 : -500;			
		}
		
		this.racketPositions = positions;
	}
}
