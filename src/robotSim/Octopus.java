package robotSim;

import java.awt.*;
import becker.robots.City;
import becker.robots.Direction;
import java.util.*;

public class Octopus extends Player {
	private int x, y;
	private int maximumEnergyLevel, stepsPerMove, energyLevel;
	private double dodgingAbility;
	private playerRecord[] playerList;
	private String name;
	private boolean resting = false;
	private int targetX, targetY;
	private double targetDistance;
	private String targetName;
	
	

	public Octopus(String name, int energyLevel, int stepsPerMove, double dodgingAbility, playerRecord[] playerList, City city, int y, int x, Direction direction) {
		super(name,energyLevel, stepsPerMove, dodgingAbility, city, y, x, direction);
		this.setColor(new Color(255, 165, 0));
		this.maximumEnergyLevel = energyLevel;
		this.energyLevel = this.maximumEnergyLevel;

	}
	
	

	
	public void takeTurn() {
		if (this.sufficientEnergy() && this.resting != true) {
			this.chase();
		} else {
			this.rest();
		}

	}
	
	private void chase() {
		this.lockOnTarget();
		this.advanceToTarget();
		this.energyLevel -= 1;
		this.tagAttempt();
	}
	
	
	private void tagAttempt() {
		
	}


	private void advanceToTarget() {
		
	}


	private void lockOnTarget() {
		this.sortByDistance();
	}


	private void rest() {
		Random r = new Random();
		if (this.resting == true) {
			for (int i =0; i < (r.nextInt(3 - 1 + 1) + 1); i++) {
				if (this.energyLevel +1 <= this.maximumEnergyLevel) {
					this.energyLevel += 1;
				} else {
					this.resting = false;
				}
			}
		}
		
	}

	private boolean sufficientEnergy() {
		if (this.energyLevel > 0) {
			return true;
		} else {
			return false;
			
		}
	}


	public int getType() {
		return 4;
	}

	@Override
	public void setColor(Color color) {
		super.setColor(color);
	}

	


}
