package robotSim;
import java.awt.Color;
import becker.robots.*;
import java.util.*;


public class Runner extends Player {
	public Runner(String name, int energyLevel, int stepsPerMove, int dodgingAbility, playerRecord[] playerList, City city, int y, int x, Direction direction) {
		super(name, energyLevel, stepsPerMove, dodgingAbility, playerList, city, y, x, direction);
	}

	int energyLevel;
	int maximumEnergyLevel;
	int stepsPerMove;
	int dodgingAbility;
	String name;

	playerRecord [] playerList;
	
	public void move() {
		
	}
	
	public String getType() {
		return "Runner";
	};

}
