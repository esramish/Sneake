//Import a necessary class
import java.util.Scanner;

/**
 * Program to play a sneake game!
 */
public class Sneake {
	/**
	 * Main method to begin execution
	 * @param args CLI args – unused
	 */
	public static void main(String[] args){
		//Prompt user for level number
		Scanner level = new Scanner(System.in);
		System.out.println("Please input a level (1-9):");
		String select = level.next();
		//Construct a new frame for gameplay and make it visible
		Frame frame = new Frame("Sneake Level "+select, select, 800, 600);
		frame.pack();
		frame.setSize(800,650);
		frame.setVisible(true);
	}
}
