/**
 * @author Joshua Boone
 *
 *
 * This class handles all the output to the user.
 * 
 */
public class View 
{
	private String logo;
	private String intro;
	private String instructions;
	private String moveInstruction;
	private String emptyBoard;
	private int    selected = 1;
	
	public View() 
	{
		logo =  "\033[2J\033[33m ____                __          __                \n" +
				"/\\  _`\\             /\\ \\        /\\ \\               \n" +
				"\\ \\,\\L\\_\\  __  __   \\_\\ \\    ___\\ \\ \\/'\\   __  __  \n" +
				" \\/_\\__ \\ /\\ \\/\\ \\  /'_` \\  / __`\\ \\ , <  /\\ \\/\\ \\ \n" +
				"   /\\ \\L\\ \\ \\ \\_\\ \\/\\ \\L\\ \\/\\ \\L\\ \\ \\ \\\\`\\\\ \\ \\_\\ \\\n" +
				"   \\ `\\____\\ \\____/\\ \\___,_\\ \\____/\\ \\_\\ \\_\\ \\____/\n" +
				"    \\/_____/\\/___/  \\/__,_ /\\/___/  \\/_/\\/_/\\/___/\033[0m \n" +
				"(c) Boone 2015";
		        
		intro = "How To Play:                                                             \n" 	+
				"                                                                         \n" 	+ 
				"Add elements to the cells so that the board is completely filled and     \n" 	+
				"there are no duplicate elements per each row, column and region.         \n" 	+
				"                                                                         \n" 	+ 
				"To add an element, enter an element in the domain that you wish to       \n" 	+ 
				"insert.                                                                  \n" 	+  
				"Then enter the row and colomn you wish to add the element to.            \n" 	+  
				"                                                                         \n" 	+ 
				"You may use a hint to randomly add a given to the board by entering 'h'. \n" 	+ 
				"                                                                         \n" 	+ 
				"You may only guess incorrectly a certain amount of times before failure. \n" 	+ 
				"                                                                         \n" 	+ 
				"Once all the cells have been filled correctly, you win!                  \n"	+
				"('NEW' to start new game)                                                \n";
		
		instructions =	"Enter the domain of your game (a perfect square),                        \n"	+
						"the number of misses allowed,                                            \n"	+
						"and the number of hints.                                                 \n" 	+
						"Otherwise just press enter to use the default values of 9, 1000, 1000.	  \n" 	+
						"('EXIT' will terminate the program)";
		
		moveInstruction =	"Enter a single number within the domain to change selection. \n"	+
							"To input selected value, enter the row and column numbers.";
		
		emptyBoard = 	"    1   2   3   4   5   6   7   8   9    \n" +
						"   ___________________________________   \n" +
						"1 |   :   :   |   :   :   |   :   :   |  \n" +
						"  |...:...:...|...:...:...|...:...:...|  \n" +
						"2 |   :   :   |   :   :   |   :   :   |  \n" +
						"  |...:...:...|...:...:...|...:...:...|  \n" +
						"3 |   :   :   |   :   :   |   :   :   |  \n" +
						"  |___;___;___|___;___;___|___;___;___|  \n" +
						"4 |   :   :   |   :   :   |   :   :   |  \n" +
						"  |...:...:...|...:...:...|...:...:...|  \n" +
						"5 |   :   :   |   :   :   |   :   :   |  \n" +
						"  |...:...:...|...:...:...|...:...:...|  \n" +
						"6 |   :   :   |   :   :   |   :   :   |  \n" +
						"  |___;___;___|___;___;___|___;___;___|  \n" +
						"7 |   :   :   |   :   :   |   :   :   |  \n" +
						"  |...:...:...|...:...:...|...:...:...|  \n" +
						"8 |   :   :   |   :   :   |   :   :   |  \n" +
						"  |...:...:...|...:...:...|...:...:...|  \n" +
						"9 |   :   :   |   :   :   |   :   :   |  \n" +
						"  |___;___;___|___;___;___|___;___;___|  \n";
	}

	
	/**
	 * Display game logo.
	 */
	public void printTitle()
	{
		//Game logo
		System.out.println(logo);
	}
	
	/**
	 * Display game introduction.
	 */
	public void printIntroScreen()
	{
		System.out.println(intro);
	}
	
	/**
	 * Display game instructions
	 */
	public void printInstructionScreen()
	{
		System.out.println(instructions);
	}
	
