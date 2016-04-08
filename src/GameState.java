import java.util.ArrayList;
import java.util.Random;

/**
 * @author Joshua Boone
 * 
 * Data type that represents the sudoku puzzle.
 * Contains methods used to generate and change the state of the puzzle.
 * 
 * Uses constraint satisfaction and backtracking to generate the puzzle, and 
 * constraint propagation arc consistency to catch errors and check for unique solutions.
 * Once a unique solution is guaranteed, an answer key is generated for efficiency. 
 */
public class GameState 
{
	private Cell[][]			gameState;
	private int[][]				solution;
	
	private int					dimension;
	
	private int					maxMisses;
	private int					maxHints;
	private int					numOfMisses;
	private int					numOfHints;
	
	private ArrayList<Cell>		elemOrder;
	private boolean				solved;
	
	private int 				sqrt;
	private Random 				rand;
	private Cell				tempCell;
	private ArrayList<Integer>	tempIntList;
	
private View view = new View();
	
	/**
	 * Constructor
	 */
	public GameState()
	{
		rand		= new Random();
		
		dimension	= 9;    // Size defaults to standard sudoku puzzle size
		maxMisses	= 1000; // "Unlimited" misses
		maxHints	= 1000; // "Unlimited" hints
		numOfMisses	= 0;
		numOfHints	= 0;
		elemOrder	= new ArrayList<Cell>();
		solved		= false;
		
		gameState 	= new Cell[dimension][dimension];
		solution	= new int[dimension][dimension];
		
		sqrt		= (int) Math.sqrt(dimension);
		
		// Fill all cells with empty cells.
		for(int row = 0; row < gameState.length; row++)
		{
			for(int col = 0; col < gameState[row].length; col++)
			{
				gameState[row][col] = new Cell();
			}
		}
		
		generatePuzzle();
	}
	
	/**
	 * Constructor
	 * 
	 * @param dim dimension of the puzzle
	 * @param mM max number of misses
	 * @param mH max number of hints
	 */
	public GameState(int dim, int mM, int mH)
	{
		rand = new Random();
		
		// Check for proper dimension
		sqrt = (int) Math.sqrt(dim);
		if (sqrt*sqrt == dim)
			dimension	= dim;
		else
		{
			dimension	= 9;
			sqrt		= 3;
		}
		
		maxMisses		= mM;
		maxHints		= mH;
		numOfMisses		= 0;
		numOfHints		= 0;
		elemOrder		= new ArrayList<Cell>();
		solved			= false;
		
		gameState		= new Cell[dimension][dimension];
		solution		= new int[dimension][dimension];
		
		// Fill all cells with empty cells.
		for(int row = 0; row < gameState.length; row++)
		{
			for(int col = 0; col < gameState[row].length; col++)
			{
				gameState[row][col] = new Cell();
			}
		}
		
		generatePuzzle();
	}
	
	
	/**
	 * Checks if the given value correctly maps to the given cell,
	 * and if so, inputs the value.
	 * Otherwise, counts as a miss.
	 * 
	 * @param row  puzzle row
	 * @param col  puzzle column
	 * @param elem value to set
	 */
	public boolean checkInput(int row, int col, int elem)
	{
		if(gameState[row][col].getElem() == -1 && solution[row][col] == elem)
		{
			gameState[row][col].setElem(elem);
			solved = isGoalState();
			return true;
		}
		numOfMisses++;
		return false;
	}
	
