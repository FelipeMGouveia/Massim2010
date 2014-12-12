package massim.competition2008;
import static massim.framework.util.DebugLog.LOGLEVEL_CRITICAL;
import static massim.framework.util.DebugLog.LOGLEVEL_NORMAL;
import static massim.framework.util.DebugLog.log;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import massim.competition2009.GridFlashXMLVitualization;
import massim.framework.SimulationConfiguration;
import massim.framework.backup.BackupWriter;
import massim.framework.rmi.RMI_Infor;
import massim.framework.simulation.simplesimulation.SimulationAgent;
import massim.framework.simulation.simplesimulation.WorldState;
import massim.server.Server;
import massim.simulationconnector.Simulation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * This class is the mainclass of the GridSimulation. It provides methods to
 * handle the initialization, configuration, presimulationstep and postsimulationstep.
 * 
 */

public class GridSimulation extends
		Simulation {
	
	GridSimulationWorldState state = null;
	GridSimulationConfiguration config = null;
	BackupWriter writer = null;
	GridFlashXMLVitualization visualization ;
	private boolean fenceResimulation = false;
	
	String backup_dir = "";
	
	//private boolean flashServerRuning = false; 
	/*
	 * (non-Javadoc)
	 * 
	 * @see massim.Simulation#configureSimulation(massim.SimulationConfiguration)
	 * 
	 */
	
	public GridSimulation(){

	}
	
	
	public void configureSimulation(SimulationConfiguration c) {
		
		super.configureSimulation(c);
		config = (GridSimulationConfiguration) c;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see massim.simulation.simplesimulation.AbstractSimulation#getSimpleSimulationState()
	 * 
	 */
	@Override
	public WorldState getSimpleSimulationState() {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see massim.simulation.simplesimulation.AbstractSimulation#initializeSimpleSimulation()
	 * 
	 */
	@Override
	public void initializeSimpleSimulation() {
		
		SimulationAgent agents[] = this.getAgents();
	//	versuchen agent und position in reihenfolge zu zuordnen
		
		// config hand crafted
		if (config instanceof GridSimulationConfigurationHandCrafted) {
			
			GridSimulationConfigurationHandCrafted configHandcrafted = (GridSimulationConfigurationHandCrafted) config;
			state = new GridSimulationWorldState(configHandcrafted);
					
			for (int i = 0; i < agents.length; i++) {
				GridSimulationAgentState agentstate = (GridSimulationAgentState) agents[i].getAgentState();
				agentstate.spreadAgent(state, configHandcrafted, i);
			}
		} 
		
		// config not hand crafted
		else {
			
			state = new GridSimulationWorldState(config);
			
			for (int i = 0; i < agents.length; i++) {
				
				GridSimulationAgentState agentstate = (GridSimulationAgentState) agents[i].getAgentState();
				agentstate.spreadAgent(state);
			}
		}
		
		
		if(!Server.recoverstep.equalsIgnoreCase("")){
			int step = Integer.parseInt(Server.recoverstep);
			this.setSteps(step);
		}
		
		backup_dir = Server.backuppath+BackupWriter.file_sep+state.simulationName+BackupWriter.getDate();
		this.connectToFlashServer();
	}

	/**
	 * try to connect to Flash Server
	 * if there is a connection, a stub will be made for sending of 
	 * simulation state to flash server 
	 */
	private void connectToFlashServer() {
		
		visualization = new GridFlashXMLVitualization(RMI_Infor.FLASH_SERVER, RMI_Infor.FLASH_PORT, RMI_Infor.FLASH_SERVICE);
		//server = vz104.in.tu-clausthal.de;
	
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see massim.simulation.simplesimulation.AbstractSimulation#canContinue()
	 * 
	 */
	public boolean isFinished() {
		
		return getSteps() >= config.maxNumberOfSteps;
	}
	
	@Override
	public void simulationStep(){
		
		cowSimulation();
		
		fenceSimulation();
	}
	

/**
 * this method calculates and makes movements of cows. When a cow is in stable, he will be removed 
 * from simulation. Score will be calculated for the team, who owns the stable.
 *  
 */
	private void cowSimulation() {
		/*
		 * movingcows vector gathers all of the cows, that able to move in this simulation step
		 * a cow can only move after 3 simulation steps
		 * 
		 */
		Vector<GridSimulationCowAgent> movingcows = new Vector<GridSimulationCowAgent>();
		
		for (int i=0;i<state.cows_manager.size();i++)
		{
			if (this.getSteps() % state.cowSpeed == state.cows_manager.get(i).cowTurn)
			{	
				GridSimulationCowAgent currCow = state.cows_manager.get(i);
				currCow.computeCowMove(state);
				movingcows.add(currCow);
			}
			else
			{
				state.cows_manager.get(i).direction = "skip";
			}
		}
		//calculate cow's directions and make movement
		this.makeCowMovement(movingcows);
	}

/***
 * this method simulates action of fences.
 * When there is an agent nearby the switch of fence, the fence will be opened.
 * and it will be closed in the other cases.
 * while closing of a fence all agents, cows will be pushed back to their old positions 
 * if they are on the same cell of fence cell.
 * 
 * if cows or agents can not be pushed back. A random position will be calculated 
 * for their teleporting 
 */
	private void fenceSimulation() {
		
		fenceResimulation = false;
		
		for(int i = 0; i< state.numberOfFences; i++){
			int x = state.switchX[i];
			int y = state.switchY[i];
			String direction = state.fenceDirection[i];
			int fenceLength = state.fenceLength[i];

			if(canOpen(x,y,direction)){
				openFence(x, y, direction, fenceLength);
			}

			// close fence
			else {
				closeFence(x, y, direction, fenceLength);
			}
			
		}
		if(fenceResimulation) fenceSimulation();
		
	}

	
	

	private boolean canOpen(int x, int y, String direction) {
		if (direction.equalsIgnoreCase("up") && (state.board[x - 1][y].agent
				|| state.board[x + 1][y].agent || state.board[x][y + 1].agent)) {
			return true;
		} else if (direction.equalsIgnoreCase("down")
				&& (state.board[x - 1][y].agent || state.board[x + 1][y].agent
				|| state.board[x][y-1].agent)) {
			return true;
		} else if (direction.equalsIgnoreCase("left")
				&& (state.board[x][y + 1].agent || state.board[x][y - 1].agent
				|| state.board[x + 1][y].agent)) {
			return true;
		} else if (direction.equalsIgnoreCase("right")
				&& (state.board[x - 1][y].agent || state.board[x][y + 1].agent
				|| state.board[x][y - 1].agent)) {
			return true;
		}
		return false;
	}


	private void closeFence(int x, int y, String direction, int fenceLength) {
		
		int j = 1;

		if (direction.equalsIgnoreCase("left")) {
			
			while ((x - j >= 0) && j < fenceLength+1) {
				
					shiftCowAgent(x-j,y,direction,"");	
				state.board[x - j][y].open = false;
				j +=1;
			}
		}
		else if (direction.equalsIgnoreCase("right")) {

			while ((x + j < state.sizex) && j < fenceLength+1) {
				shiftCowAgent(x+j, y,direction,"");
				state.board[x + j][y].open = false;

				j +=1;
			}

		}

		else if (direction.equalsIgnoreCase("up")) {

			while (y - j >= 0 && j < fenceLength+1) {
					
					shiftCowAgent(x, y-j,direction,"");
					state.board[x][y-j].open = false;

					j +=1;
			}

		}
		else if (direction.equalsIgnoreCase("down")) {
			
			while (y + j < state.sizey && j < fenceLength+1) {
				
				shiftCowAgent(x, y+j,direction,"");
				state.board[x][y+j].open = false;
				j +=1;
			}

		}	
		
	}

	private void openFence(int x, int y, String direction, int fenceLength) {
			
			int j = 1;
			if (direction.equalsIgnoreCase("left")) {
				
				while ((x - j >= 0) && j < fenceLength+1) {
					
					state.board[x - j][y].open = true;
					
					j +=1;
				}
			}
			else if (direction.equalsIgnoreCase("right")) {

				while ((x + j < state.sizex) && j < fenceLength+1) {
					
					state.board[x + j][y].open = true;

					j +=1;
				}
			}
			else if (direction.equalsIgnoreCase("up")) {

				while (y - j >= 0 && j < fenceLength+1) {
					
					state.board[x][y-j].open = true;
					j +=1;
				}
			}
			else if (direction.equalsIgnoreCase("down")) {
				
				while (y + j < state.sizey && j < fenceLength+1) {
					
				
					state.board[x][y+j].open = true;
					j +=1;
				}

			}
		}

/**
 * return true if :
 * there is no cow or agent in position x, y.
 * there is a cow or agent in position x, y and he can be pushed back to his old position
 * 
 * there is a cow or agent in position x, y and he can not be pushed back to his old position 
 * When the agent moves along the direction of fence. he will be pushed on the "up" or the "left" side
 * @param x
 * @param y
 * @return
 */
	private boolean shiftCowAgent(int x, int y,String switch_dir,String check) {
		
		//shilf agent
		Vector<String> l_r = new Vector<String>();
		l_r.add("left"); l_r.add("right");
		l_r.add("west"); l_r.add("east");
		
		Vector<String> u_d = new Vector<String>();
		u_d.add("up"); u_d.add("down");
		u_d.add("north"); u_d.add("south");
		
		if(state.board[x][y].agent){
			
			fenceResimulation  = true;
			SimulationAgent[] agents = getAgents();
			int a=x,b=y;
			for(int z = 0 ; z< agents.length; z++){
				
				GridSimulationAgentState a_state = (GridSimulationAgentState) agents[z].getAgentState();
				
				if(a_state.posx == x && a_state.posy == y){
					String dir = a_state.direction;
				
					// charge direction of agent when he moves along direction of fence
					// and can not be pushed back to his direction
					if(l_r.contains(dir)&& l_r.contains(switch_dir)){
						if(dir.equalsIgnoreCase("left") && !state.board[a+1][b].noObject() )
							dir = "up";
						if(dir.equalsIgnoreCase("right") && !state.board[a-1][b].noObject() )
							dir = "down";			
					}
					if(u_d.contains(dir)&& u_d.contains(switch_dir)){
						if(dir.equalsIgnoreCase("up") && !state.board[a][b+1].noObject() )
							dir = "left";
						if(dir.equalsIgnoreCase("down") && !state.board[a][b-1].noObject() )
							dir = "right";
					}
					//charge position of agent
					int[] pointXY = getNewPosition(a,b,dir);
					
					a = pointXY[0];
					b = pointXY[1];
					
					//update position 
					if(state.board[a][b].freeCell()){
						
						shiftAgent(a_state, x, y, a, b);
						return true;
						
					}
					//random push when agent can not be pushed to his old positition
					else {
					
						int[] free_pos = findFreePos(x,y,state);
						shiftAgent(a_state,x,y,free_pos[0],free_pos[1]);
						return true;	
					}
				}
			}	
		}
		//shift cow
		else if(state.board[x][y].cow){
			
			int[] posXY;
			for(int z = 0 ; z< state.cows_manager.size(); z++){
				
				GridSimulationCowAgent cow = state.cows_manager.get(z);
				
				if(cow.posx == x && cow.posy == y){	
					 posXY= getNewPosition(x,y, cow.old_direction);
					int a = posXY[0];
					int b = posXY[1];
					
					if(state.board[a][b].noObject()){
						
						shiftCow(cow,x,y,a,b);
					}
					else{
						posXY = findFreePos(x, y, state);
						
						shiftCow(cow, x, y, posXY[0], posXY[1]);	
					}	
				}	
			}	
		}
		return true;		
	}

	private void shiftCow(GridSimulationCowAgent cow, int fromX, int fromY, int toX, int toY) {
		cow.posx = toX;
		cow.posy = toY;
		state.board[fromX][fromY].cow = false;
		state.board[fromX][fromY].cowID = null;
		
		state.board[toX][toY].cow = true;
		state.board[toX][toY].cowID = cow.ID;
	
}


	private int[] getNewPosition(int x, int y, String dir) {
		
		//make shift process
		if(dir.equalsIgnoreCase("left")||dir.equalsIgnoreCase("west")){
			x=x+1;
		}
		else if(dir.equalsIgnoreCase("upleft")||dir.equalsIgnoreCase("northwest")){
			x= x+ 1; y = y+1;
		}
		else if(dir.equalsIgnoreCase("downleft") || dir.equalsIgnoreCase("southwest")){
			x = x+1 ; y = y-1;
		}
		else if(dir.equalsIgnoreCase("right") || dir.equalsIgnoreCase("east")){
			x = x-1;
		}
		else if(dir.equalsIgnoreCase("upright")||dir.equalsIgnoreCase("northeast")){
			x = x-1; y = y+1;
		}
		else if(dir.equalsIgnoreCase("downright") || dir.equalsIgnoreCase("southeast")){
			x = x - 1 ; y = y-1;
		}
		else if(dir.equalsIgnoreCase("up")||dir.equalsIgnoreCase("north")){
			y = y+1;
		}
		else if(dir.equalsIgnoreCase("down")||dir.equalsIgnoreCase("south")){
			y = y-1;
		}
		
	int[] point = {x,y};	
	return point;
}


	private void shiftAgent(GridSimulationAgentState a_state, int fromx, int fromy, int tox, int toy) {
		a_state.posx = tox;
		a_state.posy = toy;
		state.board[fromx][fromy].agent = false;
		state.board[fromx][fromy].agentTeam = null;
		
		state.board[tox][toy].agent = true;
		state.board[tox][toy].agentTeam = a_state.team;
	
}

/**
 * return position of a free cell around agent. when there is no free cell 
 * return a random position of a free cell on the grid
 * @param x
 * @param y
 * @param state
 * @return
 */
	private int[] findFreePos(int x, int y, GridSimulationWorldState state) {
		int[] pos = new int [2];
		Vector<int[]> v = new Vector<int[]>();
		
		for(int a = -1; a <2; a++){
			for(int b = -1; b<2; b++){
				if(state.board[a+x][b+y].noObject()){
					pos[0] = a+x ;
					pos[1] = b+y;
					v.add(pos);
				}
			}
		}
		if(v.size() != 0){
			Collections.shuffle(v);
			return v.get(0);
		}
		while(true){
			pos[0] =new Random().nextInt(state.sizex);
			pos[1] = new Random().nextInt(state.sizey);
			if(state.board[pos[0]][pos[1]].freeCell())
				break;
		}
	
		return pos;
}


	public GridSimulationAgentState getAgentState(int x, int y){
		SimulationAgent[] agents = this.getAgents();
		for(int i = 0 ; i< agents.length;i ++){
			GridSimulationAgent a = (GridSimulationAgent) agents[i];
			GridSimulationAgentState s = (GridSimulationAgentState) a.getAgentState();
	
			if(s.posx == x && s.posy == y){
				return s;
			}
		}
		return null;
	}
	/**
	 * calculate score 
	 * save this State of Game and try to connect to the Flash-Server
	 * 
	 */
	@Override
	public void postSimulationStep() {
		//calculate score  and remove cows in stable
		state.currentStep = this.getSteps();
		
		state.teamScore[0] = 0;
		state.teamScore[1] = 0;
		Iterator<GridSimulationCowAgent> iter = state.cows_manager.iterator();
		
		while(iter.hasNext()){
			
			GridSimulationCowAgent cow = iter.next();
			
			if(state.board[cow.posx][cow.posy].stable1){
				
				state.teamScore[0] +=1;	
			}
			else if(state.board[cow.posx][cow.posy].stable2){
				
				state.teamScore[1] +=1;
			}
		}
		
		//save this simulation step in backup folder
		if(!Server.backuppath.equals(""))
			this.backupSimulationStep();
		
	
	
		if(!RMI_Infor.FLASH_SERVER.equalsIgnoreCase("")){
			
			if(visualization.connectToFlashServer(RMI_Infor.FLASH_SERVER, RMI_Infor.FLASH_PORT, RMI_Infor.FLASH_SERVICE)){
				Document doc= BackupWriter.createDocument();
				visualization.generateDocument(doc, getSimulationState());
				visualization.sendDocument(doc,this.getSimulationState());
		
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else{
				log(LOGLEVEL_CRITICAL,"ERROR: Can not connect to FlashServer. HOST "+RMI_Infor.FLASH_SERVER);
				
			}
		}		
		log(LOGLEVEL_NORMAL,"Simulation at step: "+this.getSteps());
	}
	
	private void makeCowMovement(Vector<GridSimulationCowAgent> movingcows){
		/*
		 *  a cow is chosen randomly and  performs the movement
		 */
		Collections.shuffle(movingcows);
		for(int i = 0; i < movingcows.size(); i++){
			
			GridSimulationCowAgent cow = movingcows.get(i);
			
			int oldPosx = cow.posx;
			int oldPosy = cow.posy;
			
			int sizex = state.sizex;
			int sizey = state.sizey;
			
			//make movement
			if ((cow.direction.equalsIgnoreCase("up") || cow.direction.equalsIgnoreCase("north"))
					&& !collisionCheck(cow.posx, cow.posy - 1,sizex, sizey,state.board)) {
				cow.posy -= 1;
			} 
			else if ((cow.direction.equalsIgnoreCase("down") || cow.direction.equalsIgnoreCase("south"))
					&& !collisionCheck(cow.posx, cow.posy + 1,
							sizex, sizey,
							state.board)) {
				cow.posy += 1;
			}
			else if ((cow.direction.equalsIgnoreCase("left") || cow.direction.equalsIgnoreCase("west"))
					&& !collisionCheck(cow.posx - 1, cow.posy,
							sizex, sizey,
							state.board)) {
				cow.posx -= 1;
			}
			else if ((cow.direction.equalsIgnoreCase("right") || cow.direction.equalsIgnoreCase("east"))
					&& !collisionCheck(cow.posx + 1, cow.posy,
							sizex, sizey,
							state.board)) {
				cow.posx += 1;
			}
			else if((cow.direction.equalsIgnoreCase("upright") || cow.direction.equalsIgnoreCase("northeast"))
					&& !collisionCheck(cow.posx+1, cow.posy-1, sizex, sizey, state.board))
			{
				cow.posx +=1;
				cow.posy -=1;
			}
			else if((cow.direction.equalsIgnoreCase("upleft") || cow.direction.equalsIgnoreCase("northwest"))
					&& !collisionCheck(cow.posx-1, cow.posy-1, sizex, sizey, state.board))
			{
				cow.posx -=1;
				cow.posy -=1;
			}
			else if((cow.direction.equalsIgnoreCase("downright") || cow.direction.equalsIgnoreCase("southeast"))
					&& !collisionCheck(cow.posx+1, cow.posy+1, sizex, sizey, state.board))
			{
				cow.posx +=1;
				cow.posy +=1;
			}
			else if((cow.direction.equalsIgnoreCase("downleft") || cow.direction.equalsIgnoreCase("southwest"))
					&& !collisionCheck(cow.posx-1, cow.posy+1, sizex, sizey, state.board))
			{
				cow.posx -=1;
				cow.posy +=1;
			}
			
			
			else if (cow.direction.equalsIgnoreCase("skip") ){ 
				//do nothings
			}
			// update worldstate
			state.board[oldPosx][oldPosy].cow = false;
			state.board[oldPosx][oldPosy].cowID = null;
			
			state.board[cow.posx][cow.posy].cowID = cow.ID;
			state.board[cow.posx][cow.posy].cow = true;
			String newDir= getCowDirection(cow.posx, cow.posy, oldPosx, oldPosy);
			if(!newDir.equalsIgnoreCase("")){
				cow.old_direction = newDir;
			}
		}
		
	}
	/**
	 * return one of the following directions: left, upleft,upright, right, 
	 * downleft,downright and "";
	 *  
	 * @param posx actual x position of agent 
	 * @param posy actual y position of agent
	 * @param oldPosx old x position of agent
	 * @param oldPosy old y position of agent
	 * @return
	 */
	private String getCowDirection(Integer posx, Integer posy,
				Integer oldPosx, Integer oldPosy) {
			String direction = "";
			
			
			if(posy < oldPosy){
				direction = direction+"up";
			}
			else if(posy > oldPosy){
				direction = direction+"down";
			}
			
			if(posx < oldPosx){
				direction = direction+"left";	
			}
			else if(posx > oldPosx){
				direction = direction+"right";
				
			}
			return direction;
		}
	/**
	 * This method checks wether the agent collides with an other object.
	 * 
	 * @param x
	 *            GridPosition x
	 * @param y
	 *            GridPosition y
	 * @param sizex
	 *            GridSize x
	 * @param sizey
	 *            GridSize y
	 * @param board
	 *            Grid
	 * @return
	 */
	private boolean collisionCheck(int x, int y, int sizex, int sizey,
			GridSimulationCell[][] board) {
		
		if(x >= 0 && x < sizex && y >=0 && y < sizey && board[x][y].freeCell())
			return false;
		else 
			return true;
	}
	
		/**
		 * Save this simulation step in backup folder. There are 2 backup file types 
		 * 1> backup file for flash server in folder: backup/
		 * 2> backup file for recovery of this simulation in folder: backup/recovery_serverconfig/
		 * 
		 */
		public Document backupSimulationStep(){
				//Backup for flash server	
			Document doc= BackupWriter.createDocument();
			Document doc2 = null;
			if(!Server.recoverstep.equalsIgnoreCase("")){
				
				doc = Server.backup_serverconfig;
				Element conf = doc.getDocumentElement();
				String backup_dir = conf.getAttribute("current_backuppath");
				String filename = this.getSteps()+"_"+BackupWriter.getDate()+".xml";
				new GridFlashXMLVitualization().generateDocument(doc,this.getSimulationState());
				BackupWriter.write(doc, backup_dir, filename);
				
				doc2 = Server.backup_serverconfig;
				doc2 = this.updateServerConfig(doc2,backup_dir);
				String recoveryDir = Server.recoveryDir;
				BackupWriter.write(doc2,recoveryDir,Server.recoverFile);
				String file = recoveryDir+BackupWriter.file_sep+Server.recoverFile;
				if(this.isFinished()){
					removeConfigFile(file);
				}
				
			}
			else {
				String filename = this.getSteps()+"_"+BackupWriter.getDate()+".xml";
				new GridFlashXMLVitualization().generateDocument(doc,this.getSimulationState());	
				BackupWriter.write(doc, backup_dir, filename);				
				//backup serverconfig
				
			
				//backup serverconfig
				doc2 = Server.backup_serverconfig;
				doc2 = this.updateServerConfig(doc2,backup_dir);
				String recoveryDir = Server.recoveryDir;
				BackupWriter.write(doc2,recoveryDir,Server.recoverFile);
				String file = recoveryDir+BackupWriter.file_sep+Server.recoverFile;
			
				if(this.isFinished())
					
					removeConfigFile(file);
			
			}
			return doc;
		}
		
		private void removeConfigFile(String file) {
			File rm = new File(file);
			if(rm.exists())
				rm.delete();
			
		}
		private Document updateServerConfig(Document doc,String current_backupdir) {
			
			Element conf = doc.getDocumentElement();
			conf.setAttribute("current_backuppath",current_backupdir);
			updateAgentPosition(doc);
			
			//update cow's position
			Element cowx = null;
			Element cowy = null;
			
			NodeList nl = doc.getElementsByTagName("array");
			
			for(int i = 0 ; i<nl.getLength(); i ++){
				
				Element e = (Element) nl.item(i);
				String meta_name = e.getAttribute("meta:name");
				
				if(meta_name.equalsIgnoreCase("cowPositionX")){
					cowx = e;
				}
				else if(meta_name.equalsIgnoreCase("cowPositionY")){
					cowy = e;
				}
			}
			Vector<GridSimulationCowAgent> cow_manager = state.cows_manager;	
			for(int i = 0 ; i< cow_manager.size();i++){
				GridSimulationCowAgent cow = cow_manager.get(i);
				
				cowx.setAttribute("item"+i, ""+cow.posx);
				cowy.setAttribute("item"+i, ""+cow.posy);
			}
			return doc;
		}
		
}
	

