package massim.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportConfigFile {
	String path = "";
	HashMap<String, MassimEditor> id_editor;
	public ExportConfigFile(HashMap<String, MassimEditor> id_editor) {
		
		this.id_editor = id_editor;
		
	}
	public void exportXML(String path){
		Set<String> id = id_editor.keySet();
    	Iterator<String> iter = id.iterator();
    	Document doc = Skeleton.getSkeleton();
    	Element match = (Element) doc.getElementsByTagName("match").item(0);
    	Element simulation = (Element) doc.getElementsByTagName("simulation").item(0);
    	int numberofAgentproTeam = 0;
    	while(iter.hasNext()){
    		String eid = iter.next();
    		MassimEditor me = id_editor.get(eid);
           if(me.isShowing()){

           
    		El_Simulation sim = me.createEl_sim();
    		
    		Element el_sim=(Element) simulation.cloneNode(true);
            el_sim.setAttribute("id", eid);
    		Element el_conf=(Element) el_sim.getElementsByTagName("configuration").item(0);
    		el_conf.setAttribute("sizex", ""+sim.sizex);
    		el_conf.setAttribute("sizey", ""+sim.sizey);
    		
    		Element el_arr = this.createElement(doc,sim.agentx, "agentPositionX");
    		el_conf.appendChild(el_arr);
    		el_arr = this.createElement(doc, sim.agenty, "agentPositionY");
    		el_conf.appendChild(el_arr);
    		el_conf.setAttribute("numberOfAgents", ""+sim.agentx.size());
    	
    		el_arr = this.createElement(doc,sim.stable1x, "stable1X");
    		el_conf.appendChild(el_arr);
    		el_arr = this.createElement(doc, sim.stable1y, "stable1Y");
    		el_conf.appendChild(el_arr);
    	
    		el_arr = this.createElement(doc,sim.stable2x, "stable2X");
    		el_conf.appendChild(el_arr);
    		el_arr = this.createElement(doc, sim.stable2y, "stable2Y");
    		el_conf.appendChild(el_arr);
    	
    		el_arr = this.createElement(doc, sim.obstaclex, "obstaclePositionX");
    		el_conf.appendChild(el_arr);
    		el_arr = this.createElement(doc, sim.obstacley, "obstaclePositionY");
    		el_conf.appendChild(el_arr);
    		el_conf.setAttribute("numberOfObstacles", ""+sim.obstaclex.size());
    	
    		el_arr = this.createElement(doc, sim.cowx, "cowPositionX");
    		el_conf.appendChild(el_arr);
    		el_arr = this.createElement(doc, sim.cowy, "cowPositionY");
    		el_conf.appendChild(el_arr);
    		el_conf.setAttribute("numberOfCows", ""+sim.cowx.size());
    		
    		el_arr = this.createElement(doc, sim.switcherx, "switchX");
    		el_conf.appendChild(el_arr);
    		el_arr = this.createElement(doc, sim.switchery, "switchY");
    		el_conf.appendChild(el_arr);
    		el_arr = this.createElement(doc, sim.fenceslength, "fenceLength");
    		el_conf.appendChild(el_arr);
    		
    		el_arr = doc.createElement("array");
    		for(int i = 0; i< sim.fencesdirection.size();i++){
    			el_arr.setAttribute("item"+i, sim.fencesdirection.get(i));
    		}
    		el_arr.setAttribute("meta:length", ""+sim.fencesdirection.size());
    		el_arr.setAttribute("meta:name", "fenceDirections");
    		el_conf.appendChild(el_arr);
    		el_conf.setAttribute("numberOfFences", ""+sim.fencesdirection.size());
    		
    		
    		Element el_agents = (Element) el_sim.getElementsByTagName("agents").item(0);
    		Element agent = (Element) el_agents.getElementsByTagName("agent").item(0);
    		for(int i = 0 ; i< sim.agentx.size()/2; i++){
    			Element cloned = (Element) agent.cloneNode(true);
    			cloned.setAttribute("team", "red");
    			el_agents.appendChild(cloned);
    		}
    		for(int i = sim.agentx.size()/2 ; i< sim.agentx.size(); i++){
    			Element cloned = (Element) agent.cloneNode(true);
    			cloned.setAttribute("team", "blue");
    			el_agents.appendChild(cloned);
    		}
    		numberofAgentproTeam = sim.agentx.size()/2;
    		el_agents.removeChild(agent);
    		el_sim.appendChild(el_conf);
    		el_sim.appendChild(el_agents);
    		match.appendChild(el_sim);
        }
    	}
    	Element accounts =  (Element) doc.getElementsByTagName("accounts").item(0);
    	String s = (String)JOptionPane.showInputDialog(null,"numberofTeams ?");
    	int numberofTeam = Integer.parseInt(s);
        if(numberofTeam <2) numberofTeam=2;
       
       Vector<String> teamname = new Vector<String>();
       teamname.add("A");teamname.add("B");teamname.add("C");teamname.add("D");teamname.add("E");
       teamname.add("F"); teamname.add("H");teamname.add("I");teamname.add("K");teamname.add("L");
       teamname.add("Y");teamname.add("X");teamname.add("V");teamname.add("M");teamname.add("N");
       
    	Element acc = (Element) accounts.getElementsByTagName("account").item(0);
        int username = 1;
    	for( int i = 1; i<= numberofTeam; i++){

            String team=teamname.get(0);
           
            for(int j =1;j<=numberofAgentproTeam;j++){
                Element a = (Element) acc.cloneNode(true);
                a.setAttribute("username", ""+username);
                a.setAttribute("team", team);
                accounts.appendChild(a);
                username +=1;
            }
           
            teamname.remove(0);
    	}
    	accounts.removeChild(acc);
    	
    	match.removeChild(simulation);
    	this.write(doc, path, "serverconfig.xml");
	}
	

	
	private Element createElement(Document doc,Vector<Integer> data, String string) {
		Element el_arr = doc.createElement("array");
		
		for(int i =0; i<data.size(); i++){
			el_arr.setAttribute("item"+i, ""+data.get(i));
		}
		el_arr.setAttribute("meta:length", ""+data.size());
		el_arr.setAttribute("meta:name", string);
	
		return el_arr;
	}
	private  void write(Document doc, String dir,String filename) {
		
		File xmlStepFile = new File(dir);
		try{
			FileOutputStream out = new FileOutputStream(xmlStepFile);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer
					.transform(new DOMSource(doc), new StreamResult(out));
		}
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
			// throw new InvalidConfigurationException(e1);
		}

		catch (TransformerException e2) {
			e2.printStackTrace();
			// throw new InvalidConfigurationException(e2);
		}
	}

}
