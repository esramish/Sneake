//Import necessary classes
import java.util.Scanner;
import java.util.ArrayList;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Class to represent a map component containing obstacles and a goal object
 * Each effective Map needs a file as input - file will have shapes in each line.
 * Shapes in the file should be in this format: shape initX initY sizeX sizeY colorR colorG colorB
 */
public class Map extends JComponent{
	//Instance variables to hold data that is read from file:
    ArrayList<Shape> shapeList;
    ArrayList<Color> colorList;
	//Instance variables to help with parsing integers within a line:
	int prevSpace;
	int currSpace;
	String newShapeInput;
	boolean blueCoordReached;
   
    /**
	 * Constructs a blank map, necessary due to implementation in Frame.java
     */
    public Map(){
	    //Initialize the instance variables
		shapeList = new ArrayList<Shape>();
	    colorList = new ArrayList<Color>();
		prevSpace = 0;
		currSpace = 0;
		newShapeInput = null;
		blueCoordReached = false; //Has everything besides the blue coordinate sucessfully been assigned
    }
   
    /**
     * Constructs a new map based on the data in inputFile
	 * @param inputFile the file with the data for the map
	 * @throws FileNotFoundException if a file with the given name doesn't exist
	 */
    public Map(File inputFile) throws FileNotFoundException {
		//Create a new scanner instance
		Scanner s = new Scanner(inputFile);
	    //Initialize the instance variables
		shapeList = new ArrayList<Shape>();
	    colorList = new ArrayList<Color>();
	    //Read through the file
		while(s.hasNextLine()){//While there might be another shape to be read from the file
			blueCoordReached = false;
			newShapeInput = s.nextLine();//Read the next line from the file
			//Make sure newShapeInput isn't blank
			while(newShapeInput.equals("") && s.hasNextLine()){
				newShapeInput = s.nextLine();
			}
			if(!s.hasNextLine() && newShapeInput.equals("")){
				continue;
			}
		    Shape newShape;//To hold the new shape itself
		    //Keep track of the index of the space between this input item and the next within the current line
			currSpace = newShapeInput.indexOf(' ');
			if (currSpace == -1){continue;}//If the line doesn't contain ' ', skip the line
		    String shapeType = newShapeInput.substring(0,currSpace);//Read the type of the new shape
		    if(shapeType.equalsIgnoreCase("rectangle")){
		  	    newShape = new Rectangle();
		    }else if(shapeType.equalsIgnoreCase("sphere")){
		  	    newShape = new Ellipse2D.Double();
  		    }else{//It's not a valid shape
			    continue;//Skip this line of the file
		    }
			int initX = 0; int initY = 0; int sizeX = 0; int sizeY = 0; int colorR = 0; int colorG = 0; int colorB = 0;//Declare and initialize an int variable for each shape attribute
		    try {
				//Assign file data to each attribute variable, if possible
		  	    initX = readNextIntFromLine();
		  	    initY = readNextIntFromLine();
				sizeX = readNextIntFromLine();
				sizeY = readNextIntFromLine();
				colorR = readNextIntFromLine();
				colorG = readNextIntFromLine();
				prevSpace = currSpace;
				blueCoordReached = true;
				colorB = Integer.parseInt(newShapeInput.substring(prevSpace + 1));
			    if(colorR < 0 || colorR > 255 || colorG < 0 || colorG > 255 || colorB < 0 || colorB > 255){
				    continue;//Skip this line of the file if any of the rgb input is in an invalid range
			    }
		    }catch(NumberFormatException e){//The file contains something other than an integer where an integer should be
			    //The following if statement checks for a superfluous space (or multiple superfluous spaces) at the end of the line, so that a line that's so nearly correct isn't invalidated by such a small, hidden error
				if(blueCoordReached){//If we're having trouble with the last coordinate specifically
					String blueString = newShapeInput.substring(prevSpace + 1);
					if(blueString.equals("")){//In other words, there's a single space after the green coordinate, but nothing after it. This would cause the condition of the upcoming while loop to throw an exception. 
						continue;
					}
					while(blueString.charAt(blueString.length() - 1) == ' '){//While the last character is a space
						blueString = blueString.substring(0, blueString.length() - 1); //Shave that last space off of blueString
						if(blueString.length() == 0){
							break;//So that the while condition doesn't throw a StringIndexOutOfBoundsException
						}
					}
					try{
						colorB = Integer.parseInt(blueString);//Assign the remaining integer to colorB, if it is an integer
					    if(colorR < 0 || colorR > 255 || colorG < 0 || colorG > 255 || colorB < 0 || colorB > 255){
						    continue;//Skip this line of the file if any of the rgb input is in an invalid range
					    }
					}catch(NumberFormatException ex){
						continue; //What's left is still not an integer; skip this line of the text file anyway after all that
					}catch(StringIndexOutOfBoundsException ex){
						continue;//It was entirely spaces, so nothing's left; skip this line of the text file
					}	
			    }else{
					continue;//Otherwise (if we haven't reached colorB yet), skip this line of the file
				}
		    }catch(StringIndexOutOfBoundsException e){//The file contains nothing where an integer should be
				continue;//Skip this line of the file
		    }
		    //Now that all the data for this shape has been read, 
			//we'll properly cast and assign attributes to a new shape variable, 
			//which is then added to shapeList
			if(shapeType.equalsIgnoreCase("rectangle")){
			    Rectangle rectShape = (Rectangle) newShape;
			    rectShape.setFrame(initX,initY,sizeX,sizeY);
			    shapeList.add(rectShape);
		    }else if(shapeType.equalsIgnoreCase("sphere")){
			    Ellipse2D.Double ellipseShape = (Ellipse2D.Double) newShape;
			    ellipseShape.setFrame(initX,initY,sizeX,sizeY);
			    shapeList.add(ellipseShape);
		    }
		    //Finally, add the valid color to colorList in the corresponding spot
			colorList.add(new Color(colorR, colorG, colorB));
	    }
    }
	
