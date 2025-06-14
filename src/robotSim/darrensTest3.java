package robotSim;
import java.util.*;
import becker.robots.*;
/**
 * Application class to carry out the game
 * @author Darren Su
 * @version June 12th, 2025
 */
public class darrensTest3 {
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
    
    public static void main (String []args) {
        City city = new City(WALLS_WIDTH, WALLS_LENGTH);
        createWalls(city);

        // Create algae-filled room with a clear horizontal path at row 6
        int clearPathRow = 6;
        ArrayList<Player> algaeList = new ArrayList<>();
        for (int row = 0; row < WALLS_WIDTH; row++) {
            for (int col = 0; col < WALLS_LENGTH; col++) {
                if (row == clearPathRow) continue;
                Player algae = new Runner("Algae", 1, 1, 0, city, row, col, Direction.EAST, 1, null);
                ((Runner)algae).getTagged();
                algaeList.add(algae);
            }
        }

        // All players (runners + algae)
        Player[] playerArr = new Player[PLAYER_NUM + OCTO_NUM + algaeList.size()];

        // Potential names
        String[] names = {
            "Alex", "Jamie", "Taylor", "Jordan", "Morgan", "Casey"
        };

        Random gen = new Random();

        // Create runners
        for (int i = 0; i < PLAYER_NUM; i++) {
            double dodge = (gen.nextInt(DODGE_LIMIT - 1) + 1) / 10.0;
            Player runner = new Runner(names[i], gen.nextInt(ENERGY_LIMIT - 1) + 1,
                    gen.nextInt(STEPS_LIMIT - 1) + 1,
                    dodge, city, clearPathRow, i * 2 + 1, Direction.EAST,
                    gen.nextInt(STEPS_LIMIT / 2 - 1) + 1, null);
            playerArr[i] = runner;
        }

        // Add algae to playerArr
        for (int i = 0; i < algaeList.size(); i++) {
            playerArr[i + PLAYER_NUM] = algaeList.get(i);
        }

        playerRecord[] runnerArr = new playerRecord[PLAYER_NUM];
        for (int i = 0; i < runnerArr.length; i++) {
            runnerArr[i] = new playerRecord(playerArr[i].getAvenue(), playerArr[i].getStreet(), playerArr[i].getName(), playerArr[i].getType(), 0);
        }

        for (int i = 0; i < runnerArr.length; i++) {
            playerArr[i].setPlayerRecord(runnerArr);
        }

        while (true) {
            for (int i = 0; i < playerArr.length; i++) {
                playerRecord[] recordArr = updateRecords(playerArr);
                playerArr[i].setPlayerRecord(recordArr);

                if (onWall(playerArr[i])) {
                    ((Runner) playerArr[i]).changeDirection();
                }
                playerArr[i].takeTurn();
            }
        }
    }

    private static void callOctopus() {
        Random gen = new Random();
        int seconds = gen.nextInt(OCTOPUS_WAIT_MAX - OCTOPUS_WAIT_MIN) + OCTOPUS_WAIT_MIN;
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {}
    }

    private static boolean onWall(Player player) {
        return ((Runner) player).onRightWall() || ((Runner) player).onLeftWall();
    }

    private static boolean everyoneOnWall(Player[] playerArr) {
        for (int i = 0; i < playerArr.length - 2; i++) {
            if (!onWall(playerArr[i])) {
                return false;
            }
        }

        for (int a = 0; a < playerArr.length - 2; a++) {
            ((Runner) playerArr[a]).changeDirection();
        }
        return true;
    }

    private static void createWalls(City city) {
        Wall[] walls = new Wall[2 * (WALLS_WIDTH + WALLS_LENGTH)];
        for (int i = 0; i < WALLS_WIDTH; i++) {
            walls[i] = new Wall(city, i, 0, Direction.WEST);
        }

        for (int a = 0; a < WALLS_WIDTH; a++) {
            walls[a + WALLS_WIDTH] = new Wall(city, a, WALLS_LENGTH - 1, Direction.EAST);
        }

        for (int b = 0; b < WALLS_LENGTH; b++) {
            walls[b + WALLS_WIDTH * 2] = new Wall(city, 0, b, Direction.NORTH);
        }

        for (int c = 0; c < WALLS_LENGTH; c++) {
            walls[c + WALLS_WIDTH * 2 + WALLS_LENGTH] = new Wall(city, WALLS_WIDTH - 1, c, Direction.SOUTH);
        }
    }

    private static playerRecord[] updateRecords(Player[] playerArr) {
        playerRecord[] runnerArr = new playerRecord[playerArr.length];
        for (int i = 0; i < runnerArr.length; i++) {
            runnerArr[i] = new playerRecord(playerArr[i].getAvenue(), playerArr[i].getStreet(), playerArr[i].getName(), playerArr[i].getType(), 0);
        }
        return runnerArr;
    }

    private static void updateStatus(Player[] arr) {
        for (int i = 0; i < arr.length - 2; i++) {
            if (arr[i].getType() != 3)
                return;
        }
        allPlayersCaught = true;
    }
}
