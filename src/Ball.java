import processing.core.PApplet;
import processing.core.PVector;

public class Ball {

	private PVector movement;
	private PVector position;
	
	private static final int COLOUR = 0xFFDD1111;
	private static final int RADIUS = 30;
	
	private boolean x = true;
	private boolean y = true;
	private boolean z = true;
	

	public Ball(PVector startPos) {
		this.movement = new PVector(5,5,-5);
		//this.position = startPos;
		this.position = new PVector(0,0,0);
	}

	/**
	 * Called for every frame from draw():
	 */
	public void update() {
		// Bounce off walls beta
		if (this.getZ()<=-0){
			x=false;
			this.movement.z = 20;
		}
		if (this.getZ()>=750){
			x=false;
			this.movement.z = -20;
		}
		
		if (this.getX()<=-500){
			x=false;
			this.movement.x = 19;
		}
		if (this.getX()>=500){
			x=false;
			this.movement.x = -19;
		}
		
		if (this.getY()<=-500){
			x=false;
			this.movement.y = 18;
		}
		if (this.getY()>=500){
			x=false;
			this.movement.y = -18;
		}		
		this.position.add(this.movement);
	}

	public void draw(PApplet app) {		
		app.pushMatrix();
		app.translate(this.getX(), this.getY(), this.getZ());
		app.fill(Ball.COLOUR);
		app.noStroke();
		app.sphere(Ball.RADIUS);
		app.popMatrix();
	}

	public int getX() {
		return (int) this.position.x;
	}

	public int getY() {
		return (int) this.position.y;
	}

	public int getZ() {
		return (int) this.position.z;
	}

}
