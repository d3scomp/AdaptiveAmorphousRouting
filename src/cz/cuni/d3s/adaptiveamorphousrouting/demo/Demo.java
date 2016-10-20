package cz.cuni.d3s.adaptiveamorphousrouting.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.cuni.d3s.adaptiveamorphousrouting.components.Robot;
import cz.cuni.d3s.adaptiveamorphousrouting.networkmovement.EnvironmentPlugin;
import cz.cuni.d3s.adaptiveamorphousrouting.networkmovement.RobotPlugin;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
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
	private static final int MAX_NODES = 10;
	private static final int MAX_EDGES = 10;
	private static final int ROBOTS = 4;
	
	private static cz.cuni.mff.d3s.jdeeco.visualizer.network.Network createMap() {
		cz.cuni.mff.d3s.jdeeco.visualizer.network.Network network = new cz.cuni.mff.d3s.jdeeco.visualizer.network.Network();
		List<Node> nodes = new ArrayList<>(); // Hold the nodes in an array		
		
		
		
		// Create nodes
		for (int x = 0; x < MAX_NODES; x++) {			
				Node n = new Node(generator.nextInt(1000), generator.nextInt(1000));
				// Add the node to the network
				nodes.add(n);
				network.addNode(n);
		}
		
	
		// Create links
		for (int x = 0; x < MAX_EDGES; x++) {			
			Node a = nodes.get(generator.nextInt(MAX_NODES));
			Node b = nodes.get(generator.nextInt(MAX_NODES));
			
			network.addLink(new Link(a, b));
			network.addLink(new Link(b, a));
		}		
		
		return network;
	}
	

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {
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
			Node source = map.getNodes().stream().skip(generator.nextInt(MAX_NODES-1)).findFirst().get();
			Node target = map.getNodes().stream().skip(generator.nextInt(MAX_NODES-1)).findFirst().get();
			
			RobotPlugin robot = new RobotPlugin(source);
			
			DEECoNode node = realm.createNode(nodeId, robot, new PositionPlugin(0, 0));
			
			

			node.deployComponent(new Robot(nodeId, robot, target));

//			node.deployEnsemble(Ensemble.class);
		}

		// Run the simulation
		System.out.println("Running the simulation.");
		realm.start(60000);
		System.out.println("All done.");
	}

}
