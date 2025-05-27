package robotSim;

import java.awt.*;
import becker.robots.City;
import becker.robots.Direction;

public class Octopus extends Player {
	private int x, y;
	private int maximumEnergyLevel, stepsPerMove;
	private double dodgingAbility;
	private playerRecord[] playerList;
	private String name;
	public Octopus(String name, int energyLevel, int stepsPerMove, double dodgingAbility, playerRecord[] playerList, City city, int y, int x, Direction direction) {
		super(name,energyLevel, stepsPerMove, dodgingAbility, city, y, x, direction);
		setColor(new Color(255, 165, 0));
	}

	@Override
	public void move() {
		// TODO Auto-generated method stub

	}

	

	public int getType() {

		return 4;
	}

	@Override
	public void setColor(Color color) {
		setColor(color);
	}

	


}
