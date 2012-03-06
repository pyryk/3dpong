import processing.core.PApplet;
import processing.core.PVector;

public class Ball {

	private PVector movement;
	private PVector position;
	
	private static final int COLOUR = 0xFFDD1111;
	private static final int RADIUS = 30;
	
	private int areaw, areah;
	
	public Ball(PVector startPos, int areaw, int areah) {
		this.movement = new PVector(5,5,-5);
		this.position = startPos;
		this.areaw = areaw;
		this.areah = areah;
	}

	/**
	 * Called for every frame from draw():
	 */
	public void update() {
		// Bounce off walls beta
		if (this.getZ() <= -Cube.DEPTH){
			this.movement.z = 20;
		} else if (this.getZ()>= 0){
			this.movement.z = -20;
		}
		
		if (this.getX() <= -areaw/2){
			this.movement.x = 19;
		} else if (this.getX()>= areaw/2){
			this.movement.x = -19;
		}
		
		if (this.getY()<= -areah/2){
			this.movement.y = 18;
		} else if (this.getY()>= areah/2){
			this.movement.y = -18;
		}
		
		this.position.add(this.movement);
	}

	public void draw(PApplet app) {
		System.out.println(this.position);
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
