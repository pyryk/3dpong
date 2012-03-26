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
	
	long initialisationDoneAt = 0L;

	PVector referencePosition;
	public static boolean KINECT_AVAILABLE = true;
	//public static boolean KINECT_AVAILABLE = false;

	static enum Phase {
		MENU, INITIALISATION, GAME, END;
	}

	private int highlight_row;
	private Phase phase;
	private Mode gameMode;

	public void setup() {

		size(screen.width, screen.height, P3D);

		this.highlight_row = 1;
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
			//this.drawCamera(0.5f);
		}

		// draw depthImageMap
		//image(context.depthImage(), 0, 0);

		// Log.debug(this, "Users found: " + context.getNumberOfUsers());

		PFont font = createFont("DejaVu Sans",50);
		textFont(font, 50); 

		this.noStroke();
		int texty = 100;
		int textx = 100;
		int lineheight = 70;
		switch(this.phase) {
		case MENU :
			this.fill(0x00000011);
			this.text("This is 3dPong",textx,texty,0);
			if (highlight_row==1){
				this.fill(0xFFDD1111);	
			}else{
				this.fill(0x00000011);	
			}
			
			texty += lineheight*2;
			this.text("Start a "+ this.gameMode + " game",textx,texty,0);
			if (highlight_row==2){
				this.fill(0xFFDD1111);	
			}else{
				this.fill(0x00000011);	
			}
			texty += lineheight;
			this.text("Select mode",textx,texty,0);
			if (highlight_row==3){
				this.fill(0xFFDD1111);	
			}else{
				this.fill(0x00000011);	
			}
			texty += lineheight;
			this.text("End game",textx,texty,0);

			//this.camera(cam/200,0, Cube.DEPTH/2, 0,0,-Cube.DEPTH, 0, 1, 0);
			break;
		case INITIALISATION:
			this.text("Put your hands up in the air.",textx,texty,0);
			if (this.initialisationDoneAt != 0L) {
				double secondsToStart = Math.max(Math.floor((2000 + initialisationDoneAt - millis())/100.0)/10, 0);
				this.text("Game starts in " + secondsToStart ,100,200,0);
			}
			this.drawRecognisedPlayers();
			this.drawCamera(0.5f);
			this.checkInitializationDone();
			break;

		case END:
			camera();
			int player_count = this.gameModel.getPlayerCount();
			this.fill(0x00000011);
			this.text("Game over." ,textx,texty,0);
			texty += lineheight;
			this.text("End results:", textx, texty, 0);

			int i = 0;
			for (Player p : this.gameModel.getPlayers()){
				this.text("Player " + p.getId() + ": "+p.getPoints()+" points.",100,350+i*100,0);
				i++;
			}

			if (highlight_row==1){
				this.fill(0xFFDD1111);	
			}else{
				this.fill(0x00000011);	
			}
			texty += lineheight * 2;
			this.text("Play again",textx,texty,0);
			if (highlight_row==2){
				this.fill(0xFFDD1111);	
			}else{
				this.fill(0x00000011);	
			}
			texty += lineheight;
			this.text("Go to menu",textx,texty,0);
			if (highlight_row==3){
				this.fill(0xFFDD1111);	
			} else {
				this.fill(0x00000011);	
			}
			texty += lineheight;
			this.text("End game",textx,texty,0);
			break;

		case GAME:
			if(this.gameModel.isGame()) {
				this.updatePlayers(this);
				this.gameModel.update(this);
				if (!this.gameModel.isGame()){
					this.phase = Phase.END;
				}
			}
			break;
		}
	}

	private void drawCamera(float scale) {
		// draw camera
		PImage rgb = context.rgbImage();
		//rgb.resize(rgb.width/2, rgb.height/2);
		pushMatrix();
		scale(scale);
		image(rgb, width, height);
		popMatrix();
	}

	private void drawRecognisedPlayers() {
		this.text("Players recognised: " + this.gameModel.getPlayerCount()
				+ "/" + this.gameMode.getNoOfPlayers(), 100,150,0);
	}

	private void checkInitializationDone() {
		if (this.gameModel.getPlayerCount() >= this.gameMode.getNoOfPlayers()) {
			Log.debug(this, "Initialization done");
			
			if (this.initialisationDoneAt == 0L) {
				this.initialisationDoneAt = millis();
			} else if (this.initialisationDoneAt + 2000 < millis()) { // wait 2sec
				this.startGame();
			}
		}
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
		this.gameModel.removePlayer(userid);
	}

	public void onStartCalibration(int userId) {
		Log.debug(this, "onStartCalibration - userId: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull) {
		Log.debug(this, "onEndCalibration - userId: " + userId
				+ ", successfull: " + successfull);

		if (successfull && this.gameMode.getNoOfPlayers() >= this.gameModel.getPlayerCount()) {
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
			case ENTER :
				if (highlight_row==1){
					this.startInitialisation();
				}else if(highlight_row==2){
					this.gameMode = Mode.next(this.gameMode);
				}else if(highlight_row==3){
					System.exit(0);
				}
				break;
			case DOWN :
				highlight_row+=1;
				if (highlight_row==4){highlight_row=1;}
				break;
			case UP :
				highlight_row-=1;
				if (highlight_row==0){highlight_row=3;}
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
			case ENTER :
				if (highlight_row==1){
					this.startInitialisation();
				}else if(highlight_row==2){
					this.phase = Phase.MENU;
				}else if(highlight_row==3){
					System.exit(0);
				}
				break;
			case DOWN :
				highlight_row+=1;
				if (highlight_row==4){highlight_row=1;}
				break;
			case UP :
				highlight_row-=1;
				if (highlight_row==0){highlight_row=3;}
				break;				
			}			
		default:
			break;
		}
	}

	private void startInitialisation() {
		this.gameModel.resetPlayers();
		this.initialisationDoneAt = 0L;
		
		if(this.gameMode == Mode.MOUSE) {
			this.gameModel.removePlayers();
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
