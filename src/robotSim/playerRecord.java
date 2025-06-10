package robotSim;

public class playerRecord {
    private int enemyX;
    private int enemyY;
    private int dodgeAbility;
    private String name;
    private int type;

    public playerRecord(int enemyX, int enemyY, String name, int type, int dodgeAbility) {
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.name = name;
        this.type = type;
        this.dodgeAbility = dodgeAbility;
    }
    
    public String toString() {
    	return "This is a playerRecord at X " + this.enemyX + "and Y " + this.enemyY + " called " + this.name + "of type number " + this.type;
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

    public int getType() {
        return type;
    }

    public void updateRecord(int enemyX, int enemyY, int dodgeAbility) {
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.dodgeAbility = dodgeAbility;
    }
}
