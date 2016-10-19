package cz.cuni.d3s.adaptiveamorphousrouting.networkmovement;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.position.Position;
import cz.cuni.mff.d3s.jdeeco.position.PositionPlugin;
import cz.cuni.mff.d3s.jdeeco.position.PositionProvider;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Node;

public class RobotPlugin implements DEECoPlugin, PositionProvider, RobotReadonly {
	
	private Node currentNode;
	private EnvironmentPlugin environment;

	public RobotPlugin(Node source) {
		this.currentNode = source;
	}

	@Override
	public Node getCurrentNode() {
		return currentNode;
	}
	
	public boolean moveTo(Node node) {
		if (!environment.isNeighbour(currentNode, node))
			return false;
		
		if (!environment.isAvailable(node))
			return false;

		currentNode = node;		
		
		return true;
	}

	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(EnvironmentPlugin.class, PositionPlugin.class);		
	}

	public void init(DEECoContainer container) {
		PositionPlugin position = container.getPluginInstance(PositionPlugin.class);
		environment = container.getPluginInstance(EnvironmentPlugin.class);		
		
		position.setProvider(this);		
		environment.register(this);
	}

	@Override	
	public Position getPosition() {
		return new Position(currentNode.getX(), currentNode.getY());
	}	
}