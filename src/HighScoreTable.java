import java.awt.Color;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * Represents high scores for one game type, stored in a text file.
 * @author Noora Routasuo
 *
 */
public class HighScoreTable implements Iterable<HighScore> {

	private static final int SIZE = 5;
	private List<HighScore> scores;
	private File file;

	public HighScoreTable(String location) {
		this.file = new File(location);
		this.scores = new ArrayList<HighScore>();
		this.loadScores();
	}

	public HighScore getRow(int row) {
		if (row < 0 || row > this.scores.size()-1) {
			return null;
		}
		return this.scores.get(row);
	}

	private void loadScores() {
		Scanner reader;
		try {
			reader = new Scanner(this.file);
		} catch (FileNotFoundException ex) {
			System.err.println("Could not read high scores from " + this.file);
			return;
		}

		String row;
		while (reader.hasNextLine()) {
			try {
				row = reader.nextLine();
				HighScore score = this.readScore(row);
				if (score != null) {
					this.addScore(score);
				}
			} catch(IllegalStateException ex) {	}
			
		}
	}

	private HighScore readScore(String row) {
		String[] parts = row.split("&");

		if (parts.length != 3) {
			return null;
		}

		String name = parts[0];
		String date = parts[1];
		Date time;
		try {
			time = HighScore.DATEFORMAT.parse(date);
		} catch (Exception ex) {
			return null;
		}
		if (time == null) {
			return null;
		}

		String scoreS = parts[2];
		int score;
		try {
			score = Integer.parseInt(scoreS);
		} catch(NumberFormatException ex) {
			return null;
		}
		return new HighScore(name, time, score);
	}

	public int getPosition(HighScore score) {
		if(this.scores.size() < 1) return 1;
		int pos = this.scores.size();
		for (HighScore hs : this.scores) {
			int index = scores.indexOf(hs)+1;
			if (hs.compareTo(score) > 0 && index < pos) {
				pos = index;
			}
		}
		return pos;
	}

	public int addScore(HighScore row) {
		int pos = this.getPosition(row);

		if (pos > 0) {
			this.scores.add(pos-1, row);
			if (this.scores.size() >= HighScoreTable.SIZE) {
				this.scores.remove(this.scores.size()-1);
			}
		}
		return pos;
	}

	public boolean saveScores() {
		try {
			BufferedWriter w
			= new BufferedWriter(new FileWriter(this.file));
			for (HighScore hs : this.scores) {
				w.write(hs.toString());
				w.newLine();
			}
			w.close();
		} catch (IOException ex) {
			System.err.println("Could not write high scores to " + this.file);
			return false;
		}
		return true;
	}

	public Iterator<HighScore> iterator() {
		return scores.iterator();
	}
}
