import java.awt.CardLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/*
 * Todo:
 * -Save/laad knop					v
 * -Chests + items					~v
 * -NPC's							v
 * -Wapens, meer skills
 * -Animaties				
 * -Wachtknop						v
 * -Close confirmation
 * -Goede graphics
 * -Highscores
 * -Timer							~v
 * -Geen tooltip bij verre tiles	v
 * -Menu border mouse fixen
 * -Monsters aanvallen				v
 * 9 inventory slots + hotkey		v
 * font includen
 * draggable items:					v
 * magic (book in chest, fire/restoration spell?)
 * options
 */

public class start extends JFrame {
	private level lvl;
	private MainMenu menu;
	private CardLayout cardLayout; 
	private Container contentPane;
	
	ArrayList<String> songs;
	
	public static void main(String args[]) {
		new start();
	}
	
	public start() {
		contentPane = this.getContentPane();
		cardLayout = new CardLayout();
		
		lvl = new level(1, "player");
		
		menu = new MainMenu();
		menu.addMouseListener(menu);
		menu.addMouseMotionListener(menu);
		//this.addMouseMotionListener(menu);
		menu.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent mouseevent) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getX() > 100 && e.getX() < 400 && e.getY() > 128 && e.getY() < 228) {
					String name = (String)JOptionPane.showInputDialog(menu, "Enter a name:", "ThaRemo");
					if (name != null) {
						lvl.getPlayer().setName(name);
						cardLayout.next(contentPane);
						lvl.playSound("nextLevel", 15f);
					}
				}
				else if (e.getX() > 80 && e.getX() < 400 && e.getY() > 256 && e.getY() < 356) {
//					lvl.load();
//					cardLayout.next(contentPane);
				}
				else if (e.getX() > 60 && e.getX() < 400 && e.getY() > 384 && e.getY() < 484) {
					menu.toggleOptions();
					menu.repaint();
				}
				else if (e.getX() > 40 && e.getX() < 400 && e.getY() > 512 && e.getY() < 612) {
					System.exit(-1);
				}
			}
			@Override
			public void mouseEntered(MouseEvent mouseevent) {
			}
			@Override
			public void mouseExited(MouseEvent mouseevent) {}
		});
		
		menu.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent mouseevent) {
			}

			@Override
			public void mouseMoved(MouseEvent mouseevent) {
				menu.setMouseEvent(mouseevent);
				menu.repaint();
			}
		});
		
		//Mute button
		menu.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_M) {
					lvl.mute();
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		
		menu.setVisible(true);
		
		contentPane.setLayout(cardLayout);
		contentPane.add(menu, "Main menu");
		contentPane.add(lvl, "JunJunJen");
		
		this.setTitle("JunJunJen");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(1280, 800);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setResizable(false);
//		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//		this.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent ev) {
//				
//			}
//		});	
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				System.out.println("Error setting native LAF: " + e);
			}
	
	}
}
