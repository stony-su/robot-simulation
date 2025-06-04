package robotSim;
import java.util.*;
import becker.robots.*;
import java.util.*;

public class Application {
	private final static int OCTO_NUM = 1;
	private final static int PLAYER_NUM = 10;	
	private final static int ENERGY_LIMIT = 10;
	private final static int STEPS_LIMIT = 5;
	private final static double DODGE_LIMIT = 0.7;
	private static boolean allPlayersCaught = false;
	private boolean tag = false;
	
	public static void main (String []args) {
		City city = new City (12, 24);
		Player [] playerArr = new Player [PLAYER_NUM + OCTO_NUM];
		String[] names = {
			    "Alex", "Jamie", "Taylor", "Jordan", "Morgan", "Casey", "Riley", "Drew", "Cameron", "Skyler",
			    "Peyton", "Quinn", "Avery", "Rowan", "Reese", "Emerson", "Finley", "Dakota", "Harper", "Sage",
			    "Logan", "Blake", "Elliot", "Hayden", "Parker", "Sawyer", "Tatum", "Charlie", "Remy", "Alexis",
			    "Jesse", "Kai", "Spencer", "Shiloh", "Kendall", "Arden", "Lennon", "River", "Phoenix", "Marlow",
			    "Lane", "Greer", "Wren", "Indigo", "Micah", "Bailey", "Rowe", "Ellis", "Oakley", "Emery"
			};
		
		Random gen = new Random ();
		
		int energy = gen.nextInt(ENERGY_LIMIT-1)+1;
		int steps = gen.nextInt(STEPS_LIMIT-1)+1;
		double dodge = gen.nextDouble(DODGE_LIMIT-0.1)+0.1;
		int height = gen.nextInt(11)+1;
		Direction direction = Direction.EAST;
		Player octopus = new Octopus (names[names.length], energy, steps, dodge, city, 6, 12, direction);
		playerArr[playerArr.length] = octopus;
		
		for (int i = 0; i < PLAYER_NUM; i++) {
			energy = gen.nextInt(ENERGY_LIMIT-1)+1;
			int maxSteps = gen.nextInt(STEPS_LIMIT-1)+1;
			steps = gen.nextInt(STEPS_LIMIT/2-1)+1;
			dodge = gen.nextDouble(DODGE_LIMIT-0.1)+0.1;
			height = gen.nextInt(11)+1;
			direction = Direction.EAST;
			Player runner = new Runner (names[i], energy, maxSteps, dodge, city, height, 0, direction, steps, octopus);
			playerArr[i] = runner;
		}
		
		playerRecord [] runnerArr = new playerRecord[PLAYER_NUM];
		for (int i = 0; i < PLAYER_NUM; i++) {
			runnerArr[i] = new playerRecord (playerArr[i].getAvenue(), playerArr[i].getStreet(), playerArr[i].getName(), playerArr[i].getType(), 0);
		}
		
		for (int i = 0; i < PLAYER_NUM; i++) {
			playerArr[i].setPlayerRecord(runnerArr);
		}
		
		octopus.setPlayerRecord(runnerArr);
		playerRecord[] recordArr = runnerArr;
		
		//main game loop
		while (!allPlayersCaught) {
			for (int i = 0; i < playerArr.length; i++) {
				recordArr = updateRecords(playerArr);
				playerArr[i].setPlayerRecord(recordArr);
				playerArr[i].move();
				
				if (playerArr[i].getType() == 4) {
					if (((Octopus)playerArr[i]).getTagging() == true) {
						String name = ((Octopus)playerArr[i]).getTargetName();
						triggerTag(name, playerArr);
					}
				}
				updateStatus (playerArr);
			}	
		}
	}
	
	private static playerRecord[] updateRecords(Player[] playerArr) {
		playerRecord [] runnerArr = new playerRecord[PLAYER_NUM];
		for (int i = 0; i < playerArr.length; i++) {
			runnerArr[i] = new playerRecord (playerArr[i].getAvenue(), playerArr[i].getStreet(), playerArr[i].getName(), playerArr[i].getType(), 0);
		}
		return runnerArr;
	}

	private static void updateStatus (Player[] arr) {
		for (int i = 0; i<arr.length-1; i++) {
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
					((Runner)arr[i]).switchModes();
				}
			}
		}
	}
}
