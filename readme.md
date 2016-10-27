# Adaptive Amorphous Routing scenario
## About
### Environment
This scenario is comprised of robots moving in a 2D space represented by a graph consisting of nodes and edges. At all times, each robot is located in a single node, and no other robot shares this node with it. Robots are able to sense their surroundings (i.e. the locations of other robots) in a limited range. Robots are capable of instantaneous move from one node to another, assuming that an edge connects both nodes and the target node is free. Otherwise, the robot is not allowed to make the move. Note that for the purposes of visualization, the movement is animated so as to allow the observer to better track the changes.

### Robot
The purpose of each robot is to reach an assigned node in the part of the graph on the right from its initial position on the left. The robot does this by using a Dijkstra algorithm to calculate the shortest path and then follows this shortest path. Waiting in its current node if the next node on the path is currently taken.

### Mapping to DEECo
The business logic of the robot is represented by a DEECo component *Robot* and its component processes. The navigation logic is separated into two phases, each realized as a separate process. The planning phase (*plan* process) is executed once per 8 seconds and consists of finding the shortest path via the Dijsktra algorithm and storing the resulting path as component knowledge. The *move* process utilizes the stored plan by moving to the next node along the plan, and removing it from the plan.

The physical representation of the robot is realized in the RobotPlugin class. This class is accessible from the Robot component and provides the sensor and actuator API for the robot, currently limited to sensing the nearby robots and moving.

## Compiling
1. Clone the repository
2. Open Eclipse, import project AdaptiveAmorphousRouting to workspace
3. Right click the project and **Maven->Update Project**
4. Done!

## Running
### Running the Demo
To run the demo, simply run Demo.launch in Eclipse. The simulation runs for a few seconds and then saves all the required data capturing the progress of the simulation in the **logs/runtime** folder. The successful termination of the simulation is signaled by the message *Sim data saved.* in the console.

### Running the Visualizer
The Visualizer is run in a similar manner to the Demo simulation, i.e. by running the included Visualizer.launch configuration. This opens an empty visualization window. In order to open the data collected from the scenario run, follow these steps:

1. Click **Scenes->Import Scene** in the menu
2. In the resulting form, click the LAST **Select...** button (located on the lower right)
3. Navigate to the **logs/runtime** folder created during the simulation run and select the *config.txt* file
4. Hit **Load!** next to the Select button to load the provided configuration file (this should cause several form fields to be filled)
5. Hit the **OK** button to finalize the import
6. Simulation visualization window appears!
7. Check the **Show Links** option on the upper left
8. Hit the **Play** button to start the visualization
9. Use the provided playback controls to inspect the captured simulation data
 

## Known Issues
* Robots only appear in the visualization after making a move
