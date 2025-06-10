package robotSim;

import java.awt.*;
import becker.robots.City;
import becker.robots.Direction;
import java.util.*;
import java.util.*;
public class Octopus extends Player {
	Random gen = new Random();
	private int x, y;
	private int maximumEnergyLevel, maxStepsPerMove, energyLevel;
	private double dodgingAbility;
	private String name;
	private boolean resting = false;
	private boolean chasing = false;
	private boolean tagging = false;
	private int targetX, targetY;
	private double targetDistance;
	private String targetName;



	public Octopus(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x, Direction direction) {
		super(name,energyLevel, maxStepsPerMove, dodgingAbility, city, y, x, direction);
		this.setColor(new Color(255, 165, 0));
		this.maximumEnergyLevel = energyLevel;
		this.energyLevel = this.maximumEnergyLevel;
		this.maxStepsPerMove = maxStepsPerMove;
		super.setX(x);
		super.setY(y);



	}

	public void move() {
		
		if (this.chasing) {
			int stepsNum = this.gen.nextInt(this.maxStepsPerMove-1)+1;
			
			//System.out.println("About to move " + stepsNum + "steps");
			for (int i = 0; i < stepsNum; i++) {
				if (this.frontIsClear()) {
					super.move();
					System.out.println("Distance to target: " + this.distanceCalc(this.targetX, this.targetY));
					if (this.distanceCalc(this.targetX, this.targetY) <= Math.sqrt(2)) {
						this.tagAttempt();
						System.out.println("Am tagging: " + this.tagging);
						System.out.println("resting");
						this.chasing = false;
						
					}
					
					
				}
			}
			if (stepsNum == maxStepsPerMove) {
				this.energyLevel -= 2;
			} else {
				this.energyLevel -= 1;
			}

		} else {
			if (this.frontIsClear()) {
				super.move();
			}
		}
		
	}



	public void takeTurn() {
		this.x =  getAvenue();
		this.y = getStreet();
		this.tagging = false;
		//System.out.println("Current X" + this.x + " current Y" +this.y);
		//System.out.println(this.chasing);
		if (!this.resting) {
			this.chase();
		}
			//System.out.println("Chasing");
		

	}

	private void chase() {
		this.lockOnTarget();
		
		System.out.format("My target is at X %d, Y %d and named %s\n", this.targetX, this.targetY, this.targetName);
		//System.out.println("Current energy: " + this.getEnergyLevel());
		this.advanceToTarget();
		//this.tagAttempt();
		this.rest();
	}

	public String getTargetName() {
		return this.targetName;
	}
	
	public boolean getChasing() {
		return this.chasing; 
	}
	public int getTargetX() {
		return this.targetX; 
	}
	public int getTargetY() {
		return this.targetY; 
	}
	private void tagAttempt() {
		this.tagging = true;
	}
	
	

	public boolean getTagging() {
		return this.tagging;
	}

	private void advanceToTarget() {
		if (this.targetX != this.x) {
			//System.out.println("I am not at the target's x");
			if (this.targetX < this.x) {
				//System.out.println("I am to the east of the target's x");
				this.faceWest();
				this.move();
			} else if (this.targetX > this.x) {
				//System.out.println("I am to the west of the target's x");
				this.faceEast();
				this.move();
			}

		} else if (this.targetY != this.y) {
			//System.out.println("I am not at the target's y");
			if (this.targetY < y) {
				this.faceNorth();
				this.move();
			} else {
				this.faceSouth();
				this.move();
			}
		}
	}


	private void lockOnTarget() {
		
			System.out.println("Locking on");
			this.sortByDistance(super.playerList);
			for (int i = 0; i < super.playerList.length; i++) {
				//System.out.println(super.playerList[i]);
			}
			this.chasing = true;
			// first looking for medic
			for (int i =0; i < playerList.length; i++) {
				if (super.playerList[i].getType() == 1) {
					System.out.println("Locking on to medic");
					this.targetY = super.playerList[i].getY();
					this.targetX = super.playerList[i].getX();
					this.targetName = super.playerList[i].getName();
					break;
				} else { // if no medic then find other players
					for (int j =0; i < super.playerList.length; i++) {
						System.out.println("Locking on to non medic");
						if (super.playerList[j].getType() != 3) {
							this.targetY = super.playerList[j].getY();
							this.targetX = super.playerList[j].getX();
							this.targetName = super.playerList[j].getName();
							System.out.println("Target type: " + super.playerList[i].getType());
						}
					}

				}
				System.out.println("Target type: " + super.playerList[i-1].getType());

			}
		
		
		if (this.chasing == true) {
			for (int i = 0; i < super.playerList.length; i++) {
				if (super.playerList[i].getName().equals(this.targetName)) {
					//System.out.println("Updating target");
					this.targetX = super.playerList[i].getX();
					this.targetY = super.playerList[i].getY();
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
	
	private double distanceCalc(int currentX, int currentY) {
		return Math.sqrt(Math.pow((currentX - this.x),2) + Math.pow((currentY - this.y),2));
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
		super.playerList = arr;

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

	








}