	/**
	 * Fills in a cell for the user.
	 * 
	 */
	public boolean useHint()
	{
		for(int row = 0; row < gameState.length; row++)
		{
			for(int col = 0; col < gameState[row].length; col++)
			{
				if(gameState[row][col].getElem() == -1)
				{
					gameState[row][col].setElem(solution[row][col]);
					numOfHints++;
					solved = isGoalState();
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the puzzle has been completed.
	 */
	private boolean isGoalState()
	{
		for(int row = 0; row < gameState.length; row++)
		{
			for(int col = 0; col < gameState[row].length; col++)
			{
				if(gameState[row][col].getElem() != solution[row][col])
					return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Generates the puzzle randomly.
	 * Uses constraint satisfaction and backtracking to generate the puzzle, and 
	 * constraint propagation arc consistency to catch errors and check for unique solutions.
	 * Once a unique solution is guaranteed, an answer key is generated for efficiency.
	 */
	private void generatePuzzle()
	{	
		int tempRow;
		int tempCol;
		
		// Start generation at random cell
		tempRow  = rand.nextInt(dimension);
		tempCol  = rand.nextInt(dimension);
		tempCell = gameState[tempRow][tempCol];
		elemOrder.add(tempCell);
		
		tempIntList = getUsableDomain(tempRow, tempCol);
		tempCell.setElem(tempIntList.remove(rand.nextInt(tempIntList.size())));
		tempCell.setUnusedDomain(tempIntList);
		
		// Fill in given cells until puzzle has a unique solution
		int solvable = isSolvable();
		while(true)
		{
			// Puzzle is unsolvable
			if(solvable == -1)
			{
				// If current cell's usable domain options are exhausted, backtrack
				if(tempIntList.isEmpty())
				{
					elemOrder.remove(elemOrder.size()-1).setElem(-1);
					
					if(elemOrder.isEmpty())
					{
						System.out.println("ERROR: No more backtracking available.");
						System.exit(1);
					}
					
					tempCell = elemOrder.get(elemOrder.size()-1);
					tempIntList = tempCell.getUnusedDomain();
					if(tempIntList.isEmpty())
					{
						solvable = -1;
						continue;
					}
					tempCell.setElem(tempIntList.remove(rand.nextInt(tempIntList.size())));
					
					// For demo purposes.  1-16 domain takes too long to generate, but this will at least let the board be seen in progress.
					if(dimension >= 16)
						view.printGameScreen(this);
	
					solvable = isSolvable();
					continue;
				}
				// Otherwise, try another usable domain value
				else
				{
					tempCell.setElem(tempIntList.remove(rand.nextInt(tempIntList.size())));
					solvable = isSolvable();
					continue;
				}
			}
			// Puzzle can be solved, but does not have a unique solution
			if(solvable == 0)
			{
				while(gameState[tempRow][tempCol].getElem() != -1 || gameState[tempRow][tempCol].getUnusedDomain().size() == 1 || getUsableDomain(tempRow, tempCol).isEmpty())
				{
					tempRow  = rand.nextInt(dimension);
					tempCol  = rand.nextInt(dimension);
				}
				
				tempCell = gameState[tempRow][tempCol];
				elemOrder.add(tempCell);
				
				tempIntList = getUsableDomain(tempRow, tempCol);
				
				tempCell.setElem(tempIntList.remove(rand.nextInt(tempIntList.size())));
								
				solvable = isSolvable();
				continue;
			}
			// Puzzle has a unique solution
			if(solvable == 1)
			{
				break;
			}
			else
			{
				System.out.println("ERROR: Unexpected value.");
				System.exit(1);
			}
		}
		
	}
	
	/**
	 * Checks for errors and a unique solution.
	 * Uses constraint propagation arc consistency to catch errors and check for unique solutions.
	 * Once a unique solution is guaranteed, an answer key is generated for efficiency.
	 */
	private int isSolvable()
	{
		String				tempString;
		String[]			rowColStrings;
		int					rowFromString;
		int					colFromString;
		ArrayList<String>	arcQueue	= new ArrayList<String>();
		
		// Set all initial available domains
		for(int row = 0; row < gameState.length; row++)
		{
			for(int col = 0; col < gameState[row].length; col++)
			{
				if(gameState[row][col].getElem() == -1)
					gameState[row][col].setUnusedDomain(getUsableDomain(row, col));
			}
		}
		
		// Add all initial arcs
		for(int row = 0; row < dimension; row++)
		{
			for(int col = 0; col < dimension; col++)
			{
				//if(gameState[row][col].getElem() != -1)
					arcQueue.add(row + "|" + col);
			}
		}

		
		// Check arcs
		while(true)
		{
			// Check for complete generation
			if(arcQueue.isEmpty())
			{
				int retValue = 1;
				for(int row = 0; row < gameState.length; row++)
				{
					for(int col = 0; col < gameState[row].length; col++)
					{
						if(gameState[row][col].getElem() == -1)
						{
							if(gameState[row][col].getUnusedDomain().isEmpty())
								return -1;
							if(gameState[row][col].getUnusedDomain().size() > 1)
							{
								retValue = 0;
							}
							else if(gameState[row][col].getUnusedDomain().size() < 1)
							{
								return -1;
							}
							
							solution[row][col] = gameState[row][col].getUnusedDomain().get(0);
						}
						else
						{
							solution[row][col] = gameState[row][col].getElem();
						}
					}
				}
				return retValue;
			}
			
			// Check arcs
			if(!arcQueue.isEmpty())
			{
				tempString		= arcQueue.remove(0);
				rowColStrings	= tempString.split("\\|");
				rowFromString	= Integer.parseInt(rowColStrings[0]);
				colFromString	= Integer.parseInt(rowColStrings[1]);
				
				for(int i = 0; i < dimension; i++)
				{
					// If there is a conflict, backtrack
					if(gameState[rowFromString][colFromString].getElem() == -1)
					{
						if((i != colFromString  && gameState[rowFromString][colFromString].getUnusedDomain().size() == 1 && gameState[rowFromString][colFromString].getUnusedDomain().get(0) == gameState[rowFromString][i].getElem()) ||
						   (i != rowFromString  && gameState[rowFromString][colFromString].getUnusedDomain().size() == 1 && gameState[rowFromString][colFromString].getUnusedDomain().get(0) == gameState[i][colFromString].getElem()) ||
						   ((rowFromString != (rowFromString/sqrt*sqrt) + (i%sqrt) && colFromString != (colFromString/sqrt*sqrt) + (i/sqrt))  && gameState[rowFromString][colFromString].getUnusedDomain().size() == 1 && gameState[rowFromString][colFromString].getUnusedDomain().get(0) == gameState[(rowFromString/sqrt*sqrt) + (i%sqrt)][(colFromString/sqrt*sqrt) + (i/sqrt)].getElem()) ||
						   (i != colFromString  && gameState[rowFromString][colFromString].getUnusedDomain().size() == 1 && gameState[rowFromString][i].getUnusedDomain().size() == 1 && gameState[rowFromString][colFromString].getUnusedDomain().get(0) == gameState[rowFromString][i].getUnusedDomain().get(0).intValue()) ||
						   (i != rowFromString  && gameState[rowFromString][colFromString].getUnusedDomain().size() == 1 && gameState[i][colFromString].getUnusedDomain().size() == 1 && gameState[rowFromString][colFromString].getUnusedDomain().get(0) == gameState[i][colFromString].getUnusedDomain().get(0).intValue()) ||
						   ((rowFromString != (rowFromString/sqrt*sqrt) + (i%sqrt) && colFromString != (colFromString/sqrt*sqrt) + (i/sqrt))  && gameState[rowFromString][colFromString].getUnusedDomain().size() == 1 && gameState[(rowFromString/sqrt*sqrt) + (i%sqrt)][(colFromString/sqrt*sqrt) + (i/sqrt)].getUnusedDomain().size() == 1 && gameState[rowFromString][colFromString].getUnusedDomain().get(0) == gameState[(rowFromString/sqrt*sqrt) + (i%sqrt)][(colFromString/sqrt*sqrt) + (i/sqrt)].getUnusedDomain().get(0).intValue()))
						{
							return -1;
						}
					}
					else
					{
						if((i != colFromString  && gameState[rowFromString][i].getUnusedDomain().size() == 1 && gameState[rowFromString][i].getUnusedDomain().get(0) == gameState[rowFromString][colFromString].getElem()) ||
						   (i != rowFromString  && gameState[i][colFromString].getUnusedDomain().size() == 1 && gameState[i][colFromString].getUnusedDomain().get(0) == gameState[rowFromString][colFromString].getElem()) ||
						   ((rowFromString != (rowFromString/sqrt*sqrt) + (i%sqrt) && colFromString != (colFromString/sqrt*sqrt) + (i/sqrt))  && gameState[(rowFromString/sqrt*sqrt) + (i%sqrt)][(colFromString/sqrt*sqrt) + (i/sqrt)].getUnusedDomain().size() == 1 && gameState[(rowFromString/sqrt*sqrt) + (i%sqrt)][(colFromString/sqrt*sqrt) + (i/sqrt)].getUnusedDomain().get(0) == gameState[rowFromString][colFromString].getElem()))
						{
							return -1;
						}
						continue;
					}
					
					// Check row arcs
					// Make sure to not check against itself, and that value has not been assigned
					if(i != colFromString && gameState[rowFromString][i].getElem() != -1)
					{
						// Check row arc from row element to selected element
						if(gameState[rowFromString][colFromString].getUnusedDomain().size() == 1)
						{
							if(gameState[rowFromString][i].getUnusedDomain().remove((Integer)gameState[rowFromString][colFromString].getUnusedDomain().get(0)) && gameState[rowFromString][i].getUnusedDomain().size() == 1 && !arcQueue.contains(rowFromString + "|" + i))
							{
								arcQueue.add(rowFromString + "|" + i);
							}
						}
						// Check row arc from selected element to row element
						if(gameState[rowFromString][i].getUnusedDomain().size() == 1)
						{
							if(gameState[rowFromString][colFromString].getUnusedDomain().remove((Integer)gameState[rowFromString][i].getUnusedDomain().get(0)) && gameState[rowFromString][colFromString].getUnusedDomain().size() == 1 && !arcQueue.contains(rowFromString + "|" + colFromString))
							{
								arcQueue.add(rowFromString + "|" + colFromString);
							}
						}
					}
					// Check column arcs
					// Make sure to not check against itself, and that value has not been assigned
					if(i != rowFromString && gameState[i][colFromString].getElem() != -1)
					{
						// Check column arc from column element to selected element
						if(gameState[rowFromString][colFromString].getUnusedDomain().size() == 1)
						{
							if(gameState[i][colFromString].getUnusedDomain().remove((Integer)gameState[rowFromString][colFromString].getUnusedDomain().get(0)) && gameState[i][colFromString].getUnusedDomain().size() == 1 && !arcQueue.contains(i + "|" + colFromString))
							{
								arcQueue.add(i + "|" + colFromString);
							}
						}
						// Check column arc from selected element to column element
						if(gameState[i][colFromString].getUnusedDomain().size() == 1)
						{
							if (gameState[rowFromString][colFromString].getUnusedDomain().remove((Integer)gameState[i][colFromString].getUnusedDomain().get(0)) && gameState[rowFromString][colFromString].getUnusedDomain().size() == 1 && !arcQueue.contains(rowFromString + "|" + colFromString))
							{
								arcQueue.add(rowFromString + "|" + colFromString);
							}
						}
					}
					// Check region arcs
					// Make sure to not check against itself, and that value has not been assigned
					if (!((rowFromString/sqrt*sqrt) + (i%sqrt) == rowFromString && (colFromString/sqrt*sqrt) + (i/sqrt) == colFromString) && 
						gameState[(rowFromString/sqrt*sqrt) + (i%sqrt)][(colFromString/sqrt*sqrt) + (i/sqrt)].getElem() != -1)
					{
						// Check region arc from region element to selected element
						if(gameState[rowFromString][colFromString].getUnusedDomain().size() == 1)
						{
							if(gameState[(rowFromString/sqrt*sqrt) + (i%sqrt)][(colFromString/sqrt*sqrt) + (i/sqrt)].getUnusedDomain().remove((Integer)gameState[rowFromString][colFromString].getUnusedDomain().get(0)) && gameState[(rowFromString/sqrt*sqrt) + (i%sqrt)][(colFromString/sqrt*sqrt) + (i/sqrt)].getUnusedDomain().size() == 1 && !arcQueue.contains(((rowFromString/sqrt*sqrt) + (i%sqrt)) + "|" + ((colFromString/sqrt*sqrt) + (i/sqrt))))
							{
								arcQueue.add(((rowFromString/sqrt*sqrt) + (i%sqrt)) + "|" + ((colFromString/sqrt*sqrt) + (i/sqrt)));
							}
						}
						// Check region arc from selected element to region element
						if(gameState[(rowFromString/sqrt*sqrt) + (i%sqrt)][(colFromString/sqrt*sqrt) + (i/sqrt)].getUnusedDomain().size() == 1)
						{
							if (gameState[rowFromString][colFromString].getUnusedDomain().remove((Integer)gameState[(rowFromString/sqrt*sqrt) + (i%sqrt)][(colFromString/sqrt*sqrt) + (i/sqrt)].getUnusedDomain().get(0)) && gameState[rowFromString][colFromString].getUnusedDomain().size() == 1 && !arcQueue.contains(rowFromString + "|" + colFromString))
							{
								arcQueue.add(rowFromString + "|" + colFromString);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns a list of available domain values for the given cell based on its constraints.
	 */
	private ArrayList<Integer> getUsableDomain(int row, int col)
	{
		ArrayList<Integer>	usableDomain	= new ArrayList<Integer>();
		
		// Fill domain
		for(int i = 1; i <= dimension; i++)
		{
			usableDomain.add(i);
		}
		
		// Remove constraints from domain
		// Remove row and col constraints
		for(int i = 0; i < dimension; i++)
		{
			usableDomain.remove((Integer)gameState[row][i].getElem());
			usableDomain.remove((Integer)gameState[i][col].getElem());
		}
		// Remove region constraints
		for(int ir = (row/sqrt) * sqrt; ir < ((row/sqrt) + 1) * sqrt; ir++)
		{
			for(int ic = (col/sqrt) * sqrt; ic < ((col/sqrt) + 1) * sqrt; ic++)
			{
				usableDomain.remove((Integer)gameState[ir][ic].getElem());
			}
		}
		
		return usableDomain;
	}
	
	/**
	 * Originally used minimum remaining values, but switched to random to generate more interesting puzzles.
	 */
	private Cell getMostConstrained()
	{
		Cell				tempCellLoc;
		ArrayList<Integer>	tempIntListLoc;
		ArrayList<Cell>		mostConstrained = new ArrayList<Cell>();
		
		for(int row = 0; row < gameState.length; row++)
		{
			for(int col = 0; col < gameState[row].length; col++)
			{
				if(gameState[row][col].getElem() == -1)
				{
					tempCellLoc = gameState[row][col];
					tempIntListLoc = getUsableDomain(row, col);
					
					if(mostConstrained.isEmpty() || mostConstrained.get(0).getUnusedDomain().size() == tempIntListLoc.size())
					{
						tempCellLoc.setUnusedDomain(tempIntListLoc);
						mostConstrained.add(tempCellLoc);
					}
					else if(mostConstrained.get(0).getUnusedDomain().size() > tempIntListLoc.size())
					{
						mostConstrained.clear();
						tempCellLoc.setUnusedDomain(tempIntListLoc);
						mostConstrained.add(tempCellLoc);
					}
				}
			}
		}
		
		tempCellLoc = mostConstrained.get(rand.nextInt(mostConstrained.size()));
		return tempCellLoc;
	}
	

	//----------- Getters and Setters -----------\\
	
	/**
	 * @return the gameState
	 */
	public Cell[][] getGameState() {
		return gameState;
	}

	/**
	 * @param gameState the gameState to set
	 */
	public void setGameState(Cell[][] gameState) {
		this.gameState = gameState;
	}

	/**
	 * @return the solution
	 */
	public int[][] getSolution() {
		return solution;
	}

	/**
	 * @param solution the solution to set
	 */
	public void setSolution(int[][] solution) {
		this.solution = solution;
	}

	/**
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * @param dimension the dimension to set
	 */
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	/**
	 * @return the maxMisses
	 */
	public int getMaxMisses() {
		return maxMisses;
	}

	/**
	 * @param maxMisses the maxMisses to set
	 */
	public void setMaxMisses(int maxMisses) {
		this.maxMisses = maxMisses;
	}

	/**
	 * @return the maxHints
	 */
	public int getMaxHints() {
		return maxHints;
	}

	/**
	 * @param maxHints the maxHints to set
	 */
	public void setMaxHints(int maxHints) {
		this.maxHints = maxHints;
	}

	/**
	 * @return the numOfMisses
	 */
	public int getNumOfMisses() {
		return numOfMisses;
	}

	/**
	 * @param numOfMisses the numOfMisses to set
	 */
	public void setNumOfMisses(int numOfMisses) {
		this.numOfMisses = numOfMisses;
	}

	/**
	 * @return the numOfHints
	 */
	public int getNumOfHints() {
		return numOfHints;
	}

	/**
	 * @param numOfHints the numOfHints to set
	 */
	public void setNumOfHints(int numOfHints) {
		this.numOfHints = numOfHints;
	}

	/**
	 * @return the elemOrder
	 */
	public ArrayList<Cell> getElemOrder() {
		return elemOrder;
	}

	/**
	 * @param elemOrder the elemOrder to set
	 */
	public void setElemOrder(ArrayList<Cell> elemOrder) {
		this.elemOrder = elemOrder;
	}

	/**
	 * @return the solved
	 */
	public boolean isSolved() {
		return solved;
	}

	/**
	 * @param solved the solved to set
	 */
	public void setSolved(boolean solved) {
		this.solved = solved;
	}

	/**
	 * @return the sqrt
	 */
	public int getSqrt() {
		return sqrt;
	}

	/**
	 * @param sqrt the sqrt to set
	 */
	public void setSqrt(int sqrt) {
		this.sqrt = sqrt;
	}
	
}
