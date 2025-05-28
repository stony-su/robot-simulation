package robotSim;

public class Location {
	private int dangerLevel;
	private int x;
	private int y;
	public Location (int dangerLevel, int x, int y) {
		this.dangerLevel = dangerLevel;
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getDanger() {
		return dangerLevel;
	}
}
