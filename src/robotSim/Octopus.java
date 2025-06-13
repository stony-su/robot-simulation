
package robotSim;

import java.awt.*;
import becker.robots.City;
import becker.robots.Direction;
import java.util.*;


/**
 * The octopus is the main antagonist of the game of octopus. 
 * @author Maymun Rahman
 *
 */
public class Octopus extends Player {
	// declaration of variables
	Random gen = new Random();
	private int x, y;
	private int maximumEnergyLevel, maxStepsPerMove, energyLevel;
	private String name;
	private boolean resting = false;
	private boolean chasing = false;
	private boolean tagging = false;
	private int targetX, targetY;
	private String targetName;
	private static int WALL1X = 1, WALL2X = 22;
	private boolean everyoneOnWall;
	private int stepsThisTurn= 0;

	/**
	 * Constructor for the octopus
	 * @param name - name of the octopus
	 * @param energyLevel - starting energy level
	 * @param maxStepsPerMove - maximum steps per turn
	 * @param city - the City in which the game is being played
	 * @param y - starting y position
	 * @param x - starting x position
	 * @param direction - direction that it starts in
	 */
	public Octopus(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x, Direction direction) {
		super(name,energyLevel, maxStepsPerMove, dodgingAbility, city, y, x, direction);
		// setting color to orange
		this.setColor(new Color(255, 165, 0));
		// setting energy level
		this.maximumEnergyLevel = energyLevel;
		this.energyLevel = this.maximumEnergyLevel;
		this.maxStepsPerMove = maxStepsPerMove;
		super.setX(x);
		super.setY(y);
		// wall positions, the octopus cannot go beyond them
		
	   


	}
	
