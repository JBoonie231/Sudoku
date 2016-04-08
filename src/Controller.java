import java.util.Scanner;
/**
 * @author Joshua Boone
 *
 *
 * Handles input form the user.
 *
 */
public class Controller 
{
	Scanner scanner =  new Scanner(System.in);
	public Controller() 
	{
		
	}
	
	public String getInput()
	{
		String userInput = scanner.nextLine();
		userInput = userInput.toUpperCase();
		return userInput;
	}
}
