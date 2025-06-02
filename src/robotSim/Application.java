package robotSim;
import java.util.*;
import becker.robots.*;
import java.util.*;

public class Application {
	final static int OCTO_NUM = 1;
	final static int PLAYER_NUM = 10;	
	final static int ENERGY_LIMIT = 10;
	final static int STEPS_LIMIT = 5;
	final static double DODGE_LIMIT = 0.7;

	static boolean allPlayersCaught = false;
	
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
		
		Player [] runnerArr = new Player[PLAYER_NUM];
		for (int i = 0; i < PLAYER_NUM; i++) {
			runnerArr[i] = playerArr [i];
		}
		
		for (int i = 0; i < PLAYER_NUM; i++) {
			playerArr[i].setRunnerRecord(runnerArr);
		}
		
		while (!allPlayersCaught) {
			int randomNum = gen.nextInt(101);
			if (randomNum == 100) {
				for (int i = 0; i < playerArr.length; i++) {
					playerArr[i].move();
				}	
			}
		}
	}
	
	public void updateStatus (boolean allPlayersCaught) {
		this.allPlayersCaught = allPlayersCaught;
	}
}
