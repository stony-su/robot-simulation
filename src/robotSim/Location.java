package robotSim;

public class Location {
	private double dangerLevel;
	private int x;
	private int y;
	private int[][] path;
	public Location (int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int[][] getPath(){
		return path;
	}
	
	public void setDanger(double d) {
		this.dangerLevel = d;
	}
	
	public void setPath(int [][] arr) {
		this.path = arr;
	}
	
	public double getDanger() {
		return dangerLevel;
	}
}
