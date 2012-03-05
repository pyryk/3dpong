import processing.core.PApplet;

/**
 * A class for testing the game without a Kinect.
 * @author Noora
 *
 */
@SuppressWarnings("serial")
public class TestApp extends PApplet {
	
	GameModel gameModel;
	
	public static final int bgcolor = 225;
	
	public void setup() {
		this.gameModel = new GameModel();
		size(1000, 600, P3D);

		// enable logging
		Log.enabled = true;
		
		this.gameModel.startGame();
	}
	
	public void draw() {
		lights();
		background(bgcolor);
		this.gameModel.update(this);

	}

}
