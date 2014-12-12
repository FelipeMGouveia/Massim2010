package massim.simulationconnector;


import java.io.Serializable;

/**
 * This class describes a single GridCell.
 *
 */
public abstract class SimulationCell implements Serializable, Cell {
	private static final long serialVersionUID = -4580023204848715935L;
	public boolean obstacle;
	public boolean agent;
	public String object = "";
	public String agentTeam;
	
}