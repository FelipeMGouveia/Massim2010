package massim.competition2007;

import java.util.Random;

import massim.framework.SimulationConfiguration;
import massim.framework.simulation.simplesimulation.SimulationAgent;
import massim.framework.simulation.simplesimulation.WorldState;
import massim.simulationconnector.Simulation;

/**
 * 
 * This class is the mainclass of the GridSimulation. It provides methods to
 * handle the initialization, configuration, presimulationstep and
 * postsimulationstep.
 * 
 */

public class GridSimulation extends Simulation {
	GridSimulationWorldState state = null;
	GridSimulationConfiguration config;

	@Override
	public void configureSimulation(SimulationConfiguration c) {

		super.configureSimulation(c);
		config = (GridSimulationConfiguration) c;
	}

	@Override
	public void initializeSimpleSimulation() {

		SimulationAgent agents[] = this.getAgents();

		// config hand crafted
		if (config instanceof GridSimulationConfigurationHandCrafted) {

			GridSimulationConfigurationHandCrafted configHandcrafted = (GridSimulationConfigurationHandCrafted) config;
			state = new GridSimulationWorldState(configHandcrafted);

			for (int i = 0; i < agents.length; i++) {
				GridSimulationAgentState agentstate = (GridSimulationAgentState) agents[i]
						.getAgentState();
				agentstate.spreadAgent(state, configHandcrafted, i);
			}
		}

		// config not hand crafted
		else {

			state = new GridSimulationWorldState(config);

			for (int i = 0; i < agents.length; i++) {

				GridSimulationAgentState agentstate = (GridSimulationAgentState) agents[i]
						.getAgentState();
				agentstate.spreadAgent(state);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see massim.simulation.simplesimulation.AbstractSimulation#canContinue()
	 */
	public boolean isFinished() {

		return getSteps() >= config.maxNumberOfSteps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * massim.simulation.simplesimulation.AbstractSimulation#preSimulationStep()
	 */
	@Override
	public void preSimulationStep() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * massim.simulation.simplesimulation.AbstractSimulation#postSimulationStep
	 * ()
	 */
	@Override
	public void postSimulationStep() {

		state.currentStep = this.getSteps();

		// Spread Gold randomly
		Random r = new Random();
		Random r1 = new Random();

		if ((this.getSteps() % config.goldGenerationFrequency == 0)
				&& (Math.abs(r1.nextInt()) % 100 <= config.goldGenerationProbability)
				&& (config.sizex * config.sizey >= config.goldGenerationNumber
						+ state.numberOfAgents + state.numberOfGoldItems
						+ state.numberOfObstacles + 1)) {
			int i = 0;

			while (i < config.goldGenerationNumber) {

				int x = Math.abs(r.nextInt()) % state.sizex;
				int y = Math.abs(r.nextInt()) % state.sizey;

				if (state.board[x][y].noObject()) {
					state.board[x][y].gold = true;

					state.numberOfGoldItems += 1;
					i++;
				}
			}
		}
	}

	@Override
	public WorldState getSimpleSimulationState() {
		// TODO Auto-generated method stub
		return state;
	}

}
