package robotSimCopy;
import java.awt.Color;
import java.math.*;
import becker.robots.*;
import java.util.*;
/**
 * A Runner class to create runner players who run from the octopus and tries to get to the other side
 * @author Darren
 * @version June 10th, 2025
 */

public class Runner extends Player {
	//how many steps the runner can move per turn
	private int stepsPerMove;
	
	//octopus object to get XY location
	private Player octopus;
	
	//runner record to get XY location
	private playerRecord[] runnerRecord;
	
	//variables to determine robot's aversion to certain objects
	private final double RUNNER_AVOIDANCE = 0.0002;
	private final double OCTOPUS_AVOIDANCE = 0.009;
	private final double ALGAEAVOIDANCE = 0.02;
	private final double TRAVEL_IMPORTANCE = 0.0115;
	private final int MIN_STARTLE_MAX = 10;
	private final int MIN_STARTLE_MIN = 3;

	//algae flag
	private boolean isAlgae;
	
	//switching sides flag
	private boolean goingLeft = false;
	
	//static variables for City dimensions
	private static final int CITY_LENGTH = 24;
	private static final int CITY_WIDTH = 12;

	//energy level calculations for movement
	private int energyCap;
	private int energyHeld;
	private int energyRecovery;
	private int minStartle;

	/**
	 * Runner constructor that creates robot
	 * @param name name of robot
	 * @param energyLevel maximum energy level of robot
	 * @param maxStepsPerMove movement cap
	 * @param dodgingAbility dodging ability of robot
	 * @param city city where robot is created
	 * @param y y-coord
	 * @param x x-coord
	 * @param direction direction robot is init facing
	 * @param stepsPerMove init steps per move for robot
	 * @param octopus octopus object to track
	 */
	public Runner(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x, Direction direction, int stepsPerMove, Player octopus) {
		//init constructor 
		super(name, energyLevel, maxStepsPerMove, dodgingAbility, city, y, 	x, direction);
		
		//init instance variables 
		this.stepsPerMove = stepsPerMove;
		this.octopus = octopus;
		this.isAlgae = false;
		Random gen = new Random();
		this.minStartle = gen.nextInt(MIN_STARTLE_MAX-MIN_STARTLE_MIN)+MIN_STARTLE_MIN;
		this.energyRecovery = stepsPerMove;
		this.energyCap = energyLevel;
		this.energyHeld = energyLevel;
		
		//set player name onto screen
		super.setLabel(super.getName());
	}
		
	/**
	 * Tags the runner, becoming algae
	 */

	public void getTagged(){
		isAlgae = true;
		super.setColor(Color.GREEN);
	}

	/**
	 * revives the runner, switching back to normal
	 */
	public void revive() {
		//if is algae, switch back
		if (isAlgae) {
			isAlgae = false;
			super.setColor(Color.RED);
		}
	}
	
	/**
	 * the robot takes it's turn
	 */
	public void takeTurn() {
		//init xy coord
		super.setX(super.getAvenue());
		super.setY(super.getStreet());
		
		//if is not an algae
		if (!this.isAlgae) {
			//recover energy is missing energy
			if (this.energyHeld < this.energyCap)
				this.energyHeld = this.energyHeld + this.energyRecovery;
			
			//speed up if close to octopus
			if (octopus != null) {
				double distanceFromOctopus = this.accessDistance(getAvenue(), getStreet(), this.octopus.getX(), this.octopus.getY());
				if (distanceFromOctopus <this.minStartle && this.energyHeld > this.energyCap/3)
					this.stepsPerMove = super.getMaxStepsPerMove();
				else
					this.stepsPerMove = this.energyRecovery;
			}
			else {
				this.stepsPerMove = this.energyRecovery;
			}
			
			//get other runner's record
			this.runnerRecord = super.getPlayerRecord();
			
			//move through the optimal path
			this.optimalMove();
			
			//lose energy based on steps moved this turn
			this.energyHeld = this.energyHeld - this.stepsPerMove;
		}
		//if algae, spin around
		else {
			for (int i = 0; i < 4; i++) {
				super.turnLeft();
			}
		}
	}
	
