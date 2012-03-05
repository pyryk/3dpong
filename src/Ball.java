import processing.core.PApplet;
import processing.core.PVector;

public class Ball {

	private PVector movement;
	private PVector position;
	
	private static final int COLOUR = 0xFFDD1111;
	private static final int RADIUS = 30;

	public Ball(PVector startPos) {
		this.movement = new PVector(2,2,3);
		this.position = startPos;
	}

	/**
	 * Called for every frame from draw():
	 */
	public void update() {
		// Bounce off walls (margins): this.movement *= -1?
		
		this.position.add(this.movement);
		// println("speed: " + this.speed);
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
