package massim.competition2006;
import massim.simulationconnector.SimulationCell;


/**
 * This class describes a single GridCell
 *
 */
public class GridSimulationCell extends SimulationCell {
	
	private static final long serialVersionUID = -4580023204848715935L;
	public boolean mark;
	public boolean gold;
	public boolean depot;
	public String markText;
	
	/**
	 * This method checks wether the cell contains an object or not. 
	 * @return Returns false, if gold, depot, obstacle or agent is in the cell, otherwise true.
	 */
	public boolean noObject()  {
		if (!gold && !depot && !obstacle && !agent) {
			return true;
		} else {
			return false;
		}
	}

	public boolean agent() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canGetIn() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean freeCell() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean obstacle() {
		// TODO Auto-generated method stub
		return false;
	}


	
}
