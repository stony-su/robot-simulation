package robotSim;

import java.awt.*;
import becker.robots.City;
import becker.robots.Direction;
import unit3.Account;

import java.util.*;
import java.util.*;
public class Octopus extends Player {
	Random gen = new Random();
	private int x, y;
	private int maximumEnergyLevel, maxStepsPerMove, energyLevel;
	private double dodgingAbility;
	private playerRecord[] playerList;
	private String name;
	private boolean resting = false;
	private boolean chasing = false;
	private boolean tagging = false;
	private int targetX, targetY;
	private double targetDistance;
	private String targetName;



	public Octopus(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, playerRecord[] playerList, City city, int y, int x, Direction direction) {
		super(name,energyLevel, maxStepsPerMove, dodgingAbility, city, y, x, direction);
		this.setColor(new Color(255, 165, 0));
		this.maximumEnergyLevel = energyLevel;
		this.energyLevel = this.maximumEnergyLevel;


	}

	public void move() {
		// override of the super method from robot
		// if in the chasing state, then generate a random number based on the max steps per move
		// if the random generated number is at the max steps per move then consume 2 energy
		// else consume 1 energy
		// if not chasing then move 1
		if (this.chasing) {
			
			int stepsNum = this.gen.nextInt(maxStepsPerMove);
			super.move(maxStepsPerMove);
			if (stepsNum == maxStepsPerMove) {
				this.energyLevel -= 2;
			} else {
				this.energyLevel -= 1;
			}

		} else {
			super.move();
		}
	}


	
	public void takeTurn() {
		// if sufficient energy and not resting then chase
		// otherwise there's not enough energy and you need to rest
		if (this.sufficientEnergy() && this.resting != true) {
			this.chase();
		} else {
			this.rest();
		}

	}
	
	
	private void chase() {
		// first lock on to a target
		// then advance to it
		// then do a tag attempt
		// after the attempt set tagging to false
		// rest after trying to tag
		this.lockOnTarget();
		this.advanceToTarget();
		this.tagAttempt();
		this.tagging = false;
		this.rest();
	}

	
	
	private void tagAttempt() {
		// simply sets the tagging state to true
		this.tagging = true;
	}

	public boolean getTagging() {
		// returns if you're currently tagging
		return this.tagging;
	}

	private void advanceToTarget() {
		
		// basically aligning the x position then moving to the y position
		if (this.targetX != x) {
			if (this.targetX < x) {
				this.faceWest();
				for (int i = 0; i < (this.x-this.targetX); i++) {
					this.move();
				}
			} else {
				this.faceEast();
				for (int i = 0; i < (this.targetX-this.x); i++) {
					this.move();
				}
			}

		} else if (this.targetY != this.y) {
			if (this.targetY < y) {
				this.faceNorth();
				for (int i = 0; i < (this.y-this.targetY); i++) {
					this.move();
				}
			} else {
				this.faceSouth();
				for (int i = 0; i < (this.targetY-this.y); i++) {
					this.move();
				}
			}
		}
	}


	private void lockOnTarget() {
		// if you're not chasing anyone then sort the list by distance
		if (this.chasing == false) {	
			this.sortByDistance(playerList);
			this.chasing = true;
			// first looking for medic
			for (int i =0; i < playerList.length; i++) {
				if (playerList[i].getType() == 1) {
					this.targetY = playerList[i].getY();
					this.targetX = playerList[i].getX();
					this.targetName = playerList[i].getName();
					break;
				} else { // if no medic then find other players
					for (int j =0; i < playerList.length; i++) {
						if (playerList[j].getType() < 3) {
							this.targetY = playerList[j].getY();
							this.targetX = playerList[j].getX();
							this.targetName = playerList[j].getName();
						}
					}

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
		return Math.sqrt(Math.pow((player.getX() - this.x),2) + Math.pow((player.getY() - this.y),2));
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




	public void setPlayerRecord(playerRecord [] arr) {
		this.playerList = arr;

	}


	private void faceSouth() {
		while (this.isFacingSouth() == false) {
			this.turnLeft();
		}
	}
	/**
	 * General helper method, faces to the west. 
	 * If it is not facing west, then it will turn left until it does.
	 */
	private void faceWest() {
		while (this.isFacingWest() == false) {
			this.turnLeft();
		}
	}


	/**
	 * General helper method, faces to the East. 
	 * If it is not facing east, then it will turn left until it does.
	 */
	private void faceEast() {
		while (this.isFacingEast() == false) {
			this.turnLeft();
		}
	}


	/**
	 * General helper method, faces to the north.
	 * If it isn't facing north, then it will turn right until it does.
	 */
	private void faceNorth() {
		while (this.isFacingNorth() == false) {
			this.turnRight();
		}
	}

	@Override
	public void setRunnerRecord(Player[] arr) {
		// TODO Auto-generated method stub

	}








}
