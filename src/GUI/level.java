import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.JPanel;
import javax.swing.Timer;

public class level extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ActionListener {
	
	private map map;
	private int height = 24;
	private int width = 24;
	private boolean showToolTip;
	
	private player player;
	private int startX;
	private int endX;
	
	private ArrayList<npc> npcs;

	private MouseEvent me;
	private boolean draggingItem;
	private item dragging;
	
	MusicPlayer musicPlayer;
	SoundPlayer soundPlayer;
	ArrayList<String> songs;
	
	int difficulty;
	int steps;
	
	private boolean muted;
	private boolean gameOver;
	
	private Queue<String> listNotifications;
	private ArrayList<String> notifications;
	private Timer noteTimer;
	private int noteAlpha;
	
	public level(int dif, String playerName) {
		//Game setup
		map = new map(width,height, dif);
		startX = map.randomMap();
		endX = map.getAndSetRandomEnd();
		npcs = map.getNPCs();
		
		difficulty = dif;
		steps = 0;
		
		player = new player(startX, 0, playerName);
		
		songs = new ArrayList<String>();
		songs.add("DST-MH3.mp3");
		songs.add("DST-BreedingGround.mp3");
		songs.add("DST-DeepSpaceBarrier.mp3");
		
		musicPlayer = new MusicPlayer(songs, true);
		musicPlayer.start();
		
		muted = false;
		draggingItem = false;
		
		notifications = new ArrayList<String>();
		listNotifications = new LinkedList<String>();
		
		//Graphics setup
		this.setFocusable(true);
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		showToolTip = false;
		
		this.revalidate();
		this.repaint();
		
		noteTimer = new Timer(20, this);
		noteAlpha = 255;
		
		addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		gameOver = false;
	}
	
	public void nextLevel(int dif) {
		steps = 0;
		map = new map(width,height, dif);
		int startX = map.randomMap();
		endX = map.getAndSetRandomEnd();
		
		difficulty = dif;
		
		player.set(startX, 0);
		
		npcs = map.getNPCs();
		
		//Graphics setup
		showToolTip = false;
		repaint();
//		save();
	}
	
	public void playSound(String name, float minVol) {
		if (!muted){ 
		try {
			URL url = this.getClass().getClassLoader().getResource(name + ".wav");
			AudioInputStream ais = AudioSystem.getAudioInputStream(url);
			Clip clip = AudioSystem.getClip(); 
			clip.open(ais);
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-minVol);
			
			clip.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		}
	}
	
	public boolean isValidMove(int newX, int newY) {
		
		if (isExit(newX, newY)) {
			nextLevel(difficulty + 1);
			//play sound
			playSound("nextLevel", 15f);
			player.restore("Health", 1);
			player.restore("Mana", 1);
			player.gainEXP(5);
			return false;
		}
		
		if (newX < 0 || newX > width-1)
			return false;
		if (newY < 0 || newY > height -1)
			return false;
		if (map.get(newX, newY).getWalkable()) {
			if (map.get(newX, newY).getName().equals("Trap")) {
				tileTrap trap = (tileTrap) map.get(newX, newY);
				if (!trap.isTriggered()) {
					playSound("hit1", 0);
					player.receiveDamage(trap.trigger());
				}
			}
			
			else if (map.get(newX, newY).getName().equals("Container")) {
				if (player.getInventory().size() <= 10) {
					tileChest tc = (tileChest) map.get(newX, newY);
					if (!tc.isEmpty()) {
						playSound("pickup", 0);
						player.addItem(tc.getItem());
					}
				}
			}
			
			return true;
		}
		return false;
	}
	
	public boolean isValidMonsterMove(npc n, int direction) {
		int newX = n.getX();
		int newY = n.getY();
		
		switch (direction) {
			case 0: 	newY--;
						break;
			case 1: 	newX++;
						break;
			case 2:		newY++;
						break;
			case 3: 	newX--;
						break;
		}
		
		if (player.getX() == newX && player.getY() == newY) {
			return false;
		}
		
		for (npc n2 : npcs) {
			if (n2.getX() == newX && n2.getY() == newY)
				return false;
		}
		
		if (newX < 0 || newX > width-1)
			return false;
		if (newY < 0 || newY > height -1)
			return false;
		if (map.get(newX, newY).getWalkable()) {
			return true;
		}
		return false;
	}
	
