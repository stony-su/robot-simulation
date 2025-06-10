package robotSim;

import becker.robots.*;
import robotSim.*;

import java.util.*;

// Medic class that heals other players and avoids the octopus
public class Medic extends Player {
	private Player[] playerRecord;
	private Player octopus;
	private final int healRange = 2;
	private final int healAmount = 10;
	private final double octopusAvoidance = 1;
	private final int stepsPerMove;
	private boolean movingRight = true; // starts going right

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

		if (movingRight && onRightWall()) {
			movingRight = false;
		} else if (!movingRight && onLeftWall()) {
			movingRight = true;
		}

		double[][] threatMap = buildDangerMap();
		moveSafelyTowardGoal(threatMap);
	}

	private boolean onRightWall() {
		return getAvenue() >= getCity().getNumAvenues() - 1;
	}

	private boolean onLeftWall() {
		return getAvenue() <= 0;
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
		List<int[]> options = new ArrayList<>();
		int cx = getX();
		int cy = getY();

		for (int dx = -stepsPerMove; dx <= stepsPerMove; dx++) {
			for (int dy = -stepsPerMove; dy <= stepsPerMove; dy++) {
				int nx = cx + dx;
				int ny = cy + dy;

				if ((movingRight && nx > cx) || (!movingRight && nx < cx)) {
					double risk = danger[dy + stepsPerMove][dx + stepsPerMove];
					int scaledRisk = (risk == 0.0) ? 0 : (int) (risk * 10000);
					options.add(new int[] { nx, ny, scaledRisk });
				}
			}
		}

		for (int i = 0; i < options.size(); i++) {
			int minIndex = i;
			for (int j = i + 1; j < options.size(); j++) {
				if (options.get(j)[2] < options.get(minIndex)[2]) {
					minIndex = j;
				}
			}
			int[] temp = options.get(i);
			options.set(i, options.get(minIndex));
			options.set(minIndex, temp);
		}

		if (!options.isEmpty()) {
			int[] best = options.get(0);
			goTo(best[1], best[0]); // [1]=street, [0]=avenue
		}
	}

	private double calculateDistance(int x1, int y1, int x2, int y2) {
		double dx = Math.abs(x1 - x2);
		double dy = Math.abs(y1 - y2);
		return Math.sqrt(dx * dx + dy * dy);
	}

	private void goTo(int street, int avenue) {
		goToAvenue(avenue);
		goToStreet(street);
	}

	private void goToAvenue(int avenue) {
		if (getAvenue() > avenue) {
			pointWest();
			move(getAvenue() - avenue);
		} else {
			pointEast();
			move(avenue - getAvenue());
		}
	}

	private void goToStreet(int street) {
		if (getStreet() > street) {
			pointNorth();
			move(getStreet() - street);
		} else {
			pointSouth();
			move(street - getStreet());
		}
	}

	private void pointNorth() {
		if (isFacingEast()) turnLeft();
		else if (isFacingSouth()) { turnLeft(); turnLeft(); }
		else if (isFacingWest()) turnRight();
	}

	private void pointSouth() {
		if (isFacingEast()) turnRight();
		else if (isFacingNorth()) { turnLeft(); turnLeft(); }
		else if (isFacingWest()) turnLeft();
	}

	private void pointWest() {
		if (isFacingSouth()) turnRight();
		else if (isFacingEast()) { turnLeft(); turnLeft(); }
		else if (isFacingNorth()) turnLeft();
	}

	private void pointEast() {
		if (isFacingSouth()) turnLeft();
		else if (isFacingWest()) { turnLeft(); turnLeft(); }
		else if (isFacingNorth()) turnRight();
	}
}
