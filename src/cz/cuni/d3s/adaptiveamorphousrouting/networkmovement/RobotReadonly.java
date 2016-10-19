package cz.cuni.d3s.adaptiveamorphousrouting.networkmovement;

import cz.cuni.mff.d3s.jdeeco.position.Position;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Node;

public interface RobotReadonly {

	public abstract Position getPosition();

	public abstract Node getCurrentNode();
	
}
