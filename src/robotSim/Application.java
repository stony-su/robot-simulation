package robotSim;
import java.util.*;
import becker.robots.*;
/**
 * Application class to carry out the game
 * @author Darren Su
 * @version June 12th, 2025
 */
public class Application {
	//constants & variables
    private final static int OCTO_NUM = 1;
    private final static int PLAYER_NUM = 5;
    private final static int ENERGY_LIMIT = 10;
    private final static int STEPS_LIMIT = 8;
    private final static int DODGE_LIMIT = 5;
    private final static int WALLS_WIDTH = 12;
    private final static int WALLS_LENGTH = 24;
    private final static int OCTOPUS_WAIT_MAX = 4;
    private final static int OCTOPUS_WAIT_MIN = 1;
    private static boolean allPlayersCaught = false;
    private boolean tag = false;
    
    /**
     * main method, this is where the code is carried out
     * @param args default value
     */
    public static void main (String []args) {
    	//create the city
        City city = new City (12, 24);
        
        //create the walls
        createWalls(city);
        
        //create arr of all players
        Player [] playerArr = new Player [PLAYER_NUM + OCTO_NUM + 1];
        
        //potential names 
        String[] names = {
            "Alex", "Jamie", "Taylor", "Jordan", "Morgan", "Casey", "Riley", "Drew", "Cameron", "Skyler",
            "Peyton", "Quinn", "Avery", "Rowan", "Reese", "Emerson", "Finley", "Dakota", "Harper", "Sage",
            "Logan", "Blake", "Elliot", "Hayden", "Parker", "Sawyer", "Tatum", "Charlie", "Remy", "Alexis",
            "Jesse", "Kai", "Spencer", "Shiloh", "Kendall", "Arden", "Lennon", "River", "Phoenix", "Marlow",
            "Lane", "Greer", "Wren", "Indigo", "Micah", "Bailey", "Rowe", "Ellis", "Oakley", "Emery"
        };

        //random init
        Random gen = new Random ();

        //create octopus
        Player octopus = new Octopus (names[names.length-1], gen.nextInt(ENERGY_LIMIT-1)+1, 5, 0.0, city, 6, 12, Direction.WEST);
        playerArr[playerArr.length-1] = octopus;
        
     
        //create runners
        for (int i = 0; i < PLAYER_NUM; i++) {
        	//gives runner random dodge ability
        	double dodge = (gen.nextInt(DODGE_LIMIT-1)+1)/10;
            Player runner = new Runner (names[i], gen.nextInt(ENERGY_LIMIT-1)+1,
                                        gen.nextInt(STEPS_LIMIT-1)+1,
                                        dodge, city, gen.nextInt(11)+1, 1,
                                        Direction.EAST, gen.nextInt(STEPS_LIMIT/2-1)+1, octopus);
            playerArr[i] = runner;
        }

        // Add Medic
        Player medic = new Medic("Medic", 10, 1, city, 1, 1, Direction.SOUTH, octopus);
        playerArr[playerArr.length-2] = medic;

        // Create records of all runners and medics
        playerRecord[] runnerArr = new playerRecord[PLAYER_NUM + 1];
        for (int i = 0; i < runnerArr.length; i++) {
            runnerArr[i] = new playerRecord(playerArr[i].getAvenue(), playerArr[i].getStreet(), playerArr[i].getName(), playerArr[i].getType(), 0);
        }

        //gives every runner a copy of init records
        for (int i = 0; i < runnerArr.length; i++) {
            playerArr[i].setPlayerRecord(runnerArr);
        }

        //gives octopus and medic a copy of records
        octopus.setPlayerRecord(runnerArr);
        ((Medic)medic).setPlayerRecord(playerArr);
        
        //main game loop, stops when all players are caught
        while (!allPlayersCaught) {
        	//everyone takes a turn
            for (int i = 0; i < playerArr.length; i++) {
            	
            	//if everyone is on the wall, call octopus
                if (everyoneOnWall(playerArr)) {
                    callOctopus();
                }
                
                //updates player records for person taking turn
                playerRecord[] recordArr = updateRecords(playerArr);
                playerArr[i].setPlayerRecord(recordArr);
                
                //medic and algae always take their turn
                if (playerArr[i].getType() == 1 || playerArr[i].getType() == 3) {
                	playerArr[i].takeTurn();
                
                //if runner is on wall, skip it's turn
                } else if (playerArr[i].getType() == 2 && !onWall(playerArr[i])) {
                    playerArr[i].takeTurn();
                 
                // if player is octopus, take it's turn
                } else if (playerArr[i].getType() == 4) {
                    playerArr[i].takeTurn();
                    
                    //if octopus has tagged someone this turn, trigger the tag
                    ((Octopus) playerArr[playerArr.length-1]).updateIsOnWall(everyoneOnWall(playerArr));
                    if (((Octopus) playerArr[i]).getTagging()) {
                    	//get name of victim
                        String name = ((Octopus)playerArr[i]).getTargetName();
                        triggerTag(name, playerArr);
                    }
                }
                ((Octopus) playerArr[playerArr.length-1]).updateIsOnWall(everyoneOnWall(playerArr));

                //update player records based on movements this turn
                updateStatus(playerArr);
                
            }
        }
    }

