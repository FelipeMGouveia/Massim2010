package massim.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



public class MassimEditor extends JFrame {

	public static boolean mouse_pressed;
	public static String obj = "tree";
	/**
	 * another values for geometry: freedraw, line, square
	 */
	public static String geometry = "freedraw";

	public static int x1=0,x2=0,y1=0,y2=0;
	
	public static Map<String,Color> obj_color;
	
	static {
		obj_color= new HashMap<String, Color>();
		obj_color.put("agentblue",new Color(133,224,255));
		obj_color.put("agentrot",new Color(204,0,51));
		obj_color.put("cow", new Color(133,133,133));
		obj_color.put("tree",new Color(0,204,0));
		obj_color.put("switcher",new Color(153,153,102));
		obj_color.put("fence",new Color(177,177,139));
		obj_color.put("stablerot", new Color(102,0,0));
		obj_color.put("stableblue", new Color(51,0,204));
		obj_color.put("", Color.white);
	};
	
	public int sizex = 0; 
	public int sizey = 0;
	
	public void setSize(int x, int y){
		sizex = x; sizey = y;
	}
	public Cell[][] board = null;

	class Panel extends JPanel{
		public Panel(){
			 super(new BorderLayout());
			 this.setSize(0, 0);
		     JScrollPane scroller = new JScrollPane(new DrawingPane());
		     scroller.setPreferredSize(new Dimension(400,400));
		     add(scroller, BorderLayout.CENTER);
			
		}
		
	    /** The component inside the scroll pane. the component inside the scroll pane
	     * has bigger size as the size of scrollpane so that we can see
	     * the scroll at right and button of the window
	     * setPreferredSize means the size of internal component will be set as the size of 
	     * window when he bigger as the window  */
	    public class DrawingPane extends JPanel {
	    
	    	public DrawingPane(){
	    		Toolkit toolkit = Toolkit.getDefaultToolkit();
	    		// Get the current screen size
	    		Dimension scrnsize = toolkit.getScreenSize();
	    		// l=(scrnsize.height-50)/70;//this.getOutputSizeX();
	    		int h = scrnsize.height-70;
	    		if(sizex*10 < h){
	    			h = sizex*10;
	    		}
	    		this.setPreferredSize(new Dimension(h,h));
	    		//100 will be width of Grid
	    		this.setLayout(new GridLayout(0,sizex));
	    		
	    		board = new Cell[sizex][sizey];
	    		
	    		for(int i = 0; i<sizey;i++){
	    			for(int j = 0; j<sizex; j++){
	    				board[j][i] = new Cell(j,i);
	    				this.add(board[j][i]);
	    			}
	    		}		
	    	}
	    	int[][] blue_stable = {{-1,-1},{-1,-1}};
	    	int[][] rot_stable = {{-1,-1},{-1,-1}};
	    	
	    	public void drawStable(){
	    		
	    		if(MassimEditor.obj.equalsIgnoreCase("stableblue")){
	    			setCells(blue_stable[0][0], blue_stable[0][1],blue_stable[1][0],blue_stable[1][1],"");
	    		}
	    		else if(MassimEditor.obj.equalsIgnoreCase("stablerot")){
	    			setCells(rot_stable[0][0], rot_stable[0][1],rot_stable[1][0],rot_stable[1][1],"");    			
	    		}
	    		for(int i = x1; i<= x2; i++){
	    			for(int j = y1; j<= y2; j++){
	    				board[i][j].setBackground(obj_color.get(obj));
	    			}
	    		}
	    		if(MassimEditor.obj.equalsIgnoreCase("stableblue")){
	    			blue_stable[0][0] = x1;
	    			blue_stable[0][1] = y1;
	    			blue_stable[1][0] = x2;
	    			blue_stable[1][1] = y2;
	    			
	    		}
	    		else if(MassimEditor.obj.equalsIgnoreCase("stablerot")){
	    			rot_stable[0][0] = x1;
	    			rot_stable[0][1] = y1;
	    			rot_stable[1][0] = x2;
	    			rot_stable[1][1] = y2;
	    			
	    		} 
	    		
                board[x1][y1].obj=MassimEditor.obj;
                board[x2][y2].obj=MassimEditor.obj;
                board[x1][y1].setBackground(Color.BLACK);
                board[x2][y2].setBackground(Color.BLACK);

	    	}
			public void setCells(int x1, int y1, int x2, int y2, String obj) {
				if(x1 != -1 && y1 != -1 && x2 != -1 && y2 != -1){
					for(int i = x1; i<= x2; i++){
		    			for(int j = y1; j<= y2; j++){
		    				board[i][j].obj=obj;
		    				board[i][j].setBackground(obj_color.get(obj));
		    			}
		    		}
				}
			}
			public void drawFence() {
				if(x1==x2 && y1==y2){
					board[x1][y1].obj="";
					board[x1][y1].setBackground(Color.white);
					return;
				}
				
				if(x1 == x2){
					if(y1<y2){
						board[x1][y1].length=y2-y1;
						board[x1][y1].dir="down";
						
						for(int i = y1+1; i<=y2; i++){
							board[x1][i].setBackground(obj_color.get("fence"));
						}
					}
					else if(y1>y2){
						board[x1][y1].length=y1-y2;
						board[x1][y1].dir="up";
						
						for(int i = y2; i<y1; i++){
							board[x1][i].setBackground(obj_color.get("fence"));
						}
					}
				}
				else if(y1==y2){
					if(x1<x2){
						board[x1][y1].length=x2-x1;
						board[x1][y1].dir="right";
						
						for(int i = x1+1; i<=x2; i++){
							board[i][y1].setBackground(obj_color.get("fence"));
						}
					}
					else if(x1>x2){
						board[x1][y1].length=x1-x2;
						board[x1][y1].dir="left";
						
						for(int i = x2; i<x1; i++){
							board[i][y1].setBackground(obj_color.get("fence"));
						}
					}
					
				}
				
			}
	    }
	}
	public MassimEditor(int x, int y){
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(x, y);
		this.setContentPane(new Panel());

		this.pack();
			
	}
	
	
	public static void main(String[] args) {
		new MassimEditor(100,100).setVisible(true); 

	}


