package massim.editor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class Point{
	public int x, y;
}
class El_Simulation{	

public Vector<Integer> cowx ;
public Vector<Integer> cowy ;
public Vector<Integer> agentx;
public Vector<Integer> agenty;
public Vector<Integer> obstaclex;
public Vector<Integer> obstacley;
public Vector<Integer> stable1x;
public Vector<Integer> stable1y;
public Vector<Integer> stable2x;
public Vector<Integer> stable2y;
public Vector<Integer> switcherx;
public Vector<Integer> switchery;
public Vector<Integer> fenceslength;
public Vector<String> fencesdirection;

public int sizex = 0;
public int sizey = 0;

	public El_Simulation(){
	}
	public void createSim(){
		cowx = new Vector<Integer>(); cowy = new Vector<Integer>(); agentx = new Vector<Integer>();
		agenty= new Vector<Integer>(); obstaclex = new Vector<Integer>(); obstacley = new Vector<Integer>();
		stable1x=new Vector<Integer>(); stable1y=new Vector<Integer>(); stable2x=new Vector<Integer>();
		stable2y=new Vector<Integer>();
		 switcherx = new Vector<Integer>();
		 switchery= new Vector<Integer>();
		 fenceslength = new Vector<Integer>();
    fencesdirection = new Vector<String>();
	}
	
}
public class ImportConfigFile {

	HashMap<String, El_Simulation> id_simulation = new HashMap<String, El_Simulation>();
	public ImportConfigFile(String path) throws ParserConfigurationException, SAXException, IOException{
		  File file = new File(path);
		  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		  DocumentBuilder db = dbf.newDocumentBuilder();
		  Document doc = db.parse(file);
		  this.loadData(doc);
	}

	public void loadData(Document doc){
		Element root = doc.getDocumentElement();
		
		NodeList nl = root.getElementsByTagName("simulation");
		for(int i = 0 ; i< nl.getLength(); i++){
			
			Element sim = (Element) nl.item(i);
			String id = sim.getAttribute("id");
			El_Simulation simulation = new El_Simulation();
			Element conf = (Element) sim.getElementsByTagName("configuration").item(0);
			simulation.sizex = Integer.parseInt(conf.getAttribute("sizex"));
			simulation.sizey = Integer.parseInt(conf.getAttribute("sizey"));
			
			NodeList array = sim.getElementsByTagName("array");
			for(int a = 0 ; a<array.getLength(); a++){
				Element arr = (Element) array.item(a);
				String object = arr.getAttribute("meta:name");
				int length = Integer.parseInt(arr.getAttribute("meta:length"));
				
				if(object.equalsIgnoreCase("agentPositionX")){
					simulation.agentx = this.additem(length, arr);
				}
				if(object.equalsIgnoreCase("agentPositionY")){
					simulation.agenty = this.additem(length, arr);
				}
				
				if(object.equalsIgnoreCase("cowPositionX")){
					simulation.cowx = this.additem(length, arr);
				}

				if(object.equalsIgnoreCase("cowPositionY")){
					simulation.cowy = this.additem(length, arr);
				}

				if(object.equalsIgnoreCase("obstaclePositionX")){
					simulation.obstaclex= this.additem(length, arr);
				}

				if(object.equalsIgnoreCase("obstaclePositionY")){
					simulation.obstacley = this.additem(length, arr);
				}

				if(object.equalsIgnoreCase("switchX")){
					simulation.switcherx=this.additem(length, arr);
				}

				if(object.equalsIgnoreCase("switchY")){
					simulation.switchery=this.additem(length, arr);
				}
                if(object.equalsIgnoreCase("fenceLength")){
					simulation.fenceslength=this.additem(length, arr);
				}
                 if(object.equalsIgnoreCase("fenceDirections")){
					simulation.fencesdirection=this.addStringitem(length, arr);
				}
				else if(object.equalsIgnoreCase("stable1X")){
					simulation.stable1x=this.additem(length, arr);
				}
				else if(object.equalsIgnoreCase("stable1Y")){
					simulation.stable1y=this.additem(length, arr);
				}

				else if(object.equalsIgnoreCase("stable2X")){
					simulation.stable2x=this.additem(length, arr);
				}
				else if(object.equalsIgnoreCase("stable2Y")){
					simulation.stable2y=this.additem(length, arr);
				}
			}
			id_simulation.put(id, simulation);
		}
	}
	public Vector<Integer> additem(int length, Element arr){
		Vector<Integer>v = new Vector<Integer>();
		for(int s = 0 ; s < length; s++){
			int p = Integer.parseInt(arr.getAttribute("item"+s));
			v.add(s, p);
			
		}
		return v;
	}
    	public Vector<String> addStringitem(int length, Element arr){
		Vector<String>v = new Vector<String>();
		for(int s = 0 ; s < length; s++){

			v.add(s, arr.getAttribute("item"+s));

		}
		return v;
	}
	public static void main(String[] args) {
		try {
			new ImportConfigFile("../competition2009/conf/serverconfig1.xml");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
