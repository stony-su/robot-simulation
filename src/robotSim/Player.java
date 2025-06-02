package robotSim;
import becker.robots.*;

import java.awt.Color;
import java.util.*;

public abstract class Player extends RobotSE {
    private int energyLevel;
    private int maxStepsPerMove;
    private double dodgingAbility;
    private String name;
    private int y;
    private int x;

    public Player(String name, int energyLevel, int maxStepsPerMove, double dodgingAbility, City city, int y, int x, Direction direction) {
        super(city, y, x, direction);
        this.name = name;
        this.energyLevel = energyLevel;
        this.maxStepsPerMove = maxStepsPerMove;
        this.dodgingAbility = dodgingAbility;
    }

    public abstract void takeTurn();
    public abstract int getType();
    public abstract void setRunnerRecord(Player[] arr);

    public void setColor(Color color) {
        super.setColor(color);
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(int energyLevel) {
        this.energyLevel = energyLevel;
    }

    public int getMaxStepsPerMove() {
        return maxStepsPerMove;
    }

    public void setStepsPerMove(int maxStepsPerMove) {
        this.maxStepsPerMove = maxStepsPerMove;
    }

    public double getDodgingAbility() {
        return dodgingAbility;
    }

    public void setDodgingAbility(double dodgingAbility) {
        this.dodgingAbility = dodgingAbility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

	

	abstract void setPlayerRecord(Player[] arr);
    
}
