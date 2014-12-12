package massim.simulationconnector;

import static massim.framework.util.DebugLog.LOGLEVEL_CRITICAL;
import static massim.framework.util.DebugLog.LOGLEVEL_ERROR;
import static massim.framework.util.DebugLog.LOGLEVEL_NORMAL;
import static massim.framework.util.DebugLog.log;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import massim.arrangements.network.SPSConnection.RMI_Interface;
import massim.framework.SimulationState;

import org.w3c.dom.Document;

public abstract class FlashXMLVisualization {
	protected Document doc1;
	String servername ;
	int port;
	String service;
	RMI_Interface stub = null;
	SimulationState simstate ;
	public boolean flashServerRunning = false;
	
	public FlashXMLVisualization(String server , int port, String service) {
		this.servername = server ;
		this.port = port;
		this.service = service;
	}
	public FlashXMLVisualization(){}
/**
 * generate doc Document and 
 * send state of simulation to Flash Server running
 * @param doc2
 * 
 */
	public  void sendDocument(Document doc,SimulationState state){
		doc1 = doc;	
		new Thread(){
			public void run(){
				try {
					//	doc1 = BackupWriter.createDocument();
					//	this.generateDocument(doc1,state);
						log(LOGLEVEL_NORMAL,"SENDING xml to flash server ...." );
						stub.updateGameState(doc1);
						
					} catch (RemoteException e) {
						log(LOGLEVEL_CRITICAL,"Cannot send document: RemoteException...");
						flashServerRunning = false;
					//	e.printStackTrace();
					}
			}
		}.start();
	}
	
	public boolean connectToFlashServer(String server,int port,String service){	

		try {
			//neue version
			Registry RMIRegistry = LocateRegistry.getRegistry(server,port);
			stub = (RMI_Interface) RMIRegistry.lookup(service);
			flashServerRunning = true;
			return true;
			
		} catch (AccessException e) {
			flashServerRunning = false;
			log(LOGLEVEL_ERROR,"AccessException at Flash Server !!!...");
			//e.printStackTrace();
		} catch (RemoteException e) {
			log(LOGLEVEL_ERROR,"RemoteException at Flash Server !!!...");
			flashServerRunning = false;
			//e.printStackTrace();
		} catch (NotBoundException e) {
			log(LOGLEVEL_ERROR,"NotBoundException at Flash Server!!!...");
			flashServerRunning = false;
			//e.printStackTrace();
		};	
		flashServerRunning = false;
		log(LOGLEVEL_ERROR,"Flash Server is not running!!!...");
		return false;
	}
	
	public abstract void generateDocument(Document doc ,SimulationState simstate);
}
