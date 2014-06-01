
public class tileTrap extends tile {
	private int damage;
	private boolean triggered;
	
	public tileTrap(String nm, String dc, boolean w, int x, int y, int dmg) {
		super(nm, dc, w, x, y);
		triggered = false;
		damage = dmg;
	}
	
	public void setDamage(int dmg) {
		damage = dmg;
	}
	
	public int trigger() {
		if (!triggered) {
			triggered = true;
			return damage;
		}
		return 0;
	}
	
	public boolean isTriggered() {
		return triggered;
	}
}
