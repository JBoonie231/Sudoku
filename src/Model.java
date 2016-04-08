/**
 * @author Joshua Boone
 *
 *
 * Holds the instance of the game state
 *
 */
public class Model 
{
	public GameState gameState;
	
	public Model()
	{
		//gameState = newGame();
	}
	
	public GameState newGame()
	{ 
		gameState = new GameState();
		return gameState;
	}
	
	public GameState newGame(int dim, int mM, int mH)
	{ 
		gameState = new GameState(dim, mM, mH);
		return gameState;
	}
}
