import java.io.Serializable;


public class inventory implements Serializable{
	private item[][] items;
	private int size;
	
	/**
	 * Creates new 3*5 inventory (15 slots)
	 */
	public inventory() {
		items = new item[5][2];
	}
	
	public boolean addItem(item it) {
		size++;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				if (items[j][i] == null) {
					items[j][i] = it;
					return true;
				}
			}
		}
		return false;
	}
	
	public void setItem(int x, int y, item it) {
		items[x][y] = it;
	}
	
	public item removeItem(int x, int y) {
		item res = items[x][y];
		size--;
		items[x][y] = null;
		return res;
	}
	
	public item getItem(int x, int y) {
		return items[x][y];
	}
	
	public int size() {
		return size;
	}
}
