package robotSim;

import java.awt.Color;
import becker.robots.*;
import java.util.*;

public class Medic extends Player {
	private Player[] playerRecord;
	private Player octopus;
	private boolean goingLeft = false;
	private boolean skipNextTurn = false;

	// Constants
	private final int INJURED_THRESHOLD = 50;
	private final int MAX_ENERGY = 100;
	private final int MIN_REVIVE_ENERGY = 5;
	private final int MAX_REVIVE_ENERGY = 10;
	private final int MIN_HEAL_AMOUNT = 1;
	private final int MAX_HEAL_AMOUNT = 5;
	private final int CITY_LENGTH = 24;
	private final int LEFT_WALL = 0;
	private final int RIGHT_WALL = 23;
	private final int MAX_STREET = 11;
	private final int MIN_STREET = 0;
	private final int MAX_AVENUE = 24;
	private final int MIN_AVENUE = -1;
	private final double OCTOPUS_AVOIDANCE = 0.009;
	private final double TRAVEL_IMPORTANCE = 0.01;
	private final double OUT_OF_BOUNDS_DANGER = 1000000.0;

	private final int stepsPerMove;

	public Medic(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x,
			Direction direction, int stepsPerMove, Player octopus) {
		super(name, energyLevel, maxStepsPerMove, dodgingAbility, city, y, x, direction);
		this.stepsPerMove = stepsPerMove;
		this.octopus = octopus;
		super.setLabel(super.getName());
		super.setColor(Color.BLUE);
	}

	public void setPlayerRecord(Player[] arr) {
		this.playerRecord = arr;
	}

	@Override
	public int getType() {
		return 1;
	}

	@Override
	public void takeTurn() {
		super.setX(getAvenue());
		super.setY(getStreet());

		if (skipNextTurn == true) {
			skipNextTurn = false;
			System.out.println(getName() + " is recovering and skips this turn.");
			return;
		}
	    
		handleNearbyPlayers();

		if (onRightWall()) {
			goingLeft = true;
		} else if (onLeftWall()) {
			goingLeft = false;
		}

		optimalMove();
	}


	private void handleNearbyPlayers() {
		Random rand = new Random();
		for (int i = 0; i < playerRecord.length; i++) {
			Player p = playerRecord[i];
			if (p == this) {
				continue;
			}

			if (getX() == p.getX() && getY() == p.getY()) {
				if (p.getEnergyLevel() <= 0) {
					if (p instanceof Runner) {
						((Runner) p).switchModes();
					}
					int newEnergy = rand.nextInt(MAX_REVIVE_ENERGY - MIN_REVIVE_ENERGY + 1);
					newEnergy = newEnergy + MIN_REVIVE_ENERGY;
					p.setEnergyLevel(newEnergy);
					System.out.println(getName() + " revived " + p.getName() + " with " + newEnergy + " energy!");
				} else if (p.getEnergyLevel() < INJURED_THRESHOLD) {
					int heal = rand.nextInt(MAX_HEAL_AMOUNT - MIN_HEAL_AMOUNT + 1);
					heal = heal + MIN_HEAL_AMOUNT;
					int boosted = p.getEnergyLevel() + heal;
					if (boosted > MAX_ENERGY) {
						boosted = MAX_ENERGY;
					}
					p.setEnergyLevel(boosted);
					System.out.println(getName() + " healed " + p.getName() + " to " + boosted + " energy.");
				}
			}
		}
	}

	private void optimalMove() {
		int totalTiles = 1 + 2 * stepsPerMove;
		Location[] tiles = new Location[totalTiles];
		int xShift = 0;
		int yShift = stepsPerMove;
		int x = getAvenue();
		int y = getStreet();

		for (int i = 0; i < stepsPerMove * 2; i = i + 2) {
			if (goingLeft == false) {
				tiles[i] = predictTileDanger(x + xShift, y + yShift, 0, new Location(x + xShift, y + yShift));
				tiles[i + 1] = predictTileDanger(x + xShift, y - yShift, 0, new Location(x + xShift, y - yShift));
			} else {
				tiles[i] = predictTileDanger(x - xShift, y + yShift, 0, new Location(x - xShift, y + yShift));
				tiles[i + 1] = predictTileDanger(x - xShift, y - yShift, 0, new Location(x - xShift, y - yShift));
			}
			xShift = xShift + 1;
			yShift = yShift - 1;
		}

		if (goingLeft == true) {
			tiles[totalTiles - 1] = predictTileDanger(x - stepsPerMove, y, 0, new Location(x - stepsPerMove, y));
		} else {
			tiles[totalTiles - 1] = predictTileDanger(x + stepsPerMove, y, 0, new Location(x + stepsPerMove, y));
		}

		for (int i = 1; i < tiles.length; i++) {
			Location key = tiles[i];
			int j = i - 1;
			while (j >= 0 && tiles[j].getDanger() > key.getDanger()) {
				tiles[j + 1] = tiles[j];
				j = j - 1;
			}
			tiles[j + 1] = key;
		}

		int[][] path = tiles[0].getPath();
		movePath(path);
	}

