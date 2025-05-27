package robotSim;
import java.awt.Color;
import becker.robots.*;
import java.util.*;

public abstract class Player extends RobotSE{
	public Player(City city, int y, int x, Direction direction) {
		super(city, y, x, direction);
	}

	int energyLevel;
	int maximumEnergyLevel;
	int stepsPerMove;
	int dodgingAbility;
	String name;

	playerRecord [] playerList;
	
	abstract public void move();
	public void setColor(Color color) {
		setColor(color);
	}
	
	abstract public String getType();

}
