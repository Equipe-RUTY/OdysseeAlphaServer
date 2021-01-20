package pathfinding;

public class Vector2 {
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public Vector2(Vector2 vel) {
		x = vel.x;
		y = vel.y;
	}
	public float x;
	public float y;
	
	public float norm() {
		return (float)Math.sqrt(x*x + y*y);
	}
	
	public Vector2 normalized() {
		// TODO Auto-generated method stub
		float n = norm();
		return new Vector2(x/n, y / n);
	}
	
	public Vector2 multiplyScalar(float s) {
		return new Vector2(x*s, y*s);
	}
}
