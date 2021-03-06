//Import necessary classes
import javax.swing.JComponent;
import java.awt.geom.Ellipse2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * A class to represent a snake body piece
 */ 
public class SnakeBody extends JComponent {
	//Instance variables
	private Ellipse2D.Double body;
	private Color color;
	
	/**
	 * Constructs a new snake body piece with given coordinates and dimenstions
	 * @param x coordinate
	 * @param y coordinate
	 * @param width the width
	 * @param height the height
	 * @param color the color
	 */
	public SnakeBody(int x, int y, int width, int height, Color color){
		body = new Ellipse2D.Double(x,y,width,height);
		this.color = color;
	}
	
	/** 
	 * Method called when body piece is displayed
	 * @param g the Graphics object involved
	 */
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(color);
		g2.fill(body);
	}
}