	/**
	 * makes the runner do the optimal move forward
	 */
	private void optimalMove() {
		//init xy coord
		int x = super.getAvenue();
		int y = super.getStreet();
		
		//for debugging
		//this.printState();
		
		//set # of loops based on steps per move
		int formulaLoops = 1 + 2*(this.stepsPerMove);
		
		//set arr based on loop #
		Location [] tiles = new Location [formulaLoops];
		
		//init counter variables
		int xShift = 0;
		int yShift = this.stepsPerMove;
		
		//loop to find all tiles robot is capable of moving into
		for (int i = 0; i < this.stepsPerMove*2; i = i+2) {
			//if moveing left, find left robots, vice versa
			if (!this.goingLeft) {
				tiles[i] = this.predictTileDanger(x+xShift,y+yShift, 0, new Location (x+xShift, y+yShift));
				tiles[i+1] = this.predictTileDanger(x+xShift,y-yShift, 0, new Location (x+xShift, y-yShift));
			}
			else {
				tiles[i] = this.predictTileDanger(x-xShift,y+yShift, 0, new Location (x-xShift, y+yShift));
				tiles[i+1] = this.predictTileDanger(x-xShift,y-yShift, 0, new Location (x-xShift, y-yShift));
			}
			//increase counter variables
			xShift = xShift + 1;
			yShift = yShift - 1;
		}
		
		//edge case for going directly forward
		if (!this.goingLeft) {
			tiles[tiles.length-1] = this.predictTileDanger(x+this.stepsPerMove,y, 0, new Location (x+this.stepsPerMove, y));
		}
		else{
			tiles[tiles.length-1] = this.predictTileDanger(x-this.stepsPerMove,y, 0, new Location (x-this.stepsPerMove, y));
		}
		
		//sort tiles based on danger level 
		tiles = this.insertionSort(tiles);
		
		//debug method
		printTiles(tiles);
		
		//path is set to least dangerous path
		int [][] path = tiles[0].getPath();
		
		//move along path
		this.movePath(path);
	}
	
	/**
	 * do a 180 and move toward to begin new octopus round
	 */
	public void changeDirection() {
		if (this.getType() == 3)
			return;
		super.turnLeft();
		super.turnLeft();
		super.move();
		this.goingLeft = !this.goingLeft;
	}
	
	/**
	 * debug method to print out tile path, coords, & danger level
	 * @param tiles location of tiles
	 */
	private void printTiles(Location[] tiles) {
	    System.out.println("==== Tiles Info ====");
	    for (int i = 0; i < tiles.length; i++) {
	        if (tiles[i] == null) {
	            System.out.println("Tile " + i + ": null");
	            continue;
	        }

	        System.out.println("Tile " + i + ":");
	        System.out.println("  Coordinates: (" + tiles[i].getX() + ", " + tiles[i].getY() + ")");
	        System.out.println("  Danger Level: " + tiles[i].getDanger());

	        int[][] path = tiles[i].getPath();
	        System.out.print("  Path: ");
	        if (path != null) {
	            for (int j = 0; j < path.length; j++) {
	                if (path[j][0] == 0 && path[j][1] == 0 && j != 0) break; // assume 0,0 is default filler
	                System.out.print("-> (" + path[j][0] + ", " + path[j][1] + ") ");
	            }
	        } else {
	            System.out.print("null");
	        }
	        System.out.println("\n");
	    }
	    System.out.println("=====================");
	}

	/**
	 * method to make robot move along path represented in 2d arr
	 * @param path
	 */
	private void movePath(int[][] path) {
		//for step in path
		for (int i = path.length; i > 0; i--) {
			//set xy in path
			int x = path[i-1][0];
			int y = path[i-1][1];
			//move to xy
			this.moveTo(x,y);
		}
	}

	/**
	 * move to xy if able
	 * @param x target x coord
	 * @param y target y coord
	 */
	private void moveTo(int x, int y) {
	    while (super.getAvenue() > x && super.frontIsClear()) {
	        this.pointWest();
	        super.move();
	    }

	    while (super.getAvenue() < x && super.frontIsClear()) {
	        this.pointEast();
	        super.move();
	    }

	    while (this.getStreet() > y && super.frontIsClear()) {
	        this.pointNorth();
	        super.move();
	    }

	    while (super.getStreet() < y && super.frontIsClear()) {
	        this.pointSouth();
	        super.move();
	    }
	}
	