	public void paintCell(El_Simulation sim) {
        for(int i = 0; i<sim.switcherx.size();i++){
            String direction = sim.fencesdirection.get(i);
            int x = sim.switcherx.get(i);
            int y = sim.switchery.get(i);
            int lenght = sim.fenceslength.get(i);
            board[x][y].setBackground(obj_color.get("switcher"));
            if(direction.equalsIgnoreCase("up")){
                for(int j = 1 ; j<=lenght;j++){
                    board[x][y-j].setBackground(obj_color.get("fence"));
                }
            }
                if(direction.equalsIgnoreCase("down")){
                for(int j = 1 ; j<=lenght;j++){
                    board[x][y+j].setBackground(obj_color.get("fence"));
                }
            }
                if(direction.equalsIgnoreCase("left")){
                for(int j = 1 ; j<=lenght;j++){
                    board[x-j][y].setBackground(obj_color.get("fence"));
                }
            }
                if(direction.equalsIgnoreCase("right")){
                for(int j = 1 ; j<=lenght;j++){
                    board[x+j][y].setBackground(obj_color.get("fence"));
                }
            }
        }
		for(int i = 0 ; i< sim.cowx.size();i++){
			int x = sim.cowx.get(i);
			int y = sim.cowy.get(i);
			
			board[x][y].setBackground(obj_color.get("cow"));
			board[x][y].obj = "cow";
		}
		for(int i = 0 ; i< sim.obstaclex.size();i++){
			int x = sim.obstaclex.get(i);
			int y = sim.obstacley.get(i);
			board[x][y].setBackground(obj_color.get("tree"));
			board[x][y].obj= "tree";
			
		}
		for(int i = 0 ; i< sim.agentx.size();i++){
			int x = sim.agentx.get(i);
			int y = sim.agenty.get(i);
			if(i<sim.agentx.size()/2){
				board[x][y].setBackground(obj_color.get("agentrot"));
				board[x][y].obj = "agentrot";
			}
			else{
				board[x][y].setBackground(obj_color.get("agentblue"));
				board[x][y].obj= "agentblue";
			}
		}
		for(int i = sim.stable1x.get(0); i<=sim.stable1x.get(1);i++){
			for(int j = sim.stable1y.get(0);j<=sim.stable1y.get(1);j++){
				board[i][j].setBackground(obj_color.get("stablerot"));
			}
		}
		board[sim.stable1x.get(0)][sim.stable1y.get(0)].obj = "stablerot";
		board[sim.stable1x.get(1)][sim.stable1y.get(1)].obj = "stablerot";
		
		for(int i = sim.stable2x.get(0); i<=sim.stable2x.get(1);i++){
			for(int j = sim.stable2y.get(0);j<=sim.stable2y.get(1);j++){
				board[i][j].setBackground(obj_color.get("stableblue"));
			}
		}
		board[sim.stable2x.get(0)][sim.stable2y.get(0)].obj = "stableblue";
		board[sim.stable2x.get(1)][sim.stable2y.get(1)].obj = "stableblue";
	}
	public El_Simulation createEl_sim(){
		El_Simulation sim = new El_Simulation();
		sim.createSim();
		sim.sizex= sizex;
		sim.sizey= sizey;
		Vector<Integer> agentrotx = new Vector<Integer>();
		Vector<Integer> agentroty = new Vector<Integer>();
		
		Vector<Integer> agentbluex = new Vector<Integer>();
		Vector<Integer> agentbluey = new Vector<Integer>();
		
		for(int i = 0; i<sizey; i++){
			for(int j = 0 ; j<sizex; j++){
				if(board[j][i].obj.equalsIgnoreCase("tree")){
					sim.obstaclex.add(j);
					sim.obstacley.add(i);
				}
				else if(board[j][i].obj.equalsIgnoreCase("cow")){
					sim.cowx.add(j); sim.cowy.add(i);
				}
				else if(board[j][i].obj.equalsIgnoreCase("agentrot")){
					agentrotx.add(j); agentroty.add(i);
				}
				else if(board[j][i].obj.equalsIgnoreCase("agentblue")){
					agentbluex.add(j); agentbluey.add(i);
				}
				else if(board[j][i].obj.equalsIgnoreCase("stablerot")){
					sim.stable1x.add(j); sim.stable1y.add(i);
				}
				else if(board[j][i].obj.equalsIgnoreCase("stableblue")){
					sim.stable2x.add(j); sim.stable2y.add(i);
				}
				else if(board[j][i].obj.equalsIgnoreCase("switcher")){
					sim.switcherx.add(j); sim.switchery.add(i);
					sim.fenceslength.add(board[j][i].length);
					sim.fencesdirection.add(board[j][i].dir);
				}
			}
		}
		if(agentbluex.size() != agentrotx.size()){
			System.out.println("Number of rot agents and blue agents must be equals... ");

			System.out.println("ERROR: Agents will not be exported... ");

		}
		else {
			sim.agentx.addAll(agentrotx);sim.agentx.addAll(agentbluex);
			sim.agenty.addAll(agentroty);sim.agenty.addAll(agentbluey);
		}
		return sim;
	}
}
