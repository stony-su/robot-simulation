
package robotSim;


/**
 * The playerRecord class is used by all the player subclasses such as Runner, Medic and Octopus.
 * It serves the purpose of storing information about players, which is updated every turn by the application class.
 * 
 * @author Maymun Rahman and Arnnav Kudale
 *
 */
public class playerRecord {
	// variable declarations
    private int enemyX;
    private int enemyY;
    private int dodgeAbility;
    private String name;
    private int type;
    private double catchIndex;
    
    /**
     * This is the constructor for a player record
     * @param enemyX - the x position of the player
     * @param enemyY - the y position of the player
     * @param name - the name of the player
     * @param type - the type of the player (1 = medic, 2 = runner, 3 = algae, 4 = octopus)
     * @param dodgeAbility - always 0 at the start
     */
    public playerRecord(int enemyX, int enemyY, String name, int type, int dodgeAbility) {
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.name = name;
        this.type = type;
        this.dodgeAbility = 0;
        this.catchIndex = 0;
       
    }
    /**
     * toString method that just tells the position, name and type
     */
    public String toString() {
    	return "This is a playerRecord at X " + this.enemyX + "and Y " + this.enemyY + " called " + this.name + "of type number " + this.type;
    }
    /**
     * 
     * @return - an int with the dodge ability
     */
    public int getDodge() {
        return dodgeAbility;
    }
    
    /**
     * 
     * @return - x position
     */
    public int getX() {
        return enemyX;
    }
    
    /**
     * 
     * @return - y position
     */
    public int getY() {
        return enemyY;
    }
    
    /**
     * 
     * @return - name
     */
    public String getName() {
        return name;
    }
    /**
     * 
     * @return - an int representing the type of the robot (1 = medic, 2 = runner, 3 = algae, 4 = octopus)
     */
    public int getType() {
        return type;
    }
    
    /**
     * This updates the catchIndexBased on distance from the octopus
     * @param distance - a value that's calculated using the distance formula in the octopus
     */
    public void updateCatchIndex(double distance) {
    	if (type == 1) { // if type medic
    		this.catchIndex = -50;
    	} else if (type == 2) { // if type runner
    		this.catchIndex = 0;
    	} else if (type == 3) { // if type algae
    		this.catchIndex = 999;
    	}
    	
    	// adds distance and dodge ablilty
    	this.catchIndex += distance;
    	this.catchIndex += this.dodgeAbility;
    }
    /**
     * This updates the catchIndexBased on their current type. 
     * @param type - an int representing the current type of the player
     */
    public void updateCatchIndex(int type) {
    	if (type == 3) { // if type algae
    		this.catchIndex = 999;
    	}
    	this.catchIndex += this.type;
    	this.catchIndex += this.dodgeAbility;
    }
    
    /**
     * A method returning the catchIndex which will be useful for sorting
     * @return - a double representing the catchIndex of this player. A lower catchIndex means a better target to try and tag.
     */
    public double getCatchIndex() {
    	return this.catchIndex;
    }
    
    
    /**
     * Updates the record with position and dodge ability each turn.
     * @param enemyX - x position
     * @param enemyY - y position
     * @param dodgeAbility - dodging ability from 0-1
     */
    public void updateRecord(int enemyX, int enemyY, int dodgeAbility) {
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.dodgeAbility = dodgeAbility;
    }
    
    /**
     * Updates the dodgeability
     * @param i - dodging ability from 0-1
     */
	public void updateDodge(int i) {
		this.dodgeAbility = i;
		
	}
}
