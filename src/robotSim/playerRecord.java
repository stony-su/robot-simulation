package robotSim;

public class playerRecord {
    private int enemyX;
    private int enemyY;
    private int dodgeAbility;
    private String name;
    private int type;
    private double catchIndex;
  
    public playerRecord(int enemyX, int enemyY, String name, int type, int dodgeAbility) {
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.name = name;
        this.type = type;
        this.dodgeAbility = dodgeAbility;
        this.catchIndex = 0;
       
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
    
    public void updateCatchIndex(double distance) {
    	if (type == 1) {
    		this.catchIndex = -50;
    	} else if (type == 2) {
    		this.catchIndex = 0;
    	} else if (type == 3) {
    		this.catchIndex = 999;
    	}
    	this.catchIndex += distance;
    	this.catchIndex += this.dodgeAbility;
    }
    public void updateCatchIndex(int type) {
    	if (type == 3) {
    		this.catchIndex = 999;
    	}
    	this.catchIndex += this.type;
    	this.catchIndex += this.dodgeAbility;
    }
    
    public double getCatchIndex() {
    	return this.catchIndex;
    }
    public void updateRecord(int enemyX, int enemyY, int dodgeAbility) {
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.dodgeAbility = dodgeAbility;
    }

	public void updateDodge(int i) {
		this.dodgeAbility = i;
		
	}
}
