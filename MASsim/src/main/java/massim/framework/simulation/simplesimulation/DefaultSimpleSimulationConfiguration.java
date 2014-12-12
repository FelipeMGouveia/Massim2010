package massim.framework.simulation.simplesimulation;

public class DefaultSimpleSimulationConfiguration implements SimpleSimulationConfiguration {

	private AgentConfiguration[] agentConfigurations;
	
	public AgentConfiguration[] getAgentConfigurations() {
		return agentConfigurations;
	}
	public void setAgentConfigurations(AgentConfiguration[] newconfig) {
		agentConfigurations = newconfig;
	}

}
