package massim.competition2010;

import massim.framework.SimulationState;
import massim.framework.simulation.simplesimulation.SimulationStateImpl;
import massim.simulationconnector.SimulationXMLObserver;

import org.w3c.dom.Element;


/**
 * This XMLObserver provides the simulation statistics and save it into a file.
 * 
 */
public class GridSimulationXMLObserver extends SimulationXMLObserver {
	

	/**
	 * This constructor creates the document.
	 */
	public GridSimulationXMLObserver() {
		super();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see massim.Observer#notifySimulationState(massim.SimulationState)
	 */
	public void notifySimulationState(SimulationState state) {
		
		SimulationStateImpl simplestate = (SimulationStateImpl) state;
		GridSimulationWorldState worldState = (GridSimulationWorldState) simplestate.simulationState;
		
		String[] teamName = new String[2];
		Double[] averageScore = new Double[2];
		Integer[] cowsInCorral = new Integer[2];
		teamName[0] = worldState.teamName[0];
		teamName[1] = worldState.teamName[1];
		averageScore[0] = worldState.averageScore[0];
		averageScore[1] = worldState.averageScore[1];
		cowsInCorral[0] = worldState.cowsInCorral[0];
		cowsInCorral[1] = worldState.cowsInCorral[1];
		Element el_state = doc.createElement("state");
		el_state.setAttribute("Simulation",worldState.simulationName);
		el_state.setAttribute(teamName[0] + "-averageScore", averageScore[0].toString());
		el_state.setAttribute(teamName[1] + "-averageScore", averageScore[1].toString());
		el_state.setAttribute(teamName[0] + "-cowsInCorral", cowsInCorral[0].toString());
		el_state.setAttribute(teamName[1] + "-cowsInCorral", cowsInCorral[1].toString());
		
		el_state.setAttribute("Step", Integer.toString(simplestate.steps));
		el_state.setAttribute("SizeX", Integer.toString(worldState.sizex));
		el_state.setAttribute("SizeY", Integer.toString(worldState.sizey));
		el_state.setAttribute("Number-of-cows",
				Integer.toString(worldState.numberOfCows));
		el_state.setAttribute("Number-of-obstacles",
				Integer.toString(worldState.numberOfObstacles));
		el_state.setAttribute("Number-of-agents", Integer.toString(worldState.numberOfAgents));

		// AgentStates
		for (Integer i = 0; i < simplestate.agentStates.length; i++) {
			Element el_agent = doc.createElement("agent");
			GridSimulationAgentState agentstate = (GridSimulationAgentState) simplestate.agentStates[i];
			el_agent.setAttribute("Number", i.toString());
			el_agent.setAttribute("PositionX", agentstate.posx
					.toString());
			el_agent.setAttribute("PositionY", agentstate.posy
					.toString());
//			el_agent.setAttribute("Score", agentstate.score.toString());
			el_agent.setAttribute("Team", agentstate.team);
			el_agent.setAttribute("Name", agentstate.name);
			el_state.appendChild(el_agent);
		}
		el_root.appendChild(el_state);
		
	}
}
