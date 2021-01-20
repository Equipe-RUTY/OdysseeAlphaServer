package pathfinding;

public class Vector2i {
	public int x;
	public int y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object v) {
		return v instanceof Vector2i && x == ((Vector2i)v).x && y == ((Vector2i)v).y;
	}
	
	@Override
	public String toString() {
		return "Vector2i(" + Integer.toString(x) + "," + Integer.toString(y) + ")";
	}
	
	@Override
	public int hashCode() {
		return x * 1000 + y;
	}
	
	public int manhattanDistance(Vector2i v) {
		return Math.min(Math.abs(x - v.x), Math.abs(y - v.y));
	}

	public Vector2i subtract(Vector2i v) {
		return new Vector2i(x - v.x, y - v.y);
	}
}
