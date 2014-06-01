import java.io.Serializable;
import java.util.ArrayList;


/**
 * 
 * @author Remi
 *
 */
public class player implements Serializable{
	private String name;
	//Position
	private int posX;
	private int posY;
	
	//Stats
	private int LVL;	//the current level of the player
	private int EXP;	//experience (for gaining levels)
	private int DEX;	//dexterity (for dodging attacks etc.)
	private int DMG;	//damage (amount of damage the player does)
	private int HP;		//Hitpoints (amount of health)
	private int AP;		//Armour points (amount of armour)
	private int MP;		//Mana points (amount of mana)
	
	private int SCORE;
	
	private inventory inventory;
	private SpellBook spellBook;
	
	private ArrayList<String> notifications;
	
	public player(int x, int y, String nm) {
		name = nm;
		posX = x;
		posY = y;
		
		LVL = 1;
		EXP = 0;
		DEX = 1;
		DMG = 1;
		HP = 10;
		AP = 5;
		MP = 5;
		
		SCORE = 0;
		
		inventory = new inventory();
		spellBook = new SpellBook();
		
		notifications = new ArrayList<String>();
	}
	
	public void setDMG(int dmg) {
		DMG = dmg;
	}
	
	public ArrayList<String> getNotifications() {
		return notifications;
	}
	
	public void clearNotifications() {
		notifications.clear();
	}
	
	public inventory getInventory() {
		return inventory;
	}
	
	public void addItem(item it) {
		inventory.addItem(it);
	}
	
	public void useItem(int x, int y) {
		if (inventory.getItem(x, y) != null) {
			consumable co = (consumable) inventory.getItem(x, y);
			if (inventory.getItem(x, y).getName().equals("Health")) {
				this.restore("Health", co.getHP());
			}
			else if (inventory.getItem(x, y).getName().equals("Armor")) {
				this.restore("Armor", co.getAP());
			}
			else if (inventory.getItem(x, y).getName().equals("Mana")) {
				this.restore("Mana", co.getMP());
			}
			this.removeItem(x, y);
		}
	}
	
	public void restore(String Att, int points) {
		if (Att.equals("Health")) {
			HP += points;
			if (HP > 20) 
				HP = 20;
			
		}
		else if (Att.equals("Armor")) {
			AP += points;
			if (AP > 20) 
				AP = 20;
		}
		else if (Att.equals("Mana")) {
			MP += points;
			if (MP > 20) 
				MP = 20;
		}
	}
	

	private void removeItem(int x, int y) {
		inventory.removeItem(x, y);
	}
	/**
	 * 
	 * @param direction 0 is north, 1 is east, 2 is south, 3 is west
	 */
	public void move(int direction) {
		if (direction == 0)
			posY--;
		else if (direction == 1)
			posX++;
		else if (direction == 2) 
			posY++;
		else if (direction == 3)
			posX--;
	}
	
	public void addEXP(int xp) {
		EXP += xp;
	}
	
	public int getEXP() {
		return EXP;
	}
	
	public int receiveDamage(int recDMG) {
		System.out.println("Receiving damage: Damage: " + recDMG + ", Dex: " + DEX);
		//If DEX is greater than the damage * random or a random 5 %, 
		if (DEX > Math.random() * DEX + 5 || Math.random() > 0.05) {
			//10% chance for a miss
			if (Math.random() > 0.1) {
				if (AP > 0) {
					int minDMG =  Math.round(recDMG/2);
					if (minDMG < 1)
						minDMG = 1;
					HP -= minDMG; 
					notifications.add("Health: " + -minDMG);
					
					if (Math.random() > 0.5) {
						AP--;
						notifications.add("Armor:  " + -1);
					}
					
					return -minDMG;
				}
				else {
					HP -= recDMG;
					notifications.add("Health: " + -recDMG);
					return -recDMG;
				}
			}
			else {
				notifications.add("Miss");
				if (Math.random() > .5) {
					notifications.add("Dex:   +1");
					DEX++;
				}
				return 0;
			}
			
		}
		else {
			DEX++;
			notifications.add("Dodge");
			notifications.add("Dex: +1");
			return 0;
		}
			
				
	}
	
	public int dealDamage() {
		return DMG;
	}
	
	public int getX() {
		return posX;
	}
	public int getY() {
		return posY;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String nm) {
		name = nm;
	}
	
	public void set(int x, int y) {
		posX = x;
		posY = y;
	}
	
	public int getHP() {
		return HP;
	}
	public int getMP() {
		return MP;
	}
	public int getAP() {
		return AP;
	}
	public int getDMG() {
		return DMG;
	}
	public int getDEX() {
		return DEX;
	}
	public int getLVL() {
		return LVL;
	}
	public void setHP(int hp) {
		HP = hp;
	}
	public void setMP(int mp) {
		MP = mp;
	}
	public void setAP(int ap) {
		AP = ap;
	}
	public void setEXP(int exp) {
		EXP = exp;
	}
	public void gainEXP(int score) {
		EXP += score;
		SCORE += score * 100;
	}
	public void gainLevel() {
		LVL++;
		DMG = Math.round(LVL / 2) + 1;
	}
	
	public void gainScore(int score) {
		SCORE += score;
	}
	
	public int getScore() {
		return SCORE;
	}
	
	public void drainMP(int points) {
		MP -= points;
	}
}
