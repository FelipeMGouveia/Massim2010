package massim.competition2009;

import massim.simulationconnector.SimulationAgentInitialPerception;

/**
 * This class is the initial perception, which is send to the agent.
 *
 */
public class GridSimulationAgentInitialPerception extends SimulationAgentInitialPerception {
	
	private static final long serialVersionUID = -2107830240215498719L;
	//extra attribute for this simulation
	public int corralx0;
	public int corraly0;
	public int corralx1;
	public int corraly1;
	public int lineOfSight;
}