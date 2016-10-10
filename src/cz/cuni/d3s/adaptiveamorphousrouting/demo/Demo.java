package cz.cuni.d3s.adaptiveamorphousrouting.demo;

import cz.cuni.d3s.adaptiveamorphousrouting.components.Robot;
import cz.cuni.d3s.adaptiveamorphousrouting.environment.RobotPlugin;
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

public class Demo {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {
		System.out.println("Setting up DEECo simulaiton");
				
		DEECoSimulation realm = new DEECoSimulation(new DiscreteEventTimer());
		realm.addPlugin(new SimpleBroadcastDevice(25, 10, 150, 1024));
		realm.addPlugin(Network.class);
		realm.addPlugin(KnowledgeInsertingStrategy.class);
		realm.addPlugin(DefaultKnowledgePublisher.class);
		
		// nodes
		for (int nodeCnt = 0; nodeCnt < 5; ++nodeCnt) {			
			DEECoNode node = realm.createNode(nodeCnt, new RobotPlugin(), new PositionPlugin(0, 0));

			node.deployComponent(new Robot(nodeCnt));

//			node.deployEnsemble(Ensemble.class);
		}

		// Run the simulation
		System.out.println("Running the simulation.");
		realm.start(60000);
		System.out.println("All done.");
	}

}
