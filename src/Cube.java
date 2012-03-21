import processing.core.*;

class Cube{

	public static final int DEPTH = 2500;

	PVector[] vertices = new PVector[24];
	int w, h, d;

	Cube(int w, int h) {
		this.w = w;
		this.h = h;
		this.d = Cube.DEPTH;

		// cube composed of 6 quads
		// front
		vertices[0] = new PVector(-w/2,-h/2,0);
		vertices[1] = new PVector(w/2,-h/2,0);
		vertices[2] = new PVector(w/2,h/2,0);
		vertices[3] = new PVector(-w/2,h/2,0);
		// left
		vertices[4] = new PVector(-w/2,-h/2,0);
		vertices[5] = new PVector(-w/2,-h/2,-d);
		vertices[6] = new PVector(-w/2,h/2,-d);
		vertices[7] = new PVector(-w/2,h/2,0);
		// right
		vertices[8] = new PVector(w/2,-h/2,0);
		vertices[9] = new PVector(w/2,-h/2,-d);
		vertices[10] = new PVector(w/2,h/2,-d);
		vertices[11] = new PVector(w/2,h/2,0);
		// back
		vertices[12] = new PVector(-w/2,-h/2,-d); 
		vertices[13] = new PVector(w/2,-h/2,-d);
		vertices[14] = new PVector(w/2,h/2,-d);
		vertices[15] = new PVector(-w/2,h/2,-d);
		// top
		vertices[16] = new PVector(-w/2,-h/2,0);
		vertices[17] = new PVector(-w/2,-h/2,-d);
		vertices[18] = new PVector(w/2,-h/2,-d);
		vertices[19] = new PVector(w/2,-h/2,0);
		// bottom
		vertices[20] = new PVector(-w/2,h/2,0);
		vertices[21] = new PVector(-w/2,h/2,-d);
		vertices[22] = new PVector(w/2,h/2,-d);
		vertices[23] = new PVector(w/2,h/2,0);
	}

	public int getW() {
		return this.w;
	}
	
	public int getH() {
		return this.h;
	}

	void draw(PApplet app, int ballZ){
		
		// Cube outline
		app.noFill(); 
		app.stroke(1);
		for (int i=0; i<6; i++){
			app.beginShape(PConstants.QUADS);
			for (int j=0; j<4; j++){
				app.vertex(vertices[j+4*i].x, vertices[j+4*i].y, vertices[j+4*i].z);
			}
			app.endShape();
		}
		
		// Ball depth cue
		app.stroke(0xFF11DD11);
		app.beginShape();
		app.vertex(-w/2, h/2, ballZ);
		app.vertex(w/2, h/2, ballZ);
		app.vertex(w/2, -h/2, ballZ);
		app.vertex(-w/2, -h/2, ballZ);
		app.endShape(PConstants.CLOSE);
	}

}
