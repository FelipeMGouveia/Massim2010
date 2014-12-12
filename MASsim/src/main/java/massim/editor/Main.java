package massim.editor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main {
    public ImportConfigFile loadconfig = null;
    HashMap<String, MassimEditor> id_editor = new HashMap<String, MassimEditor>();
    
    public void displayEditor(){
        if(loadconfig != null){
            HashMap<String,El_Simulation> id_sim = loadconfig.id_simulation;
            Set<String> id = id_sim.keySet();
            Iterator<String> iter = id.iterator();
            while(iter.hasNext()){
                String sid = iter.next();
                El_Simulation sim = id_sim.get(sid);
                MassimEditor editor = new MassimEditor(sim.sizex, sim.sizey);
                editor.setTitle(sid);
                id_editor.put(sid,editor);
                editor.paintCell(sim);
                editor.setVisible(true);
            }
        }
    }
        public void createEditor(String id, int sizex, int sizey){
            MassimEditor editor = new MassimEditor(sizex, sizey);
            id_editor.put(id, editor);
            editor.setTitle(id);
            editor.setVisible(true);
        }
    
    public void exportXML(String path){
    	ExportConfigFile ex = new ExportConfigFile(id_editor);
    	ex.exportXML(path);
    	
    }
    public static void main(String[] args){
    	
    	try {
    		
			ImportConfigFile conf = new ImportConfigFile("bin/conf/2009-contest.xml");
			Main m = new Main();
			m.createEditor("testEditor", 100, 100);
			//m.loadconfig = conf;
			//m.displayEditor();
			new BufferedReader(new InputStreamReader(System.in)).readLine();
			m.exportXML("serverconfig");
    	} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}

