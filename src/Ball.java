import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PVector;

public class Ball {

	private PVector position;	// current position of the ball
	private PVector movement;	// normalised movement vector
	private float speed;		// current speed of the ball

	private static final int COLOUR = 0xFFDD1111;
	public static final int RADIUS = 30;

	// Threshold for z movement (to make sure the Ball moves enough on the z axis)
	private static final float Z_MOVEMENT_THRESHOLD = (float) 0.2;

	private int areaw, areah;

	private ArrayList<Flame> flames;

	public Ball(PVector startPos, int areaw, int areah) {
		this.movement = new PVector(
				(float)Math.random(),
				(float)Math.random(),
				(float) 0.5);
		this.speed = 30;
		this.position = startPos;
		this.areaw = areaw;
		this.areah = areah;
		flames = new ArrayList<Flame>();
	}

	/**
	 * Called for every frame from draw()
	 */
	public void update(GameModel game) {
		this.bounce(game);		
		
		// Normalise movement and make sure movement on the z axis is enough
		this.movement.normalize();
		if(Math.abs(this.movement.z) < Z_MOVEMENT_THRESHOLD) {
			boolean negative = this.movement.z < 0;
			if(negative) {
				this.movement.z = -Z_MOVEMENT_THRESHOLD;
			} else {
				this.movement.z = Z_MOVEMENT_THRESHOLD;				
			}
			this.movement.normalize();
		}

		// Update position
		this.movement.mult(this.speed);
		this.position.add(this.movement);
	}

	// Bounce off walls and rackets and things
	private void bounce(GameModel game) {
		// Z (back or front)
		if (this.getZ() < -Cube.DEPTH){
			this.movement.z = -this.movement.z;
		} else if (this.getZ()> 0){
			// Collision with a racket (or escape)
			Racket hit = game.hitByRacket(this);
			if(hit != null) {
				this.movement.z = -this.movement.z;
				PVector hitDir = hit.getHitDirection(this.position);
				this.movement.add(hitDir);
				this.speed += 1;
			} else {
				game.ballEscaped(this);
				this.position = new PVector(0, 0, -Cube.DEPTH);
				return;
			}
		}

		// X (sides)
		if (this.getX() < -areaw/2){
			this.movement.x = -this.movement.x;
		} else if (this.getX()> areaw/2){
			this.movement.x = -this.movement.x;
		}

		// Y (top and bottom)
		if (this.getY()< -areah/2){
			this.movement.y = -this.movement.y;
		} else if (this.getY()> areah/2){
			this.movement.y = -this.movement.y;
		}
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
