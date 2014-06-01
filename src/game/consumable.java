
public class consumable extends item {
	private int HP;
	private int AP;
	private int MP;
	
	public consumable(String nm, String dc, int hp, int ap, int mp) {
		super(nm, dc);
		HP = hp;
		AP = ap;
		MP = mp;
	}

	public int getHP() {
		return HP;
	}
	
	public int getAP() {
		return AP;
	}
	
	public int getMP() {
		return MP;
	}
}
