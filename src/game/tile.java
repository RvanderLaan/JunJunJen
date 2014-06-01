import java.io.Serializable;


public class tile implements Serializable{
	private String name;
	private String description;
	private boolean walkable;
	private int posX;
	private int posY;
	
	/**
	 * Creates a plain walkable floor tile
	 */
	public tile(int x, int y) {
		name = "Floor";
		description = "A plain old floor.";
		walkable = true;
		posX = x;
		posY = y;
	}
	
	public tile(String nm, String dc, boolean w, int x, int y) {
		name = nm;
		description = dc;
		walkable = w;
		posX = x;
		posY = y;
	}
	
	public void replace(String nm, String dc, boolean w) {
		name = nm;
		description = dc;
		walkable = w;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean getWalkable() {
		return walkable;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public String toString() {
		return "(" + posX + "," + posY + "," + walkable + ")";
	}
}