	/**
	 * insertion sort method that sorts tiles based on danger value
	 * @param tiles arr of reachable locations 
	 * @return sorted tiles arr in ascending order
	 */
	private Location[] insertionSort(Location[] tiles) {
		// for each item in tiles, starting at second item
	    for (int i = 1; i < tiles.length; ++i) {
	    	
	    	//current item to sort
	        Location key = tiles[i];
	        
	        //counter variable
	        int j = i - 1;

	        //while item's danger is less than current item's danger
	        while (j >= 0 && tiles[j].getDanger() > key.getDanger()) {
	        	
	        	//move item up by one
	            tiles[j + 1] = tiles[j];
	            j = j - 1;
	        }
	        
	        //slot current item into place
	        tiles[j + 1] = key;
	    }
	    
	    //return sorted arr
	    return tiles;
	}
	
	/**
	 * checks if robot is on left wall
	 * @return true/false
	 */
	public boolean onLeftWall() {
		int x = super.getAvenue();
		if (x == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * checks if robot is on right wall
	 * @return true/false
	 */
	public boolean onRightWall() {
		int x = super.getAvenue();
		if (x == 23) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * predicts tile danger level based on every single tile in it's optimal path (recursive function) - more detail in write up
	 * @param xTarget tile x-coord
	 * @param yTarget tile y-coord
	 * @param step recursive init counter
	 * @param tile tile object
	 * @return tile with danger level and optimal path
	 */
	private Location predictTileDanger(int xTarget, int yTarget, int step, Location tile) {
		//init xy coord
		int x = super.getAvenue();
		int y = super.getStreet();
		
		//if reached robot, recurse back
		if (x == xTarget && y == yTarget) {
			tile.setDanger(0);
			return tile;
		}
		
		//if first call, init arr based on steps per move
		if (step == 0) {
			tile.setPath(new int [this.stepsPerMove][2]);
		}
		
		//init danger variables
		double totalXDanger = 0;
		double totalYDanger = 0;
		
		//determines danger level of tiles around target tile
		if (!this.goingLeft) {
			totalXDanger = accessDangerXY(xTarget+1, yTarget);
		}
		else {
			totalXDanger = accessDangerXY(xTarget-1, yTarget);
		}
		
		if (y < yTarget) {
			totalYDanger = 	accessDangerXY(xTarget, yTarget+1);

		}
		else {
			totalYDanger = accessDangerXY(xTarget, yTarget-1);
		}
		
		//picks the least dangerous tile and recurses back until robot init location is hit
		if ((x != xTarget && totalXDanger < totalYDanger) || y == yTarget) {
			
			//recurse back in direction going if x movement danger value is less
			if (!this.goingLeft) 
		        tile = this.predictTileDanger(xTarget - 1, yTarget, step+1, tile);
			else 
		        tile = this.predictTileDanger(xTarget + 1, yTarget, step+1, tile);
		    
			//adds danger level to tile
		    tile.setDanger(tile.getDanger() + totalXDanger);
			
		}
		//recurse back in direction going if y movement danger value is less
		else if (y != yTarget){
			
			//determines to go up or down
			if (y <= yTarget) 
				tile = this.predictTileDanger(xTarget, yTarget-1, step+1, tile);
			else
				tile = this.predictTileDanger(xTarget, yTarget+1, step+1, tile);
			
			//adds danger level to tile
			tile.setDanger(tile.getDanger() + totalYDanger);

		}

		//loop from back!
		//optimal path to move
		int [][] optimalPath = tile.getPath();
		optimalPath [step][0] = xTarget;
		optimalPath [step][1] = yTarget;
		
		//sets tile XY to optimal path
		tile.setPath(optimalPath);

		//return tile
		return tile;
	}
	
	/**
	 * access danger level of XY location
	 * @param targetX x coord to check
	 * @param targetY y coord to check
	 * @return danger level
	 */
	private double accessDangerXY(int targetX, int targetY) {
		//init danger is 1
		double danger = 1;
		
		//if target is out of bounds, return huge value
		if (targetX < -1 || targetX > this.CITY_LENGTH || targetY > this.CITY_WIDTH-1 || targetY < 0)
			return 1000000;
		
		//tile becomes more dangerous if in close proximity to other runners
		double distanceFromRunners = 0;
		
		//loop through runner record
		for(int i = 0; i < this.runnerRecord.length; i++) {
			//if runner is an algae, avoid it more
			distanceFromRunners = accessDistance(targetX, targetY, this.runnerRecord[i].getX(), this.runnerRecord[i].getY());
			if (this.runnerRecord[i].getType() == 3) {
				if (runnerRecord[i].getX() == targetX && runnerRecord[i].getY() == targetY) {
					return 100000;
				}
			}
			//if regular runner, avoid it less
			else {
				danger = danger * (1 - this.RUNNER_AVOIDANCE * distanceFromRunners);
			}
		}
		
		//incentivize runner to move to target wall
		if (!this.goingLeft) {
			danger = danger * (1 - this.TRAVEL_IMPORTANCE * (targetX));
		}
		else {
			danger = danger * (1 - this.TRAVEL_IMPORTANCE * (this.CITY_LENGTH - targetX));
		}
		
		//avoid the octopus
		if (octopus != null) {
			double distanceFromOctopus = accessDistance(targetX, targetY, this.octopus.getX(), this.octopus.getY());
			danger = danger * (1 - distanceFromOctopus*this.OCTOPUS_AVOIDANCE) ;
		}
		
		//return danger value
		return danger;
	}
	
	/**
	 * distance formula
	 * @param targetX x1
	 * @param targetY y1
	 * @param itemX x2
	 * @param itemY y2
	 * @return distance from (x1,y1) to (x2, y2)
	 */
	private double accessDistance(int targetX, int targetY, int itemX, int itemY) {
		double xDiff = Math.abs(targetX - itemX);
		double yDiff = Math.abs(targetY - itemY);
		return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
	}

	/** 
	 * get type of runner
	 */
	public int getType() {
		if (this.isAlgae)
			return 3;
		else
			return 2;
	}
	
	/**
	 * points the robot in the North direction
	 * @post robot is facing north
	 */
	private void pointNorth() {
		//if facing east/south/west, turn as needed
		if(this.isFacingEast()) {
			super.turnLeft();
		}
		else if(this.isFacingSouth()) {
			for (int i = 0; i < 2; i++)
				super.turnLeft();
		}
		else if(this.isFacingWest()) {
			super.turnRight();
		}
	}
	
	/**
	 * points the robot in the south direction
	 * @post robot is facing south
	 */
	private void pointSouth() {
		//if facing north/east/west, turn as needed
		if(this.isFacingEast()) {
			super.turnRight();
		}
		else if(this.isFacingNorth()) {
			for (int i = 0; i < 2; i++)
				super.turnLeft();
		}
		else if(this.isFacingWest()) {
			super.turnLeft();
		}
	}
	
	/**
	 * points the robot in the west direction
	 * @post robot is facing west
	 */
	private void pointWest() {
		//if facing north/east/south, turn as needed
		if(this.isFacingSouth()) {
			super.turnRight();
		}
		else if(this.isFacingEast()) {
			for (int i = 0; i < 2; i++)
				super.turnLeft();
		}
		else if(this.isFacingNorth()) {
			super.turnLeft();
		}
	}
	
	/**
	 * points the robot in the east direction
	 * @post robot is facing east
	 */
	private void pointEast() {
		//if facing north/south/west, turn as needed
		if(this.isFacingSouth()) {
			super.turnLeft();
		}
		else if(this.isFacingWest()) {
			for (int i = 0; i < 2; i++)
				super.turnLeft();
		}
		else if(this.isFacingNorth()) {
			super.turnRight();
		}
	}
	
	/**
	 * debug method
	 */
	public void printState() {
		System.out.println("------------------------------------");
		System.out.println("- Runner State                     -");
		System.out.println("- Steps Per Move: " + stepsPerMove + "            -");
		System.out.println("- Current Pos: (" + getAvenue() + "," + getStreet() + ")        -");
		System.out.println("- Going Left: " + goingLeft + "                  -");
		System.out.println("- Algae Mode: " + isAlgae + "                  -");
		System.out.println("------------------------------------");
	}

}
