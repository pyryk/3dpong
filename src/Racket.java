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

	public Racket(int playerid) {
		this.mov = new PVector(0,0,0);
		this.width = 400;
		this.height = 320;
		this.thickness = 7;
		this.playerID = playerid;
	}

	public Racket(int playerid, PVector pos) {
		this(playerid);
		this.pos = pos;
	}

	public PVector getPosition() {
		return this.pos;
	}
	
	public PVector[] getRacketDimensions() {
		PVector[] dim = {pos, new PVector(pos.x + width, pos.y + height)};
		return dim;
	}

	public void setPosition(PVector newpos) {
		this.updateMov(this.pos, newpos);
		this.pos = newpos;
		this.pos.z = Racket.Z_POS;
	}

	public PVector getMovement() {
		return this.mov;
	}

	private void updateMov(PVector pos, PVector newpos) {
		if(pos == null) return;
		// TODO: test
		float weight = (float) 0.5;
		
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

	public void draw(App app, boolean active) {
		
		int r = 200 - this.playerID*120;
		int g = 0 + this.playerID*50;
		int b = 150 + this.playerID*30;
		int a = active ? 180 : 40;
		
		app.fill(r,g,b,a); 
		app.stroke(r,g,b,a);
		
		// app.pushMatrix();
		// Shift overall coordinate system to the centre of the display
		// app.translate(width/2, height/2, -GameModel.D_MARGIN);
		// popMatrix();
				
		//Log.debug(this, "Drawing racket " + this.pos);
		
		app.pushMatrix();
		app.translate(this.pos.x, this.pos.y, this.pos.z);
		app.box(width,height,thickness);
		app.popMatrix();
		
		/*
		float frontZ = this.pos.z + this.thickness/2;
		float leftsideX = this.pos.x - this.width/2;
		float topY = this.pos.y - this.height/2;

		app.beginShape(PConstants.QUADS);
		
		// Front
		app.vertex(leftsideX, topY, frontZ);
		
		// Back
		
		// Sides
		
		// Top

		// Bottom
		
		app.endShape();
		*/
	}

}

