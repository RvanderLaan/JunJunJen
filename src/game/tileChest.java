
public class tileChest extends tile {
	
	private item item;
	private int level;
	private boolean empty;

	public tileChest(String nm, String dc, boolean w, int x, int y, int lvl) {
		super(nm, dc, w, x, y);
		level = lvl;
		empty = false;
		
		int random3 = (int) Math.round(Math.random() * 3);
		int amount = lvl;
		if (level > 5)
			amount = 5;
		if (random3 == 0) {
			item = new consumable("Armor", "It's not very pretty, but it'll come in handy.", 0, amount, 0);
		}
		else if (random3 == 1) {
			item = new consumable("Mana", "That doesn't smell like something I'd like to drink.", 0, 0, amount);
		}
		else {
			item = new consumable("Health", "Just what I need!", amount, 0, 0);
		}
		
	}
	
	public item getItem() {
		empty = true;
		return item;
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	
}
