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
		for (int i = 0; i < PLAYER_NUM; i++) {
			int energy = gen.nextInt(ENERGY_LIMIT-1)+1;
			int steps = gen.nextInt(STEPS_LIMIT-1)+1;
			double dodge = gen.nextDouble(DODGE_LIMIT-0.1)+0.1;

			Player runner = new Runner (names[i], energy, steps, dodge)
		}
		
		Random gen = new Random();
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
