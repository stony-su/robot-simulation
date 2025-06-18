package robotSim;

import java.awt.Color;
import becker.robots.*;
import java.util.*;

/**
 * Medic class that supports teammates by healing or reviving them in the arena.
 * It avoids danger from the Octopus, moves toward algae players, and
 * supports those who are injured. Has logic to avoid danger and conserve
 * energy.
 * 
 * @author Arnnav Kudale
 * @version 06-13-2025
 */
public class Medic extends Player {
	Random rand = new Random();

	private Player[] playerRecord; // Array storing all players in the game
	private boolean goingLeft = false; // Direction flag for moving
	private boolean skipNextTurn = false; // Flag for turn-skipping
	private boolean isAlgae = false; // Indicates if the Medic has been tagged
	private boolean startedLeft = false; // Indicates which wall Medic started at
	private boolean touchedOppositeWall = true; // Revive enabled only after wall is touched

	// Constants for healing and revival behavior
	private final int INJURED_THRESHOLD = 5;
	private final int MAX_ENERGY = 10;
	private final int MIN_REVIVE_ENERGY = 5;
	private final int MAX_REVIVE_ENERGY = 10;
	private final int MIN_HEAL_AMOUNT = 1;
	private final int MAX_HEAL_AMOUNT = 5;

	// Grid boundaries
	private final int CITY_LENGTH = 24;
	private final int LEFT_WALL = 0;
	private final int RIGHT_WALL = 23;
	private final int MAX_STREET = 11;
	private final int MIN_STREET = 0;
	private final int MAX_AVENUE = 24;
	private final int MIN_AVENUE = 0;

	// Danger and movement weights
	private final double OCTOPUS_AVOIDANCE = 0.009;
	private final double TRAVEL_IMPORTANCE = 0.01;
	private final double OUT_OF_BOUNDS_DANGER = 1000000.0;

	private final int stepsPerMove; // Maximum steps Medic can take per move
	private Player octopus;

	/**
	 * Constructor initializes Medic robot
	 * 
	 * @param name            The Medic's name
	 * @param energyLevel     Starting energy
	 * @param maxStepsPerMove How far Medic can move in one turn
	 * @param city            City map where the game is played
	 * @param y               Starting street
	 * @param x               Starting avenue
	 * @param direction       Initial facing direction
	 * @param octopus         Reference to the Octopus player
	 */
	public Medic(String name, int energyLevel, int maxStepsPerMove, City city, int y, int x, Direction direction, Player octopus) {
		super(name, energyLevel, maxStepsPerMove, 0, city, y, x, direction);
		this.stepsPerMove = maxStepsPerMove;
		this.startedLeft = (x == LEFT_WALL);
		super.setLabel(super.getName());
		super.setColor(Color.BLUE);
		this.octopus = octopus;
	}

	/**
	 * Main behavior logic for Medic during its turn. Handles revival, healing, and
	 * moving toward targets.
	 */
	@Override
	public void takeTurn() {
		super.setX(this.getAvenue());
		super.setY(this.getStreet());

		if (this.isAlgae) {
			//System.out.println(this.getName() + " is algae and spins in place.");
			this.spinInPlace();
			return;
		}

		this.checkWallContact();

		if (this.skipNextTurn) {
			this.skipNextTurn = false;
			//System.out.println(this.getName() + " is recovering and skips this turn.");
			return;
		}

		this.handleNearbyPlayers(); // Heal or revive if someone is on the same tile

		if (this.onRightWall()) {
			this.goingLeft = true;
		} else if (this.onLeftWall()) {
			this.goingLeft = false;
		}

		int mx = this.getAvenue();
		int my = this.getStreet();
		Player nearestAlgae = this.findNearestAlgae(mx, my);

		// Revive nearest algae
		if (nearestAlgae != null) {
			//System.out.println(
					//this.getName() + " sees algae at (" + nearestAlgae.getY() + ", " + nearestAlgae.getX() + ")");
			this.moveTowardAlgae(nearestAlgae);
		} else {
			//System.out.println(this.getName() + " sees no algae. Performing optimal move.");
			this.optimalMove();
		}
	}

