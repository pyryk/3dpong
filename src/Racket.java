import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * Represents one Racket in the game that can be used to hit the Ball.
 * @author Noora Routasuo
 *
 */
public class Racket {

	public static final int Z_POS = 0;

	private PVector pos;	// position (centre of the racket)
	private PVector mov;	// movement (averaged over a few last frames)

	private int playerID;

	private int width, height, thickness;

	// Percentage of the width and height of the Racket that is considered "centre"
	// of the racket for hitting calculations
	private float centreSize;

	public Racket(int playerid) {
		this.mov = new PVector(0,0,-1);
		this.width = 400;
		this.height = 300;
		this.centreSize = (float) 0.5;
		this.thickness = 5;
		this.playerID = playerid;
	}

	public Racket(int playerid, PVector pos) {
		this(playerid);
		this.pos = pos;
	}

	public PVector getPosition() {
		return this.pos;
	}

	public PVector getMovement() {
		return this.mov;
	}

	public PVector[] getDimensions() {
		PVector[] dim = {pos, new PVector(pos.x + width, pos.y + height)};
		return dim;
	}

	public void setPosition(PVector newpos) {
		this.updateMov(this.pos, newpos);
		this.pos = newpos;
		this.pos.z = Racket.Z_POS;
	}

	/**
	 * Returns the direction where the Racket sends a Ball in the given position. This
	 * is affected by the Racket's speed and the position where the Ball hits the Racket,
	 * and the final direction also depends on the Ball's movement vector.
	 * @param position	Position of the Ball when the hit happens
	 * @return			Direction where the Racket sends the Ball
	 */
	public PVector getHitDirection(PVector position) {
		// Hit position (sides may add up in the corners of the racket)
		int posweight = 8;
		PVector hitpos = new PVector(0,0,0);
		if(position.y > this.pos.y + centreSize*this.height/2) {
			// Bottom
			hitpos.add(new PVector(0,posweight,0));
		}
		if(position.y < this.pos.y - centreSize*this.height/2) {
			// Top
			hitpos.add(new PVector(0,-posweight,0));
		}
		if(position.x > this.pos.x + centreSize*this.width/2) {
			// Right
			hitpos.add(new PVector(posweight,0,0));
		}
		if(position.x < this.pos.x - centreSize*this.width/2) {
			// Left
			hitpos.add(new PVector(-posweight,0,0));
		}

		return hitpos;
	}

	private void updateMov(PVector pos, PVector newpos) {
		if(pos == null) return;
		
		float weight = (float) 0.3;

		PVector frameMovement = new PVector(newpos.x - pos.x, newpos.y - pos.y, 0);

		float oldMagnitude = this.mov.mag();
		float frameMagnitude = frameMovement.mag();
		float newMagnitude = ((1-weight)*oldMagnitude + weight*frameMagnitude)/2;

		this.mov = PVector.add(this.mov, frameMovement);
		this.mov.normalize();
		this.mov.mult(newMagnitude);
	}

	public boolean hits(Ball ball) {
		if(ball.getZ() + Ball.RADIUS >= Racket.Z_POS) {
			float xdistance = Math.abs(ball.getX() - this.pos.x);
			float ydistance = Math.abs(ball.getY() - this.pos.y);

			if(xdistance < this.width/2 && ydistance < this.height/2) {
				return true;
			}
		}
		return false;
	}
	
	public static int[] getColor(int playerID, boolean active) {
		int[] colour = Colour.values()[playerID].getRGB();
		int r = colour[0];
		int g = colour[1];
		int b = colour[2];
		int a = active ? 180 : 20;
		
		return new int[] {r,g,b,a};
	}

	public void draw(PApplet app, boolean active) {
		
		int[] color = getColor(this.playerID, active);
		
		app.fill(color[0],color[1],color[2],color[3]); 
		app.stroke(color[0],color[1],color[2],color[3]);

		// app.pushMatrix();
		// Shift overall coordinate system to the centre of the display
		// app.translate(width/2, height/2, -GameModel.D_MARGIN);
		// popMatrix();

		//Log.debug(this, "Drawing racket " + this.pos);

		app.pushMatrix();
		app.translate(this.pos.x, this.pos.y, this.pos.z);
		app.box(width,height,thickness);
		app.noFill();
		app.box(width*centreSize, height*centreSize, thickness);
		app.popMatrix();
	}

}

