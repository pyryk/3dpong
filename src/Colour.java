/**
 * Player colour (rackets etc)
 * @author Noora
 *
 */
public enum Colour {
	ORANGE, BLUE, GREEN, PINK, RED;

	public int[] getRGB() {
		switch(this) {
		case ORANGE: return new int[]{ 255, 120, 0 };
		case BLUE: return new int[]{ 50, 80, 255 };
		case GREEN: return new int[]{ 70, 250, 70};
		case PINK: return new int[]{ 255, 170, 190};
		case RED: return new int[]{ 255, 50, 50};
		default: return new int[]{ 255, 200, 50 };
		}
	}
}