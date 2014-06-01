import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class MainMenu extends JPanel implements MouseListener, MouseMotionListener {
	private MouseEvent me;
	private String button;
	private boolean showOptions;
	
	public MainMenu() {
		this.setFocusable(true);
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		showOptions = false;
	}
	
	public void paint(Graphics g) {
		
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		//Image
		if (me != null) {
			int width = Math.round(this.getWidth() / 32);
			int height = Math.round(this.getHeight() / 32);
			for (int i = 0; i<width; i++) {
				for (int j = 0; j < height; j++) {
					g2d.setColor(Color.white);
					g2d.drawRect(1 + i * 32, 1 + j * 32, 30, 30);
					if (Math.abs(Math.round(me.getX() / 32) - i) > 5 || Math.abs(Math.round(me.getY() / 32) - j) > 5) {
						g2d.setColor(new Color(0,0,0,240));
						g2d.fillRect(i * 32, j * 32, 32, 32);
					}
					else if (Math.abs(Math.round(me.getX() / 32) - i) > 4 || Math.abs(Math.round(me.getY() / 32) - j) > 4) {
						g2d.setColor(new Color(0,0,0,200));
						g2d.fillRect(i * 32, j * 32, 32, 32);
					}
					else if (Math.abs(Math.round(me.getX() / 32) - i) > 3 || Math.abs(Math.round(me.getY() / 32) - j) > 3) {
						g2d.setColor(new Color(0,0,0,150));
						g2d.fillRect(i * 32, j * 32, 32, 32);
					}
					else if (Math.abs(Math.round(me.getX() / 32) - i) > 2 || Math.abs(Math.round(me.getY() / 32) - j) > 2) {
						g2d.setColor(new Color(0,0,0,100));
						g2d.fillRect(i * 32, j * 32, 32, 32);
					}
					else
					{
						g2d.setColor(new Color(0,0,0,50));
						g2d.fillRect(i * 32, j * 32, 32, 32);
					}
				}
			}
		}
		int midX = this.getWidth()/2 + 3 + 3 * 32;
		int midY = this.getHeight() + 3 + 3 * 32;
		for (int i = Math.round(midX/32); i < 5; i++) {
			for (int j = Math.round(midY/32) ; j < 5 ; j ++) {
				g2d.setColor(Color.white);
				g2d.drawRect(1 + i * 32, 1 + j * 32, 30, 30);
				
//				if (midX - i > 5 || midY - j > 5) {
//					g2d.setColor(Color.black);
//					g2d.fillRect(i * 32, j * 32, 32, 32);
//				}
//				else if (midX - i > 4 || midY - j > 4) {
//					g2d.setColor(new Color(0,0,0,200));
//					g2d.fillRect(i * 32, j * 32, 32, 32);
//				}
//				else if (midX - i > 3 || midY - j > 3) {
//					g2d.setColor(new Color(0,0,0,150));
//					g2d.fillRect(i * 32, j * 32, 32, 32);
//				}
//				else if (midX - i > 2 || midY - j > 2) {
//					g2d.setColor(new Color(0,0,0,100));
//					g2d.fillRect(i * 32, j * 32, 32, 32);
//				}
			}
		}
	
		
		//Fade
		g2d.setColor(new Color(0,0,0,200));
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight() - 130);
				
		//Title
		g2d.setColor(Color.white);
		Font title = new Font("Monospaced", Font.BOLD, 64);
		Font ver = new Font("Monospaced", Font.PLAIN, 16);
		g2d.setFont(title);
		g2d.drawString("JunJunJen", getWidth()/4, 64);
		g2d.setFont(ver);
		g2d.drawString("Version Alpha 1.0.0 - \u00a9 ThaRemo", getWidth()/4, 80);
		
		//Buttons
		g2d.setColor(Color.black);
		//Black background of buttons
		g2d.fillRect(100, 128, 300, 100);
		g2d.fillRect(80, 256, 320, 100);
		g2d.fillRect(60, 384, 340, 100);
		g2d.fillRect(40, 512, 360, 100);
	
		
		g2d.setColor(Color.white);
		Font menu = new Font("Monospaced", Font.ITALIC, 48);
		g2d.setFont(menu);
		g2d.drawRect(100, 128, 300, 100);
		g2d.drawString("New game", 170, 170);
		g2d.drawRect(80, 256, 320, 100);
		g2d.drawString("Load", 170, 170 + 128);
		g2d.drawRect(60, 384, 340, 100);
		g2d.drawString("Options", 170, 170 + 256);
		g2d.drawRect(40, 512, 360, 100);
		g2d.drawString("Exit", 170, 170 + 389);
		
		if (showOptions) {
			int startX = 800;
			int startY = 200;
			int vertSpacing = 20;
			
			Font subTitle = new Font("Monospaced", Font.PLAIN, 24);
			Font controls = new Font("Monospaced", Font.PLAIN, 16);
			
			g2d.setColor(new Color(0,0,0,150));
			g2d.fillRect(startX, startY-50, 400, 300);
			
			g2d.setColor(Color.white);
			g2d.setFont(subTitle);
			g2d.drawString("Controls:" , startX + 50 , 0 * vertSpacing + startY);
			
			g2d.setFont(controls);
			g2d.drawString("Movement:     Arrow keys or WASD", 		startX + 50 , 1 * vertSpacing + startY);
			g2d.drawString("Attack:       Z or Space", 				startX + 50 , 2 * vertSpacing + startY);
			g2d.drawString("Use item:     Click or Number key",		startX + 50 , 3 * vertSpacing + startY);
			g2d.drawString("Wait:         T",		                startX + 50 , 4 * vertSpacing + startY);
			g2d.drawString("Mute:         M", 						startX + 50 , 5 * vertSpacing + startY);
		}
		
	}
	
	public void toggleOptions() {
		showOptions = !showOptions;
	}
	
	public void setMouseEvent(MouseEvent e) {
		me = e;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	
	public String getButton() {
		return button;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent mouseevent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent mouseevent) {
	
	}

}
