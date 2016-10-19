package cz.cuni.d3s.adaptiveamorphousrouting.networkmovement;

import java.util.Collections;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.position.Position;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Network;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Node;

public class RobotNetPlugin implements DEECoPlugin {
	private Network network;
	
	public RobotNetPlugin() {
		network = new Network();
	}
	
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Collections.emptyList();
	}

	public void init(DEECoContainer container) {
		// TODO Auto-generated method stub

	}
	
	public Node resolveNode(Position position) {
		Node nearest = null;
		double distance = Double.POSITIVE_INFINITY;
		
		for (Node node: network.getNodes()) {
			double dx = node.getX() - nearest.getX();
			double dy = node.getY() - nearest.getY();
			double dist = Math.sqrt(dx*dx + dy*dy);
			if(nearest == null || dist < distance) {
				nearest = node;
				distance = dist;
			}
		}
		
		return nearest;
	}
}