	public void update() {
		steps++;
		//Control npcs
		for (int i = 0; i < npcs.size(); i++) {
			npc n = npcs.get(i);
			
			if (n.getHP() <= 0) {
				player.gainEXP(5/n.getLVL() + 4);
				//70% chance for fat loot
				if (Math.random() > 0.3)
					map.setTile(n.getX(), n.getY(), new tileChest("Container", "What could be in it?", true, n.getX(), n.getY(), (int) Math.round(Math.random() * difficulty + difficulty/2) + 1));
				npcs.remove(i);
				playSound("monsterDie", 0);
			}
			
			//If at the same spot as a player, move randomly and attack
			if (n.getX() == player.getX() && n.getY() == player.getY()) {
				//Play sound
				playSound("hit2", 0);
				
				player.receiveDamage(n.dealDamage());
				
				int d = (int) Math.round(Math.random() * 3);
				boolean isValidMove = false;
				while (!isValidMove) {
					d = (int) Math.round(Math.random() * 3);
					isValidMove = isValidMonsterMove(n, d);
				}
				n.move(d);
			}
			
			//If in player radius
			if (n.inPlayerInRadius(player.getX(), player.getY())) {
				playSound("monsterMove", 20f);
				if (Math.abs(player.getX() - n.getX()) > Math.abs(player.getY() - n.getY())) {
					if (n.getX() > player.getX()) {
						if (isValidMonsterMove(n, 3))
								n.move(3);
					}
					else {
						if (isValidMonsterMove(n, 1))
							n.move(1);
					}
				}
				else {
					if (n.getY() > player.getY()) {
						if (isValidMonsterMove(n, 0))
							n.move(0);
					}
					else
						if (isValidMonsterMove(n, 2)) {
							n.move(2);
					}
				}
			}
			else {
				int d = (int) Math.round(Math.random() * 3);
				if (isValidMonsterMove(n, d))
					n.move(d);
			}
				
		}
		
		//Check if player is dead
		if (player.getHP() <= 0) {
			playSound("die", 0);
			gameOver = true;
//			player = new player(0, 0, player.getName());
//			listNotifications.clear();
//			nextLevel(1);
		}
		
		if (player.getEXP() >= 15) {
			player.gainLevel();
			player.setEXP(0);
			playSound("lvlUp", 0);
			notifications.add("Level up!");
		}
		
		//Reset notifications
		if (!player.getNotifications().isEmpty()) {
			notifications.addAll(player.getNotifications());
			listNotifications.addAll(player.getNotifications());
			
			//Never have more than 10 notes in the list
			while(listNotifications.size() > 10) 
				listNotifications.remove();
		}
			
		
		repaint();
	}

	private boolean isExit(int X, int Y) {
		if (Y != height -1)
			return false;
		else
			if (X == endX)
				return true;
		return false;		
	}
	

	
	public void paint(Graphics g) {
		super.paint(g);
	
		//Smooth setup
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);	
		
		//Paint the level, player and tooltip
		paintLevel(g2d);
		
		//Draw seperation
		g2d.setColor(Color.white);
		g2d.setStroke(new BasicStroke(3));
		g2d.drawLine(33 * width - 14, 0, 33 * width - 14, 33 * height);
		
		//Paint the Sidebar
		int sideBarStartX = 33 * width;
		
		paintSidebar(sideBarStartX, g2d);
		
