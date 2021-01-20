package pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Pathfinder {
	boolean[][] grid;
	
	public Pathfinder(boolean[][] grid) {
		assert(grid.length == 80 && grid[0].length == 42);
		this.grid = grid;
	}
	
	public Vector2i getProchaineCase(Vector2i depart, Vector2i arrivee) {
		if (depart.equals(arrivee)) return null;
		Queue<Vector2i> frontier = new LinkedList<Vector2i>();
		HashMap<Vector2i, Vector2i> pred = new HashMap<Vector2i, Vector2i>();
		frontier.add(depart);
		
		Vector2i current = null;
		boolean found = false;
		while (!frontier.isEmpty() && !found) {
			current = frontier.remove();
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i == 0 && j == 0) continue;
					Vector2i next = new Vector2i(current.x + i, current.y + j);
					if (next.x < 0 || next.x >= 80 || next.y < 0 || next.y >= 42) continue;
					if (!pred.containsKey(next) && (!grid[next.x][next.y] || next.equals(arrivee))) {
						// on ne traverse pas en diagonale
						if (next.x != 0 && next.y != 0 && (grid[current.x][next.y] || grid[next.x][current.y])) continue;
						frontier.add(next);
						pred.put(next, current);
						if (next.equals(arrivee)) found = true;
					}
				}
			}
		}
		if (frontier.isEmpty()) return null;
		
		Vector2i prochain = arrivee;
		while (pred.get(prochain) != depart) {
			prochain = pred.get(prochain);
		}
		return prochain;
	}
	
	public Vector2 corrigeMouvement(Vector2 pos, Vector2 vel) {
		Vector2 new_x = new Vector2(pos.x + vel.x, pos.y);
		Vector2 new_y = new Vector2(pos.x, pos.y + vel.y);
		
		Vector2 ret = new Vector2(vel);
		
		Vector2i new_x_grid = new Vector2i((int)new_x.x/10, (int)new_x.y/10);
		Vector2i new_y_grid = new Vector2i((int)new_y.x/10, (int)new_y.y/10);
		
		if (grid[new_x_grid.x][new_x_grid.y]) ret.x = 0.f;
		if (grid[new_y_grid.x][new_y_grid.y]) ret.y = 0.f;
		
		return ret;
	}
}
