
public class Spell {
	private String name;
	private String description;
	private int cost;
	
	public Spell(String nm, String desc, int mana) {
		name = nm;
		description = desc;
		cost = mana;
	}
	//Effect class: private Stat, private Points
	//public Effect 
	public boolean cast() {
		if (!castable())
			return false;
//		player.restore("Health", 5);
//		player.drainMP(10);
		return true;
	}
	
	public boolean castable() {
//		return player.getMP() >= cost;
		return true;
	}
}
