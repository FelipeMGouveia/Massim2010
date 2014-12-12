package massim.simulationconnector;

import massim.framework.InitialStickyPerception;

/**
 * This class is the initial perception, which is send to the agent.
 *
 */
public class SimulationAgentInitialPerception implements InitialStickyPerception {
	
	private static final long serialVersionUID = -2107830240215498719L;
	
	public String opponent;
	public int steps;
	public int gsizex;
	public int gsizey;
}