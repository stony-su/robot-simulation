package robotSim;

public class Application {
	final static int OCTO_NUM = 1;
	final static int PLAYER_NUM = 10;	
	
	public static void main (String []args) {
		Player [] playerArr = new Player [PLAYER_NUM + OCTO_NUM];
		while (true) {
			for (int i = 0; i < playerArr.length; i++) {
				playerArr[i].move();
			}
			
		}
	}
}
