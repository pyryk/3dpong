import processing.core.PApplet;
import processing.core.PVector;

/*
 * This class draws flames of the ball.
 */
public class Flame {
	
	private int x;
	private int y;
	private int z;
	private int size;
	
	public Flame(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = 30;
	}
	
	boolean draw(PApplet app){
		app.pushMatrix();
		app.translate(x,y,z);
		app.fill(255, 255-(8*this.size), 0,0+(8*this.size));
		app.noStroke();
		app.sphere(this.size);
		app.popMatrix();
		this.size=this.size-2;
		if (size==0){
		   return false;
		}else{
		   return true;		   
		}
	}
}
