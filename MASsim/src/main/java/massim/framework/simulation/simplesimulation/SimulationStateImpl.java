package massim.framework.simulation.simplesimulation;
import massim.framework.SimulationState;

public class SimulationStateImpl implements SimulationState {
	public WorldState simulationState;
	public AgentState[] agentStates;
	public int steps;
}
