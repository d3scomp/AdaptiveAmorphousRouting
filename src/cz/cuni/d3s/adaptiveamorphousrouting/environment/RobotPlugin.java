package cz.cuni.d3s.adaptiveamorphousrouting.environment;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.position.Position;
import cz.cuni.mff.d3s.jdeeco.position.PositionPlugin;
import cz.cuni.mff.d3s.jdeeco.position.PositionProvider;

public class RobotPlugin implements DEECoPlugin, PositionProvider {

	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(RobotNetPlugin.class, PositionPlugin.class);		
	}

	public void init(DEECoContainer container) {
		container.getPluginInstance(PositionPlugin.class).setProvider(this);

	}

	@Override
	public Position getPosition() {
		// TODO Auto-generated method stub
		return null;
	}


}
