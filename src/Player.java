import java.util.ArrayList;
import java.util.List;

import processing.core.PVector;


public class Player {

	private int id;
	private List<Racket> rackets;
	private int points;

	public Player(int id) {
		this.setId(id);
		this.rackets = new ArrayList<Racket>();
		this.points = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Racket> getRackets() {
		return this.rackets;
	}
	
	public void givePoint() {
		this.points+=1;
	}
	
	public int getPoints() {
		return this.points;
	}
	
	public void resetPoints() {
		this.points = 0;
	}

	public PVector[] getRacketPositions() {
		
		PVector[] positions = new PVector[rackets.size()];
		for(int i = 0; i < rackets.size(); i++) {
			Racket r = rackets.get(i);
			if(r != null) {
				positions[i] = r.getPosition();
			} else {
				positions[i] = null;
			}
		}
		return positions;
	}

	public PVector getRacketPosition(int i) {
		Racket r = this.rackets.get(i);
		if(r == null) return null;
		else return r.getPosition();
	}

	public void setRacketPositions(PVector[] positions) {
		// adjust the z according to the player number
		for (int i = 0; i<positions.length; i++) {
			PVector pos = positions[i];
			pos.z = this.id == 0 ? 0 : -500;
			if(rackets.size() > i && rackets.get(i) != null) {
				rackets.get(i).setPosition(pos);
			} else {
				rackets.add(new Racket(this.id, pos));
			}
		}

	}

	public void setRacketPosition(int i, PVector pos) {
		Racket r = rackets.get(i);
		if(r != null) r.setPosition(pos);
	}

	public void drawRackets(App app, boolean active) {
		for(Racket r : this.rackets) {
			if(r != null) r.draw(app, active);
		}
	}

	public void addRacket(PVector pos) {
		this.rackets.add(new Racket(this.id, pos));
		
	}
}
