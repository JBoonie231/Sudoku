import java.util.ArrayList;

/**
 * @author Joshua Boone
 * 
 * Data type that represents a single sudoku cell.
 * Holds the cell value and available domain.
 *
 */
public class Cell 
{
	private int					elem;
	private ArrayList<Integer>	unusedDomain;
	
	public Cell()
	{
		elem = -1;
	}

	//----------- Getters and Setters -----------\\
	
	/**
	 * @return the elem
	 */
	public int getElem() {
		return elem;
	}

	/**
	 * @param elem the elem to set
	 */
	public void setElem(int elem) {
		this.elem = elem;
	}

	/**
	 * @return the unusedDomain
	 */
	public ArrayList<Integer> getUnusedDomain() {
		return unusedDomain;
	}

	/**
	 * @param unusedDomain the unusedDomain to set
	 */
	public void setUnusedDomain(ArrayList<Integer> unusedDomain) {
		this.unusedDomain = unusedDomain;
	}
}
