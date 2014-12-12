package massim.competition2010;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import massim.framework.SimulationState;
import massim.framework.backup.BackupReader;
import massim.framework.backup.BackupWriter;
import massim.framework.rmi.RMI_Infor;
import massim.framework.simulation.simplesimulation.SimulationStateImpl;
import massim.server.Server;
import massim.simulationconnector.SimulationRMIXMLDocumentObserver;
import massim.visualization.MainPolicy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This RMIXMLDocumentObserver provides the simulation statistics for the webserver and the servermonitor.
 * 
 */
public class GridSimulationRMIXMLDocumentObserverWebInterface extends SimulationRMIXMLDocumentObserver{
	
	String outputFolder;
	private String configPath = Server.configurationFilenamePath + System.getProperty("file.separator") + "visualization" + System.getProperty("file.separator");
	private String configFile = configPath + "visualconfig.xml";
	
	
	private DocumentBuilder documentbuilder;
	private Document document;
	
	public GridSimulationRMIXMLDocumentObserverWebInterface() {
		super();
		Document doc = BackupReader.openFile(configFile);
		Element el = (Element) doc.getElementsByTagName("simulationOutput").item(0);
		outputFolder = el.getAttribute("path");
		
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		documentbuilder=null;
		try {
			documentbuilder=factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {throw new RuntimeException(e);}
		document =  documentbuilder.newDocument();
	}
	
	
	public void generateDocument(Document doc, SimulationState simstate) {
	
		SimulationStateImpl simplestate = (SimulationStateImpl) simstate;
		GridSimulationWorldState worldState = (GridSimulationWorldState) simplestate.simulationState;
		
		String[] teamName = new String[2];
		double [] averageScore = new double[2];
		int[] cowsInCorral = new int[2];
		teamName[0] = worldState.teamName[0];
		teamName[1] = worldState.teamName[1];
		averageScore[0] = worldState.averageScore[0];
		averageScore[1] = worldState.averageScore[1];
		cowsInCorral[0] = worldState.cowsInCorral[0];
		cowsInCorral[1] = worldState.cowsInCorral[1];
		
		
		String outputFile;
		String backgroundFile = "masSim-0.svg";
		if(RMI_Infor.FLASH_SERVER_ACTIVATED){
			simulationName  = worldState.simulationName;
			outputFile = outputFolder+simulationName+BackupWriter.file_sep+"masSim-"+simplestate.steps+".swf";
		}
		else{
			outputFile = MainPolicy.svgFile;
			backgroundFile= outputFolder+simulationName+BackupWriter.file_sep+"masSim-0.svg";
		}
		
			//doc = resetDocument();
			Element el_root = doc.getDocumentElement();
			//Element el_root = doc.createElement("statistics");
			el_root.setAttribute(teamName[0] + "averagescore", Double.toString(averageScore[0]));
			el_root.setAttribute(teamName[1] + "averagescore", Double.toString(averageScore[1]));
			el_root.setAttribute(teamName[0] + "cowsincorral", Integer.toString(cowsInCorral[0]));
			el_root.setAttribute(teamName[1] + "cowsincorral", Integer.toString(cowsInCorral[1]));
			el_root.setAttribute("simulation", simulationName);
			el_root.setAttribute("output",outputFile);
			el_root.setAttribute("output2",backgroundFile);
		//	doc.appendChild(el_root);
	}
}
