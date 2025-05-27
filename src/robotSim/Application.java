package robotSim;
import java.util.*;
import becker.robots.*;
import java.util.*;

public class Application {
	final static int OCTO_NUM = 1;
	final static int PLAYER_NUM = 10;	
	static boolean allPlayersCaught = false;
	
	public static void main (String []args) {
		City city = new City (12, 24);
		Player [] playerArr = new Player [PLAYER_NUM + OCTO_NUM];
		
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
