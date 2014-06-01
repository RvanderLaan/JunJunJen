
public class weapon extends item {
	private int attack;
	private int range;

	public weapon(String nm, String dc, int vl, int atk, int rn) {
		super(nm, dc);
		attack = atk;
		range = rn;
	}

}
