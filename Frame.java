//Import necessary classes
import javax.swing.*;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.*;


/**
 * A class to represent a graphics frame of a current Sneake game
 */
public class Frame extends JFrame implements MouseMotionListener{
	//Instance variables:
	private Timer moveTimer;
	private Move listener;
	private double currX;
	private double currY;
	private int mouseX;
	private int mouseY;
	private double dx;
	private double dy;
	private SnakeBody[] snakeBodies;
	private Map map;
	private SnakeBody head;
	private SnakeEye leftEye;
	private SnakeEye rightEye;
	private String levelNumber;
	private JLabel grassDisplay;

	//Constants:
	private static final int STARTING_SNAKE_X = 200;
	private static final int STARTING_SNAKE_Y = 200;
	private static final int DELAY = 25;
	private static final int BODY_DIAM = 30;
	private static final int BODY_RADIUS = BODY_DIAM/2;
	private static final int EYE_DIAM = 10;
	private static final int NUM_PARTS = 100;//Includes head and tail

	/**
	 * Constructs a new Frame instance with a given title, width, and height
	 * @param title the title of the frame
	 * @param levelNumber the number of the desired level, 1-4
	 * @param width width of the frame
	 * @param height height of the frame
	 */
	public Frame(String title, String levelNumber, int width, int height){
		//Set up Frame
		this.setTitle(title);//Method inherited from JFrame
		this.setSize(width, height);//Method inherited from JFrame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Method inherited from JFrame
		//Initialize instance variables:
		this.levelNumber = levelNumber;
		dx = 0;
		dy = 0;
		currX = STARTING_SNAKE_X;
		currY = STARTING_SNAKE_Y;
		//The following "-10000"s work with timer method to ensure snake doesn't move before mouse does
		mouseX = -10000;
		mouseY = -10000;
		myLayeredPane();//Helper function to set up graphics
		listener = new Move();//Instantiate a listener class for the timer
		moveTimer = new Timer(DELAY, listener);//Instantiate a timer
		moveTimer.start();//Start the timer
	}

