package cz.cuni.d3s.adaptiveamorphousrouting.networkmovement;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.runtimelog.RuntimeLogger;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.position.Position;
import cz.cuni.mff.d3s.jdeeco.position.PositionPlugin;
import cz.cuni.mff.d3s.jdeeco.position.PositionProvider;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Link;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Node;
import cz.cuni.mff.d3s.jdeeco.visualizer.records.EnteredLinkRecord;
import cz.cuni.mff.d3s.jdeeco.visualizer.records.LeftLinkRecord;
import cz.cuni.mff.d3s.jdeeco.visualizer.records.LinkRecord;

public class RobotPlugin implements DEECoPlugin, PositionProvider, RobotReadonly {
	private class ArrivalListener implements TimerTaskListener {
		final private Link destination;
		final private TimerTask arrivalTask;

		public ArrivalListener(Link destination, long delayMs) {
			this.destination = destination;
			arrivalTask = new TimerTask(scheduler, this, "RobotPlugin_arrival_task", delayMs);
			arrivalTask.schedule();
		}

		@Override
		public void at(long time, Object triger) {
			System.out.println("Robot " + robotId + " arrived at " + destination.getId());
			
			LinkRecord recordLeave = new LeftLinkRecord(robotId);
			recordLeave.setLink(destination);
			recordLeave.setPerson(robotId);
			
			try {
				logger.log(recordLeave);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			
			inProgress = false;
						
			arrivalTask.unSchedule();
		}
	}
	
	private String robotId;
	private Node currentNode;
	private EnvironmentPlugin environment;
	private Scheduler scheduler;
	private RuntimeLogger logger;
	private boolean inProgress;
	
	public static final double SENSE_RANGE_M = 4200;

	public RobotPlugin(int nodeId, Node source) {
		this.robotId = Integer.toString(nodeId);
		this.currentNode = source;
		this.inProgress = false;
	}

	@Override
	public Node getCurrentNode() {
		return currentNode;
	}
	
	public boolean moveTo(Node node) {
		if (!environment.isNeighbour(currentNode, node))
			return false;

		if(inProgress)
			return false;
		
		if (!environment.isAvailable(node))
			return false;
		
		forceMove(node);
		
		return true;
	}
	
	private void forceMove(Node node) {
		inProgress = true;
		
		Link traversedLink = environment.getLink(currentNode, node);
		
		LinkRecord recordEnter = new EnteredLinkRecord(robotId);		
		recordEnter.setLink(traversedLink);
		recordEnter.setPerson(robotId);
				
		try {
			logger.log(recordEnter);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}		

		new ArrivalListener(traversedLink, 500);
		
		currentNode = node;
	}

	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(EnvironmentPlugin.class, PositionPlugin.class);		
	}

	public void init(DEECoContainer container) {
		scheduler = container.getRuntimeFramework().getScheduler();
		logger = container.getRuntimeLogger();
		PositionPlugin position = container.getPluginInstance(PositionPlugin.class);
		environment = container.getPluginInstance(EnvironmentPlugin.class);		
		
		position.setProvider(this);
		environment.register(this);
		
//		Node via = environment.getRandomNeigbour(currentNode);
//		forceMove(via);
	}

	@Override	
	public Position getPosition() {
		return new Position(currentNode.getX(), currentNode.getY());
	}	
	
	
	public Set<RobotReadonly> senseRobots() {
		return environment.getSurroundingRobots(this, SENSE_RANGE_M);
	}
}