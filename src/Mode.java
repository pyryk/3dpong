public enum Mode {
	SINGLE_PLAYER, TWO_PLAYER, MOUSE;

	public String toString() {
		switch(this) {
		case SINGLE_PLAYER: return "single player Kinect";
		case TWO_PLAYER: return "two player Kinect";
		case MOUSE: return "mouse";
		default: return "unknown! O_o";
		}
	}

	public static Mode next(Mode gameMode) {
		if(App.KINECT_AVAILABLE) {
			switch(gameMode) {
			case TWO_PLAYER: return SINGLE_PLAYER;
			case SINGLE_PLAYER: return MOUSE;
			case MOUSE: return TWO_PLAYER;
			default: return TWO_PLAYER;
			}
		} else {
			return MOUSE;
		}
	}
	
	public int getNoOfPlayers() {
		switch(this) {
		case SINGLE_PLAYER: return 1;
		case TWO_PLAYER: return 2;
		case MOUSE: return 2;
		default: return 0;
		}
	}
}