	/**
	 * This method gets from the application class if every runner is on the wall
	 * @param isOnWall
	 */
	public void updateIsOnWall(boolean isOnWall) {
		this.everyoneOnWall = isOnWall;
	}
	/**
	 * Overridden move method. It handles the energy consumption and makes sure that the front is clear before moving
	 * It also makes sure that it's not going past the player safe zone 
	 */
	public void move() {

		if (this.chasing) {
			//int stepsNum = this.gen.nextInt(this.maxStepsPerMove-1)+1;
			int stepsNum = 1;
			//System.out.println("About to move " + stepsNum + "steps");
			for (int i = 0; i < stepsNum; i++) {
				// if clear and not moving into safe zone
				if (this.frontIsClear() && (this.x) != WALL1X && this.x != WALL2X && this.everyoneOnWall == false && this.stepsThisTurn+stepsNum < this.maxStepsPerMove) {
					super.move();
					this.stepsThisTurn +=1;
					this.x = getAvenue();
					this.y = getStreet();
					/*
					 * if you are on another player who isn't your current target
					 */
					for (int j = 0; j < super.playerList.length; j++) {
						if (super.playerList[i].getX()== this.x && this.y == super.playerList[i].getY() && super.playerList[i].getType() != 3) {
							this.targetName = super.playerList[i].getName();
							this.tagAttempt();
							this.chasing = false;
							this.lockOnTarget();
							break;
						}
						//System.out.println("Distance to target: " + this.distanceCalc(this.targetX, this.targetY));
					}
					
					/*
					 * if you are on your current target, then tag
					 */
					if (this.targetX == this.x && this.targetY == this.y) {
						this.tagAttempt();
						for (int j = 0; j < super.playerList.length; j++) {
							if (super.playerList[j].getName().equals(this.targetName)) {
								/*
								 * Checking if your target got algaed or not, and updating their dodge skill
								 */
								if (super.playerList[j].getType() == 3) {
									super.playerList[j].updateDodge(super.playerList[j].getDodge() -1);
								} else {
									super.playerList[j].updateDodge(super.playerList[j].getDodge() +1);
								}
								// making their catchIndex very high because it's an algae
								super.playerList[i].updateCatchIndex(3);
								break;
							}
							// lock on to a new target
							this.lockOnTarget();
							//System.out.println("Am tagging: " + this.tagging);

							this.chasing = false;
							

						}


					}
					
				}
				
				
				/*
				 * If you try entering the safe zone, turn around
				 */
				if (this.x == WALL1X) {
					this.faceEast();
					super.move();
				}
				
				if (this.x == WALL2X) {
					this.faceWest();
					super.move();
				}
			}
			/*
			 * If out of energy, spin
			 */
		} else if (this.stepsThisTurn == this.maxStepsPerMove) {
			this.turnAround();
			this.turnAround();
		} else {
			if (this.frontIsClear()) {
				super.move();
			}
			
		}

	}

	
	/**
	 * This is the method running in the application class that does most of the work.
	 * It handles resting based on energy level, locking on to a target and tagging
	 */
	public void takeTurn() {
		if (this.energyLevel > 0) {
			this.stepsThisTurn = 0;
			this.x = getAvenue();
			this.y = getStreet();
	
			this.tagging = false;
			//System.out.println("Current X" + this.x + " current Y" +this.y);
			//System.out.println(this.chasing);
			if (!this.resting) {
				this.chase();
			}
			
			/*
			 * Checking that you are on someone who's your target.
			 */
			for (int i =0; i< super.playerList.length; i++) {
				if (this.targetName.equals(super.playerList[i].getName()) && super.playerList[i].getType() == 3) {
					super.playerList[i].updateCatchIndex(3);
					this.chasing = false;
					this.lockOnTarget();
				}
			}
			this.x = getAvenue();
			this.y = getStreet();
			/*
			 * Checking that you are on someone who's your target.
			 */
			for (int i =0; i< super.playerList.length; i++) {
				if (this.targetName.equals(super.playerList[i].getName()) && super.playerList[i].getType() == 3) {
					super.playerList[i].updateCatchIndex(3);
					this.chasing = false;
					this.lockOnTarget();
				}
			}
			//System.out.println("Chasing");
			
			/*
			 * If you've moved at your maximum steps per move, then deduct 2 energy
			 * If you've moved below maximum steps, deduct 1 energy
			 */
			if (this.stepsThisTurn == this.maxStepsPerMove) {
				this.energyLevel -= 2;
			} else {
				this.energyLevel -=1;
			}
		/*
		 * If your energy level is below or equal to 0 (this can happen because if your energy is 1 and you move at maximum steps then you
		 * "go into negative" this is an intended feature (also realistic because sometimes you can overexert yourself)	
		 */
		} else if (this.energyLevel <= 0) {
			this.rest();
		}
	}
	
	private void chase() {
		// locking on to a target
		this.lockOnTarget();

		//System.out.format("My target is at X %d, Y %d and named %s\n", this.targetX, this.targetY, this.targetName);
		for (int i = 0; i < super.playerList.length; i++) {
			/*
			 * If your target has been algaefied, update their catch index to be very high
			 */
			if (super.playerList[i].getName().equals(this.targetName)) {
				//System.out.println("Target type" + super.playerList[i].getType());
				if (this.targetName.equals(super.playerList[i].getName()) && super.playerList[i].getType() == 3) {
					super.playerList[i].updateCatchIndex(3);
					this.chasing = false;
					this.lockOnTarget();
				}
			}


		}
		//System.out.format("My is at X %d, Y %d \n", this.x, this.y);
		//System.out.println("Current energy: " + this.getEnergyLevel());
		
		// get to your target
		this.advanceToTarget();
	}
	/**
	 * Accessor method for your target's name
	 * @return - your current target's name
	 */
	public String getTargetName() {
		return this.targetName;
	}
	
	/**
	 * returns if you're chasing someone
	 * @return - currently chasing or not
	 */
	public boolean getChasing() {
		return this.chasing; 
	}
	
	/**
	 * Accessor method for your target's x
	 * @return - target's current x
	 */
	public int getTargetX() {
		return this.targetX; 
	}
	
