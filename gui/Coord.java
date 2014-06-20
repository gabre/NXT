package gui;

public class Coord {
	Coord(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}
	public Coord(Coord another) {
		this.x = another.x;
		this.y = another.y;	
	}
	
	public double x;
	public double y;
}
