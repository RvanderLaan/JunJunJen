import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class map implements Serializable{
	private final int height;
	private final int width;
	private tile[][] tiles;
	private int difficulty;
	ArrayList<tile> walkableTiles;
	ArrayList<npc> npcs;
	
	public map(int h, int w, int dif) {
		difficulty = dif;
		height = h;
		width = w;
		tiles = new tile[width][height];
		walkableTiles = new ArrayList<tile>();
		npcs = new ArrayList<npc>();
	}
	
	/**
	 * Returns the random X coordinate as start position
	 * @return
	 */
	public int randomMap() {
		//Create random start and end points (the Y values are 0 and the height of the map)
		int startX = (int) Math.round((width-1) * Math.random());
		
		for (int i = 0; i<width; i++) {
			for (int j = 0; j < height; j++) {
					tiles[i][j] = new tile("Wall", "Just an ordinary wall.", false, i, j);
			}
		}
		//Create a path from start to end, starting at the top
		createPath(startX, 0, new LinkedList());
		createTraps();
		createChest();
		createNPCs();
		return startX;
	}
	
	public void setTile(int x, int y, tile tile) {
		tiles[x][y] = tile;
	}
	
	public int getAndSetRandomEnd() {
		int endX = -1;
		for (tile t : walkableTiles) {
			if (t.getPosY() == height-1)
				endX = t.getPosX();
		}
		tiles[endX][height-1].replace("Exit", "A door to the next level.", true);
		return endX;
	}

	private void createSidePaths(Queue s) {
		if (!s.isEmpty()) {
			//Get the first tile from the queue
			tile temp = (tile) s.remove();
			int curX = temp.getPosX();
			int curY = temp.getPosY();
			
			//Create a random 1 or -1
			int randomPlusMin = -1;
			if (Math.random() > 0.5)
				randomPlusMin = 1;
			
			//Create a random count for the length of a side path
			int randomCount = (int) Math.round(Math.random() * width + 1);
			
			while (randomCount > 0) {
				randomCount--;
				if (this.surroundingTiles(curX, curY) == 1) 
					tiles[curX][curY] = new tile(curX, curY);
				
				//Choose between horizontal or vertical side path (prefers horizontal
				if (Math.random() > 0.4) {
					int nextX = curX + randomPlusMin;
					if (nextX >= 0 && nextX < width) {
						
						if (this.surroundingTiles(nextX, curY) <4) {
							curX = nextX;
							if (!tiles[curX][curY].getWalkable()) {
								tiles[nextX][curY] = new tile(nextX, curY);
								if (!walkableTiles.contains(tiles[curX][curY]))
									walkableTiles.add(tiles[curX][curY]);
								if (Math.random() > 0.6) 
									if (!s.contains(tiles[curX][curY]))
										s.add(tiles[curX][curY]);
							}
						}
					}
				}
				else {
					int nextY = curY + randomPlusMin;
					if (nextY >= 0 && nextY < height-1) {
						
						if (this.surroundingTiles(curX, nextY) < 4) {
							curY = nextY;
							if (!tiles[curX][curY].getWalkable()) {
								tiles[curX][nextY] = new tile(curX, nextY);
								if (!walkableTiles.contains(tiles[curX][curY]))
									walkableTiles.add(tiles[curX][curY]);
								if (Math.random() > 0.6) 
									if (!s.contains(tiles[curX][curY]))
										s.add(tiles[curX][curY]);
							}
						}
					}
				}
			}
			createSidePaths(s);
		}
	}

	private void createPath(int curX, int curY, Queue s) {
		//Create a floor at the current position
		tiles[curX][curY] = new tile(curX, curY);
		if (!s.contains(tiles[curX][curY]))
			s.add(tiles[curX][curY]);
		if (!walkableTiles.contains(tiles[curX][curY]))
			walkableTiles.add(tiles[curX][curY]);
		
		//If the current tile is not the endpoint, create another floor tile
		if (curY != height - 1) {			
			
			//Create a random 1 or -1
			int randomPlusMin = -1;
			if (Math.random() > 0.5)
				randomPlusMin = 1;
			
			//Choose between X or Y translation
			if (Math.random() > 0.4) {
				int nextX = curX + randomPlusMin;
				
				if (this.surroundingTiles(nextX, curY) > 2)
					createPath(curX, curY,s);
				else if (nextX > width-1)
					createPath(curX -1, curY,s);
				else if (nextX < 0) 
					createPath(curX +1, curY,s);
				else
					createPath(nextX, curY,s);
			}
			
			else {
				int nextY = curY + randomPlusMin;
				if (this.surroundingTiles(curX, nextY) > 2)
					createPath(curX, curY + 1,s);
				else if (nextY > height-1)
					createPath(curX, curY -1,s);
				else if (nextY < 0) 
					createPath(curX, curY + 1,s);
				else
					createPath(curX, nextY, s);
			}
		}
		else
			createSidePaths(s);
	}
	
	private int surroundingTiles(int x, int y) {
		int count = 0;

		// Create a 3x3 grid around (x,y)
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {

				// If it isn't the current tile
				if (j != 0 || i != 0) {
					int x2 = x + j;
					int y2 = y + i;

					// If the tile exists
					if (y2 >= 0 && y2 < height && x2 >= 0
							&& x2 < width) {

						// If it's alive
						if (tiles[x2][y2].getWalkable()) {
							count++;
						}
					}
				}
			}
		}

		return count;
	}
	
	
	private void createNPCs() {
		int amount = (int) Math.round(Math.sqrt(difficulty) * (walkableTiles.size() * 0.006 )) - 1;
		
		for (int i = 0; i < amount; i++) {
			tile randomTile = walkableTiles.get((int) Math.round((walkableTiles.size()-1) * Math.random()));
			
			if (randomTile.getPosY() != height -1 && randomTile.getPosY() != 0) {
				npc npc = new npc("Monster", randomTile.getPosX(), randomTile.getPosY(), difficulty);
				npcs.add(npc);
			}
			else
				amount++;
		}
		
	}

	private void createTraps() {
		//int amount = (int) ((Math.sqrt((difficulty * 1000) / walkableTiles.size()))) + 1;
		int amount = (int) Math.round(Math.sqrt(difficulty) * (walkableTiles.size() * 0.006 ));
		System.out.println("Traps Amount: " + amount + ", Monster amount: " + (amount -1));
		
		for (int i = 0; i < amount; i++) {
			tile randomTile = walkableTiles.get((int) Math.round((walkableTiles.size()-1) * Math.random()));
			if (randomTile.getPosY() != height -1 && randomTile.getPosY() != 0) {
				int dmg = (int) Math.round(Math.random() * (difficulty/2) + 1);
				tiles[randomTile.getPosX()][randomTile.getPosY()] = new tileTrap("Trap", "A nasty spike trap.", true, randomTile.getPosX(), randomTile.getPosY(), dmg);
			}
			else
				amount++;
		}
		
	}
	
	private void createChest() {
		tile leastSur = walkableTiles.get(0);
		for (tile t : walkableTiles) {
			if (t.getPosY() != height -1 && t.getPosY() != 0) {
				if (surroundingTiles(t.getPosX(), t.getPosY()) < surroundingTiles(leastSur.getPosX(), leastSur.getPosY())) {
					leastSur = t;
				}
			}
		}
		tiles[leastSur.getPosX()][leastSur.getPosY()] = new tileChest("Container", "What could be in it?", true, leastSur.getPosX(), leastSur.getPosY(), (int) Math.round(Math.random() * difficulty + difficulty/2) + 1);
	}
	
	public tile get(int x, int y) {
		return tiles[x][y];
	}
	
	public ArrayList<npc> getNPCs() {
		return npcs;
	}
	
	public String toString() {
		String res = "";
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j< width; j++) {
				if (tiles[j][i].getWalkable())
					if (tiles[j][i].getName().equals("Trap"))
						res += "O ";
					else if (tiles[j][i].getName().equals("Exit"))
						res += "U ";
					else
						res += "X ";
				else
					res += "- ";
				if (j == width-1) {
					res += "\n";
				}
			}
		}
		return res;
	}
}
