package robotSim;

public abstract class Player {
    private int energyLevel;
    private int stepsPerMove;
    private int dodgingAbility;
    private String name;

    protected playerRecord[] playerList;

    public Player(String name, int energyLevel, int stepsPerMove, int dodgingAbility, playerRecord[] playerList) {
        this.name = name;
        this.energyLevel = energyLevel;
        this.stepsPerMove = stepsPerMove;
        this.dodgingAbility = dodgingAbility;
        this.playerList = playerList;
    }

    public abstract void move();
    public abstract void setColor();
    public abstract String getType();

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

    public int getDodgingAbility() {
        return dodgingAbility;
    }

    public void setDodgingAbility(int dodgingAbility) {
        this.dodgingAbility = dodgingAbility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
