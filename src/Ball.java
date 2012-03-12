import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

public class Ball {

	private PVector movement;	// normalized movement vector
	private PVector position;	// current position of the ball
	private int speed;			// current speed of the ball

	private static final int COLOUR = 0xFFDD1111;
	private static final int RADIUS = 30;

	private int areaw, areah;
	
	private ArrayList<Flame> flames;

	public Ball(PVector startPos, int areaw, int areah) {
		this.movement = new PVector(1,1,-1);
		this.speed = 30;
		this.position = startPos;
		this.areaw = areaw;
		this.areah = areah;
		flames = new ArrayList();
	}

	/**
	 * Called for every frame from draw():
	 */
	public void update(GameModel game) {
		// Bounce off walls beta
		if (this.getZ() <= -Cube.DEPTH){
			this.movement.z = -this.movement.z;
		} else if (this.getZ()>= 0){

			// Collision with a racket (or escape)
			if(game.hitByRacket(this)) {
				this.movement.z = -this.movement.z;
				// TODO: ADD movement vector of the racket to the ball's movement
			} else {
				game.ballEscaped();
			}
		}

		if (this.getX() <= -areaw/2){
			this.movement.x = -this.movement.x;
		} else if (this.getX()>= areaw/2){
			this.movement.x = -this.movement.x;
		}

		if (this.getY()<= -areah/2){
			this.movement.y = -this.movement.y;
		} else if (this.getY()>= areah/2){
			this.movement.y = -this.movement.y;
		}

		this.movement.normalize();
		this.movement.mult(this.speed);
		this.position.add(this.movement);
	}

	public void draw(PApplet app) {
		//System.out.println(this.position);
		app.pushMatrix();
		app.noLights();
		flames.add(new Flame(this.getX(), this.getY(), this.getZ()));
		for (int i=flames.size()-1;i!=0;i--) {
		   if (!flames.get(i).draw(app)){
			   flames.remove(i);
		   }
		}
		app.lights();
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
