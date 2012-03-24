import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import SimpleOpenNI.*;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

@SuppressWarnings("serial")
public class App extends PApplet {

	SimpleOpenNI context;

	GameModel gameModel;

	PVector referencePosition;
	//public static boolean KINECT_AVAILABLE = true;
	public static boolean KINECT_AVAILABLE = false;

	static enum Phase {
		MENU, INITIALISATION, GAME, END;
	}
	
	private Phase phase;
	private Mode gameMode;

	public void setup() {

		size(screen.width, screen.height, P3D);

		this.gameModel = new GameModel(this);
		this.phase = Phase.MENU;

		// enable logging
		Log.enabled = true;

		if (KINECT_AVAILABLE) {
			context = new SimpleOpenNI(this);
			this.gameMode = Mode.TWO_PLAYER;
		} else {
			this.gameMode = Mode.MOUSE;
		}

		// enable depthMap generation
		if (KINECT_AVAILABLE && context.enableDepth() == false) {
			Log.error(this,
			"Can't open the depthMap, maybe the camera is not connected!");
			exit();
			return;
		}

		if (KINECT_AVAILABLE) {
			context.setMirror(true);
			// enable camera image generation
			context.enableRGB();
			// enable skeletons
			context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
		}

		background(200, 0, 0);

		//this.gameModel.startGame();
	}

	public void draw() {
		lights();
		background(255);

		// update the cam
		if (KINECT_AVAILABLE) {
			context.update();
			this.drawCamera();
		}

		// draw depthImageMap
		//image(context.depthImage(), 0, 0);

		// Log.debug(this, "Users found: " + context.getNumberOfUsers());

		PFont font = createFont("DejaVu Sans",50);
		textFont(font, 50); 
		this.fill(0xFFDD1111);	
		this.noStroke();
		switch(this.phase) {
		case MENU : 
			this.text("This is 3dPong",100,100,0);

			this.text("Start a "+ this.gameMode + " game",100,300,0);
			this.text("Select mode",100,400,0);
			this.text("End game",100,500,0);

			//this.camera(cam/200,0, Cube.DEPTH/2, 0,0,-Cube.DEPTH, 0, 1, 0);
			break;
		case INITIALISATION:
			this.text("Put your hands up in the air.",100,100,0);
			this.drawCamera();
			break;

		case END:
			camera();
			int player_count = this.gameModel.getPlayerCount();
			this.text("Game over.\n\nEnd results:" ,100,100,0);
			for (int i=0;i<player_count;i++){
				this.text("Player "+this.gameModel.getPlayer(i).getId() +": "+this.gameModel.getPlayer(i).getPoints()+" points.",100,350+i*100,0);
			}
			this.text("Press n for new game, m for menu or q to quit.",100,800,0);
			break;

		case GAME:
			this.updatePlayers(this);
			this.gameModel.update(this);
			if (!this.gameModel.isGame()){
				this.phase = Phase.END;
			}
			break;
		}
	}

	private void drawCamera() {
		// draw camera
		PImage rgb = context.rgbImage();
		//rgb.resize(rgb.width/2, rgb.height/2);
		pushMatrix();
		scale(0.5f);
		image(rgb, -width, -height);
		popMatrix();
	}

	public void updatePlayers(App app) {
		if(this.gameMode == Mode.MOUSE && this.gameModel.isGame()) {
			Player activePlayer = this.gameModel.getActivePlayer();
			if (activePlayer != null) {
				if (activePlayer.getRacketPositions().length > 0) {					
					PVector pos = this.gameModel.get3DCoordinates(mouseX, mouseY, app);
					activePlayer.setRacketPosition(0, pos);
				} else {
					activePlayer.addRacket(new PVector(mouseX, mouseY, Racket.Z_POS));
				}
			}
		} else {
			// draw skeletons
			// Log.debug(this, "Players: " + this.gameModel.getPlayerCount());
			List<PVector> allHands = new ArrayList<PVector>();

			for (Player player : this.gameModel.getPlayers()) {
				if (KINECT_AVAILABLE && context.isTrackingSkeleton(player.getId())) {
					// Log.debug(this, "Drawing skeleton for player " +
					// player.getId());
					drawSkeleton(player.getId());
					PVector[] hands = getUserHands(player.getId());
					player.setRacketPositions(hands);

				} else if(KINECT_AVAILABLE) {
					Log.debug(this,
							"Not tracking skeleton for player " + player.getId());
				}
			}

			if (this.referencePosition == null && this.gameModel.getPlayerCount() > 0) {
				recalibrate();
			}
		}
	}

	public void recalibrate() {
		if (!KINECT_AVAILABLE) {
			return;
		}

		Log.debug(this, "Recalibrating racket positions");
		List<PVector> allHands = new ArrayList<PVector>();
		for (Player player : this.gameModel.getPlayers()) {
			// add hands to all hands for calibration
			PVector[] hands = getUserHands(player.getId());
			for (PVector hand : hands) {
				allHands.add(hand);
			}
		}

		// TODO calculate average position for all hands
		if (allHands.size() > 0) {
			PVector vect = allHands.get(0);
			this.referencePosition = new PVector(vect.x, vect.y, vect.z);
		}		
	}

