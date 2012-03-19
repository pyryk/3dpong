import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import SimpleOpenNI.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

@SuppressWarnings("serial")
public class App extends PApplet {

	SimpleOpenNI context;

	GameModel gameModel;
	
	PVector referencePosition;
	public static boolean KINECT_AVAILABLE = true;

	Racket debugRacket = new Racket(new PVector(0,0,0));
	
	public void setup() {

		size(screen.width, screen.height, P3D);
		
		this.gameModel = new GameModel(this);

		// enable logging
		Log.enabled = true;

		if (KINECT_AVAILABLE) {
			context = new SimpleOpenNI(this);
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
		} else { // debug mode - use mouse
			Player player = new Player(0);
			this.gameModel.addPlayer(player, true);	
			Player player2 = new Player(1);
			this.gameModel.addPlayer(player2, true);	
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
			
			// draw camera
			PImage rgb = context.rgbImage();
			//rgb.resize(rgb.width/2, rgb.height/2);
			pushMatrix();
			scale(0.5f);
			image(rgb, -width, -height);
			popMatrix();
		}

		// draw depthImageMap
		//image(context.depthImage(), 0, 0);

		
		//background(255, 25);
		
		// Log.debug(this, "Users found: " + context.getNumberOfUsers());

		this.updatePlayers();
		this.gameModel.update(this);
	}

	public void updatePlayers() {
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
				
			} else if(!KINECT_AVAILABLE) {
				Log.debug(this, "No kinect available - using debug player");
			} else {
				Log.debug(this,
						"Not tracking skeleton for player " + player.getId());
			}
		}
		
		if (this.referencePosition == null && this.gameModel.getPlayerCount() > 0) {
			recalibrate();
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
		Log.debug(this, "Left hand " + hands[0]);
		Log.debug(this, "Right hand " + hands[1]);
		return hands;
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		this.gameModel.startGame();
		super.keyPressed(e);
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			debugRacket.getPosition().x += 10;
			break;
		case KeyEvent.VK_RIGHT:
			debugRacket.getPosition().x -= 10;
			break;
		case KeyEvent.VK_UP:
			debugRacket.getPosition().y += 10;
			break;
		case KeyEvent.VK_DOWN:
			debugRacket.getPosition().y -= 10;
			break;
		case KeyEvent.VK_W:
			debugRacket.getPosition().z -= 100;
			break;
		case KeyEvent.VK_S:
			debugRacket.getPosition().z += 100;
			break;
		default:
			break;
		}
	}
	
	@Override
	public void mouseMoved() {
		if (this.gameModel.getDebugPlayer() != null) {
			if (this.gameModel.getDebugPlayer().getRacketPositions().length > 0) {
				PVector old = this.gameModel.getDebugPlayer().getRacketPositions()[0];
				PVector[] pos = new PVector[1];
				pos[0] = new PVector(old.x -(pmouseX - mouseX), old.y -(pmouseY - mouseY), old.z);
				Log.debug(this, "Debug player position: " + pos[0]);
				this.gameModel.getDebugPlayer().setRacketPositions(pos);
			} else {
				this.gameModel.getDebugPlayer().addRacket(
						new PVector(mouseX, mouseY, Racket.Z_POS));
			}
		}
	}
	
	public static void main(String args[]) {
		    PApplet.main(new String[] { "--present", "App" });
	}

}
