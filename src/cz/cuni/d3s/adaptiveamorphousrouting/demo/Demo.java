package cz.cuni.d3s.adaptiveamorphousrouting.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cz.cuni.d3s.adaptiveamorphousrouting.components.Robot;
import cz.cuni.d3s.adaptiveamorphousrouting.networkmovement.EnvironmentPlugin;
import cz.cuni.d3s.adaptiveamorphousrouting.networkmovement.RobotPlugin;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.deeco.runtimelog.RuntimeLogWriters;
import cz.cuni.mff.d3s.deeco.timer.DiscreteEventTimer;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.device.SimpleBroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.network.l2.strategy.KnowledgeInsertingStrategy;
import cz.cuni.mff.d3s.jdeeco.position.PositionPlugin;
import cz.cuni.mff.d3s.jdeeco.publishing.DefaultKnowledgePublisher;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Link;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Node;

public class Demo {
	
	private static final Random generator = new Random(42);
	private static final int MAX_NODES = 30;
	private static final int MAX_EDGES = 30;
	private static final int ROBOTS = 10;
	private static final int BRIDGES = 5;
	private static List<Node> webLeft;
	private static List<Node> webRight;
	
	private static List<Node> generateWeb(cz.cuni.mff.d3s.jdeeco.visualizer.network.Network network, int size, int edges, double xOffset, double yOffset) {
		List<Node> nodes = new ArrayList<>(); // Hold the nodes in an array
		
		// Create nodes
		for (int x = 0; x < size; x++) {			
				Node n = new Node(generator.nextInt(1000) + xOffset, generator.nextInt(1000) + yOffset);
				// Add the node to the network
				nodes.add(n);
				network.addNode(n);
		}
		
	
		// Create links
		for (int x = 0; x < edges; x++) {			
			Node a = nodes.get(generator.nextInt(size));
			Node b = nodes.get(generator.nextInt(size));
			
			network.addLink(new Link(a, b));
			network.addLink(new Link(b, a));
		}
		
		return nodes;
	}
	
	private static cz.cuni.mff.d3s.jdeeco.visualizer.network.Network createMap() {
		cz.cuni.mff.d3s.jdeeco.visualizer.network.Network network = new cz.cuni.mff.d3s.jdeeco.visualizer.network.Network();
		webLeft = generateWeb(network, MAX_NODES / 2, (MAX_EDGES - BRIDGES) / 2, -800, 0);
		webRight = generateWeb(network, MAX_NODES / 2, (MAX_EDGES - BRIDGES) / 2, 800, 0);
		
		for (int i = 0; i < BRIDGES; i++) {
			Node a = webLeft.get(generator.nextInt(MAX_NODES / 2));
			Node b = webRight.get(generator.nextInt(MAX_NODES / 2));
			
			network.addLink(new Link(a, b));
			network.addLink(new Link(b, a));			
		}
		
		return network;
	}
	

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException, IOException {
		System.out.println("Setting up DEECo simulaiton");
				
		DEECoSimulation realm = new DEECoSimulation(new DiscreteEventTimer());
		realm.addPlugin(new SimpleBroadcastDevice(25, 10, 150, 1024));
		realm.addPlugin(Network.class);
		realm.addPlugin(KnowledgeInsertingStrategy.class);
		realm.addPlugin(DefaultKnowledgePublisher.class);
		
		cz.cuni.mff.d3s.jdeeco.visualizer.network.Network map = createMap();
		Robot.network = map;
		
		realm.addPlugin(new EnvironmentPlugin(map));
		
		// nodes
		for (int nodeId = 0; nodeId < ROBOTS; ++nodeId) {
			Node source = webLeft.stream().skip(nodeId).findFirst().get();
			Node target = webRight.stream().skip(nodeId).findFirst().get();
			
			RobotPlugin robot = new RobotPlugin(nodeId, source);
			
			DEECoNode node = realm.createNode(nodeId, /*writers ,*/robot, new PositionPlugin(0, 0));
			
			

			node.deployComponent(new Robot(nodeId, robot, target));

//			node.deployEnsemble(Ensemble.class);
		}

		// Run the simulation
		System.out.println("Running the simulation.");
		realm.start(60000);
		System.out.println("All done.");
		
		Settings.createConfigFile();
		
		File file = new File("logs/runtime/network.xml");
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		writer.write(map.toString());
		writer.close();
		
		System.out.println("Sim data saved.");
	}
}
