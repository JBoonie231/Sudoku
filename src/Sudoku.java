/**
 * @author Joshua Boone
 *
 *
 * This program allows users to play randomly generated, unique solution, sudoku games.
 * The user can choose any (perfect square) number size for their game, but note that generation 
 * time increases exponentially the bigger the game board.
 * 
 * This program uses constraint satisfaction and backtracking to generate the puzzles, and 
 * constraint propagation arc consistency to catch errors and check for unique solutions. 
 */
public class Sudoku 
{

	/**
	 * Initiates MVC and starts the game loop.
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{	
		String 		userInput;
		String[]	dimMissHintString	= new String[3];
		String[]	rowColString		= new String[2];
		int			selectedElem		= 1;
		int			dim;
		int			miss;
		int			hint;
		int			row;
		int			col;
		
		Model 		model 				= new Model();
		View 		view 				= new View();
		Controller 	controller 			= new Controller();
		
		// Begin
		view.printTitle();
		view.printIntroScreen();
		
		
		// Begin Game Loop
		while (true)
		{
			userInput = controller.getInput();
			
			// Start new game
			if(userInput.equalsIgnoreCase("new"))
			{
				view.printTitle();
				view.printInstructionScreen();
				
				userInput = controller.getInput();
				
				if(userInput.isEmpty())
					model.newGame();
				else
				{
					dimMissHintString 	= userInput.split(" ");
					dim					= Integer.parseInt(dimMissHintString[0]);
					miss				= Integer.parseInt(dimMissHintString[1]);
					hint				= Integer.parseInt(dimMissHintString[2]);
					
					model.newGame(dim, miss, hint);
				}
				
				view.printTitle();
				view.printStatusScreen(selectedElem, model.gameState.getMaxMisses() - model.gameState.getNumOfMisses(), model.gameState.getMaxHints() - model.gameState.getNumOfHints());
				view.printGameScreen(model.gameState);
			}
			// Exit game
			else if(userInput.equalsIgnoreCase("exit"))
			{
				view.printTitle();
				view.printExitScreen();
				System.exit(0);
			}
			// Use hint
			else if(userInput.equalsIgnoreCase("hint"))
			{
				model.gameState.useHint();
				
				view.printTitle();
				view.printStatusScreen(selectedElem, model.gameState.getMaxMisses() - model.gameState.getNumOfMisses(), model.gameState.getMaxHints() - model.gameState.getNumOfHints());
				view.printGameScreen(model.gameState);
			}
			// Select cell
			else if(userInput.contains(" "))
			{
				rowColString	= userInput.split(" ");
				row				= Integer.parseInt(rowColString[0]);
				col				= Integer.parseInt(rowColString[1]);
				
				model.gameState.checkInput(row-1, col-1, selectedElem);
				
				view.printTitle();
				view.printStatusScreen(selectedElem, model.gameState.getMaxMisses() - model.gameState.getNumOfMisses(), model.gameState.getMaxHints() - model.gameState.getNumOfHints());
				view.printGameScreen(model.gameState);
			}
			// select value
			else
			{
				selectedElem 	= Integer.parseInt(userInput);
				
				view.printTitle();
				view.printStatusScreen(selectedElem, model.gameState.getMaxMisses() - model.gameState.getNumOfMisses(), model.gameState.getMaxHints() - model.gameState.getNumOfHints());
				view.printGameScreen(model.gameState);
			}
			
			// Check for goal state
			if(model.gameState.isSolved())
			{
				view.printVictoryScreen();
				
				userInput = controller.getInput();
				
				if(userInput.equalsIgnoreCase("n"))
				{
					view.printExitScreen();
					System.exit(0);
				}
				
				view.printTitle();
				view.printIntroScreen();
			}
						
			// Check for failure state
			if(model.gameState.getMaxMisses() - model.gameState.getNumOfMisses() < 1)
			{
				view.printTitle();
				view.printFailureScreen();
				
				userInput = controller.getInput();
				
				if(userInput.equalsIgnoreCase("n"))
				{
					view.printExitScreen();
					System.exit(0);
				}
				
				view.printTitle();
				view.printIntroScreen();
			}
		}
	}
}