	/**
	 * Method to parse through the current line for the next unparsed integer
	 * @return the next integer in the line
	 */
	private int readNextIntFromLine(){
	    //Update space index variables to surround the wanted integer
		prevSpace = currSpace;
	    currSpace = newShapeInput.indexOf(' ', currSpace + 1);
	    //Read, parse, and return the integer
		return Integer.parseInt(newShapeInput.substring(prevSpace + 1, currSpace));
	}

	/** 
	 * Method called when map is displayed
	 * @param g the Graphics object involved
	 */
    public void paintComponent(Graphics g){
	    //Loop through the shapes and paint each one with the proper color
		for(int i = 0; i < shapeList.size(); i++){
		    Graphics2D g2 = (Graphics2D) g;
		    g2.setPaint(colorList.get(i));
		    g2.fill(shapeList.get(i));
	    }
    }

    /** 
	 * Checks if the snake head is touching any obstacles or the goal object
	 * @param frame the Frame.Move instance that called this method
	 */
	public void checkCollision(Frame.Move frame){
        //Import values from frame
        double radius = frame.getRadius();
	    double xCenter = frame.headX() + radius;
        double yCenter = frame.headY() + radius;
	    //Loops through shapes to check each for contact with snake head
		for (int m=0; m<shapeList.size();m++){
            Shape shape = shapeList.get(m);
            //Loop 360 degrees around the snake head to check for contact with the selected shape
			for(int degrees = 0; degrees < 360; degrees++){
			    double radians = degrees * Math.PI / 180;
			    //Check if shape contains this point:
				double x = xCenter + Math.cos(radians)*radius;
			    double y = yCenter + Math.sin(radians)*radius;
			    if(shape.contains(x,y)){//Snake is in contact with this shape
				    if(shape.equals(shapeList.get(0))){//This is the goal object
	          	        System.out.println("\nYou won! Congrats! \nPlease type 'java Sneake' to play again.");
	          	        System.exit(0);
				    }else{//Snake is not touching the goal object, and is touching an obstacle
	          	        System.out.println("\nYou died! \nPlease type 'java Sneake' to play again.");
	          	        System.exit(0);
				    }
			    }
	        }
        }
    }
}
