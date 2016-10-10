package cz.cuni.d3s.adaptiveamorphousrouting.environment;

import java.util.Collections;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.runtime.PluginInitFailedException;

public class RobotPlugin implements DEECoPlugin {

	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Collections.emptyList();
	}

	public void init(DEECoContainer container) throws PluginInitFailedException {
		// TODO Auto-generated method stub

	}

}
