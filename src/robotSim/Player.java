package robotSim;
import java.awt.Color;
import java.math.*;
import becker.robots.*;
import java.util.*;


public class Runner extends Player {
	private int stepsPerMove;
	private Player octopus;
	private playerRecord[] runnerRecord;
	private double runnerAvoidance = 0.1;
	private double octopusAvoidance = 1;
	private boolean isAlgae;
	private boolean goingLeft = false;
	private final int CITY_LENGTH = 24;
	private final int CITY_WIDTH = 12; // change to constructor later
	private final double TRAVEL_IMPORTANCE = 0.99;
    
	public Runner(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x, Direction direction, int stepsPerMove, Player octopus) {
		super(name, energyLevel, maxStepsPerMove, dodgingAbility, city, y, x, direction);
		this.stepsPerMove = stepsPerMove;
		this.octopus = octopus;
		isAlgae = false;
	}
		
	public void switchModes(){
		if (isAlgae)
			isAlgae = false;
		else
			isAlgae = true;
	}
	
	public void takeTurn() {
		if (!isAlgae) {
			this.runnerRecord = super.getPlayerRecord();
			optimalMove();
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
		this.printState();
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
				tiles[i] = predictTileDanger(x-xShift,y+yShift, 0, new Location (x-xShift, y+yShift));
				tiles[i+1] = predictTileDanger(x-xShift,y-yShift, 0, new Location (x-xShift, y-yShift));

			}
			xShift = xShift + 1;
			yShift = yShift - 1;
		}
		
		Location location = predictTileDanger(x+stepsPerMove,y, 0, new Location (x+stepsPerMove, y));
		tiles[tiles.length-1] = location;
		
		tiles = insertionSort(tiles);
		printTiles(tiles);
		//int [][] path = tiles[tiles.length-1].getPath();
		int [][] path = tiles[0].getPath();
		
		movePath(path);
	}
	
	public void changeDirection() {
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
		System.out.println("path length:" + path.length);
		for (int i = path.length; i > 0; i--) {
			int x = path[i-1][0];
			int y = path[i-1][1];
			System.out.println("Moving to:" + x + ", " + y);
			moveTo(x,y);
		}
	}

	private void moveTo(int x, int y) {
		System.out.println("moveing to:" + x + ", " + y);
	    while (getAvenue() > x) {
	        pointWest();
	        move();
	    }

	    while (getAvenue() < x) {
	        pointEast();
	        move();
	    }

	    while (getStreet() > y) {
	        pointNorth();
	        move();
	    }

	    while (getStreet() < y) {
	        pointSouth();
	        move();
	    }
		System.out.println("exit");

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
		System.out.println("-                                  -");
		System.out.println("-                                  -");
		System.out.println("-   Steps Per Move: "+stepsPerMove+"              -");
		System.out.println("-                                  -");
		System.out.println("-                                  -");
		System.out.println("-                                  -");
		System.out.println("-                                  -");
		System.out.println("-                                  -");
		System.out.println("-                                  -");
		System.out.println("-                                  -");
		System.out.println("------------------------------------");

	}
}
