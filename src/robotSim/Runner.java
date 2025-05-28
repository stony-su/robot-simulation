package robotSim;
import java.awt.Color;
import becker.robots.*;
import java.util.*;


public class Runner extends Player {
    private playerRecord[] playerList;
	private int stepsPerMove;
	private Location [] tiles;
    
	public Runner(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x, Direction direction, int stepsPerMove) {
		super(name, energyLevel, maxStepsPerMove, dodgingAbility, city, y, x, direction);
		this.stepsPerMove = stepsPerMove;
		
	}
	
	public void takeTurn() {
		for (int i = 0; i < stepsPerMove; i++) {
			optimalMove();
		}
	}
	
	private void optimalMove() {
		
		
	}

	public int getType() {
		return 2;
	}

}
