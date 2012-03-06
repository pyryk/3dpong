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
		size(screen.width, screen.height, P3D);
		this.gameModel = new GameModel(this);

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
