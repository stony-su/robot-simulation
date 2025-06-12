package robotSim;
import java.util.*;
import becker.robots.*;
/**
 * Application class to carry out the game
 * @author Darren Su
 * @version June 12th, 2025
 */
public class darrensTest1 {
	//constants & variables
    private final static int OCTO_NUM = 0;
    private final static int PLAYER_NUM = 1;
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
        Player [] playerArr = new Player [PLAYER_NUM + OCTO_NUM];
        
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
     
        //create runners
        for (int i = 0; i < PLAYER_NUM; i++) {
        	//gives runner random dodge ability
        	double dodge = (gen.nextInt(DODGE_LIMIT-1)+1)/10;
            Player runner = new Runner (names[i], gen.nextInt(ENERGY_LIMIT-1)+1,
            							gen.nextInt(STEPS_LIMIT-1)+1,
                                        dodge, city, 6, 6,
                                        Direction.EAST, gen.nextInt(STEPS_LIMIT/2-1)+1, null);
            playerArr[i] = runner;
        }

        // Create records of all runners and medics
        playerRecord[] runnerArr = new playerRecord[PLAYER_NUM];
        for (int i = 0; i < runnerArr.length; i++) {
            runnerArr[i] = new playerRecord(playerArr[i].getAvenue(), playerArr[i].getStreet(), playerArr[i].getName(), playerArr[i].getType(), 0);
        }

        //gives every runner a copy of init records
        for (int i = 0; i < runnerArr.length; i++) {
            playerArr[i].setPlayerRecord(runnerArr);
        }
        
        //main game loop, stops when all players are caught
        while (true) {
        	//everyone takes a turn
            for (int i = 0; i < playerArr.length; i++) {

            	//if everyone is on the wall, call octopus
                if (onWall(playerArr[i])) {
                	((Runner)playerArr[i]).changeDirection();
                }               
                playerArr[i].takeTurn();
                 
                
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
        for (int i = 0; i < playerArr.length-2; i++) {
            if (!onWall(playerArr[i])) {
                return false;
            }
        }

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
        for (int i = 0; i < WALLS_WIDTH; i++) {
            walls[i] = new Wall (city, i, 0, Direction.WEST);
        }

        for (int a = 0; a < WALLS_WIDTH; a++) {
            walls[a+WALLS_WIDTH] = new Wall (city, a, WALLS_LENGTH-1, Direction.EAST);
        }

        for (int b = 0; b < WALLS_LENGTH; b++) {
            walls[b+WALLS_WIDTH*2] = new Wall (city, 0, b, Direction.NORTH);
        }

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
        playerRecord[] runnerArr = new playerRecord[PLAYER_NUM];
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
        for (int i = 0; i<arr.length-2; i++) {
            if (arr[i].getType() != 3)
                return;
        }
        allPlayersCaught = true;
    }

}
