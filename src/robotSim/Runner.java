package robotSim;
import java.awt.Color;
import java.math.*;
import becker.robots.*;
import java.util.*;


public class Runner extends Player {
    private playerRecord[] playerList;
	private int stepsPerMove;
	private double[][] danger;
	private Player octopus;
	private Player[] runnerRecord;
	private double runnerAvoidance = 0.1;
	private double octopusAvoidance = 1;
    
	public Runner(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x, Direction direction, int stepsPerMove, Player octopus) {
		super(name, energyLevel, maxStepsPerMove, dodgingAbility, city, y, x, direction);
		this.stepsPerMove = stepsPerMove;
		this.octopus = octopus;
	}
	
	public void setRunnerRecord(Player[] arr) {
		this.runnerRecord = arr;
	}
		
	public void takeTurn() {
		danger =  dangerMap();
		optimalMove();
	}

	private void optimalMove() {
		int x = super.getX();
		int y = super.getY();
		int formulaLoops = 1 + 2*(stepsPerMove);
		Location [] tiles = new Location [formulaLoops];
		int xShift = 0;
		int yShift = stepsPerMove;
		for (int i = 0; i < stepsPerMove*2; i = i+2) {
			tiles[i] = predictTileDanger(x+xShift,y+yShift, 0, new Location (x+xShift, y+yShift));
			tiles[i+1] = predictTileDanger(x+xShift,y-yShift, 0, new Location (x+xShift, y-yShift));
			
			xShift = xShift + 1;
			yShift = yShift - 1;
		}
		
		Location location = predictTileDanger(x+stepsPerMove,y+stepsPerMove+1, 0, new Location (x+stepsPerMove, y+stepsPerMove+1));
		tiles[tiles.length] = location;
		
		tiles = mergeSort(tiles);
		int [][] path = tiles[0].getPath();
		
		movePath(path);
		//super.setX(x);
		//super.setY(y);
	}

	private void movePath(int[][] path) {
		for (int i = 0; i < path.length; i++) {
			int x = path[i][0];
			int y = path[i][1];
			moveTo(x,y);
		}
	}

	private void moveTo(int x, int y) {
		if (super.getX() > x) 
			pointWest();
		else if (super.getX() < x) 
			pointEast();
		else if (super.getY() > y) 
			pointSouth();
		else if (super.getY() < y) 
			pointNorth();
		
		move();
	}

	private Location[] mergeSort(Location[] tiles) {
		
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
		if (x < xTarget) {
			if (y < yTarget)
				totalXDanger = danger[(stepsPerMove+1) - (yTarget-y)][xTarget-x];
			else
				totalXDanger = danger[(stepsPerMove+1) + (y-yTarget)][xTarget-x];
		}
		
		if (y < yTarget) {
			totalYDanger = danger[(stepsPerMove+1) - (yTarget-y)][xTarget-x];
		}
		else if (y > yTarget) {
			totalYDanger = danger[(stepsPerMove+1) + (y-yTarget)][xTarget-x];
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

	private double[][] dangerMap() {
	    int gridSize = 2 * (stepsPerMove - 1) + 1;
	    double[][] dangerMap = new double[gridSize][gridSize];
	    
	    int centerX = super.getX();
	    int centerY = super.getY();
	    
	    for (int dx = -stepsPerMove + 1; dx <= stepsPerMove - 1; dx++) {
	        for (int dy = -stepsPerMove + 1; dy <= stepsPerMove - 1; dy++) {
	            int targetX = centerX + dx;
	            int targetY = centerY + dy;
	            double predicted = accessDangerXY(targetX, targetY);
	            dangerMap[dy + (stepsPerMove - 1)][dx + (stepsPerMove - 1)] = predicted;
	        }
	    }
	    
	    return dangerMap;
	}

	private double accessDangerXY(int targetX, int targetY) {
		double danger = 0;
		double distanceFromOctopus = accessDistance(targetX, targetY, octopus.getX(), octopus.getY());
		danger = danger + distanceFromOctopus * octopusAvoidance;
		
		double distanceFromRunners = 0;
		for(int i = 0; i < this.runnerRecord.length; i++) {
			distanceFromRunners = distanceFromRunners + accessDistance(targetX, targetY, runnerRecord[i].getX(), runnerRecord[i].getY());
		}
		distanceFromRunners = distanceFromRunners / this.runnerRecord.length;
		danger = danger + distanceFromRunners * runnerAvoidance;
		return danger;
	}
	
	private double accessDistance(int targetX, int targetY, int itemX, int itemY) {
		double xDiff = Math.abs(targetX - itemX);
		double yDiff = Math.abs(targetY - itemY);
		return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
	}

	public int getType() {
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

}
