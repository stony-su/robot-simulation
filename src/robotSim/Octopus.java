package robotSim;

import java.awt.*;
import becker.robots.City;
import becker.robots.Direction;
import unit3.Account;

import java.util.*;

public class Octopus extends Player {
	private int x, y;
	private int maximumEnergyLevel, maxStepsPerMove, energyLevel;
	private double dodgingAbility;
	private boolean chasing = false;
	private playerRecord[] playerList;
	private String name;
	private boolean resting = false;
	private int targetX, targetY;
	private double targetDistance;
	private String targetName;



	public Octopus(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, playerRecord[] playerList, City city, int y, int x, Direction direction) {
		super(name,energyLevel, maxStepsPerMove, dodgingAbility, city, y, x, direction);
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
		this.chasing= true;
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
		if (distanceCalc(playerList[0]) > maxStepsPerMove/energyLevel) {
			this.sortByDistance(playerList);
			for (int i =0; i < playerList.length; i++) {
				if (playerList[i].getType() < 3) {
					this.targetY = playerList[i].getY();
					this.targetX = playerList[i].getX();
					this.targetName = playerList[i].getName();
				}
			}

		}
	}

	private void sortByDistance(playerRecord [] numbersArray) {
		final int ARRAYLENGTH = numbersArray.length;
		for (int i = 0; i < ARRAYLENGTH; i++) {
			for (int j = i; j > 0; j--) {
				if (distanceCalc(numbersArray[j]) < distanceCalc(numbersArray[j-1])) {
					swap(j,j-1, numbersArray);
				}
			}
		}
	}

	private double distanceCalc(playerRecord player) {
		return Math.sqrt((player.getX() - this.x)^2 + (player.getY() - this.y));
	}
	private static void swap(int pos1, int pos2, playerRecord swapArray[]) {
		// saving the 2 numbers to temp variables
		playerRecord swapped1 = swapArray[pos1];

		// swapping the variables
		swapArray[pos1] = swapArray[pos2];
		swapArray[pos2] = swapped1;
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
