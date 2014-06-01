import java.awt.Color;


public class npc extends player {
	private int level;
	int radius;
	
	public npc(String name, int x, int y, int lvl) {
		super(x, y, name);
		level = lvl;
		radius = 2 + (int) Math.round(Math.sqrt(lvl));
		if (radius > 5) 
			radius = 5;
		this.setDMG((int) Math.round(Math.random() * (lvl / 3)) + 1);
		this.setAP(lvl / 5);
		this.setHP(5 + lvl / 3);
	}
	
	public boolean inPlayerInRadius(int playerX, int playerY) {
		if (Math.abs(this.getX() - playerX) < radius && Math.abs(this.getY() - playerY) < radius ) 
			return true;
		return false;
	}
	
}