    /**
     * octopus waits an random amount of time before calling octopus, letting the players move
     * @post players start moving again
     */
    private static void callOctopus() {
        Random gen = new Random();
        //waits an random amount of seconds based on constants
        int seconds = gen.nextInt(OCTOPUS_WAIT_MAX-OCTOPUS_WAIT_MIN)+OCTOPUS_WAIT_MIN;
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {}
    }

    /**
     * checks if player is on the wall
     * @param player the player in question
     * @return boolean value if the player is on the wall
     */
    private static boolean onWall(Player player) {
        return ((Runner) player).onRightWall() || ((Runner) player).onLeftWall();
    }

    /**
     * checks if everyone is on the wall
     * @param playerArr an array of all the players
     * @return boolean value if all the players are on the wall
     */
    private static boolean everyoneOnWall(Player[] playerArr) {
    	//loops through each player, and checks if it're on the wall and is a algae
        for (int i = 0; i < playerArr.length-2; i++) {
            if (!(onWall(playerArr[i]) || playerArr[i].getType() == 3)) {
            	//if not an algae and off the wall, return false and break loop
                return false;
            }
        }
        
        //tell each player to chance direction to get started on the next round
        for (int a = 0; a < playerArr.length-2; a++) {
            ((Runner)playerArr[a]).changeDirection();
        }
        return true;
    }

    /**
     * creates the walls around the city
     * @param city location of walls
     * @post walls of city is created
     */
    private static void createWalls(City city) {
        Wall [] walls = new Wall[2*(WALLS_WIDTH+WALLS_LENGTH)];
        
        //loop to create west walls
        for (int i = 0; i < WALLS_WIDTH; i++) {
            walls[i] = new Wall (city, i, 0, Direction.WEST);
        }
        
        //loop for east walls
        for (int a = 0; a < WALLS_WIDTH; a++) {
            walls[a+WALLS_WIDTH] = new Wall (city, a, WALLS_LENGTH-1, Direction.EAST);
        }
        
        //loop for north walls
        for (int b = 0; b < WALLS_LENGTH; b++) {
            walls[b+WALLS_WIDTH*2] = new Wall (city, 0, b, Direction.NORTH);
        }
        
        //loop for south walls
        for (int c = 0; c < WALLS_LENGTH; c++) {
            walls[c+WALLS_WIDTH*2+WALLS_LENGTH] = new Wall (city, WALLS_WIDTH-1, c, Direction.SOUTH);
        }
    }
    /**
     * update the player records based on current x and y location of players
     * @param playerArr array of all players
     * @return an array of all player records
     */
    private static playerRecord[] updateRecords(Player[] playerArr) {
        playerRecord[] runnerArr = new playerRecord[PLAYER_NUM + 1];
        //loops through each runner object to set their XY values to the record
        for (int i = 0; i < runnerArr.length; i++) {
            runnerArr[i] = new playerRecord(playerArr[i].getAvenue(), playerArr[i].getStreet(), playerArr[i].getName(), playerArr[i].getType(), 0);
        }
        return runnerArr;
    }

    /**
     * update the status of the players
     * @param arr array of all the players
     * @post stops the game if all player are caught
     */
    private static void updateStatus(Player[] arr) {
    	//if all players are algae, make the main game loop stop
        for (int i = 0; i<arr.length-2; i++) {
            if (arr[i].getType() != 3)
                return;
        }
        allPlayersCaught = true;
    }

    /**
     * when tag is called, random chance for the player to be tagged based on dodge ability
     * @param name name of player whom is tested for tag 
     * @param arr arr of all players
     * @post player might become algae
     */
    private static void triggerTag(String name, Player[] arr) {
    	//loops through the array of players to find the one with target name
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getName().equals(name)){
            	
            	//uses a random value to see if player is "caught" or not based on their dodge ability
                double dodge = arr[i].getDodgingAbility();
                if (Math.random() > dodge) {
                	if (arr[i].getType() == 2)
                		((Runner)arr[i]).getTagged();
                	else if (arr[i].getType() == 1)
                		((Medic)arr[i]).getTagged();

                }
            }
        }
    }
}