	/**
	 * Helper function for the constructor.
	 * Sets up layeredPane and its components: the snake parts and the map
	 */
	public void myLayeredPane(){
        //Set up layeredPane
		JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800,600));
        layeredPane.addMouseMotionListener(this);
		//Initialize snakeBodies
		snakeBodies = new SnakeBody[NUM_PARTS];
		//Fill snakeBodies
		for(int i = 0; i < NUM_PARTS; i++){
			//Create each body part, including the head and tail, and add a new layer for it
			SnakeBody body = new SnakeBody(0,0,BODY_DIAM, BODY_DIAM, new Color(0,255,0));//Shape position set to (and stays at) zero, because the component, not the shape, will move
			snakeBodies[i] = body;
			layeredPane.add(body, Integer.valueOf(i + 2));
			body.setSize(800,600);
			body.setOpaque(false);
			body.setLocation((int)currX, (int)currY);//Here's where the component location is set
		}
		head = snakeBodies[0];//Initialize head
		//Create the eyes and add both to different layers
		leftEye = new SnakeEye(0,0,EYE_DIAM, EYE_DIAM, new Color(0,0,0));
		layeredPane.add(leftEye, Integer.valueOf(NUM_PARTS + 2));
		leftEye.setSize(800,600);
		leftEye.setLocation((int)Math.round(currX + BODY_RADIUS*3/4 + BODY_RADIUS*Math.cos(Math.PI/6)), (int)Math.round(currY + BODY_RADIUS*3/4 + BODY_RADIUS*Math.sin(Math.PI/6)));
		leftEye.setOpaque(false);
		rightEye = new SnakeEye(0,0,EYE_DIAM, EYE_DIAM, new Color(0,0,0));
		layeredPane.add(rightEye, Integer.valueOf(NUM_PARTS + 3));
		rightEye.setSize(800,600);
		rightEye.setLocation((int)Math.round(currX + BODY_RADIUS*3/4 + BODY_RADIUS*Math.cos(-(Math.PI/6))), (int)Math.round(currY + BODY_RADIUS*3/4 + BODY_RADIUS*Math.sin(-(Math.PI/6))));
		rightEye.setOpaque(false);
		//Create the map
		map = new Map();//Initialize it using the blank Map constructor
		try {
			map = new Map(new File("map"+levelNumber+".txt"));//Create it properly if the file exists
			System.out.println("Move mouse to start and control the snake!");
		}catch(FileNotFoundException e){//File with the given name doesn't exist
			System.err.println("Invalid level number or file. Please type 'java Sneake' again.");
			System.exit(0);
		}
      layeredPane.add(map, Integer.valueOf(1));//Make a new, bottom-level layer for the map
      map.setSize(800,600);
		map.setOpaque(false);
		final ImageIcon grass = new ImageIcon("grass.jpg");//imports background
		grassDisplay = new JLabel(grass);
		layeredPane.add(grassDisplay, Integer.valueOf(0));//displays background
		grassDisplay.setLocation(0,0);
		grassDisplay.setSize(800,600);
		layeredPane.setOpaque(false);
	   this.add(layeredPane);//Add layeredPane to the frame itself
   }

	/**
	 * Called when the mouse is moved
	 * @param e the event representing the mouse movement
	 */
	 public void mouseMoved(MouseEvent e){
	     mouseX = e.getX();
	     mouseY = e.getY();
     }

    /**
     * Called when the mouse is dragged
     * @param e the event representing the mouse drag
     */
    public void mouseDragged(MouseEvent e){
		this.mouseMoved(e);
    }

	/**
	 * A class to contain the method of a timer
	 */
	class Move implements ActionListener{
		/**
		 * Called every time the main timer fires
		 * @param e the event representing the timer firing
		 */
		public void actionPerformed(ActionEvent e){
			if(mouseX != -10000 && mouseY != -10000){//Checks that the mouse has actually moved by now
				double xDiff = mouseX - currX - BODY_RADIUS;//Horiz component of distance b/w mouse and snake head
				double yDiff = mouseY - currY - BODY_RADIUS;//Vert component of distance b/w mouse and snake head
				double theta = Math.atan(yDiff/xDiff);//Angle b/w mouse and snake head
				if(xDiff < 0){//Looks like this correction is needed to make Math.atan fully accurate:
					theta+=Math.PI;
				}
				double speed = Math.sqrt(Math.pow(mouseX-currX-BODY_RADIUS, 2) + Math.pow(mouseY-currY-BODY_RADIUS, 2)) / 20;//A speed coefficient that is greater when the mouse is further from the snake head
				//Now update dx and dy
				dx = speed * Math.cos(theta);
				dy = speed * Math.sin(theta);
				//Ensure direction of motion is correct
				if(xDiff<0 && dx > 0){
					dx = -dx;
				}
				if(yDiff<0 && dy > 0){
					dy = -dy;
				}
				//Stop the snake's motion along each axis if the snake is so close to the mouse along that axis it's going to overshoot the mouse if it's not stopped
				if(Math.abs(xDiff) < Math.abs(dx)){
					dx = 0;
				}
				if(Math.abs(yDiff) < Math.abs(dy)){
					dy = 0;
				}
				//Update position instance variables
				currX+=dx;
				currY+=dy;
				map.checkCollision(this);//Check if snake has touched an obstacle or goal object
				for(int i = NUM_PARTS - 1; i > 0; i--){//Update position of each non-head body part, based on the previous position of the one in front of it
					SnakeBody part = snakeBodies[i];
					SnakeBody nextPart = snakeBodies[i-1];
					part.setLocation(nextPart.getX(), nextPart.getY());
				}
				head.setLocation((int)Math.round(currX), (int)Math.round(currY));//Set the new location of head
				//Set the new locations of the eyes, based on head location and angle b/w mouse and head
				leftEye.setLocation((int)Math.round((currX + BODY_RADIUS*3/4 + BODY_RADIUS*Math.cos(theta + Math.PI/6))), (int)Math.round((currY + BODY_RADIUS*3/4 + BODY_RADIUS*Math.sin(theta + Math.PI/6))));
				rightEye.setLocation((int)Math.round((currX + BODY_RADIUS*3/4 + BODY_RADIUS*Math.cos(theta - Math.PI/6))), (int)Math.round((currY + BODY_RADIUS*3/4 + BODY_RADIUS*Math.sin(theta - Math.PI/6))));
			}
		}

		/**
		 * Returns the current x-coordinate of the top-left corner of the box containing the snake's head
		 * @return the x-coordinate of head
		 */
		public double headX(){
			return currX;
		}

		/**
		 * Returns the current y-coordinate of the top-left corner of the box containing the snake's head
		 * @return the y-coordinate of head
		 */
		public double headY(){
			return currY;
		}

		/**
		 * Returns the radius of a single circle of the snake's body
		 * @return the snake's body radius
		 */
		public int getRadius(){
			return BODY_RADIUS;
		}
	}
}
