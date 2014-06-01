
public class SpellBook {
	private Spell[] spells;
	
	public SpellBook() {
		spells = new Spell[3];
	}
	
	public void addSpell(Spell spell) {
		if (!isFull()) {
			for (int i = 0; i<3; i++) {
				if (spells[i] == null) {
					spells[i] = spell;
					break;
				}
			}
		}
	}
	
	public boolean isFull() {
		for (int i = 0; i < 3; i++) {
			if (spells[i] == null)
				return false;
		}
		return true;
	}
}
