package massim.simulationconnector;

public interface Cell {
	public boolean noObject();
	public boolean freeCell();
	public boolean agent();
	public boolean obstacle();
	public boolean canGetIn();
}
