package massim.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Skeleton {
//
//	public  static String skeleton = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\n"
//	+		"<!-- launch-sync-type = timer or key -->"
//	+		"<conf  tournamentname=\"GridSimulation\" tournamentmode=\"0\" launch-sync-type=\"key\" time-to-launch=\"10000\" reportpath=\".\" backuppath=\"backup\">"+"\n"
//
//	+"<simulation-server port=\"12300\" backlog=\"10\"/>"+"\n"
//	+"<visualization  flashserver=\"vz104.in.tu-clausthal.de\" flashport=\"1099\" flashservice=\"WebServer-SPSInterface\"/>"+"\n"
//
//	+"<match>"+"\n"
//	+	"<simulation"+"\n"
//	+		   	"id=\"\""+"\n"
//	+		   	"simulationclass=\"GridSimulation\""+"\n"
//	+		   	"configurationclass=\"GridSimulationConfigurationHandCrafted\""+"\n"
//	+		  	"rmixmlobserver=\"GridSimulationRMIXMLDocumentObserver\""+"\n"
//	+		  	"rmixmlobsserverhost=\"localhost\""+"\n"
//	+		  	"rmixmlobsserverport=\"1097\""+"\n"
//	+		   	"visualisationobserver=\"GridSimulationVisualizationObserver\""+"\n"
//	+		 	"rmiobserver=\"\""+"\n"
//	+		 	"xmlobserver=\"\"" +"\n"
//	+		 	"xmlstatisticsobserver=\"\""+"\n"
//	+		   	"xmlobserverpath=\".\""+"\n"
//	+		   	"file-simulationlog=\"./log\">"+"\n"
//
//	+		"<configuration"+"\n"
//	+			      "xmlns:meta=\"http://www.tu-clausthal.de/\""+"\n"
//	+			      "sizex=\"\""+"\n"
//	+			      "sizey=\"\""+"\n"
//	+			      "maxNumberOfSteps=\"1000\""+"\n"
//	+			      "numberOfAgents=\"\""+"\n"
//	+			      "numberOfObstacles=\"\""+"\n"
//	+			      "numberOfCows=\"\""+"\n"
//	+			      "numberOfFences = \"\""+"\n"
//	+			      "cowSpeed=\"3\""+"\n"
//	+			      "agentSpeed=\"1\""+"\n"
//	+			      "lineOfSight=\"3\""+"\n"
//	+			      "cowSight=\"5\""+"\n"
//	+			      "cowPrivateField=\"3\""+"\n"
//	+			      "cowAttractedWeight=\"10\""+"\n"
//	+			      "cowScareWeight =\"-7\""+"\n"
//	+			      "agentWeight=\"-200\""+"\n"
//	+			      "emptyWeight=\"3\""+"\n"
//	+			      "obstacleWeight=\"-4\""+"\n"
//	+			      "weight = \"0.3\""+"\n"
//	+			      "epsilon = \"0.2\""+"\n"
//	+			      "htaccess = \"1\"" +"\n"
//	+			      ">"+"\n"
//
//	+		"</configuration>"+"\n"
//
//	+		"<agents>"+"\n"
//				+"<agent agentclass=\"GridSimulationAgent\" agentcreationclass=\"GridSimulationAgentParameter\" team=\"\"><configuration/></agent>"+"\n"
//	+		"</agents>"+"\n"
//
//	+	"</simulation>"+"\n"
//
//	+"</match>"+"\n"
//
//	+"<accounts>"+"\n"
//	+	"<actionclassmap name=\"Grid\">"+"\n"
//	+		"<actionclass id=\"action\" class=\"GridSimulationAgentAction\"/>"+"\n"
//	+		"<actionclass id=\"invalid\" class=\"massim.InvalidAction\"/>"+"\n"
//	+	"</actionclassmap>"+"\n"
//	+	"<account actionclassmap=\"Grid\" defaultactionclass=\"GridSimulationAgentAction\" timeout=\"8000\" auxtimeout=\"500\" maxpacketlength=\"65536\" username=\"\" password=\"1\" team=\"\" />"+"\n"
//	+"</accounts>"+"\n"
//+"</conf>";
	
	public static Document getSkeleton(){
		Document doc = null;
		String filesep = System.getProperty("file.separator");
		try {
			//org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(simplemassimeditor.SimpleMassimEditorApp.class).getContext().getResourceMap(Skeleton.class);
            //File skeleton = new File(resourceMap.getString("skeleton.path"));
            java.net.URL url =    Skeleton.class.getResource(filesep+"simplemassimeditor"+filesep+"resources"+filesep+"configskeleton.xml");
            InputStream input = url.openStream();
            
            //File skeleton = new File(url.toString());
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			try {
				doc = parser.parse(input);
				
             //   saveXMLDocument("skeleton"+System.getProperty("file.separator")+"configskeleton.xml", doc);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
        return doc;
		
	}
    
     public static boolean saveXMLDocument(String fileName, Document doc) {
         System.out.println("Saving XML file... " + fileName);
         // open output stream where XML Document will be saved
         
         File xmlOutputFile = new File(fileName);
         FileOutputStream fos;
         Transformer transformer;
         try {
             fos = new FileOutputStream(xmlOutputFile);
        }
         catch (FileNotFoundException e) {
             System.out.println("Error occured: " + e.getMessage());
            
             return false;
         }
        // Use a Transformer for output
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         try {
             transformer = transformerFactory.newTransformer();
         }
catch (TransformerConfigurationException e) {
             System.out.println("Transformer configuration error: " + e.getMessage());
             return false;
         }
         DOMSource source = new DOMSource(doc);
         StreamResult result = new StreamResult(fos);
         // transform source into result will do save
         try {
             transformer.transform(source, result);
         }
         catch (TransformerException e) {
             System.out.println("Error transform: " + e.getMessage());
         }
         System.out.println("XML file saved.");
         return true;
     }
	public static void main(String[] args) {
	Document doc=	Skeleton.getSkeleton();

	}

}
