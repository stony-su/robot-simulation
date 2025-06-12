package robotSim;
import java.awt.Color;
import java.math.*;
import becker.robots.*;
import java.util.*;


public class Runner extends Player {
	private int stepsPerMove;
	private Player octopus;
	private playerRecord[] runnerRecord;
	private double runnerAvoidance = 0.0002;
	private double octopusAvoidance = 0.009;
	private double algaeAvoidance = 0.0075;
	private boolean isAlgae;
	private boolean goingLeft = false;
	private final int CITY_LENGTH = 24;
	private final double TRAVEL_IMPORTANCE = 0.01;
	private int energyCap;
	private int energyHeld;
	private int energyRecovery;
	private int minStartle;
	private static final int MIN_STARTLE_MAX = 10;
	private static final int MIN_STARTLE_MIN = 3;

	public Runner(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x, Direction direction, int stepsPerMove, Player octopus) {
		super(name, energyLevel, maxStepsPerMove, dodgingAbility, city, 6, x, direction);
		this.stepsPerMove = stepsPerMove;
		this.octopus = octopus;
		isAlgae = false;
		Random gen = new Random();
		this.minStartle = gen.nextInt(MIN_STARTLE_MAX-MIN_STARTLE_MIN)+MIN_STARTLE_MIN;
		this.energyRecovery = stepsPerMove;
		this.energyCap = energyLevel;
		this.energyHeld = energyLevel;
		super.setLabel(super.getName());
	}
		
	public void switchModes(){
		if (isAlgae) {
			isAlgae = false;
			super.setColor(Color.RED);
		}
		else {
			isAlgae = true;
			super.setColor(Color.GREEN);
		}
	}
	
	public void takeTurn() {
		super.setX(getAvenue());
		super.setY(getStreet());
		
		if (!isAlgae) {
			if (energyHeld < energyCap)
				energyHeld = energyHeld + energyRecovery;
			
			double distanceFromOctopus = accessDistance(getAvenue(), getStreet(), octopus.getX(), octopus.getY());
			if (distanceFromOctopus < minStartle && energyHeld > energyCap/3)
				stepsPerMove = super.getMaxStepsPerMove();
			else
				stepsPerMove = energyRecovery;
			
			this.runnerRecord = super.getPlayerRecord();
			optimalMove();
			energyHeld = energyHeld - stepsPerMove;
		}
		else {
			for (int i = 0; i < 4; i++) {
				this.turnLeft();
			}
		}
	}

	private void optimalMove() {
		int x = getAvenue();
		int y = getStreet();
		//this.printState();
		int formulaLoops = 1 + 2*(stepsPerMove);
		Location [] tiles = new Location [formulaLoops];
		int xShift = 0;
		int yShift = stepsPerMove;
		
		for (int i = 0; i < stepsPerMove*2; i = i+2) {
			if (!goingLeft) {
				tiles[i] = predictTileDanger(x+xShift,y+yShift, 0, new Location (x+xShift, y+yShift));
				tiles[i+1] = predictTileDanger(x+xShift,y-yShift, 0, new Location (x+xShift, y-yShift));
			}
			else {
				//System.out.println("going left");
				tiles[i] = predictTileDanger(x-xShift,y+yShift, 0, new Location (x-xShift, y+yShift));
				tiles[i+1] = predictTileDanger(x-xShift,y-yShift, 0, new Location (x-xShift, y-yShift));
			}
			xShift = xShift + 1;
			yShift = yShift - 1;
		}
		if (!goingLeft) {
			tiles[tiles.length-1] = predictTileDanger(x+stepsPerMove,y, 0, new Location (x+stepsPerMove, y));
		}
		else{
			tiles[tiles.length-1] = predictTileDanger(x-stepsPerMove,y, 0, new Location (x-stepsPerMove, y));
		}
		
		tiles = insertionSort(tiles);
		//printTiles(tiles);
		//int [][] path = tiles[tiles.length-1].getPath();
		int [][] path = tiles[0].getPath();
		
		movePath(path);
	}
	
	public void changeDirection() {
		if (this.getType() == 3)
			return;
		
		turnLeft();
		turnLeft();
		move();
		goingLeft = !goingLeft;
	}
	
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


	private void movePath(int[][] path) {
		//System.out.println("path length:" + path.length);
		for (int i = path.length; i > 0; i--) {
			int x = path[i-1][0];
			int y = path[i-1][1];
			//System.out.println("Moving to:" + x + ", " + y);
			moveTo(x,y);
		}
	}

	private void moveTo(int x, int y) {
	    while (getAvenue() > x && frontIsClear()) {
	        pointWest();
	        move();
	    }

	    while (getAvenue() < x && frontIsClear()) {
	        pointEast();
	        move();
	    }

	    while (getStreet() > y && frontIsClear()) {
	        pointNorth();
	        move();
	    }

	    while (getStreet() < y && frontIsClear()) {
	        pointSouth();
	        move();
	    }
	}

	private Location[] insertionSort(Location[] tiles) {
	    for (int i = 1; i < tiles.length; ++i) {
	        Location key = tiles[i];
	        int j = i - 1;

	        while (j >= 0 && tiles[j].getDanger() > key.getDanger()) {
	            tiles[j + 1] = tiles[j];
	            j = j - 1;
	        }
	        tiles[j + 1] = key;
	    }
	    return tiles;
	}
	
