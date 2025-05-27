package robotSim;
import java.awt.Color;
import becker.robots.*;
import java.util.*;


public class Runner extends Player {
    private playerRecord[] playerList;
	int energyLevel;
	int maximumEnergyLevel;
	int stepsPerMove;
	int dodgingAbility;
	String name;

	public Runner(String name, int energyLevel, int stepsPerMove, int dodgingAbility, City city, int y, int x, Direction direction) {
		super(name, energyLevel, stepsPerMove, dodgingAbility, city, y, x, direction);
	}
	
	public void move() {
		
	}
	
	public int getType() {
		return 2;
	}

}
