package cz.cuni.d3s.adaptiveamorphousrouting.components;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;

@Component
public class Robot {
	
	public String id;
	
	public Robot(Integer id) {
		this.id = id.toString();
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void status(@In("id") String id) {
		System.out.println("Status: " + id);
	}
	
	
	
}
