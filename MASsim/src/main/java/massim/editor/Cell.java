package massim.editor;


import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import massim.editor.MassimEditor.Panel.DrawingPane;



public class Cell extends JPanel implements MouseListener{
	    	public int x, y;
			public String obj="";
			public int length = 0;
			public String dir = "";
	
	    	public Cell(int x, int y){
	    		
	    		this.addMouseListener(this);
	    		this.setBackground(Color.white);
	    		this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
	    		this.x = x;this.y=y;
	    		this.setToolTipText(x+"x"+y);
	    	}
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			public void mouseEntered(MouseEvent e) {				
				if(MassimEditor.mouse_pressed){
					if(MassimEditor.obj.equalsIgnoreCase("cow")
                            &&
							MassimEditor.geometry.equalsIgnoreCase("freedraw")){
						this.obj = MassimEditor.obj;
						this.setBackground(MassimEditor.obj_color.get(obj));
						
					}
				
					else if(MassimEditor.obj.equalsIgnoreCase("tree") && 
							MassimEditor.geometry.equalsIgnoreCase("freedraw")){
					
					this.obj = MassimEditor.obj;
					this.setBackground(MassimEditor.obj_color.get(obj));
				
				}
                   else if(MassimEditor.obj.equalsIgnoreCase("") &&
							MassimEditor.geometry.equalsIgnoreCase("freedraw")){

					this.obj = MassimEditor.obj;
					this.setBackground(MassimEditor.obj_color.get(obj));

				}
					
					MassimEditor.x2=x;
					MassimEditor.y2=y;
				}
			}
			public void mouseExited(MouseEvent e) {
				
			
			}
			public void mousePressed(MouseEvent e) {
			  MassimEditor.mouse_pressed = true;
			  this.obj=MassimEditor.obj;
			  MassimEditor.x1=x;
			  MassimEditor.y1=y;
			  MassimEditor.x2=x;
			  MassimEditor.y2=y;
			  this.setBackground(MassimEditor.obj_color.get(obj));
			
			}
			public void mouseReleased(MouseEvent e) {
				MassimEditor.mouse_pressed = false;
				DrawingPane parent = (DrawingPane) this.getParent();
				if(MassimEditor.obj.contains("stable")){
					parent.drawStable();
				}
				else if(MassimEditor.obj.equalsIgnoreCase("switcher")){
					parent.drawFence();	
				}
				else if(MassimEditor.obj.equalsIgnoreCase("") && MassimEditor.geometry.equalsIgnoreCase("line") || (
						MassimEditor.obj.equalsIgnoreCase("tree") && MassimEditor.geometry.equalsIgnoreCase("line"))
                        ||(
						MassimEditor.obj.equalsIgnoreCase("cow") && MassimEditor.geometry.equalsIgnoreCase("line")))
					
					parent.setCells(MassimEditor.x1, MassimEditor.y1, MassimEditor.x2, MassimEditor.y2,obj);
			}
	    }