package robotSim;

public abstract class Player {
	int energyLevel;
	int stepsPerMove;
	int dodgingAbility;
	String name;

	playerRecord [] playerList;
	
	abstract public void move();

}