	/**
	 * Scans player list for nearest algae to move toward.
	 * 
	 * @param mx Current avenue of Medic
	 * @param my Current street of Medic
	 */
	private Player findNearestAlgae(int mx, int my) {
		Player nearestAlgae = null;
		ArrayList<Player> algaeList = new ArrayList<>();

		// Loop through all players to collect those in algae form
		for (int i = 0; i < this.playerRecord.length; i++) {
			Player p = this.playerRecord[i];

			if (p instanceof Runner && ((Runner) p).getType() == 3) {
				algaeList.add(p); // Add algae players to the list
			}
		}

		// Sort algaeList using selection sort based on distance to Medic
		for (int i = 0; i < algaeList.size() - 1; i++) {
			int minIndex = i;
			for (int j = i + 1; j < algaeList.size(); j++) {
				// Calculate distances for algae
				int distJ = Math.abs(mx - algaeList.get(j).getX()) + Math.abs(my - algaeList.get(j).getY());
				int distMin = Math.abs(mx - algaeList.get(minIndex).getX())
						+ Math.abs(my - algaeList.get(minIndex).getY());
				if (distJ < distMin) {
					// If this algae is closer, update minIndex
					minIndex = j;
				}
			}

			// Swap closer algae to the current index
			Player temp = algaeList.get(i);
			algaeList.set(i, algaeList.get(minIndex));
			algaeList.set(minIndex, temp);
		}

		// Return the closest algae if any exist
		if (!algaeList.isEmpty()) {
			nearestAlgae = algaeList.get(0);
		}

		return nearestAlgae;
	}

	/**
	 * Moves toward a given algae player.
	 * 
	 * @param algae The algae player to move toward
	 */
	private void moveTowardAlgae(Player algae) {
		int targetX = algae.getX();
		int targetY = algae.getY();

		int bestX = this.getX();
		int bestY = this.getY();
		double bestDanger = 9999999;
		int currDist = Math.abs(targetX - bestX) + Math.abs(targetY - bestY);

		// Possible movement directions: East, West, South, North
		int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

		// Try each direction and evaluate its danger and distance
		for (int i = 0; i < directions.length; i++) {
			int[] dir = directions[i];
			int nx = this.getX() + dir[0];
			int ny = this.getY() + dir[1];

			// Check if next tile is within bounds
			if (nx >= this.MIN_AVENUE && nx <= this.MAX_AVENUE && ny >= this.MIN_STREET && ny <= this.MAX_STREET) {
				int newDist = Math.abs(targetX - nx) + Math.abs(targetY - ny);
				double danger = this.accessDangerXY(nx, ny);

				// If move brings Medic closer to target or is equally close but safer, update
				// choice
				if (newDist < currDist || (newDist == currDist && danger < bestDanger)) {
					bestX = nx;
					bestY = ny;
					bestDanger = danger;
				}
			}
		}

		// Move to the selected best position
		this.goTo(bestY, bestX);
	}

	/**
	 * Interacts with players on the same tile. revives algae or heals injured.
	 */
	private void handleNearbyPlayers() {
		// Loop through all players in the game
		for (int i = 0; i < this.playerRecord.length; i++) {
			Player p = this.playerRecord[i];

			// Skip checking the medic itself
			if (p == this) {
				continue;
			}

			// Check if player is on the same tile
			if (this.getX() == p.getX() && this.getY() == p.getY()) {
				// If the player is algae, attempt to revive
				if (this.isAlgae(p)) {
					this.reviveAlgae(p);
				}
				// If the player is injured, attempt to heal
				else if (this.isInjured(p)) {
					this.healPlayer(p);
				}
			}
		}
	}

	/**
	 * Revives an algae if the Medic has touched the opposite wall.
	 * 
	 * @param p Player to revive
	 */
	private void reviveAlgae(Player p) {
		// Check if Medic is allowed to revive based on wall contact condition
		if (this.touchedOppositeWall) {
			((Runner) p).revive();
			// Generate random energy level within allowed range
			int newEnergy = rand.nextInt(this.MAX_REVIVE_ENERGY - this.MIN_REVIVE_ENERGY + 1) + this.MIN_REVIVE_ENERGY;
			p.setEnergyLevel(newEnergy);
			//System.out.println(this.getName() + " revived " + p.getName() + " with " + newEnergy + " energy!");
		} else {
			//System.out.println(this.getName() + " sees " + p.getName() + " but cannot revive yet.");
		}
	}

