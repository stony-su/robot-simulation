package robotSim;

import java.awt.Color;
import becker.robots.*;
import java.util.*;

public class Medic extends Player {
	private Player[] playerRecord;
	private Player octopus;
	private boolean goingLeft = false;
	private boolean skipNextTurn = false;
	private boolean revivedThisTurn = false;
	private boolean isAlgae = false;

	private final int INJURED_THRESHOLD = 5;
	private final int MAX_ENERGY = 10;
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
	private final int MIN_AVENUE = 0;
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
		if (isAlgae) {
			return 3;
		}
		return 1;
	}

	@Override
	public void takeTurn() {
		super.setX(getAvenue());
		super.setY(getStreet());

		if (isAlgae) {
			System.out.println(getName() + " is algae and spins in place.");
			spinInPlace();
			return;
		}

		if (skipNextTurn) {
			skipNextTurn = false;
			System.out.println(getName() + " is recovering and skips this turn.");
			return;
		}

		handleNearbyPlayers();

		if (onRightWall()) {
			goingLeft = true;
		} else {
			if (onLeftWall()) {
				goingLeft = false;
			}
		}

		Player nearestAlgae = null;
		int minDist = Integer.MAX_VALUE;
		int mx = getAvenue();
		int my = getStreet();

		for (int i = 0; i < playerRecord.length; i++) {
			Player p = playerRecord[i];
			if (p instanceof Runner) {
				if (((Runner) p).getType() == 3) {
					int px = p.getX();
					int py = p.getY();
					int dist = Math.abs(mx - px) + Math.abs(my - py);
					if (dist < minDist) {
						minDist = dist;
						nearestAlgae = p;
					}
				}
			}
		}

		if (nearestAlgae != null) {
			System.out.println(getName() + " sees algae at (" + nearestAlgae.getY() + ", " + nearestAlgae.getX() + ")");
			moveTowardAlgae(nearestAlgae);
		} else {
			System.out.println(getName() + " sees no algae. Performing optimal move.");
			optimalMove();
		}
	}

	private void moveTowardAlgae(Player algae) {
		int targetX = algae.getX();
		int targetY = algae.getY();

		int bestX = getX();
		int bestY = getY();
		double bestDanger = 9999999;
		int currDist = Math.abs(targetX - bestX) + Math.abs(targetY - bestY);

		int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		for (int i = 0; i < directions.length; i++) {
			int[] dir = directions[i];
			int nx = getX() + dir[0];
			int ny = getY() + dir[1];

			if (nx >= MIN_AVENUE && nx <= MAX_AVENUE && ny >= MIN_STREET && ny <= MAX_STREET) {
				int newDist = Math.abs(targetX - nx) + Math.abs(targetY - ny);
				double danger = accessDangerXY(nx, ny);
				if (newDist < currDist || (newDist == currDist && danger < bestDanger)) {
					bestX = nx;
					bestY = ny;
					bestDanger = danger;
				}
			}
		}
		goTo(bestY, bestX);
	}

	private void handleNearbyPlayers() {
		Random rand = new Random();
		for (int i = 0; i < playerRecord.length; i++) {
			Player p = playerRecord[i];
			if (p == this) {
				continue;
			}

			if (getX() == p.getX() && getY() == p.getY()) {
				if (((Runner) p).getType() == 3) {
					((Runner) p).revive();
					int newEnergy = rand.nextInt(MAX_REVIVE_ENERGY - MIN_REVIVE_ENERGY + 1) + MIN_REVIVE_ENERGY;
					p.setEnergyLevel(newEnergy);
					System.out.println(getName() + " revived " + p.getName() + " with " + newEnergy + " energy!");
				} else {
					if (p.getEnergyLevel() < INJURED_THRESHOLD) {
						int heal = rand.nextInt(MAX_HEAL_AMOUNT - MIN_HEAL_AMOUNT + 1) + MIN_HEAL_AMOUNT;
						int boosted = Math.min(MAX_ENERGY, p.getEnergyLevel() + heal);
						p.setEnergyLevel(boosted);
						System.out.println(getName() + " healed " + p.getName() + " to " + boosted + " energy.");
					}
				}
			}
		}
	}

	private void optimalMove() {
		if (goingLeft) {
			pointTo(Direction.WEST);
		} else {
			pointTo(Direction.EAST);
		}
		move(stepsPerMove);
	}

	private void goTo(int street, int avenue) {
		goToAvenue(avenue);
		goToStreet(street);
	}

	private void goToStreet(int street) {
		street = Math.max(MIN_STREET, Math.min(MAX_STREET, street));
		if (getStreet() > street) {
			pointTo(Direction.NORTH);
			move(getStreet() - street);
		} else {
			if (getStreet() < street) {
				pointTo(Direction.SOUTH);
				move(street - getStreet());
			}
		}
	}

	private void goToAvenue(int avenue) {
		avenue = Math.max(MIN_AVENUE, Math.min(MAX_AVENUE, avenue));
		if (getAvenue() > avenue) {
			pointTo(Direction.WEST);
			move(getAvenue() - avenue);
		} else {
			if (getAvenue() < avenue) {
				pointTo(Direction.EAST);
				move(avenue - getAvenue());
			}
		}
	}

	public boolean onLeftWall() {
		return getAvenue() == LEFT_WALL;
	}

	public boolean onRightWall() {
		return getAvenue() == RIGHT_WALL;
	}

	private double accessDangerXY(int tx, int ty) {
		if (tx < MIN_AVENUE || tx > MAX_AVENUE || ty < MIN_STREET || ty > MAX_STREET) {
			return OUT_OF_BOUNDS_DANGER;
		}

		double dx = Math.abs(tx - octopus.getX());
		double dy = Math.abs(ty - octopus.getY());
		double distance = Math.sqrt(dx * dx + dy * dy);
		double danger = 1 - distance * OCTOPUS_AVOIDANCE;

		if (goingLeft) {
			danger *= (1 - TRAVEL_IMPORTANCE * (CITY_LENGTH - tx));
		} else {
			danger *= (1 - TRAVEL_IMPORTANCE * tx);
		}

		for (int i = 0; i < playerRecord.length; i++) {
			Player p = playerRecord[i];
			if (p != this && p.getX() == tx && p.getY() == ty) {
				if (p instanceof Runner && ((Runner) p).getType() == 3) {
					danger *= 0.01;
				} else {
					if (p.getEnergyLevel() < INJURED_THRESHOLD) {
						danger *= 0.2;
					}
				}
			}
		}
		return danger;
	}

	@Override
	public void move(int steps) {
		for (int i = 0; i < steps; i++) {
			if (getEnergyLevel() <= 0) {
				System.out.println(getName() + " is exhausted. Recovering...");
				spinInPlace();
				setEnergyLevel(5);
				skipNextTurn = true;
				return;
			}

			if (!frontIsClear()) {
				break;
			}

			super.move(1);
			setEnergyLevel(getEnergyLevel() - 1);
			System.out.println(getName() + " moved 1 tile and now has " + getEnergyLevel() + " energy.");
		}
	}

	public void getTagged() {
		System.out.println(getName() + " was tagged! Turning into algae.");
		switchModes();
	}

	public void switchModes() {
		if (!isAlgae) {
			isAlgae = true;
			super.setColor(Color.GREEN);
			super.setEnergyLevel(0);
			super.setLabel("ALGAE");
		}
	}

	private void spinInPlace() {
		turnAround();
		turnAround();
	}

	private void pointTo(Direction direction) {
		switch (direction) {
		case NORTH:
			if (isFacingEast()) {
				turnLeft();
			} else if (isFacingSouth()) {
				turnAround();
			} else if (isFacingWest()) {
				turnRight();
			}
			break;
		case SOUTH:
			if (isFacingEast()) {
				turnRight();
			} else if (isFacingNorth()) {
				turnAround();
			} else if (isFacingWest()) {
				turnLeft();
			}
			break;
		case WEST:
			if (isFacingSouth()) {
				turnRight();
			} else if (isFacingEast()) {
				turnAround();
			} else if (isFacingNorth()) {
				turnLeft();
			}
			break;
		case EAST:
			if (isFacingSouth()) {
				turnLeft();
			} else if (isFacingWest()) {
				turnAround();
			} else if (isFacingNorth()) {
				turnRight();
			}
			break;
		default:
			break;
		}
	}
}
