package robotSim;
import java.awt.Color;
import becker.robots.*;
import java.util.*;


public class Runner extends Player {
    private playerRecord[] playerList;
	private int stepsPerMove;
    
	public Runner(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x, Direction direction, int stepsPerMove) {
		super(name, energyLevel, maxStepsPerMove, dodgingAbility, city, y, x, direction);
		this.stepsPerMove = stepsPerMove;
		
	}
	
	public void takeTurn() {
		
	}
	
	private void optimalMove() {
		int x = super.getX();
		int y = super.getY();
		int formulaLoops = 1 + 2*(stepsPerMove);
		Location [] tiles = new Location [formulaLoops];
		int xShift = 0;
		int yShift = stepsPerMove;
		for (int i = 0; i < stepsPerMove*2; i = i+2) {
			int d1 = predictTileDanger(x+xShift,y+yShift);
			tiles[i] = new Location (d1, x, y+yShift);
			
			int d2 = predictTileDanger(x+xShift,y-yShift);
			tiles[i+1] = new Location (d2, x, y-yShift);
			
			xShift = xShift + 1;
			yShift = yShift - 1;
		}
		int[][] blankPath = new int [stepsPerMove][2];
		int d3= predictTileDanger(x+stepsPerMove,stepsPerMove+1, 0, blankPath, 0);
		tiles[tiles.length] = new Location (d3, x+stepsPerMove,stepsPerMove+1);
		
		super.setX(x);
		super.setY(y);
	}

	private int[][] predictTileDanger(int xTarget, int yTarget, int totalDanger, int[][] optimalPath, int step) {
		int x = super.getX();
		int y = super.getY();
		int[][] danger = new int[2*stepsPerMove-1][stepsPerMove];
		danger = dangerMap();
		
		int totalXDanger = 0;
		int totalYDanger = 0;
		
		if (x < xTarget) {
			if (y < yTarget)
				totalXDanger = danger[(stepsPerMove+1) - (yTarget-y)][xTarget-x];
			else
				totalXDanger = danger[(stepsPerMove+1) + (y-yTarget)][xTarget-x];
		}
		if (y < yTarget) {
			totalYDanger = danger[(stepsPerMove+1) - (yTarget-y)][xTarget-x];
		}
		if (y > yTarget) {
			totalYDanger = danger[(stepsPerMove+1) + (y-yTarget)][xTarget-x];
		}
		
		if (x == xTarget && y == yTarget) {
			return 1;
		}
		
		if (totalXDanger > totalYDanger) {
			predictTileDanger(xTarget-1, yTarget, totalDanger + totalXDanger, optimalPath, step+1);
		}
		else {
			if (y < yTarget) {
				optimalPath = predictTileDanger(xTarget, yTarget-1, totalDanger + totalYDanger, optimalPath, step+1);
			}
			if (y > yTarget) {
				optimalPath = predictTileDanger(xTarget, yTarget+1, totalDanger + totalYDanger, optimalPath, step+1);
			}
		}
		//loop from back!
		optimalPath [step][0] = xTarget;
		optimalPath [step][1] = yTarget;

		return optimalPath;
	}

	private int[][] dangerMap() {
		int[][] danger = new int[2*stepsPerMove-1][stepsPerMove];
		
		return null;
	}

	public int getType() {
		return 2;
	}

}