	/**
	 * Accessor method for your target's y
	 * @return - target's current y
	 */
	public int getTargetY() {
		return this.targetY; 
	}
	
	/**
	 * Modifier method to set tagging to true
	 */
	private void tagAttempt() {
		this.tagging = true;
	}


	/**
	 * Checks if your tagging or not
	 * @return
	 */
	public boolean getTagging() {
		return this.tagging;
	}
	
	/**
	 * This method gets the octopus to the target's x and y
	 */
	private void advanceToTarget() {
		/*
		 * If your target is above/below you then move to your target's y location first
		 * I programmed it like this because I noticed that runner's usually run in a straight line instead of ducking up and down.
		 */
		if (this.targetY != this.y) {
			//System.out.println("I am not at the target's x");
			
			/*
			 * If below your target
			 */
			if (this.targetY < this.y) {
				//System.out.println("I am to the south of the target's y");
				this.faceNorth();
				this.x = getAvenue();
				this.y = getStreet();
				for (int i =0; i < (this.y - this.targetY)*2; i++) {
					if (this.targetY == this.y) {
						break;
					}
					System.out.println(this.y - this.targetY);
					this.move();
				}
				
				/*
				 * If above your target 
				 */
			} else if (this.targetY > this.y) {
				//System.out.println("I am to the north of the target's y");
				this.faceSouth();
				this.x = getAvenue();
				this.y = getStreet();
				for (int i =0; i < (this.targetY - this.y)*2; i++) {
					if (this.targetY == this.y) {
						break;
					}
					System.out.println(this.targetY - this.y);
					this.move();
				}
			}
			
			/*
			 * If not at target x
			 */
		} else if (this.targetX != this.x) {
			//System.out.println("I am not at the target's x");
			
			/*
			 * If to the east of target
			 */
			if (this.targetX < x) {
				this.faceWest();
				this.x = getAvenue();
				this.y = getStreet();
				for (int i =0; i < (this.x - this.targetX); i++) {
					if (this.targetX == this.x) {
						break;
					}
					System.out.println(this.x - this.targetX);
					this.move();
				}
				/*
				 * If to the west of target
				 */
			} else {
				this.faceEast();

				for (int i =0; i < (this.targetX - this.x); i++) {
					if (this.targetX == this.x) {
						break;
					}
					System.out.println(this.targetX- this.x);
					this.move();
				}

			}
		}



	}
	
	/**
	 * This is probably the most important part of the program: the targeting AI. 
	 * It functions using an insertion sort and the catchIndex of the playerRecord to determine which player
	 * should be targeted first. Players with a lower  
	 */
	private void lockOnTarget() {
		
		System.out.println("Locking on");
		if (chasing == false) {
			for (int i = 0; i < super.playerList.length; i++) {
				super.playerList[i].updateCatchIndex(distanceCalc(super.playerList[i]));
			}
			this.sortByCatchability(super.playerList);
			for (int i = 0; i < super.playerList.length; i++) {
				System.out.println("Target name: " + super.playerList[i].getName() + " Target catchability: " + super.playerList[i].getCatchIndex());
			}
			this.chasing = true;
			// first looking for medic
			
				if (super.playerList[0].getType() != 3) {
					
					this.targetName = super.playerList[0].getName();
					this.targetX = super.playerList[0].getX();
					this.targetY = super.playerList[0].getY();
					System.out.println("Distance to target: " + this.distanceCalc(this.targetX, this.targetY));
					System.out.println(targetName);

				}
			
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



	private void sortByCatchability(playerRecord [] numbersArray) {
		final int ARRAYLENGTH = numbersArray.length;
		for (int i = 0; i < ARRAYLENGTH; i++) {
			for (int j = i; j > 0; j--) {
				if ((numbersArray[j]).getCatchIndex() < (numbersArray[j-1]).getCatchIndex()) {
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
		this.turnLeft();
		this.turnLeft();
		this.turnRight();
		this.turnRight();
		this.energyLevel = this.maximumEnergyLevel;
		

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