	private void movePath(int[][] path) {
		for (int i = path.length; i > 0; i = i - 1) {
			int street = path[i - 1][0];
			int avenue = path[i - 1][1];
			goTo(street, avenue);
		}
	}

	private void goTo(int street, int avenue) {
		goToAvenue(avenue);
		goToStreet(street);
	}

	private void goToAvenue(int avenue) {
		if (getAvenue() > avenue) {
			pointWest();
			move(getAvenue() - avenue);
		} else if (getAvenue() < avenue) {
			pointEast();
			move(avenue - getAvenue());
		}
	}

	private void goToStreet(int street) {
		if (getStreet() > street) {
			pointNorth();
			move(getStreet() - street);
		} else if (getStreet() < street) {
			pointSouth();
			move(street - getStreet());
		}
	}

	private boolean onLeftWall() {
		if (getAvenue() == LEFT_WALL) {
			return true;
		} else {
			return false;
		}
	}

	private boolean onRightWall() {
		if (getAvenue() == RIGHT_WALL) {
			return true;
		} else {
			return false;
		}
	}

	private Location predictTileDanger(int xTarget, int yTarget, int step, Location tile) {
		int x = getAvenue();
		int y = getStreet();

		if (x == xTarget && y == yTarget) {
			tile.setDanger(0);
			return tile;
		}

		if (step == 0) {
			tile.setPath(new int[stepsPerMove][2]);
		}

		double totalXDanger;
		if (goingLeft == true) {
			totalXDanger = accessDangerXY(xTarget - 1, yTarget);
		} else {
			totalXDanger = accessDangerXY(xTarget + 1, yTarget);
		}

		double totalYDanger;
		if (y < yTarget) {
			totalYDanger = accessDangerXY(xTarget, yTarget + 1);
		} else {
			totalYDanger = accessDangerXY(xTarget, yTarget - 1);
		}

		if (x != xTarget) {
			if (goingLeft == true) {
				tile = predictTileDanger(xTarget + 1, yTarget, step + 1, tile);
			} else {
				tile = predictTileDanger(xTarget - 1, yTarget, step + 1, tile);
			}
			tile.setDanger(tile.getDanger() + totalXDanger);
		} else if (y != yTarget) {
			if (y <= yTarget) {
				tile = predictTileDanger(xTarget, yTarget - 1, step + 1, tile);
			} else {
				tile = predictTileDanger(xTarget, yTarget + 1, step + 1, tile);
			}
			tile.setDanger(tile.getDanger() + totalYDanger);
		}

		tile.getPath()[step][0] = xTarget;
		tile.getPath()[step][1] = yTarget;
		return tile;
	}

	private double accessDangerXY(int tx, int ty) {
		if (tx < MIN_AVENUE || tx > MAX_AVENUE || ty < MIN_STREET || ty > MAX_STREET) {
			return OUT_OF_BOUNDS_DANGER;
		}

		double dx = Math.abs(tx - octopus.getX());
		double dy = Math.abs(ty - octopus.getY());
		double distance = Math.sqrt(dx * dx + dy * dy);
		double danger = 1 - distance * OCTOPUS_AVOIDANCE;

		if (goingLeft == true) {
			danger = danger * (1 - TRAVEL_IMPORTANCE * (CITY_LENGTH - tx));
		} else {
			danger = danger * (1 - TRAVEL_IMPORTANCE * tx);
		}

		return danger;
	}

	public void tagAttempt() {
		System.out.println(getName() + " was tagged! Spinning and skipping next turn.");
		spinInPlace();
		skipNextTurn = true;
	}

	private void spinInPlace() {
		turnAround();
		turnAround();
	}

	private void pointNorth() {
		if (isFacingEast()) {
			turnLeft();
		} else if (isFacingSouth()) {
			turnAround();
		} else if (isFacingWest()) {
			turnRight();
		}
	}

	private void pointSouth() {
		if (isFacingEast()) {
			turnRight();
		} else if (isFacingNorth()) {
			turnAround();
		} else if (isFacingWest()) {
			turnLeft();
		}
	}

	private void pointWest() {
		if (isFacingSouth()) {
			turnRight();
		} else if (isFacingEast()) {
			turnAround();
		} else if (isFacingNorth()) {
			turnLeft();
		}
	}

	private void pointEast() {
		if (isFacingSouth()) {
			turnLeft();
		} else if (isFacingWest()) {
			turnAround();
		} else if (isFacingNorth()) {
			turnRight();
		}
	}
}
