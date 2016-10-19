package cz.cuni.d3s.adaptiveamorphousrouting.components;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cz.cuni.d3s.adaptiveamorphousrouting.networkmovement.RobotPlugin;
import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Dijkstra;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Link;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Network;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Node;

@Component
public class Robot {
	
	public String id;
	
	public static Network network;
	
	@Local
	public RobotPlugin robot;
	
	@Local
	public Node target;	
	
	public ArrayList<Node> plan = new ArrayList<>();
	
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
		
		robot.moveTo(plan.value.get(0));
		plan.value.remove(0);
	}
}
