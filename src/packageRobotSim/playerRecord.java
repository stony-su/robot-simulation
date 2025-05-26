package src.packageRobotSim;

public class playerRecord {
    private int enemyX;
    private int enemyY;
    private int dodgeAbility;
    private String name;
    private String type;

    public playerRecord(int enemyX, int enemyY, String name, String type) {
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.name = name;
        this.type = type;
        this.dodgeAbility = 5;
    }

    public int getDodge() {
        return dodgeAbility;
    }

    public int getX() {
        return enemyX;
    }

    public int getY() {
        return enemyY;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void updateRecord(int enemyX, int enemyY, int dodgeAbility) {
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.dodgeAbility = dodgeAbility;
    }
}
