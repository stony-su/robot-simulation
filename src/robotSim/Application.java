package robotSim;
import java.util.*;
import becker.robots.*;

public class Application {
    private final static int OCTO_NUM = 1;
    private final static int PLAYER_NUM = 5;
    private final static int ENERGY_LIMIT = 10;
    private final static int STEPS_LIMIT = 6;
    private final static int DODGE_LIMIT = 7;
    private final static int WALLS_WIDTH = 12;
    private final static int WALLS_LENGTH = 24;
    private final static int OCTOPUS_WAIT_MAX = 4;
    private final static int OCTOPUS_WAIT_MIN = 1;
    private static boolean allPlayersCaught = false;
    private boolean tag = false;

    public static void main (String []args) {
        City city = new City (12, 24);
        createWalls(city);
        Player [] playerArr = new Player [PLAYER_NUM + OCTO_NUM + 1];
        String[] names = {
            "Alex", "Jamie", "Taylor", "Jordan", "Morgan", "Casey", "Riley", "Drew", "Cameron", "Skyler",
            "Peyton", "Quinn", "Avery", "Rowan", "Reese", "Emerson", "Finley", "Dakota", "Harper", "Sage",
            "Logan", "Blake", "Elliot", "Hayden", "Parker", "Sawyer", "Tatum", "Charlie", "Remy", "Alexis",
            "Jesse", "Kai", "Spencer", "Shiloh", "Kendall", "Arden", "Lennon", "River", "Phoenix", "Marlow",
            "Lane", "Greer", "Wren", "Indigo", "Micah", "Bailey", "Rowe", "Ellis", "Oakley", "Emery"
        };

        Random gen = new Random ();

        Player octopus = new Octopus (names[names.length-1], gen.nextInt(ENERGY_LIMIT-1)+1, 5, 0.0, city, 6, 12, Direction.WEST);
        playerArr[playerArr.length-1] = octopus;

        for (int i = 0; i < PLAYER_NUM; i++) {
            Player runner = new Runner (names[i], gen.nextInt(ENERGY_LIMIT-1)+1,
                                        gen.nextInt(STEPS_LIMIT-1)+1,
                                        0.0, city, gen.nextInt(11)+1, 1,
                                        Direction.EAST, gen.nextInt(STEPS_LIMIT/2-1)+1, octopus);
            playerArr[i] = runner;
        }

        // Add Medic
        Player medic = new Medic("Medic", 10, 1, 0.0, city, 1, 1, Direction.SOUTH, 2, octopus);
        playerArr[playerArr.length-2] = medic;

        // Create records
        playerRecord[] runnerArr = new playerRecord[PLAYER_NUM + 1];
        for (int i = 0; i < runnerArr.length; i++) {
            runnerArr[i] = new playerRecord(playerArr[i].getAvenue(), playerArr[i].getStreet(), playerArr[i].getName(), playerArr[i].getType(), 0);
        }

        for (int i = 0; i < runnerArr.length; i++) {
            playerArr[i].setPlayerRecord(runnerArr);
        }

        octopus.setPlayerRecord(runnerArr);
        ((Medic)medic).setPlayerRecord(playerArr);
        
        while (!allPlayersCaught) {
            for (int i = 0; i < playerArr.length; i++) {

                if (everyoneOnWall(playerArr)) {
                    callOctopus();
                }

                playerRecord[] recordArr = updateRecords(playerArr);
                playerArr[i].setPlayerRecord(recordArr);

                if (playerArr[i].getType() == 1) {
                	playerArr[i].takeTurn();
                } else if (playerArr[i].getType() != 4 && !onWall(playerArr[i])) {
                    playerArr[i].takeTurn();
                } else if (playerArr[i].getType() == 4) {
                    playerArr[i].takeTurn();
                    if (((Octopus) playerArr[i]).getTagging()) {
                        String name = ((Octopus)playerArr[i]).getTargetName();
                        triggerTag(name, playerArr);
                    }
                }

                updateStatus(playerArr);
            }
        }
    }

    private static void callOctopus() {
        Random gen = new Random();
        int seconds = gen.nextInt(OCTOPUS_WAIT_MAX-OCTOPUS_WAIT_MIN)+OCTOPUS_WAIT_MIN;
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {}
    }

    private static boolean onWall(Player player) {
        return ((Runner) player).onRightWall() || ((Runner) player).onLeftWall();
    }

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

    private static playerRecord[] updateRecords(Player[] playerArr) {
        playerRecord[] runnerArr = new playerRecord[PLAYER_NUM + 1];
        for (int i = 0; i < runnerArr.length; i++) {
            runnerArr[i] = new playerRecord(playerArr[i].getAvenue(), playerArr[i].getStreet(), playerArr[i].getName(), playerArr[i].getType(), 0);
        }
        return runnerArr;
    }

    private static void updateStatus(Player[] arr) {
        for (int i = 0; i<arr.length-2; i++) {
            if (arr[i].getType() != 3)
                return;
        }
        allPlayersCaught = true;
    }

    private static void triggerTag(String name, Player[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getName().equals(name)){
                double dodge = arr[i].getDodgingAbility();
                if (Math.random() > dodge) {
                    ((Runner)arr[i]).getTagged();
                }
            }
        }
    }
}
