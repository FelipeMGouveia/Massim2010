package massim.server;


import static massim.framework.util.DebugLog.LOGLEVEL_CRITICAL;
import static massim.framework.util.DebugLog.LOGLEVEL_ERROR;
import static massim.framework.util.DebugLog.LOGLEVEL_NORMAL;
import static massim.framework.util.DebugLog.log;
import static massim.test.ConfigurationUtilities.getClassFromConfig;
import static massim.test.ConfigurationUtilities.getNewInstanceFromConfig;
import static massim.test.ConfigurationUtilities.getObjectFromConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import massim.framework.AgentManager;
import massim.framework.AgentProviderAgentManager;
import massim.framework.ArrayAgentProvider;
import massim.framework.BroadcastObserver;
import massim.framework.Component;
import massim.framework.Observer;
import massim.framework.Simulation;
import massim.framework.SimulationConfiguration;
import massim.framework.TeamAgentFilter;
import massim.framework.TeamAgentParameter;
import massim.framework.XMLOutputObserver;
import massim.framework.backup.BackupReader;
import massim.framework.connection.InetSocketListener;
import massim.framework.connection.UsernamePasswordSocketLoginManager;
import massim.framework.rmi.RMI_Infor;
import massim.framework.rmi.XMLDocumentServer;
import massim.framework.simulation.simplesimulation.DefaultSimpleSimulationConfiguration;
import massim.framework.simulation.simplesimulation.SimpleSimulationConfiguration.AgentConfiguration;
import massim.framework.util.XMLUtilities;
import massim.test.InvalidConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/*import org.mortbay.jetty.*;
import org.mortbay.jetty.bio.*;
import org.mortbay.jetty.webapp.*;
import org.mortbay.jetty.handler.*;
import org.mortbay.jetty.servlet.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;*/

public class CopyofServer extends AbstractServer {
	public final int AGENT_PORT_DEFAULT=12300; 
	public final int AGENT_BACKLOG_DEFAULT=10;
	
	
	protected InetSocketListener socketlistener;
	protected ServerInetSocketListener serverinetsocketlistener;
	
	protected ServerSimulationAgents serversimulationagents;
	
	protected UsernamePasswordSocketLoginManager loginsocketmanager;
	protected ArrayAgentProvider arrayagentprovider;
	protected AgentManager agentmanager;
	protected TeamAgentFilter teamagentfilter;
	protected Registry rmiregistry;
	protected RMIServerStatus rmiinfoserver2;
	protected RMITournamentServer rmitournamentserver;
	protected Document xmlTournamentReport; 
	protected File xmlTournamentReportFile;
	protected List<String> manual;

	protected String tournamentname;
	
	protected LaunchSync launchSync;
	
	protected int tournamentmode;
	protected BackupReader reader;

	interface LaunchSync {
		void waitForStart();
	}
	
	class TimerLaunchSync implements LaunchSync {
		long time;
		public TimerLaunchSync(Element e) {
			time = Long.parseLong(e.getAttribute("time-to-launch"));
		}
		public void waitForStart() {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {}
		}
	}
	
	class KeyLaunchSync implements LaunchSync {
		public void waitForStart() {
			try {System.in.read();} catch (IOException e) {}
		}
	}
	
	private class RMITournamentServer extends UnicastRemoteObject implements XMLDocumentServer {
		private static final long serialVersionUID = 6468903192414320442L;

		public RMITournamentServer() throws RemoteException {
			super();
		}

		public Document getXMLDocument() {
			return xmlTournamentReport;//FIXME: missing synchronization
		}
	}
	
