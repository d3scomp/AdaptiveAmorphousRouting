package cz.cuni.d3s.adaptiveamorphousrouting.networkmovement;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.position.Position;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Link;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Network;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Node;

public class EnvironmentPlugin implements DEECoPlugin {
	private Network network;	
	private Set<RobotReadonly> robots;
	
	public EnvironmentPlugin(Network network) {
		this.network = network;
		this.robots = new HashSet<>();
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

	public boolean isAvailable(Node node) {
		return robots.stream().noneMatch(x -> x.getCurrentNode() == node);
	}

	public void register(RobotReadonly robot) {
		robots.add(robot); 		
	}

	public boolean isNeighbour(Node currentNode, Node node) {
		return network.getSuccessors(currentNode).contains(node);
	}

	public Set<RobotReadonly> getSurroundingRobots(RobotPlugin robotPlugin, double senseRangeM) {
		return robots.stream().
			filter(x -> x != robotPlugin).
			filter(x -> x.getPosition().euclidDistanceTo(robotPlugin.getPosition()) <= senseRangeM).
			collect(Collectors.toSet());
	}

	public Link getLink(Node currentNode, Node node) {		
		return network.getLinksFrom(currentNode).stream().filter(x -> {System.out.print(System.identityHashCode(x.getTo()) + "?=" + System.identityHashCode(node)); return x.getTo() == node; }).findFirst().get();		
	}	
}
