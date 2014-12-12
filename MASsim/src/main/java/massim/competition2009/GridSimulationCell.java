package massim.competition2009;


import massim.simulationconnector.SimulationCell;


/**
 * This class describes a single GridCell.
 *
 */
public class GridSimulationCell extends SimulationCell {
	
	private static final long serialVersionUID = -4580023204848715935L;
	public boolean cow = false;
	public boolean fence = false;
	public boolean open = false;
	public boolean switcher = false;
	public boolean canOpen = false;
	
	
	
	public int cowturn;
	public String agentTeam;
	boolean stable1 = false;
	boolean stable2 = false;
	public String cowID;
	
	
	/**
	 * This method checks whether the cell contains an object or not. 
	 * @return Returns false, if cow, obstacle or agent is in the cell, otherwise true.
	 */
	public boolean noObject()  {
		
		return (!cow && !obstacle && !agent && !fence && !switcher);
			
	}
	
	
	/**
	 * This method checks, if a cell is free to get in
	 * @param x Position x on the grid
	 * @param y Position y on the grid
	 * @return True in a such case, else false
	 */
	public boolean freeCell() {

		
	    if (!agent && !obstacle && !cow &&  !switcher 
	    		&& (!fence || (fence  && open) )) 
	    {
	    	
	    	return true;
	    	
		}
		
		else{
			return false;
		}
	}
	/**
	 * This method checks, if a cell is free to get in
	 * @param x Position x on the grid
	 * @param y Position y on the grid
	 * @return True in a such case, else false
	 */
	public boolean freeCellforCow() {

		
	    if (!agent && !obstacle && !cow && !switcher  
	    		&& (!fence || (fence  && open) )) 
	    {
	    	
	    	return true;
	    	
		}
		
		else{
			return false;
		}
	}

	public boolean agent() {
		// TODO Auto-generated method stub
		return this.agent;
	}


	public boolean canGetIn() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean obstacle() {
		// TODO Auto-generated method stub
		return this.obstacle;
	}
}