	public CopyofServer(String[] args) throws InvalidConfigurationException {
		Element xmlconfiguration = parseCommandLineToConfig(args);

		log(LOGLEVEL_NORMAL,"Server launched");
		tournamentname = xmlconfiguration.getAttribute("tournamentname");
		tournamentmode = Integer.parseInt(xmlconfiguration.getAttribute("tournamentmode"));
		//create socket listener
		serverinetsocketlistener = new ServerInetSocketListener((Element)XMLUtilities.getChildsByTagName(xmlconfiguration, "network-agent").item(0));
		socketlistener = serverinetsocketlistener.object;
		//read rmiport and host
		
		Element rmiservice = (Element) XMLUtilities.getChildsByTagName(xmlconfiguration, "rmiservice").item(0);
		
		if(rmiservice != null){
			RMI_Infor.RMI_PORT_DEFAULT = Integer.parseInt(rmiservice.getAttribute("myport"));
			RMI_Infor.RMI_HOST_DEFAULT = rmiservice.getAttribute("myhost");
			RMI_Infor.FLASH_SERVER = rmiservice.getAttribute("flashserver");
			RMI_Infor.FLASH_PORT = Integer.parseInt(rmiservice.getAttribute("flashport"));
			RMI_Infor.FLASH_SERVICE = rmiservice.getAttribute("flashservice");
			
			RMI_Infor.RMI_URL_DEFAULT = "rmi://"+RMI_Infor.RMI_HOST_DEFAULT+":"+RMI_Infor.RMI_PORT_DEFAULT+"/";
		}
		//create Registry for RMI-service
		try {
			LocateRegistry.createRegistry(RMI_Infor.RMI_PORT_DEFAULT);
			log(LOGLEVEL_NORMAL,"Create rmiregistry on port  "+RMI_Infor.RMI_PORT_DEFAULT);
		} catch (RemoteException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		
		//read account list
		NodeList nl=XMLUtilities.getChildsByTagName(xmlconfiguration, "accounts");
		serversimulationagents = new ServerSimulationAgents((Element) nl.item(0)); 
		
		nl = XMLUtilities.getChildsByTagName(xmlconfiguration,"match");
		for (int i=0;i<nl.getLength();i++) System.out.println("node:"+nl.item(i));
		if (nl.getLength()!=1) {
			log(LOGLEVEL_CRITICAL,"simulation configuration invalid");
			System.exit(1);
		}
		el_match = (Element) nl.item(0);
		
		//create UsernamePasswordSocketLoginManager 
		loginsocketmanager = new UsernamePasswordSocketLoginManager(serversimulationagents.accounts,serversimulationagents.accountSocketHandlerMap);

		//connect loginsocketmanager with socketlistener
		socketlistener.setSocketHandler(loginsocketmanager);
		
		//create arrayagentprovider
		arrayagentprovider = new ArrayAgentProvider(serversimulationagents.agents);

		//create team filter
		teamagentfilter = new TeamAgentFilter(serversimulationagents.accountTeamMap,arrayagentprovider);
		
		//create agent manager, based on teamagentfilter
		agentmanager = new AgentProviderAgentManager(teamagentfilter);
		
		//create launch sync
		String launchsynctype = xmlconfiguration.getAttribute("launch-sync-type");
		if (launchsynctype.equalsIgnoreCase("key")) {
			launchSync = new KeyLaunchSync();
		} else if (launchsynctype.equalsIgnoreCase("timer")) {
			launchSync = new TimerLaunchSync(xmlconfiguration);
		}
		xmlTournamentReportFile = new File(xmlconfiguration
				.getAttribute("reportpath"), tournamentname + "_report.xml");

		if (this.tournamentmode == 2) {

			manual = new LinkedList<String>();

			try {

				nl = XMLUtilities.getChildsByTagName(xmlconfiguration,
						"manual-mode");
				Element el_match = (Element) nl.item(0);
				NodeList matches = el_match.getElementsByTagName("match");

				for (int i = 0; i < matches.getLength(); i++) {

					Element match = (Element) matches.item(i);
					manual.add(match.getAttribute("team1") + "VS"
							+ match.getAttribute("team2"));
				}
			}

			catch (Exception e) {

				log(LOGLEVEL_CRITICAL, "manual mode configuration invalid");
				System.exit(1);
			}
			;
		}
	}

	protected CopyofServer.ServerSimulationRun2 sr;
	protected Element el_match;
	
	protected class ServerSimulationRun2 extends massim.framework.SimulationRun {
		public XMLOutputObserver xmlstatisticsobserver;
		
		protected BroadcastObserver broadcastObserver;
		private ConfigurationDelivererController configurationDelivererController;
		private List<Observer> observer;
		
		
		public ServerSimulationRun2(Element xmlconfiguration, Map<String,String> teammapping, final String name) throws InvalidConfigurationException {
			observer = new LinkedList<Observer>();

			//initialize broadbast Observer
			broadcastObserver = new BroadcastObserver();
			
			//set broadcast Observer as primary observer for this simulation run
			setObserver(broadcastObserver);
			
			//initialize raw dumping observer
			ObserverFactory oc1 = new ObjectDumperObserverFactory();
			Observer o1 = oc1.createObserver(xmlconfiguration, name);
			broadcastObserver.addObserver(o1);
			observer.add(o1);

			// create RMI XML observer
			ObserverFactory oc2 = new RMIXMLDocumentObserverFactory();
			Observer o2 = oc2.createObserver(xmlconfiguration, name);
			broadcastObserver.addObserver(o2);
			observer.add(o2);
			
			// create XML observer
			ObserverFactory oc3 = new FileXMLDocumentObserverFactory();
			Observer o3 = oc3.createObserver(xmlconfiguration, name);
			broadcastObserver.addObserver(o3);
			observer.add(o3);
			
			xmlstatisticsobserver = getNewInstanceFromConfig(xmlconfiguration.getAttribute("xmlstatisticsobserver"));
			broadcastObserver.addObserver(xmlstatisticsobserver);
			
			// create visualisation observer
			String className = xmlconfiguration.getAttribute("visualisationobserver");
//			String prefix = xmlconfiguration.getAttribute("xmlobserverpath");
			if (className!="") {
				System.err.println("Classname:"+className);
				Observer o4 = getNewInstanceFromConfig(className);
				broadcastObserver.addObserver(o4);
				observer.add(o4);
			}
			final String tn = tournamentname;
			final Map<String, String> tm = teammapping;
			ServerSimulationContext context = new ServerSimulationContext() {
				public String getSimulationName() {return name;}
				public String getGlobalName() {return tn;}
				public String[] getTeamNames() {
					String[] r = new String[tm.size()];
					for (int i=0;i<r.length;i++) {
						//r[i] = tm.get
					}
					return r;
				}
			};
			
			for (int i=0;i<observer.size();i++) {
				if (observer.get(i) instanceof ServerSimulationContextReceiver) {
					ServerSimulationContextReceiver rec = (ServerSimulationContextReceiver) observer.get(i);
					rec.setSimulationContext(context);
				}
			}
			
			
			//create controller
			configurationDelivererController = (ConfigurationDelivererController) new TrivialControllerFactory().createController(xmlconfiguration, name);
			setController(configurationDelivererController);
			
			log(LOGLEVEL_NORMAL,"######################### new simulation run ----");
			
			//create simulation
			Simulation simulation = getNewInstanceFromConfig(xmlconfiguration.getAttribute("simulationclass"));
			setSimulation(simulation);
			
			NodeList nl=XMLUtilities.getChildsByTagName(xmlconfiguration, "configuration");
			if (nl.getLength()!=1) {
				log(LOGLEVEL_CRITICAL,"invalid simulation configuration");
			}
			Element el_simconf = (Element) nl.item(0);
			
			// read classes for simulation configuration, agent and it's configuration
			DefaultSimpleSimulationConfiguration simconf = getObjectFromConfig(xmlconfiguration.getAttribute("configurationclass"), el_simconf);
			
			nl=XMLUtilities.getChildsByTagName(xmlconfiguration, "agents");
			if (nl.getLength()!=1) {
				log(LOGLEVEL_CRITICAL,"invalid simulation configuration");
			}
			Element el_agents = (Element) nl.item(0);
			
			NodeList agents=XMLUtilities.getChildsByTagName(el_agents, "agent");
			
			AgentConfiguration[] agentconfigs;
			agentconfigs=new AgentConfiguration[agents.getLength()];
			for (int i=0;i<agents.getLength();i++) {
				Element el_agent = (Element) agents.item(i);
				
				// create agent configuration
				agentconfigs[i]=new AgentConfiguration();

				//configure simulation agent class
				agentconfigs[i].agentClass = getClassFromConfig(el_agent.getAttribute("agentclass"));
				
				//retrieve agent parameter class
				NodeList nl2 = XMLUtilities.getChildsByTagName(el_agent, "configuration");
				if (nl2.getLength()!=1) {
					log(LOGLEVEL_CRITICAL,"Invalid number of agent parameter configurations.");
					System.exit(1);
				}
				Element el_agentparam = (Element) nl2.item(0);
				agentconfigs[i].agentParameter=getObjectFromConfig(el_agent.getAttribute("agentcreationclass"),el_agentparam);
				if (agentconfigs[i].agentParameter instanceof TeamAgentParameter) {
					((TeamAgentParameter) agentconfigs[i].agentParameter).setTeam(teammapping.get(el_agent.getAttribute("team")));
				}
			}

			if (simconf instanceof ServerSimulationConfiguration) {
				ServerSimulationConfiguration s = (ServerSimulationConfiguration) simconf;
				s.setSimulationName(name);
				for (int i=0;i<agents.getLength();i++) {
					Element el_agent = (Element) agents.item(i);
					s.setTeamName(i,teammapping.get(el_agent.getAttribute("team")));
				}
				s.setTournamentName(tournamentname);
			}
			
			simconf.setAgentConfigurations(agentconfigs);
			configurationDelivererController.setConfiguration((SimulationConfiguration)simconf);
		}

		@Override
		public String runSimulation() {
			broadcastObserver.start();
			
			for (int i=0;i<observer.size();i++) observer.get(i).start();
			
			getController().start();
			getSimulation().start();
			String winner = super.runSimulation();
			getSimulation().stop();
			getController().stop();

			for (int i=observer.size()-1;i>=0;i--) observer.get(i).stop();

			broadcastObserver.stop();
			return winner;
		}
	}

	public void runMatch(Element el_match, Map<String, String> teammap, String name, Node statmatchparent) throws InvalidConfigurationException {
		NodeList nl = XMLUtilities.getChildsByTagName(el_match,"simulation");

		Document statdoc = statmatchparent.getOwnerDocument();
		
		Element el_statmatch=statdoc.createElement("match");
		
		for (String a : teammap.keySet()) {
			el_statmatch.setAttribute(a,teammap.get(a));
		}
		
		statmatchparent.appendChild(el_statmatch);
		for (int i=0;i<nl.getLength();i++) {
			Element simuconfig = (Element) nl.item(i);
			String simname = tournamentname+"_";
			String[] teams=teammap.values().toArray(new String[teammap.size()]);
			for (int j=0;j<teams.length;j++) {
				simname += teams[j];
			}
			simname +="_"+simuconfig.getAttribute("id");
			sr=new ServerSimulationRun2(simuconfig,teammap,simname);
			sr.setAgentmanager(agentmanager);
			long starttime = System.currentTimeMillis();
			
			sr.runSimulation();
			long endtime = System.currentTimeMillis();
			Element n = sr.xmlstatisticsobserver.getDocument().getDocumentElement();
			Element el_statsim = statdoc.createElement("simulation");
			Element el_simresult = statdoc.createElement("result");
			
			el_statsim.setAttribute("starttime",Long.toString(starttime));
			el_statsim.setAttribute("endtime",Long.toString(endtime));
			el_statsim.setAttribute("name",simname);
			
			el_statsim.appendChild(el_simresult);
			
			el_statmatch.appendChild(el_statsim);
			el_statmatch.getOwnerDocument().adoptNode(n);
			
			for (int j=0;j<n.getAttributes().getLength();j++) {
				Attr a = (Attr) n.getAttributes().item(j);
				el_simresult.setAttribute(a.getName(),a.getValue());
			}
			for (int j=0;j<n.getChildNodes().getLength();j++) {
				el_simresult.appendChild(n.getChildNodes().item(j));
			}
		}
	}
	public int[] runMatch(Element el_match, Map<String, String> teammap,
			String name, Node statmatchparent, String team1Name,
			String team2Name) throws InvalidConfigurationException {

		NodeList nl = XMLUtilities.getChildsByTagName(el_match, "simulation");
		Document statdoc = statmatchparent.getOwnerDocument();
		Element el_statmatch = statdoc.createElement("match");

		int[] score = { 0, 0 };

		for (String a : teammap.keySet()) {
			el_statmatch.setAttribute(a, teammap.get(a));
		}

		statmatchparent.appendChild(el_statmatch);

		for (int i = 0; i < nl.getLength(); i++) {
			Element simuconfig = (Element) nl.item(i);
			String simname = tournamentname + "_";
			String[] teams = teammap.values().toArray(
					new String[teammap.size()]);

			for (int j = 0; j < teams.length; j++) {
				simname += teams[j];
			}

			simname += "_" + simuconfig.getAttribute("id");
			sr = new ServerSimulationRun2(simuconfig, teammap, simname);
			sr.setAgentmanager(agentmanager);

			long starttime = System.currentTimeMillis();
			String winner = sr.runSimulation();
			long endtime = System.currentTimeMillis();

			System.out.println(":" + winner + ":");
			System.out.println(":" + team1Name + ":");
			System.out.println(":" + team2Name + ":");

			if (winner == team1Name) {
				score[0] += 3;
			}
			if (winner == team2Name) {
				score[1] += 3;
			}
			if (winner == "draw") {
				score[0] += 1;
				score[1] += 1;
			}

			System.out.println(":" + winner + ":");
			System.out.println(score[0]);
			System.out.println(score[1]);

			Element n = sr.xmlstatisticsobserver.getDocument()
					.getDocumentElement();
			System.out.println("Ending the simulation " + i);

			Element el_statsim = statdoc.createElement("simulation");
			System.out.println("Ending the simulation " + i);

			Element el_simresult = statdoc.createElement("result");
			System.out.println("Ending the simulation " + i);

			el_statsim.setAttribute("starttime", Long.toString(starttime));
			el_statsim.setAttribute("endtime", Long.toString(endtime));
			el_statsim.setAttribute("name", simname);

			el_statsim.appendChild(el_simresult);

			el_statmatch.appendChild(el_statsim);
			el_statmatch.getOwnerDocument().adoptNode(n);

			for (int j = 0; j < n.getAttributes().getLength(); j++) {
				Attr a = (Attr) n.getAttributes().item(j);
				el_simresult.setAttribute(a.getName(), a.getValue());
			}

			for (int j = 0; j < n.getChildNodes().getLength(); j++) {
				el_simresult.appendChild(n.getChildNodes().item(j));
			}
		}

		return score;
	}

	
	public void run() throws InvalidConfigurationException {
		//initialize xml report
		try {
			xmlTournamentReport = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Element report_el_root = xmlTournamentReport
				.createElement("tournament");
		report_el_root.setAttribute("tournament-name", tournamentname);

		int[] score = new int[serversimulationagents.teams.length];

		for (int i = 0; i < score.length; i++) {
			score[0] = 0;
		}

		for (int i = 0; i < serversimulationagents.teams.length; i++) {
			Element el_team = xmlTournamentReport.createElement("team");
			el_team.setAttribute("name",serversimulationagents.teams[i]);
			report_el_root.appendChild(el_team);
		}
		
		xmlTournamentReport.appendChild(report_el_root); 
		
		agentmanager.start();
		for (int i=0;i<serversimulationagents.agents.length;i++) ((Component)serversimulationagents.agents[i]).start();
		socketlistener.start();
		//start RMI server info component
		try {
			//create RMIInfoServer
			rmiinfoserver2 = new RMIServerStatus(serversimulationagents);
			Registry r = LocateRegistry.getRegistry(RMI_Infor.RMI_PORT_DEFAULT);
			r.rebind("server2",rmiinfoserver2);
		
		} catch (RemoteException e) {
			log(LOGLEVEL_ERROR,"Error: couldn't bind RMIInfoServer");
			e.printStackTrace();
			System.exit(1);
		}

		try { 
			//create RMIXMLServer
			rmitournamentserver = new RMITournamentServer();
			Registry r = LocateRegistry.getRegistry(RMI_Infor.RMI_PORT_DEFAULT);
			r.rebind("statistics",rmitournamentserver);
		
		} catch (RemoteException e) {
			log(LOGLEVEL_ERROR,"Error: couldn't bind RMIInfoServer");
			e.printStackTrace();
			System.exit(1);
		}

		// launch synchronization
		launchSync.waitForStart();

		switch (tournamentmode) {
		case 0:
			/*
			 * Tournament mode: each team plays against each other team
			 */
			for (int t1 = 0; t1 < serversimulationagents.teams.length; t1++) {
				for (int t2 = t1 + 1; t2 < serversimulationagents.teams.length; t2++) {
					log(LOGLEVEL_NORMAL, "now playing: "
							+ serversimulationagents.teams[t1] + " vs "
							+ serversimulationagents.teams[t2]);
					Map<String, String> m = new HashMap<String, String>();
					m.put("red", serversimulationagents.teams[t1]);
					m.put("blue", serversimulationagents.teams[t2]);

					// runMatch(el_match, m, serversimulationagents.teams[t1]
					// + "_VS_" + serversimulationagents.teams[t2],
					// report_el_root);

					int[] a = runMatch(el_match, m,
							serversimulationagents.teams[t1] + "_VS_"
									+ serversimulationagents.teams[t2],
							report_el_root, serversimulationagents.teams[t1],
							serversimulationagents.teams[t2]);
					score[t1] += a[0];
					score[t2] += a[1];

					try {
						TransformerFactory.newInstance().newTransformer()
								.transform(new DOMSource(xmlTournamentReport),
										new StreamResult(System.out));
					} catch (Exception e) {
					}
				}
			}
			break;
		case 1: {
			/*
			 * Testing phase mode: each team against the Bot
			 */
			int t2 = 0;
			for (int i = 0; i < serversimulationagents.teams.length; i++)
				if (serversimulationagents.teams[i].equalsIgnoreCase("TUCBot"))
					t2 = i;
			for (int t1 = 0; t1 < serversimulationagents.teams.length; t1++) {
				// search bot team
				if (t1 == t2)
					continue;
				log(LOGLEVEL_NORMAL, "now playing: "
						+ serversimulationagents.teams[t1] + " vs "
						+ serversimulationagents.teams[t2]);
				Map<String, String> m = new HashMap<String, String>();
				m.put("red", serversimulationagents.teams[t1]);
				m.put("blue", serversimulationagents.teams[t2]);

				// runMatch(el_match, m, serversimulationagents.teams[t1] +
				// "_VS_"
				// + serversimulationagents.teams[t2], report_el_root);

				int[] a = runMatch(el_match, m,
						serversimulationagents.teams[t1] + "_VS_"
								+ serversimulationagents.teams[t2],
						report_el_root, serversimulationagents.teams[t1],
						serversimulationagents.teams[t2]);
				score[t1] += a[0];
				score[t2] += a[1];

				try {
					TransformerFactory.newInstance().newTransformer()
							.transform(new DOMSource(xmlTournamentReport),
									new StreamResult(System.out));
				} catch (Exception e) {
				}
			}
		}
		
		break;
	
		case 2: {

			for (int i = 0; i < manual.size(); i++) {

				String teamsTogether[] = manual.get(i).split("VS");
				String team1 = teamsTogether[0];
				String team2 = teamsTogether[1];

				int t1 = findTeam(team1);
				int t2 = findTeam(team2);

				log(LOGLEVEL_NORMAL, "now playing: "
						+ serversimulationagents.teams[t1] + " vs "
						+ serversimulationagents.teams[t2]);
				Map<String, String> m = new HashMap<String, String>();
				m.put("red", serversimulationagents.teams[t1]);
				m.put("blue", serversimulationagents.teams[t2]);

				runMatch(el_match, m, serversimulationagents.teams[t1] + "_VS_"
						+ serversimulationagents.teams[t2], report_el_root);

				try {
					TransformerFactory.newInstance().newTransformer()
							.transform(new DOMSource(xmlTournamentReport),
									new StreamResult(System.out));
				} catch (Exception e) {
				}

			}
		}
		
			break;
		}
	

		try {
			Registry r = LocateRegistry.getRegistry(RMI_Infor.RMI_PORT_DEFAULT);
			r.unbind("statistics");
			rmitournamentserver = null;
		
		} catch (RemoteException e) {
			log(LOGLEVEL_ERROR,"Error: couldn't unbind RMIInfoServer");
			e.printStackTrace();
			System.exit(1);
		} catch (NotBoundException e) {
			log(LOGLEVEL_ERROR,"Error: couldn't unbind RMIInfoServer");
			e.printStackTrace();
			System.exit(1);
		}

		socketlistener.stop();
		for (int i=0;i<serversimulationagents.agents.length;i++) ((Component)serversimulationagents.agents[i]).stop();
		agentmanager.stop();
		
		//write result to file
		writeTournamentReportToFile();
	}
	private int findTeam(String name) {

		int index = 0;

		for (int i = 0; i < serversimulationagents.teams.length; i++) {

			if (serversimulationagents.teams[i].equals(name)) {

				index = i;
				break;
			}
		}

		return index;
	}

	private void writeTournamentReportToFile() throws InvalidConfigurationException {
		try {
			FileOutputStream out = new FileOutputStream(xmlTournamentReportFile);
			Transformer transformer = TransformerFactory.newInstance().newTransformer(); 
			transformer.setOutputProperty(OutputKeys.INDENT,"yes");
			transformer.transform(new DOMSource(xmlTournamentReport), new StreamResult(out));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			throw new InvalidConfigurationException(e1);
		} catch (TransformerException e2) {
			e2.printStackTrace();
			throw new InvalidConfigurationException(e2);
		}
	}

	public static void main(String[] args) throws InvalidConfigurationException {
		new CopyofServer(args).run();
		
	}
}