	/**
	 * Displays the sudoku board to the screen.
	 * 
	 * @param gameState the gameState to print
	 */
	public void printGameScreen(GameState gameState)
	{
		String topKey  = "";
		String topRow  = "";
		String thisRow = "";
		String nextRow = "";
		
		// Special case for single value domain.
		if(gameState.getDimension() == 1)
		{
			topKey  = "    1 ";
			topRow  = "   ___";
			thisRow = "1 | " + gameState.getGameState()[0][0].getElem() + " |";
			nextRow = "  |___|";
		
			System.out.println(topKey);
			System.out.println(topRow);
			System.out.println(thisRow);
			System.out.println(nextRow);
			
			return;
		}
		
		// Iterate through the sudoku cells to build the display strings.
		for(int row = 0; row < gameState.getDimension(); row++)
		{
			for(int col = 0; col < gameState.getDimension(); col++)
			{
				// Build leftmost column of the board.
				if(col == 0)
				{
					// Build top row
					if(row == 0)
					{
						topKey = "    1 ";
						topRow = "   ___";
					}
					
					// Insert cell value and leftmost key.
					if(gameState.getDimension() < 10)
						thisRow = (row+1) + " | ";
					else
						thisRow = (row+1) + "| ";
					if(gameState.getGameState()[row][col].getElem() != -1)
					{
						if(gameState.getGameState()[row][col].getElem() == selected)
							thisRow += "\033[32m" + selected + "\033[0m";
						else
							thisRow += gameState.getGameState()[row][col].getElem();
						if(gameState.getGameState()[row][col].getElem() < 10)
							thisRow += " ";
					}
					else
						thisRow += "  ";
					
					if((row+1)/gameState.getSqrt()*gameState.getSqrt() == (row+1))
						nextRow = "  |___";
					else
						nextRow = "  |...";
				}
				// Build last column in board.
				else if(col == gameState.getDimension() - 1)
				{
					// Build top row
					if(row == 0)
					{
						topKey += "  " + (col+1) + " ";
						topRow += "____ ";
					}
					
					if(col/gameState.getSqrt()*gameState.getSqrt() == col)
						thisRow += "| ";
					else
						thisRow += ": ";
					
					// Insert cell value
					if(gameState.getGameState()[row][col].getElem() != -1)
					{
						if(gameState.getGameState()[row][col].getElem() == selected)
							thisRow += "\033[32m" + selected + "\033[0m";
						else
							thisRow += gameState.getGameState()[row][col].getElem();
						if(gameState.getGameState()[row][col].getElem() < 10)
							thisRow += " |";
						else
							thisRow += "|";
					}
					else
						thisRow += "  |";
					
					if((row+1)/gameState.getSqrt()*gameState.getSqrt() == (row+1))
						nextRow += ";___|";
					else
						nextRow += ":...|";
				}
				// Build middle columns.
				else
				{
					// Build top row
					if(row == 0)
					{
						topKey += "  " + (col+1) + " ";
						topRow += "____";
					}
					
					if(col/gameState.getSqrt()*gameState.getSqrt() == col)
						thisRow += "| ";
					else
						thisRow += ": ";
					
					// Insert cell value
					if(gameState.getGameState()[row][col].getElem() != -1)
					{
						if(gameState.getGameState()[row][col].getElem() == selected)
							thisRow += "\033[32m" + selected + "\033[0m";
						else
							thisRow += gameState.getGameState()[row][col].getElem();
						if(gameState.getGameState()[row][col].getElem() < 10)
							thisRow += " ";
					}
					else
						thisRow += "  ";
					
					if((row+1)/gameState.getSqrt()*gameState.getSqrt() == (row+1))
					{
						if(col/gameState.getSqrt()*gameState.getSqrt() == col)
							nextRow += "|___";
						else
							nextRow += ";___";
					}
					else
						if(col/gameState.getSqrt()*gameState.getSqrt() == col)
							nextRow += "|...";
						else
							nextRow += ":...";
				}
			}
			// Display the built rows
			if(row == 0)
			{
				System.out.println(topKey);
				System.out.println(topRow);
			}
			System.out.println(thisRow);
			System.out.println(nextRow);
		}
		System.out.println(moveInstruction);
	}
	
	/**
	 * Displays the current selscted value, number of misses left, and number of hints left.
	 * 
	 * @param value of the currently selected element
	 * @param misses number of misses left
	 * @param hints number of hints left
	 */
	public void printStatusScreen(int value, int misses, int hints)
	{
		selected = value;
		System.out.println(	" ------------------------------------------------- \n"	+
							"  Current Selected Value : \033[32m" + value  + "\033[0m          \n"	+
							"   Number of Misses left : " + misses + "          \n"	+
							"    Number of Hints left : " + hints  + "          \n"	+
							" ------------------------------------------------- \n");
	}
	
	/**
	 * Prints victory screen
	 */
	public void printVictoryScreen()
	{
		System.out.println("Congratulations!" +
						   "Keep playing? [y|n]");
	}
	
	/**
	 * Prints game over screen
	 */
	public void printFailureScreen()
	{
		System.out.println("Better luck next time!" +
							"Keep playing? [y|n]");
	}
	
	/**
	 * Prints a goodbye message.
	 */
	public void printExitScreen()
	{
		System.out.println("Goodbye!");
	}
	
	/**
	 * Prints an error message to the screen.
	 * 
	 * @param error to print
	 */
	public void printErrorScreen(String error)
	{
		 System.out.println("The program has encountered an error: " + error);
	}
}
