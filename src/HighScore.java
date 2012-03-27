import java.util.Date;
import java.text.SimpleDateFormat;

public class HighScore implements Comparable<HighScore> {

	private String name;
	private Date time;
	private int score;
	
	public static SimpleDateFormat DATEFORMAT = 
		new SimpleDateFormat("dd.MM.yyyy HH:mm");

	public HighScore(String name, Date time, int score) {
		this.name = name;
		this.time = time;
		this.score = score;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public Date getTime() {
		return this.time;
	}

	private String getName() {
		return this.name;
	}

	public int compareTo(HighScore hs) {
		if (hs == null) {
			return 5;
		}
		if (this.getScore() < hs.getScore()) {
			return 4;
		} else if (this.getScore() > hs.getScore()) {
			return -4;
		}
		if (this.getTime().before(hs.getTime())) {
			return 2;
		} else if (this.getTime().after(hs.getTime())) {
			return -2;
		}
		if (this.getName().compareToIgnoreCase(hs.getName()) 
				< 0) {
			return 1;
		} else if (this.getName().compareToIgnoreCase(hs.getName())
				> 0) {
			return -1;
		}
		return 0;
	}
	
	public String toString() {
		return this.name + "&" + DATEFORMAT.format(this.time) + "&" + this.score;
	}

}


