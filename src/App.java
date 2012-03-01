import SimpleOpenNI.*;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class App extends PApplet {

	SimpleOpenNI context;

	GameModel gameModel;

	public void setup() {

		this.gameModel = new GameModel();

		// enable logging
		Log.enabled = true;

		context = new SimpleOpenNI(this);

		// enable depthMap generation
		// enable depthMap generation
		if (context.enableDepth() == false) {
			Log.error(this, "Can't open the depthMap, maybe the camera is not connected!");
			exit();
			return;
		}

		// enable camera image generation
		context.enableRGB();

		// enable skeletons
		context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);

		background(200, 0, 0);
		size(context.depthWidth() + context.rgbWidth() + 10,
				context.rgbHeight());
	}

	public void draw() {
		// update the cam
		context.update();

		// draw depthImageMap
		image(context.depthImage(), 0, 0);

		// draw camera
		image(context.rgbImage(), context.depthWidth() + 10, 0);

		// Log.debug(this, "Users found: " + context.getNumberOfUsers());

		// draw skeletons
		//Log.debug(this, "Players: " + this.gameModel.getPlayerCount());
		for (Player player : this.gameModel.getPlayers()) {
			if (context.isTrackingSkeleton(player.getId())) {
				//Log.debug(this, "Drawing skeleton for player " + player.getId());
				drawSkeleton(player.getId());
			} else {
				Log.debug(this,
					"Not tracking skeleton for player " + player.getId());
			}

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
		Log.debug(this, "onEndCalibration - userId: " + userId + ", successfull: "
				+ successfull);

		if (successfull) {
			// TODO player adding only pregame
			Log.debug(this, "User calibrated !!!");
			this.gameModel.addPlayer(new Player(userId));
			context.startTrackingSkeleton(userId);
			Log.debug(this, "User added to players.");
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

}
