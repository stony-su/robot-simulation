package robotSim;
import java.awt.Color;

public abstract class Player {
	int energyLevel;
	int stepsPerMove;
	int dodgingAbility;
	String name;

	playerRecord [] playerList;
	
	abstract public void move();
	abstract public void setColor(Color color);
	abstract public String getType();

}