	/**
	 * Heals an injured player with a random energy boost.
	 * 
	 * @param p Player to heal
	 */
	private void healPlayer(Player p) {
		int heal = rand.nextInt(this.MAX_HEAL_AMOUNT - this.MIN_HEAL_AMOUNT + 1) + this.MIN_HEAL_AMOUNT;
		int boosted = Math.min(this.MAX_ENERGY, p.getEnergyLevel() + heal);
		p.setEnergyLevel(boosted);
		//System.out.println(this.getName() + " healed " + p.getName() + " to " + boosted + " energy.");
	}

	/**
	 * Evaluates danger at a given tile using proximity to Octopus and teammates.
	 * 
	 * @param tx Target avenue (x-coordinate)
	 * @param ty Target street (y-coordinate)
	 */
	private double accessDangerXY(int tx, int ty) {
		// Check if tile is outside the map boundaries
		if (tx < this.MIN_AVENUE || tx > this.MAX_AVENUE || ty < this.MIN_STREET || ty > this.MAX_STREET) {
			return this.OUT_OF_BOUNDS_DANGER; // Extreme penalty for out-of-bounds
		}

		// Calculate distance from Octopus
		double dx = Math.abs(tx - octopus.getX());
		double dy = Math.abs(ty - octopus.getY());
		double distance = Math.sqrt(dx * dx + dy * dy);

		// Base danger score decreases with distance from Octopus
		double danger = 1 - distance * this.OCTOPUS_AVOIDANCE;

		// Adjust danger based on horizontal movement direction
		if (goingLeft) {
			danger *= (1 - this.TRAVEL_IMPORTANCE * (this.CITY_LENGTH - tx));
		} else {
			danger *= (1 - this.TRAVEL_IMPORTANCE * tx);
		}

		// Loop through all players to check for special tile conditions
		for (int i = 0; i < this.playerRecord.length; i++) {
			Player p = this.playerRecord[i];

			// If another player is standing on the target tile
			if (p != this && p.getX() == tx && p.getY() == ty) {
				if (p instanceof Runner && ((Runner) p).getType() == 3) {
					danger *= 0.01; // Very low danger near algae
				} else if (p.getEnergyLevel() < this.INJURED_THRESHOLD) {
					danger *= 0.2; // Lower danger near injured teammate
				}
			}
		}

		return danger;
	}

	/**
	 * Patrols left/right when no algae is found
	 */
	private void optimalMove() {
		if (this.goingLeft) {
			this.pointTo(Direction.WEST);
		} else {
			this.pointTo(Direction.EAST);
		}
		this.move(this.stepsPerMove);
	}

	/**
	 * Moves Medic a specified number of steps, reducing energy accordingly. If
	 * energy is exhausted, Medic spins in place to recover.
	 * 
	 * @param steps Number of steps to move
	 */
	@Override
	public void move(int steps) {
		for (int i = 0; i < steps; i++) {
			if (this.getEnergyLevel() <= 0) {
				//System.out.println(this.getName() + " is exhausted. Recovering...");
				this.spinInPlace();
				this.setEnergyLevel(5);
				this.skipNextTurn = true;
				return;
			}

			if (!this.frontIsClear()) {
				break;
			}

			super.move(1);
			this.setEnergyLevel(this.getEnergyLevel() - 1);
			//System.out.println(this.getName() + " moved 1 tile and now has " + this.getEnergyLevel() + " energy.");
		}
	}

	/**
	 * Moves Medic to a target grid coordinate.
	 * 
	 * @param street Target street (y-coordinate)
	 * @param avenue Target avenue (x-coordinate)
	 */
	private void goTo(int street, int avenue) {
		this.goToAvenue(avenue);
		this.goToStreet(street);
	}

	/**
	 * Moves Medic to a specific street and avenue.
	 * 
	 * @param street Target street (y-coordinate)
	 */
	private void goToStreet(int street) {
		street = Math.max(this.MIN_STREET, Math.min(this.MAX_STREET, street));
		if (this.getStreet() > street) {
			this.pointTo(Direction.NORTH);
			this.move(this.getStreet() - street);
		} else if (this.getStreet() < street) {
			this.pointTo(Direction.SOUTH);
			this.move(street - this.getStreet());
		}
	}