		//Draw gameOver screen
				if(gameOver) {
					Font bold = new Font("Monospaced", Font.BOLD, 20);
					Font normal = new Font("Monospaced", Font.PLAIN, 16);
					
					g2d.setColor(Color.black);
					g2d.fillRect(32, 128, (width-1)*32, (height-4)*32);
					
					g2d.setColor(Color.white);
					g2d.setFont(bold);
					g2d.drawString("You deaded.", 32, 20);
					
					g2d.setFont(normal);
					g2d.drawString("Your total score was: " + player.getScore(), 32, 40);
					g2d.drawString("Your level was: " + player.getLVL(), 32,  60);
				}
		
	}
	
	private void paintLevel(Graphics2D g2d) {
		//Paint level
		for (int i = 0; i<width; i++) {
			for (int j = 0; j < height; j++) {
				//Paint tiles
				if (map.get(i, j).getName().equals("Wall")) {
					g2d.setColor(Color.white);
					g2d.drawRect(1 + i * 32, 1 + j * 32, 30, 30);
				}
				else if (map.get(i, j).getName().equals("Trap")) {
					g2d.setColor(Color.red);
					g2d.drawOval(1 + i * 32, 1 + j * 32, 30, 30);
				}
				else if (map.get(i, j).getName().equals("Exit") ) {
					g2d.setColor(Color.green);
					g2d.drawOval(1 + i * 32, 1 + j * 32, 30, 30);
				}
				else if (map.get(i, j).getName().equals("Container") ) {
					tileChest tc =  (tileChest) map.get(i, j);
					g2d.setColor(Color.yellow);
					if (!tc.isEmpty()) 
						g2d.fillRect(8 + i * 32, 8 + j * 32, 16, 16);
					else
						g2d.drawRect(8 + i * 32, 8 + j * 32, 16, 16);
				}
			}
		}
		//Draw player
		g2d.setColor(Color.white);
		g2d.fillOval(8 + 32 * player.getX(), 8 + 32 * player.getY(), 16, 16);
		
		//Draw enemies
		for (int i = 0; i < npcs.size(); i++) {
			g2d.setColor(Color.red);
			g2d.fillOval(8 + 32 * npcs.get(i).getX(), 8 + 32 * npcs.get(i).getY(), 16, 16);
		}
		
		//Draw mask
		for (int i = 0; i<width; i++) {
			for (int j = 0; j < height; j++) {
				if (Math.abs(player.getX() - i) > 3 || Math.abs(player.getY() - j) > 3) {
					g2d.setColor(Color.black);
					g2d.fillRect(i * 32, j * 32, 32, 32);
				}
				else if (Math.abs(player.getX() - i) > 2 || Math.abs(player.getY() - j) > 2) {
					g2d.setColor(new Color(0,0,0,127));
					g2d.fillRect(i * 32, j * 32, 32, 32);
				}
			}
		}
		
		//Draw tooltip
		if (showToolTip) {
			int x = me.getX();
			int y = me.getY();
			System.out.println(x + ", " + y);
			int dX = (int) Math.round(x / 32);
			int dY = (int) Math.round(y / 32);
			if (Math.abs(player.getX() - dX) < 4 && Math.abs(player.getY() - dY) < 4) {
				if (dY >= 0 && dY <= height -1) {
					if (x + 200> 32*width)
						x = 32*width - 200;
					if (y + 100> 32*height)
						y = 32*height - 100;
					
					g2d.setColor(Color.white);
					g2d.fillRect(x, y, 200, 100);
					g2d.setColor(Color.black);
					g2d.fillRect(x + 2, y + 2, 196, 96);
					
					Font bold = new Font("Monospaced", Font.BOLD, 16);
					Font normal = new Font("Monospaced", Font.PLAIN, 14);
					
					g2d.setColor(Color.white);
					g2d.setFont(bold);
					g2d.drawString(map.get(dX, dY).getName(), x + 4,y + 20);
					g2d.setFont(normal);
					g2d.drawString(map.get(dX, dY).getDescription(), x + 4, y + 40);
					
					//If the player is on that tile
					if (player.getX() == dX && player.getY() == dY) {
						g2d.setColor(Color.white);
						g2d.setFont(bold);
						g2d.drawString(player.getName(), x + 4,y + 60);
						g2d.setFont(normal);
						g2d.drawString("That's me!", x + 4, y + 80);
					}
					
					//If an enemy is on that tile
					for (npc n : npcs) {
						if (n.getX() == dX && n.getY() == dY) {
							g2d.setColor(Color.white);
							g2d.setFont(bold);
							g2d.drawString(n.getName(), x + 4,y + 60);
							g2d.setFont(normal);
							g2d.drawString("I wonder if it likes me.", x + 4, y + 80);
						}
					}
				}
			}
		}
		
		Font noti = new Font("Monospaced", Font.BOLD, 14);
		g2d.setFont(noti);
		//Paint the current notification
		if (!notifications.isEmpty())
			paintNotifications(g2d);
		
		//paint the list
		int listStartX = (int) Math.round((32*width) + 300);
		paintNoteList(listStartX, 500, g2d );
	}
	private void paintNotifications(Graphics2D g2d) {
		int x = 32 + 32 * player.getX();
		int y = -16 + 32*player.getY();
		if (x + 80> 32*width)
			x = 32*width - 80;
		if (y + 14 * notifications.size()> 32*height)
			y = 32*height - 14 * notifications.size();
		if (y + 14 * notifications.size() < 0)
			y = 0;
		
		//Draw notifications
		//Font noti = new Font("Monospaced", Font.BOLD, 14);
		//g2d.setFont(noti);
		g2d.setColor(new Color(0,0,0, noteAlpha));
		g2d.fillRect(x, y, 80, 14 * notifications.size() + 4);
		
		g2d.setColor(new Color(255, 255, 255, noteAlpha));
		
		for (int i = 0; i < notifications.size(); i++ ) {
			g2d.drawString(notifications.get(i), x, y + 14 * i + 14);
		}
		noteTimer.start();
	}
	
	private void paintNoteList(int x, int y, Graphics g2d) {
		y = y + 140;
		g2d.setColor(Color.white);
		for (int i = 0; i < listNotifications.size(); i++) {
			String temp = (String) listNotifications.remove();
			g2d.drawString(temp, x, y - i * 14);
			listNotifications.add(temp);
		}
	}
	
	private void paintSidebar(int sideBarStartX, Graphics2D g2d) {
		//Title
		g2d.setStroke(new BasicStroke(1));
		Font title = new Font("Monospaced", Font.BOLD, 32);
		g2d.setFont(title);
		g2d.drawString("JunJunJen (Alpha 1.0.0)", sideBarStartX, 32);
		
		//Level indicator
		Font menu = new Font("Monospaced", Font.PLAIN, 16);
		g2d.setFont(menu);
		g2d.drawString("Stage: " + difficulty,sideBarStartX, 48);
		g2d.drawString("Steps: " + steps,sideBarStartX + 128, 48);
		g2d.drawString("Score: " + player.getScore(),sideBarStartX + 256, 48);
		
		//Draw the EXP, HP, AP, MP, etc of the player
		paintPlayerIndicators(sideBarStartX, 45, g2d);
		
		//Draw stats
		int statsY = 500;
		paintStats(sideBarStartX, statsY, g2d);
		
		//Inventory
		g2d.setColor(Color.white);
		int invY = 150;
		int invX = sideBarStartX;
		paintInventory(invX, invY, g2d);
		
		
		if (draggingItem) {
			if (dragging != null) {
				paintDrag(g2d);
			}
		}
	}
	
	public void paintInventory(int invX, int invY, Graphics2D g2d) {
		int sideBarStartX = 33*width;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				g2d.drawRect(invX + 1, invY + 1, 62, 62);
				if (i == 0)
					g2d.drawString(j + 1 + "", invX + 55, invY + 60);
				else if (i == 1 && j == 4)
					g2d.drawString("0", invX + 55, invY + 60);
				else 
					g2d.drawString((j+6) + "", invX + 55, invY + 60);
				
				if (player.getInventory().getItem(j, i) != null) {
					consumable co = (consumable) player.getInventory().getItem(j, i);
					if (co.getName().equals("Health"))
						g2d.setColor(Color.red);
					else if (co.getName().equals("Armor")) 
						g2d.setColor(Color.gray);
					else if (co.getName().equals("Mana"))
						g2d.setColor(Color.blue);
					
					g2d.fillOval(invX + 17, invY + 17, 30, 30);
				
					g2d.setColor(Color.white);
				}
				
				invX += 64;
				if (invX == sideBarStartX + 64 * 5) {
					invY += 64;
					invX = sideBarStartX;
				}
			}
			
		}
	}
	
	public void paintDrag(Graphics2D g2d) {
		if (dragging.getName().equals("Health"))
			g2d.setColor(Color.red);
		else if (dragging.getName().equals("Armor")) 
			g2d.setColor(Color.gray);
		else if (dragging.getName().equals("Mana"))
			g2d.setColor(Color.blue);
		
		g2d.fillOval(me.getX(), me.getY(), 30, 30);
	}
	
	public boolean useItem(int x, int y) {
		System.out.println(x + ", " + y);
		
		if (player.getInventory().getItem(x, y) != null) {
			playSound("useItem", 0);
			player.useItem(x, y);
			return true;
		}
		return false;
	}

	private void paintPlayerIndicators(int sideBarStartX, int startY, Graphics2D g2d) {
		int vertSpacing = 24;
		int horSpacing = 24;
		//HP
		g2d.setColor(Color.green);
		int expStartX = sideBarStartX;
		for (int i = 0; i < player.getEXP(); i++) {
			g2d.fillOval(expStartX, startY + vertSpacing/3, 8, 8);
			expStartX += (horSpacing / 2);
		}
		
		//HP
		g2d.setColor(Color.red);
		int hpStartX = sideBarStartX;
		for (int i = 0; i < player.getHP(); i++) {
			g2d.fillOval(hpStartX, startY + vertSpacing, 16, 16);
			hpStartX += horSpacing;
		}
		//MP
		g2d.setColor(Color.blue);
		int mpStartX = sideBarStartX;
		for (int i = 0; i < player.getMP(); i++) {
			g2d.fillOval(mpStartX, startY + vertSpacing*2, 16, 16);
			mpStartX += horSpacing;
		}
		//MP
		g2d.setColor(Color.gray);
		int apStartX = sideBarStartX;
		for (int i = 0; i < player.getAP(); i++) {
			g2d.fillOval(apStartX, startY + vertSpacing*3, 16, 16);
			apStartX += horSpacing;
		}
	}
	private void paintStats(int sideBarStartX, int startY, Graphics2D g2d) {
		int vertSpacing = 20;
		//Stats
		g2d.setColor(Color.white);
		g2d.drawString("Name:      " + player.getName(),sideBarStartX, startY);
		g2d.drawString("Level:     " + player.getLVL(),sideBarStartX, startY + vertSpacing);
		g2d.drawString("Damage:    " + player.getDMG(),sideBarStartX, startY + 2 * vertSpacing);
		g2d.drawString("Dexterity: " + player.getDEX(),sideBarStartX, startY + 3 * vertSpacing);
	}

	@Override
	public void mouseClicked(MouseEvent mouseevent) {	
		if (!draggingItem) {
		me = mouseevent;
		//Tooltip
		if (me.getX() < 32 * width) {
			if (!showToolTip) {
				showToolTip = true;
			}
			repaint();
		}
		else {
			//Using items
			if (me.getY() > 150 & me.getY() < 150 + 64 * 2
					&& me.getX() > 33 * width && me.getX() < 33*width + 5 * 64) {
				if (useItem((int) Math.round((me.getX()-33*(width))/64), (int) Math.round((me.getY()-150)/64)))
					update();
			}
			
		}
		}
	}
	@Override
	public void mouseEntered(MouseEvent mouseevent) {
		
	}
	@Override
	public void mouseExited(MouseEvent mouseevent) {
		
	}
	@Override
	public void mousePressed(MouseEvent mouseevent) {
		
	}
	@Override
	public void mouseReleased(MouseEvent mouseevent) {
		if (draggingItem) {
			int dX = (mouseevent.getX()-33*width)/64;
			int dY = (mouseevent.getY()-150)/64;
			System.out.println(dX + ", " + dY);
			if (dX >=0 && dX <= 4 && dY >= 0 && dY <= 1 && player.getInventory().getItem(dX, dY) == null) 
				player.getInventory().setItem(dX, dY, dragging);
			else if (dX >=0 && dX <= 4 && dY >= 0 && dY <= 1 && player.getInventory().getItem(dX, dY) != null) {
				item it = player.getInventory().getItem(dX, dY);
				player.getInventory().setItem(dX, dY, dragging);
				player.addItem(it);
			}
			else
				player.addItem(dragging);
			repaint();
		}
		draggingItem = false;
		dragging = null;
		
	}
	@Override
	public void mouseDragged(MouseEvent mouseevent) {
		
		//Dragging items
		if (mouseevent.getY() > 150 & mouseevent.getY() < 150 + 64 * 2
				&& mouseevent.getX() > 33 * width && mouseevent.getX() < 33*width + 5 * 64) {
			int dX = (mouseevent.getX()-33*width)/64;
			int dY = (mouseevent.getY()-150)/64;
			me = mouseevent;
			if (player.getInventory().getItem(dX, dY) != null && dragging == null) {
				dragging = player.getInventory().removeItem(dX, dY);
			}
			if (dragging != null) 
				draggingItem = true;
			
			repaint();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent mouseevent) {
		if (showToolTip) {
			if (mouseevent.getX() > me.getX() + 100 || mouseevent.getX() < me.getX() -20
					||mouseevent.getY() > me.getY() + 100 || mouseevent.getY() < me.getY() - 20) {
				showToolTip = false;
				repaint();
			}	
		}
	}

	public player getPlayer() {
		return player;
	}
	
	private void save() {
	   String s = "save";
	    s.concat(".dat");
	    try {
	        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(s));
	        oos.writeObject(this);
	        oos.close();
	    } catch(Exception ex) {
	        ex.printStackTrace();
	    }
	}
	public void load() {
		level load;
		String s = "save.dat";
	    if(s.contains(".dat")){
	        try {
	        	ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:/Users/Remi/Eclipse/DungeonGen/save"));
	            load =  (level) ois.readObject();
	            ois.close();
	            this.requestFocus();
	            this.revalidate();
	            this.repaint();
	            
	            map = load.getMap();
	            endX = map.getAndSetRandomEnd();
	            player = load.getPlayer();
	            player.set(load.getStartX(), 0);
	            
	            
	            difficulty = load.getDifficulty();
	            steps = 0;
	            
	            
	        } catch(Exception ex) {
	            ex.printStackTrace();
	        } 
		}
	}
	
	public map getMap() {
		return map;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public int getStartX() {
		return startX;
	}
	public void setMap(map m) {
		map = m;
	}

	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case (KeyEvent.VK_UP):
		case (KeyEvent.VK_W):
			if (isValidMove(player.getX(), player.getY()-1))
			{player.move(0); update();}
			break;
		case (KeyEvent.VK_RIGHT):
		case (KeyEvent.VK_D):
			if (isValidMove(player.getX() + 1, player.getY()))
			{player.move(1); update();}
			break;
		case (KeyEvent.VK_DOWN):
		case (KeyEvent.VK_S):
			if (isValidMove(player.getX(), player.getY()+1))
			{player.move(2); update();}
			break;
		case(KeyEvent.VK_LEFT):
		case (KeyEvent.VK_A):
			if (isValidMove(player.getX()-1, player.getY()))
			{player.move(3); update();}
			break;
		case(KeyEvent.VK_SPACE):
		case(KeyEvent.VK_Z):
			attack();
			break;
		case(KeyEvent.VK_M):
			mute();
			break;
		case(KeyEvent.VK_T):
			update();
			break;
		case(KeyEvent.VK_1):
			if (useItem(0,0))
				update();
			break;
		case(KeyEvent.VK_2):
			if (useItem(1,0))
				update();
			break;
		case(KeyEvent.VK_3):
			if (useItem(2,0))
				update();
			break;
		case(KeyEvent.VK_4):
			if (useItem(3,0))
				update();
			break;
		case(KeyEvent.VK_5):
			if (useItem(4,0))
				update();
			break;
		case(KeyEvent.VK_6):
			if (useItem(0,1))
				update();
			break;
		case(KeyEvent.VK_7):
			if (useItem(1,1))
				update();
			break;
		case(KeyEvent.VK_8):
			if (useItem(2,1))
				update();
			break;
		case(KeyEvent.VK_9):
			if (useItem(3,1))
				update();
			break;
		case(KeyEvent.VK_0):
			if (useItem(4,1))
				update();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	public void mute() {
		muted = !muted;
		if (muted)
			musicPlayer.close();
		else {
			musicPlayer = new MusicPlayer(songs,true);
			musicPlayer.start();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Fading notifications with timer
		noteAlpha -= 5;
		
		if (!player.getNotifications().isEmpty() && !player.getNotifications().equals(notifications)) {
			noteAlpha = 255;
		}
		
		else if (noteAlpha <= 0) {
			noteAlpha = 255;
			noteTimer.stop();
			player.clearNotifications();
			notifications.clear();
		}
		
		player.clearNotifications();
		
		repaint();
		
		if (notifications.size() > 4)
			noteTimer.setInitialDelay(100);
	}
	
	public void attack() {
		int x = player.getX();
		int y = player.getY();
		for (npc n : npcs) { 
			if (((n.getX() == x + 1 || n.getX() == x - 1) && n.getY() == y) || ((n.getY() == y + 1 || n.getY() == y - 1) && n.getX() == x)) {
				n.receiveDamage(player.dealDamage());
				player.receiveDamage(n.dealDamage());
				playSound("hit1", 0);
				playSound("hit2", 0);
			}
		}
		update();
	}
}