	public void onNewUser(int userid) {
		Log.debug(this, "Found user " + userid);
		context.startPoseDetection("Psi", userid);
	}

	public void onLostUser(int userid) {
		Log.debug(this, "Lost user " + userid);
	}

	public void onStartCalibration(int userId) {
		Log.debug(this, "onStartCalibration - userId: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull) {
		Log.debug(this, "onEndCalibration - userId: " + userId
				+ ", successfull: " + successfull);

		if (successfull) {
			// TODO player adding only pregame
			Log.debug(this, "User calibrated !!!");
			this.gameModel.addPlayer(new Player(userId));
			context.startTrackingSkeleton(userId);
			Log.debug(this, "User added to players.");

			// recalibrate positions
			// TODO this should only be done when game is not on
			this.referencePosition = null;
		} else {
			Log.debug(this, "  Failed to calibrate user !!!");
			Log.debug(this, "  Start pose detection");
			context.startPoseDetection("Psi", userId);
		}
	}

	public void onStartPose(String pose, int userId) {
		Log.debug(this, "onStartPose - userId: " + userId + ", pose: " + pose);
		Log.debug(this, " stop pose detection");

		context.stopPoseDetection(userId);
		context.requestCalibrationSkeleton(userId, true);
	}

	public void onEndPose(String pose, int userId) {
		Log.debug(this, "onEndPose - userId: " + userId + ", pose: " + pose);
	}

	public void drawSkeleton(int userId) {
		// to get the 3d joint data
		/*
		 * PVector jointPos = new PVector();
		 * context.getJointPositionSkeleton(userId
		 * ,SimpleOpenNI.SKEL_NECK,jointPos); Log.debug(this, jointPos);
		 */

		context.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

		context.drawLimb(userId, SimpleOpenNI.SKEL_NECK,
				SimpleOpenNI.SKEL_LEFT_SHOULDER);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER,
				SimpleOpenNI.SKEL_LEFT_ELBOW);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW,
				SimpleOpenNI.SKEL_LEFT_HAND);

		context.drawLimb(userId, SimpleOpenNI.SKEL_NECK,
				SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER,
				SimpleOpenNI.SKEL_RIGHT_ELBOW);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW,
				SimpleOpenNI.SKEL_RIGHT_HAND);

		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER,
				SimpleOpenNI.SKEL_TORSO);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER,
				SimpleOpenNI.SKEL_TORSO);

		context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO,
				SimpleOpenNI.SKEL_LEFT_HIP);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP,
				SimpleOpenNI.SKEL_LEFT_KNEE);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE,
				SimpleOpenNI.SKEL_LEFT_FOOT);

		context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO,
				SimpleOpenNI.SKEL_RIGHT_HIP);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP,
				SimpleOpenNI.SKEL_RIGHT_KNEE);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE,
				SimpleOpenNI.SKEL_RIGHT_FOOT);
	}

	public PVector[] getUserHands(int userid) {
		PVector leftHand = new PVector();
		PVector rightHand = new PVector();
		PVector leftHandWorld = new PVector();
		PVector rightHandWorld = new PVector();
		context.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_HAND,
				leftHand);
		context.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_HAND,
				rightHand);
		leftHand.y = -leftHand.y;
		rightHand.y = -rightHand.y;
		context.convertProjectiveToRealWorld(leftHand, leftHandWorld);
		context.convertProjectiveToRealWorld(rightHand, rightHandWorld);
		//PVector[] hands = { leftHandWorld, rightHandWorld };
		PVector[] hands = { leftHand, rightHand };
		//Log.debug(this, "Left hand " + hands[0]);
		//Log.debug(this, "Right hand " + hands[1]);
		return hands;
	}


	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);

		switch(this.phase) {
		case MENU :
			switch (e.getKeyCode()) {
			case 'N' :
				this.startInitialisation();
				break;
			case 'M' :
				this.gameMode = Mode.next(this.gameMode);
				break;
			case 'Q' :
				System.exit(0);
				break;
			default:
				break;
			}
			break;
		case END :
			switch (e.getKeyCode()) {
			case 'N' :
				this.startInitialisation();
				break;
			case 'M' :
				this.phase = Phase.MENU;
				break;
			case 'Q' :
				System.exit(0);
				break;
			}			
			default:
				break;
		}
	}

	private void startInitialisation() {
		if(this.gameMode == Mode.MOUSE) {
			this.startGame();
			return;
		}

		this.phase = Phase.INITIALISATION;
	}

	private void startGame() {
		this.phase = Phase.GAME;
		this.gameModel.startGame(this.gameMode);
	}

	@Override
	public void mouseMoved() {
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "App" });
	}

}
