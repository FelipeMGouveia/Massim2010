package massim.arrangements.network.SPSConnection;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.w3c.dom.Document;

/**
 * 
 * This interface regulates the 
 * communication with the SPS.
 * 
 */

/**
 * 
 * @author Marcus Petersen [nana.101]
 * 
 * @version 0.0.6
 * 
 */
public interface RMI_Interface extends Remote {

	/**
	 * 
	 * updateGameState() provides
	 * the RMI interface for the SPS.
	 * 
	 * @param WorldState (Map<String, String>)
	 * 
	 * @throws RemoteException
	 * 
	 */
	void updateGameState(Document doc) throws RemoteException;

}
