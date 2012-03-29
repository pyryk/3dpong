import java.applet.*;
import java.net.MalformedURLException;
import java.net.URL;
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

	private static final float Y_MOVEMENT_LIMIT = (float) 0.4;
	private static final float X_MOVEMENT_LIMIT = (float) 0.4;
	
	private static final PVector STARTPOS = new PVector(0, 0, -Cube.DEPTH + Ball.RADIUS);

	private int areaw, areah;

	private ArrayList<Flame> flames;
	private AudioClip hit_sound;
	private AudioClip fail_sound;

	public Ball(int areaw, int areah) {
		this.speed = 60;
		this.areaw = areaw;
		this.areah = areah;
		flames = new ArrayList<Flame>();
		try {
			hit_sound = Applet.newAudioClip(new URL("file:beep.wav"));
			fail_sound = Applet.newAudioClip(new URL("file:beep2.wav"));
		} catch (MalformedURLException e) {
			Log.debug(this, e.getLocalizedMessage());
		}
		this.start();
	}

	private void start() {
		float x = 1 - (float) Math.random()*2;
		float y = 1 - (float) Math.random()*2;
		float z = 1 + (float) Math.random();

		this.movement = new PVector(x, y, z);
		this.position = new PVector(Ball.STARTPOS.x, Ball.STARTPOS.y, Ball.STARTPOS.z);
	}

	/**
	 * Called for every frame from draw()
	 */
	public void update(GameModel game) {
		this.bounce(game);		
		
		// Normalise movement and make sure movement on the z axis is enough
		// and the movement along x and y axes not too steep
		this.movement.normalize();
		if(Math.abs(this.movement.x) > X_MOVEMENT_LIMIT) {
			boolean negative = this.movement.x < 0;
			if(negative) {
				this.movement.x = -X_MOVEMENT_LIMIT;
			} else {
				this.movement.x = X_MOVEMENT_LIMIT;				
			}
			this.movement.normalize();
		}
		if(Math.abs(this.movement.y) > Y_MOVEMENT_LIMIT) {
			boolean negative = this.movement.y < 0;
			if(negative) {
				this.movement.y = -Y_MOVEMENT_LIMIT;
			} else {
				this.movement.y = Y_MOVEMENT_LIMIT;				
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
				hit_sound.play();
				this.movement.z = -this.movement.z;
				PVector hitDir = hit.getHitDirection(this.position);
				this.movement.add(hitDir);
				this.speed += 1.5;
			} else {
				fail_sound.play();
				game.ballEscaped(this);
				this.start();	// reset position and movement direction
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