	public boolean onLeftWall() {
		int x = getAvenue();
		if (x == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean onRightWall() {
		int x = getAvenue();
		if (x == 23) {
			return true;
		}
		else {
			return false;
		}
	}


	private Location predictTileDanger(int xTarget, int yTarget, int step, Location tile) {
		int x = getAvenue();
		int y = getStreet();
		//System.out.println("you are at:" + x + ", " + y);
		//System.out.println("target is: " + xTarget + ", " + yTarget);
		
		if (x == xTarget && y == yTarget) {
			//System.out.println("recursed back");
			//System.out.println("==============================");
			tile.setDanger(0);
			return tile;
		}
		
		if (step == 0) {
			tile.setPath(new int [stepsPerMove][2]);
			//tile.setPath(new int [20][2]);
		}
		
		double totalXDanger = 0;
		double totalYDanger = 0;
		if (!goingLeft) {
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
		
		if (x != xTarget) {
			
				if (!goingLeft) {
					//System.out.println("Moved back one");
			        tile = predictTileDanger(xTarget - 1, yTarget, step+1, tile);
			    } 
				else {
					//System.out.println("Moved foward one");
			        tile = predictTileDanger(xTarget + 1, yTarget, step+1, tile);
			    }
				
			    tile.setDanger(tile.getDanger() + totalXDanger);
			
		}
		else if (y != yTarget){
			if (y <= yTarget) {
				//System.out.println("Moved Down One");
				tile = predictTileDanger(xTarget, yTarget-1, step+1, tile);
				tile.setDanger(tile.getDanger() + totalYDanger);
			}
			else{
				//System.out.println("Moved Up One");
				tile = predictTileDanger(xTarget, yTarget+1, step+1, tile);
				tile.setDanger(tile.getDanger() + totalYDanger);
			}
		}

		//loop from back!
		int [][] optimalPath = tile.getPath();
		optimalPath [step][0] = xTarget;
		optimalPath [step][1] = yTarget;
		tile.setPath(optimalPath);

		return tile;
	}

	private double accessDangerXY(int targetX, int targetY) {
		double danger = 1;
		
		if (targetX < -1 || targetX > 24 || targetY > 11 || targetY < 0)
			return 1000000;
		
		double distanceFromRunners = 0;
		for(int i = 0; i < this.runnerRecord.length-1; i++) {
			distanceFromRunners = accessDistance(targetX, targetY, runnerRecord[i].getX(), runnerRecord[i].getY());
			if (runnerRecord[i].getType() == 3) {
				danger = danger * (1 - algaeAvoidance * distanceFromRunners);
			}
			else {
				danger = danger * (1 - runnerAvoidance * distanceFromRunners);
			}
		}
		
		if (!goingLeft) {
			danger = danger * (1 - TRAVEL_IMPORTANCE * (targetX));
		}
		else {
			danger = danger * (1 - TRAVEL_IMPORTANCE * (CITY_LENGTH - targetX));
		}
		
		double distanceFromOctopus = accessDistance(targetX, targetY, octopus.getX(), octopus.getY());
		danger = danger * (1 - distanceFromOctopus*octopusAvoidance) ;
		//System.out.println("I am " + distanceFromOctopus + "m away from the octopus");
		//System.out.println("Total danger at (" + targetX + "," + targetY + ") = " + danger);
		return danger;
	}
	
	private double accessDistance(int targetX, int targetY, int itemX, int itemY) {
		double xDiff = Math.abs(targetX - itemX);
		double yDiff = Math.abs(targetY - itemY);
		return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
	}

	public int getType() {
		if (isAlgae)
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
		if(isFacingEast()) {
			turnLeft();
		}
		else if(isFacingSouth()) {
			for (int i = 0; i < 2; i++)
				turnLeft();
		}
		else if(isFacingWest()) {
			turnRight();
		}
	}
	
	/**
	 * points the robot in the south direction
	 * @post robot is facing south
	 */
	private void pointSouth() {
		//if facing north/east/west, turn as needed
		if(isFacingEast()) {
			turnRight();
		}
		else if(isFacingNorth()) {
			for (int i = 0; i < 2; i++)
				turnLeft();
		}
		else if(isFacingWest()) {
			turnLeft();
		}
	}
	
	/**
	 * points the robot in the west direction
	 * @post robot is facing west
	 */
	private void pointWest() {
		//if facing north/east/south, turn as needed
		if(isFacingSouth()) {
			turnRight();
		}
		else if(isFacingEast()) {
			for (int i = 0; i < 2; i++)
				turnLeft();
		}
		else if(isFacingNorth()) {
			turnLeft();
		}
	}
	
	/**
	 * points the robot in the east direction
	 * @post robot is facing east
	 */
	private void pointEast() {
		//if facing north/south/west, turn as needed
		if(isFacingSouth()) {
			turnLeft();
		}
		else if(isFacingWest()) {
			for (int i = 0; i < 2; i++)
				turnLeft();
		}
		else if(isFacingNorth()) {
			turnRight();
		}
	}
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
