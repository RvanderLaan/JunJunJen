
public class testing {
	public static void main(String args[]) {	
		map m = new map(24,24,1);
		int startX = m.randomMap();
		int endX = m.getAndSetRandomEnd();
		System.out.println(m);
		player p = new player(startX, 0, "player");
	}
}