	/**
	 * Moves Medic to a specific avenue.
	 * 
	 * @param avenue Target avenue (x-coordinate)
	 */
	private void goToAvenue(int avenue) {
		avenue = Math.max(this.MIN_AVENUE, Math.min(this.MAX_AVENUE, avenue));
		if (this.getAvenue() > avenue) {
			this.pointTo(Direction.WEST);
			this.move(this.getAvenue() - avenue);
		} else if (this.getAvenue() < avenue) {
			this.pointTo(Direction.EAST);
			move(avenue - this.getAvenue());
		}
	}

	/**
	 * Checks if Medic has touched opposite wall for revive to be enabled.
	 */
	private void checkWallContact() {
		if (this.startedLeft && this.getAvenue() == this.RIGHT_WALL) {
			this.touchedOppositeWall = true;
		} else if (!this.startedLeft && this.getAvenue() == this.LEFT_WALL) {
			this.touchedOppositeWall = true;
		}
	}

	/**
	 * Checks if the player is an algae type.
	 * 
	 * @param p Player to check
	 * @return true if the player is algae, false otherwise
	 */
	private boolean isAlgae(Player p) {
		return (((Runner) p).getType() == 3);
	}

	/**
	 * Checks if the player is injured (energy level below threshold).
	 * 
	 * @param p Player to check
	 * @return true if the player is injured, false otherwise
	 */
	private boolean isInjured(Player p) {
		return p.getEnergyLevel() < this.INJURED_THRESHOLD;
	}

	/**
	 * Called when the Medic is tagged by Octopus
	 */
	public void getTagged() {
		//System.out.println(this.getName() + " was tagged! Turning into algae.");
		if (!this.isAlgae) {
			this.isAlgae = true;
			super.setColor(Color.GREEN);
			super.setEnergyLevel(0);
			super.setLabel("ALGAE");
		}
	}

	/**
	 * Checks if Medic is on the left or right wall.
	 * 
	 * @return true if on left wall, false otherwise
	 */
	public boolean onLeftWall() {
		return this.getAvenue() == this.LEFT_WALL;
	}

	/**
	 * Checks if Medic is on the right wall.
	 * 
	 * @return true if on right wall, false otherwise
	 */
	public boolean onRightWall() {
		return this.getAvenue() == this.RIGHT_WALL;
	}

	/**
	 * Spins the Medic in place (used when algae or recovering)
	 */
	private void spinInPlace() {
		this.turnAround();
		this.turnAround();
	}

	/**
	 * Assigns the global list of all players in the game to this Medic.
	 * 
	 * @param arr The array of all Player objects currently active in the game
	 */
	public void setPlayerRecord(Player[] arr) {
		this.playerRecord = arr;
	}

	/**
	 * Turns Medic to face a specific direction
	 * 
	 * @param direction The direction to face (NORTH, SOUTH, EAST, WEST)
	 */
	private void pointTo(Direction direction) {
		switch (direction) {
			case NORTH:
				if (this.isFacingEast()) {
					this.turnLeft();
				} else if (this.isFacingSouth()) {
					this.turnAround();
				} else if (this.isFacingWest()) {
					this.turnRight();
				}
				break;
			case SOUTH:
				if (this.isFacingEast()) {
					this.turnRight();
				} else if (this.isFacingNorth()) {
					this.turnAround();
				} else if (this.isFacingWest()) {
					this.turnLeft();
				}
				break;
			case WEST:
				if (this.isFacingSouth()) {
					this.turnRight();
				} else if (this.isFacingEast()) {
					this.turnAround();
				} else if (this.isFacingNorth()) {
					this.turnLeft();
				}
				break;
			case EAST:
				if (this.isFacingSouth()) {
					this.turnLeft();
				} else if (this.isFacingWest()) {
					this.turnAround();
				} else if (this.isFacingNorth()) {
					this.turnRight();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Returns type: 3 for algae, 1 for Medic
	 */
	@Override
	public int getType() {
		if (this.isAlgae) {
			return 3;
		}
		return 1;
	}
}
