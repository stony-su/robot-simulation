package robotSim;

import becker.robots.*;

import java.awt.Color;
import java.util.*;

/**
 * Abstract base class for all players in the simulation.
 * Encapsulates shared data such as position, energy, movement range, dodging
 * ability, and name.
 * Subclasses implement the core game logic, e.g takeTurn(), getType()
 * 
 * This class extends the Becker Robots framework and gives utility methods to
 * manage the player's state and position.
 * 
 * @author Arnnav Kudale
 * @version 06-13-2025
 */
public abstract class Player extends RobotSE {
    private int energyLevel; // Current player energy
    private int maxStepsPerMove; // Max tiles player can move each turn
    private double dodgingAbility; // Probability to dodge an attack
    private String name; // Player name (label & identity)
    private int y; // Street (row)
    private int x; // Avenue (col)
    public playerRecord[] playerList; // List of player states

    /**
     * Constructs a new Player object with given attributes.
     *
     * @param name            Player name
     * @param energyLevel     Starting energy level
     * @param maxStepsPerMove Maximum number of steps per turn
     * @param dodgingAbility  Chance to dodge attacks (0.0–1.0)
     * @param city            City environment (Becker)
     * @param y               Starting street
     * @param x               Starting avenue
     * @param direction       Initial facing direction
     */
    public Player(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x,
            Direction direction) {
        super(city, y, x, direction);
        this.name = name;
        this.energyLevel = energyLevel;
        this.maxStepsPerMove = maxStepsPerMove;
        this.dodgingAbility = dodgingAbility;
    }

    /**
     * Main turn logic that all subclasses must define
     */
    public abstract void takeTurn();

    /**
     * Returns integer representing player type.
     */
    public abstract int getType();

    /**
     * Sets playerRecord array
     * 
     * @param arr Array of playerRecord (not Player)
     */
    public void setPlayerRecord(playerRecord[] arr) {
        this.playerList = arr;
    }

    /**
     * Returns playerRecord array.
     */
    public playerRecord[] getPlayerRecord() {
        return this.playerList;
    }

    /**
     * Sets the color of the player.
     * 
     * @param color Color to be given
     */
    public void setColor(Color color) {
        super.setColor(color);
    }

    /**
     * Returns the player's energy level.
     */
    public int getEnergyLevel() {
        return energyLevel;
    }

    /**
     * Sets the player's energy level.
     * 
     * @param energyLevel New energy value
     */
    public void setEnergyLevel(int energyLevel) {
        this.energyLevel = energyLevel;
    }

    /**
     * Gets the maximum steps the player can move.
     */
    public int getMaxStepsPerMove() {
        return maxStepsPerMove;
    }

    /**
     * Sets the max steps per move.
     * 
     * @param maxStepsPerMove New step limit
     */
    public void setStepsPerMove(int maxStepsPerMove) {
        this.maxStepsPerMove = maxStepsPerMove;
    }

    /**
     * Returns the player's dodging ability.
     */
    public double getDodgingAbility() {
        return dodgingAbility;
    }

    /**
     * Sets the player's dodging ability.
     * 
     * @param dodgingAbility Dodge probability (0.0–1.0)
     */
    public void setDodgingAbility(double dodgingAbility) {
        this.dodgingAbility = dodgingAbility;
    }

    /**
     * Returns the player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the player's name.
     * 
     * @param name Desired name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the player's X coordinate (avenue).
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the player's Y coordinate (street).
     */
    public int getY() {
        return this.y;
    }

    /**
     * Manually sets the X position.
     * 
     * @param x Desired x coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Manually sets the Y position.
     * 
     * @param y Desired y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Determines if player is touching the left wall
     */
    public abstract boolean onLeftWall();

    /**
     * Determines if player is touching the right wall
     */
    public abstract boolean onRightWall();

}
