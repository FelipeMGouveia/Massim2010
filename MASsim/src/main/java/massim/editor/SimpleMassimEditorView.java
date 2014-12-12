/*
 * SimpleMassimEditorView.java
 */

package massim.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.xml.parsers.ParserConfigurationException;

import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.TaskMonitor;
import org.xml.sax.SAXException;

/**
 * The application's main frame.
 */
public class SimpleMassimEditorView extends FrameView {
    private String path = "/home/vhc/studium/job/myMassim/competition2009/conf/serverconfig1.xml";
    private Main m = new Main();

    public SimpleMassimEditorView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        if(resourceMap == null) System.out.println("IST NULL!");
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SimpleMassimEditorApp.getApplication().getMainFrame();
            aboutBox = new SimpleMassimEditorAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SimpleMassimEditorApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        cow = new javax.swing.JRadioButton();
        agentrot = new javax.swing.JRadioButton();
        agentblue = new javax.swing.JRadioButton();
        tree = new javax.swing.JRadioButton();
        switcher = new javax.swing.JRadioButton();
        remove = new javax.swing.JRadioButton();
        stablerot = new javax.swing.JRadioButton();
        stableblue = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        freedraw = new javax.swing.JRadioButton();
        line = new javax.swing.JRadioButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        export = new javax.swing.JMenuItem();
        newEditor = new javax.swing.JMenuItem();
        importfile = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        buttonGroup1.add(cow);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(massim.editor.SimpleMassimEditorApp.class).getContext().getResourceMap(SimpleMassimEditorView.class);
        cow.setText(resourceMap.getString("cow.text")); // NOI18N
        cow.setName("cow"); // NOI18N
        cow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cowMouseClicked(evt);
            }
        });

        buttonGroup1.add(agentrot);
        agentrot.setText(resourceMap.getString("agentrot.text")); // NOI18N
        agentrot.setName("agentrot"); // NOI18N
        agentrot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                agentrotMouseClicked(evt);
            }
        });

        buttonGroup1.add(agentblue);
        agentblue.setText(resourceMap.getString("agentblue.text")); // NOI18N
        agentblue.setName("agentblue"); // NOI18N
        agentblue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                agentblueMouseClicked(evt);
            }
        });

        buttonGroup1.add(tree);
        tree.setSelected(true);
        tree.setText(resourceMap.getString("tree.text")); // NOI18N
        tree.setName("tree"); // NOI18N
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeMouseClicked(evt);
            }
        });

        buttonGroup1.add(switcher);
        switcher.setText(resourceMap.getString("switcher.text")); // NOI18N
        switcher.setName("switcher"); // NOI18N
        switcher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                switcherMouseClicked(evt);
            }
        });

        buttonGroup1.add(remove);
        remove.setText(resourceMap.getString("remove.text")); // NOI18N
        remove.setName("remove"); // NOI18N
        remove.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                removeMouseClicked(evt);
            }
        });

        buttonGroup1.add(stablerot);
        stablerot.setText(resourceMap.getString("stablerot.text")); // NOI18N
        stablerot.setName("stablerot"); // NOI18N
        stablerot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stablerotMouseClicked(evt);
            }
        });

        buttonGroup1.add(stableblue);
        stableblue.setText(resourceMap.getString("stableblue.text")); // NOI18N
        stableblue.setName("stableblue"); // NOI18N
        stableblue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stableblueMouseClicked(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        buttonGroup2.add(freedraw);
        freedraw.setSelected(true);
        freedraw.setText(resourceMap.getString("freedraw.text")); // NOI18N
        freedraw.setName("freedraw"); // NOI18N
        freedraw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                freedrawMouseClicked(evt);
            }
        });

        buttonGroup2.add(line);
        line.setText(resourceMap.getString("line.text")); // NOI18N
        line.setName("line"); // NOI18N
        line.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lineMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(freedraw)
                    .addComponent(line))
                .addContainerGap(154, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(freedraw)
                .addGap(18, 18, 18)
                .addComponent(line)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cow)
                            .addComponent(agentrot)
                            .addComponent(agentblue)
                            .addComponent(tree))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(remove)
                                    .addComponent(switcher)
                                    .addComponent(stablerot))
                                .addGap(21, 21, 21))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(stableblue)
                                .addContainerGap())))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cow)
                    .addComponent(switcher))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(agentrot)
                    .addComponent(remove))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(agentblue)
                    .addComponent(stablerot))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tree)
                    .addComponent(stableblue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(massim.editor.SimpleMassimEditorApp.class).getContext().getActionMap(SimpleMassimEditorView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        export.setAction(actionMap.get("exportXML")); // NOI18N
        export.setText(resourceMap.getString("export.text")); // NOI18N
        export.setName("export"); // NOI18N
        fileMenu.add(export);

        newEditor.setAction(actionMap.get("newEditor")); // NOI18N
        newEditor.setText(resourceMap.getString("newEditor.text")); // NOI18N
        newEditor.setName("newEditor"); // NOI18N
        fileMenu.add(newEditor);

        importfile.setAction(actionMap.get("importXML")); // NOI18N
        importfile.setText(resourceMap.getString("importfile.text")); // NOI18N
        importfile.setName("importfile"); // NOI18N
        fileMenu.add(importfile);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 177, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void stableblueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stableblueMouseClicked
        MassimEditor.obj="stableblue";
}//GEN-LAST:event_stableblueMouseClicked

    private void stablerotMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stablerotMouseClicked
        // TODO add your handling code here:
        MassimEditor.obj="stablerot";
}//GEN-LAST:event_stablerotMouseClicked

    private void removeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_removeMouseClicked
        // TODO add your handling code here:
        MassimEditor.obj="";
}//GEN-LAST:event_removeMouseClicked

    private void switcherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_switcherMouseClicked
        // TODO add your handling code here:
        MassimEditor.obj="switcher";
}//GEN-LAST:event_switcherMouseClicked

    private void treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMouseClicked
        // TODO add your handling code here:
        MassimEditor.obj="tree";
}//GEN-LAST:event_treeMouseClicked

    private void agentblueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_agentblueMouseClicked
        // TODO add your handling code here:
        MassimEditor.obj="agentblue";
}//GEN-LAST:event_agentblueMouseClicked

    private void cowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cowMouseClicked
        // TODO add your handling code here:
        MassimEditor.obj="cow";
}//GEN-LAST:event_cowMouseClicked

    private void agentrotMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_agentrotMouseClicked
        // TODO add your handling code here:
        MassimEditor.obj="agentrot";
}//GEN-LAST:event_agentrotMouseClicked

    private void freedrawMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_freedrawMouseClicked
        MassimEditor.geometry="freedraw";
    }//GEN-LAST:event_freedrawMouseClicked

    private void lineMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lineMouseClicked
        // TODO add your handling code here:
        MassimEditor.geometry="line";
    }//GEN-LAST:event_lineMouseClicked

    @Action
    public void importXML(){
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this.getComponent());
        System.out.println(option);
        if(option == JFileChooser.APPROVE_OPTION){
           String path= chooser.getSelectedFile().getPath();
            try {
                m.loadconfig = new ImportConfigFile(path);
                m.displayEditor();
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(SimpleMassimEditorView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(SimpleMassimEditorView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SimpleMassimEditorView.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    @Action
    public void exportXML(){
                JFileChooser chooser = new JFileChooser();
                //chooser.setFileSelectionMode(IRECTORIES_ONLY);
        int option = chooser.showSaveDialog(this.getComponent());

        System.out.println(option);
        if(option == JFileChooser.APPROVE_OPTION){
           String path= chooser.getSelectedFile().getAbsolutePath();
           m.exportXML(path);

        }
    }
    @Action
    public void newEditor(){
        String s = (String)JOptionPane.showInputDialog(this.getComponent(),"input format;  id:sizex:sizey");
        String[] a = s.split(":");
        String id =a[0];
        int sizex = Integer.parseInt(a[1]);
        int sizey = Integer.parseInt(a[2]);
        m.createEditor(id, sizex, sizey);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton agentblue;
    private javax.swing.JRadioButton agentrot;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JRadioButton cow;
    private javax.swing.JMenuItem export;
    private javax.swing.JRadioButton freedraw;
    private javax.swing.JMenuItem importfile;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton line;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newEditor;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton remove;
    private javax.swing.JRadioButton stableblue;
    private javax.swing.JRadioButton stablerot;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JRadioButton switcher;
    private javax.swing.JRadioButton tree;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}