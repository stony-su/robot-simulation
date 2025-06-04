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
	private double octopusAvoidance = 2;
	private boolean isAlgae;
	private boolean goingLeft = false;
	private final int CITY_LENGTH = 24;
	private final double TRAVEL_IMPORTANCE = 0.95;
    
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
		int x = super.getX();
		int y = super.getY();
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
		
		Location location = predictTileDanger(x+stepsPerMove,y+stepsPerMove+1, 0, new Location (x+stepsPerMove, y+stepsPerMove+1));
		tiles[tiles.length] = location;
		
		tiles = insertionSort(tiles);
		int [][] path = tiles[tiles.length].getPath();
		
		movePath(path);
		super.setX(getAvenue());
		super.setY(getStreet());
	}

	private void movePath(int[][] path) {
		for (int i = path.length; i > 0; i++) {
			int x = path[i][0];
			int y = path[i][1];
			moveTo(x,y);
		}
	}

	private void moveTo(int x, int y) {
		while (getAvenue() > x) {
			pointWest();
			move();
		}
		
		while (getAvenue() < x) {
			pointEast();
			move();
		}

		while(getStreet() > y) {
			pointSouth();
			move();
		}
		
		while (getStreet() < y) {
			pointNorth();
			move();
		}
	}

	private Location[] insertionSort(Location[] tiles) {
		
		for (int i = 1; i < tiles.length; ++i) {
	        Location key = tiles[i];
	        double keyValue = tiles[i].getDanger();
	        int j = i - 1;

	        while (j >= 0 && tiles[j].getDanger() > keyValue) {
	            tiles[j + 1] = tiles[j];
	            j = j - 1;
	        }
	        tiles[j + 1] = key;
	    }
		
		return tiles;
	}

	private Location predictTileDanger(int xTarget, int yTarget, int step, Location tile) {
		int x = super.getX();
		int y = super.getY();
		if (x == xTarget && y == yTarget) {
			tile.setDanger(0);
			return tile;
		}
		
		if (step == 0) {
			tile.setPath(new int [stepsPerMove][2]);
		}
		
		double totalXDanger = 0;
		double totalYDanger = 0;
		if (!goingLeft) {
			if (x < xTarget) {
				totalXDanger = accessDangerXY(xTarget+1, yTarget);
			}
		}
		else {
			if (x > xTarget) {
				totalXDanger = accessDangerXY(xTarget-1, yTarget);
			}
		}
		
		if (y < yTarget) {
			totalYDanger = 	accessDangerXY(xTarget, yTarget+1);

		}
		else if (y > yTarget) {
			totalYDanger = accessDangerXY(xTarget, yTarget-1);
		}
		
		
		if (totalXDanger > totalYDanger) {
			tile = predictTileDanger(xTarget-1, yTarget, step+1, tile);
			tile.setDanger(tile.getDanger() + totalXDanger);
		}
		else {
			if (y < yTarget) {
				tile = predictTileDanger(xTarget, yTarget-1, step+1, tile);
				tile.setDanger(tile.getDanger() + totalYDanger);
			}
			if (y > yTarget) {
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
		double danger = 0;
		double distanceFromOctopus = accessDistance(targetX, targetY, octopus.getX(), octopus.getY());
		danger = danger + distanceFromOctopus * octopusAvoidance;
		
		double distanceFromRunners = 0;
		for(int i = 0; i < this.runnerRecord.length; i++) {
			if (runnerRecord[i].getType() == 3) {
				distanceFromRunners = distanceFromRunners + 12 * accessDistance(targetX, targetY, runnerRecord[i].getX(), runnerRecord[i].getY());
			}
			else {
				distanceFromRunners = distanceFromRunners + accessDistance(targetX, targetY, runnerRecord[i].getX(), runnerRecord[i].getY());
			}
		}
		distanceFromRunners = distanceFromRunners / this.runnerRecord.length;
		danger = danger + distanceFromRunners * runnerAvoidance;
		
		if (!goingLeft) {
			danger = danger / Math.pow(TRAVEL_IMPORTANCE, (CITY_LENGTH - targetX));
		}
		else {
			danger = danger * Math.pow(TRAVEL_IMPORTANCE, (CITY_LENGTH - targetX));
		}
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

	public static void printState() {
		System.out.println("------------------------------------");
		System.out.println("-                                  -");
		System.out.println("-                                  -");
		System.out.println("-                                  -");
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
