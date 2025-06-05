package robotSim;

import becker.robots.*;
import java.util.*;

public class Medic extends Player {
	private Player[] playerRecord;
	private Player octopus;
	private final int healRange = 2;
	private final int healAmount = 10;
	private final double octopusAvoidance = 1;
	private final int stepsPerMove;

	public Medic(String name, int energyLevel, int stepsPerMove, double dodgingAbility, City city, int y, int x,
	             Direction direction, Player octopus) {
		super(name, energyLevel, stepsPerMove, dodgingAbility, city, y, x, direction);
		this.stepsPerMove = stepsPerMove;
		this.octopus = octopus;
	}

	public void setPlayerRecord(Player[] arr) {
		this.playerRecord = arr;
	}


	@Override
	public int getType() {
		return 3;
	}

	@Override
	public void takeTurn() {
		handleNearbyPlayers();
		double[][] threatMap = buildDangerMap();
		moveSafelyTowardGoal(threatMap);
	}

	private void handleNearbyPlayers() {
		for (Player p : playerRecord) {
			if (p == this) continue;
			if (calculateDistance(this.getX(), this.getY(), p.getX(), p.getY()) <= healRange) {
				if (p.getEnergyLevel() <= 0) {
					p.switchModes();
					int newEnergy = new Random().nextInt(6) + 5; // 5–10
					p.setEnergyLevel(newEnergy);
					System.out.println(getName() + " revived " + p.getName() + " with " + newEnergy + " energy!");
				} else if (p.getEnergyLevel() < 50) {
					int boosted = p.getEnergyLevel() + healAmount;
					p.setEnergyLevel(boosted);
					System.out.println(getName() + " healed " + p.getName() + " to " + boosted + " energy.");
				}
			}
		}
	}

	private double[][] buildDangerMap() {
		int size = 2 * stepsPerMove + 1;
		double[][] map = new double[size][size];
		int cx = getX(), cy = getY();

		for (int dx = -stepsPerMove; dx <= stepsPerMove; dx++) {
			for (int dy = -stepsPerMove; dy <= stepsPerMove; dy++) {
				int tx = cx + dx;
				int ty = cy + dy;
				double danger = calculateDistance(tx, ty, octopus.getX(), octopus.getY()) * octopusAvoidance;
				map[dy + stepsPerMove][dx + stepsPerMove] = danger;
			}
		}
		return map;
	}

	private void moveSafelyTowardGoal(double[][] danger) {
		int targetY = 0;
		int bestX = getX();
		int bestY = getY();
		double minDanger = Double.MAX_VALUE;

		for (int dx = -stepsPerMove; dx <= stepsPerMove; dx++) {
			for (int dy = -stepsPerMove; dy <= stepsPerMove; dy++) {
				int nx = getX() + dx;
				int ny = getY() + dy;
				if (ny < getY()) {
					double risk = danger[dy + stepsPerMove][dx + stepsPerMove];
					if (risk < minDanger) {
						minDanger = risk;
						bestX = nx;
						bestY = ny;
					}
				}
			}
		}
		goTo(bestY, bestX);
	}

	private double calculateDistance(int x1, int y1, int x2, int y2) {
		double dx = Math.abs(x1 - x2);
		double dy = Math.abs(y1 - y2);
		return Math.sqrt(dx * dx + dy * dy);
	}

	/** Go to a location by avenue then street */
	private void goTo(int street, int avenue) {
		goToAvenue(avenue);
		goToStreet(street);
	}

	private void goToAvenue(int avenue) {
		if (getAvenue() > avenue) {
			turnWest();
			move(getAvenue() - avenue);
		} else {
			turnEast();
			move(avenue - getAvenue());
		}
	}

	private void goToStreet(int street) {
		if (getStreet() > street) {
			turnNorth();
			move(getStreet() - street);
		} else {
			turnSouth();
			move(street - getStreet());
		}
	}

	private void turnNorth() {
		if (this.getDirection() == Direction.SOUTH) {
			this.turnAround();
		} else if (this.getDirection() == Direction.WEST) {
			this.turnRight();
		} else if (this.getDirection() == Direction.EAST) {
			this.turnLeft();
		}
	}

	private void turnSouth() {
		if (this.getDirection() == Direction.NORTH) {
			this.turnAround();
		} else if (this.getDirection() == Direction.EAST) {
			this.turnRight();
		} else if (this.getDirection() == Direction.WEST) {
			this.turnLeft();
		}
	}

	private void turnWest() {
		if (this.getDirection() == Direction.NORTH) {
			this.turnLeft();
		} else if (this.getDirection() == Direction.EAST) {
			this.turnAround();
		} else if (this.getDirection() == Direction.SOUTH) {
			this.turnRight();
		}
	}

	private void turnEast() {
		if (this.getDirection() == Direction.SOUTH) {
			this.turnLeft();
		} else if (this.getDirection() == Direction.WEST) {
			this.turnAround();
		} else if (this.getDirection() == Direction.NORTH) {
			this.turnRight();
		}
	}
}
