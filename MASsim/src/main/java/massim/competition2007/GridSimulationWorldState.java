package massim.competition2007;

import java.util.Random;

import massim.simulationconnector.SimulationWorldState;

/**
 * This class describes the SimulationWorldState.
 *
 */
public class GridSimulationWorldState extends SimulationWorldState{

	private static final long serialVersionUID = 7294929408559440428L;
	public GridSimulationCell[][] board;
	public Integer numberOfGoldItems;
	public Integer maxNumberOfCarriedGoldItems;
	public Integer depotx;
	public Integer depoty;
	public Integer informationDistortionProbability;
	public Integer actionFailureProbability;
	public Integer maxActionFailureProbability;
	

	/**
	 * This constructor is for hand crafted simulations.
	 * @param config
	 */
	public GridSimulationWorldState(GridSimulationConfigurationHandCrafted config) {
		
		this.sizex = (config.sizex <= 100) ? config.sizex : 100;
		this.sizey = (config.sizex <= 100) ? config.sizex : 100;
		this.numberOfGoldItems = config.numberOfGoldItems;
		this.maxNumberOfCarriedGoldItems = config.maxNumberOfGoldItems;
		this.numberOfObstacles = config.numberOfObstacles;
		this.numberOfAgents = config.numberOfAgents;
		this.board = new GridSimulationCell[config.sizex][config.sizey];
		this.informationDistortionProbability = config.informationDistortionProbability;
		this.actionFailureProbability = config.actionFailureProbability;
		this.maxActionFailureProbability = config.maxActionFailureProbability;
		this.numberOfSteps = config.maxNumberOfSteps;
		String[] teams = {config.teamName0, config.teamName1};
		this.teamName = teams;

		// Initialize the board
		board = new GridSimulationCell[sizex][sizey];
		for (int i = 0; i < sizex; i++) {
			for (int j = 0; j < sizey; j++) {
				board[i][j] = new GridSimulationCell();
			}
		}
		
		// Spread Obstacles
		int i = 0;
		while (i < numberOfObstacles) {
			int x = config.obstaclePositionX[i];
			int y = config.obstaclePositionY[i];
			board[x][y].obstacle = true;
			i++;
		}
		
		// Spread Gold
		i = 0;
		while (i < numberOfGoldItems) {
			int x = config.GoldPositionX[i];
			int y = config.GoldPositionY[i];
			board[x][y].gold = true;
			i++;
		}
		
		// Spread Depot
		this.depotx = config.depotx;
		this.depoty = config.depoty;
		board[depotx][depoty].depot = true;
		
	}

	/**
	 * This constructor is for normal simulations.
	 * @param config
	 */
	public GridSimulationWorldState(GridSimulationConfiguration config) {
		
		this.sizex = (config.sizex <= 100) ? config.sizex : 100;
		this.sizey = (config.sizex <= 100) ? config.sizex : 100;
		this.numberOfGoldItems = config.numberOfGoldItems;
		this.maxNumberOfCarriedGoldItems = config.maxNumberOfGoldItems;
		this.numberOfObstacles = config.numberOfObstacles;
		this.numberOfAgents = config.numberOfAgents;
		this.board = new GridSimulationCell[config.sizex][config.sizey];
		this.informationDistortionProbability = config.informationDistortionProbability;
		this.actionFailureProbability = config.actionFailureProbability;
		this.maxActionFailureProbability = config.maxActionFailureProbability;
		this.numberOfSteps = config.maxNumberOfSteps;
		String[] teams = {config.teamName0, config.teamName1};
		this.teamName = teams;

		// Initialize the board
		board = new GridSimulationCell[sizex][sizey];
		for (int i = 0; i < sizex; i++) {
			for (int j = 0; j < sizey; j++) {
				board[i][j] = new GridSimulationCell();
			}
		}
		// Spread Obstacles
		Random r = new Random();
		int i = 0;
		while (i < numberOfObstacles) {
			int x = Math.abs(r.nextInt()) % (sizex - 1);
			int y = Math.abs(r.nextInt()) % (sizey - 1);
			if (board[x][y].noObject()) {
				board[x][y].obstacle = true;
				i++;
			}
		}
		// Spread Gold
		i = 0;
		while (i < numberOfGoldItems) {
			int x = Math.abs(r.nextInt()) % (sizex - 1);
			int y = Math.abs(r.nextInt()) % (sizey - 1);
			if (board[x][y].noObject()) {
				board[x][y].gold = true;
				i++;
			}
		}
		// Spread Depot
		i = 0;
		boolean placed = false;
		while (!placed) {
			int x = Math.abs(r.nextInt()) % (sizex - 1);
			int y = Math.abs(r.nextInt()) % (sizey - 1);
			if (board[x][y].noObject()) {
				board[x][y].depot = true;
				depotx = x;
				depoty = y;
				placed = true;
			}
		}
	}

}
