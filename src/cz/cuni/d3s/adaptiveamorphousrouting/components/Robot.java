package cz.cuni.d3s.adaptiveamorphousrouting.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cz.cuni.d3s.adaptiveamorphousrouting.networkmovement.RobotPlugin;
import cz.cuni.d3s.adaptiveamorphousrouting.networkmovement.RobotReadonly;
import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.cuni.mff.d3s.deeco.task.ProcessContext;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Dijkstra;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Link;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Network;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Node;
import cz.cuni.mff.d3s.jdeeco.visualizer.records.LeftLinkRecord;
import cz.cuni.mff.d3s.jdeeco.visualizer.records.LinkRecord;

@Component
public class Robot {
	
	public String id;
	
	public static Network network;
	
	@Local
	public RobotPlugin robot;
	
	@Local
	public Node target;	
	
	@Local
	public ArrayList<Node> plan = new ArrayList<>();
	
	@Local
	public Set<RobotReadonly> nearbyRobots = new HashSet<>();
	
	public Robot(Integer id, RobotPlugin robot, Node target) {
		this.id = id.toString();
		this.robot = robot;
		this.target = target;		
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void status(@In("id") String id, @In("robot") RobotPlugin robot) {
		System.out.println("Status of robot " + id + ": " + robot.getPosition());
	}
	
	@Process
	@PeriodicScheduling(period = 500)
	public static void senseSurroundings(@In("id") String id, @In("robot") RobotPlugin robot, @Out("nearbyRobots") ParamHolder<Set<RobotReadonly>> nearbyRobots) {
		nearbyRobots.value = robot.senseRobots();		
	}
	
	@Process
	@PeriodicScheduling(period = 8000)
	public static void plan(@In("id") String id, @In("robot") RobotPlugin robot, @In("target") Node target, @Out("plan") ParamHolder<ArrayList<Node>> plan) {
		List<Link> links = Dijkstra.getShortestPath(network, robot.getCurrentNode(), target);		
		plan.value = new ArrayList<>(links.stream().map(x -> x.getTo()).collect(Collectors.toList()));		
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void move(@In("id") String id, @In("robot") RobotPlugin robot, @InOut("plan") ParamHolder<ArrayList<Node>> plan) {
		if (plan.value.isEmpty())
			return;
		
		robot.moveTo(ProcessContext.getRuntimeLogger(), plan.value.get(0));
		plan.value.remove(0);
	}
}
