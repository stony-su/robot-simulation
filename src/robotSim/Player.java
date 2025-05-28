package robotSim;
import becker.robots.*;

import java.awt.Color;
import java.util.*;

public abstract class Player extends RobotSE {
    private int energyLevel;
    private int stepsPerMove;
    private double dodgingAbility;
    private String name;

    public Player(String name, int energyLevel, int stepsPerMove, double dodgingAbility, City city, int y, int x, Direction direction) {
        super(city, y, x, direction);
        this.name = name;
        this.energyLevel = energyLevel;
        this.stepsPerMove = stepsPerMove;
        this.dodgingAbility = dodgingAbility;
    }

    public abstract void takeTurn();
    public abstract int getType();

    public void setColor(Color color) {
        super.setColor(color);
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(int energyLevel) {
        this.energyLevel = energyLevel;
    }

    public int getStepsPerMove() {
        return stepsPerMove;
    }

    public void setStepsPerMove(int stepsPerMove) {
        this.stepsPerMove = stepsPerMove;
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
        return this.getAvenue();
    }

    public int getY() {
        return this.getStreet();
    }

    public void setX(int x) {
        this.setAvenue(x);
    }

    public void setY(int y) {
        this.setStreet(y);
    }
}
