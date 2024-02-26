package drones;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;                                                                                                                          
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.WrapAroundBorders; 
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;



public class DronesBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		context.setId("drones");
		
		//NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("infection network", context, true);
		//netBuilder.buildNetwork();
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, 
				new RandomCartesianAdder<Object>(), 
				new repast.simphony.space.continuous.WrapAroundBorders(),
				50, 50);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, 
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
				new  SimpleGridAdder<Object>(),
				true, 50, 50));
		
		Parameters params = RunEnvironment.getInstance().getParameters();
	
		// Drones
		int droneCount = params.getInteger("drone_count");
		for(int i=0; i<droneCount; i++) {
			context.add(new Drone(space, grid));
		}
		
		// Survivor
		int survivorCount = params.getInteger("survivor_count");
		for(int i=0; i<survivorCount; i++) {
			int energy = RandomHelper.nextIntFromTo(4,10);
			context.add(new Survivor(space, grid, energy));
		}
		
		// Rescued Survivor
		int rescuedSurvivorCount = params.getInteger("rescuedSurvivor_count");
		for(int i=0; i<rescuedSurvivorCount; i++) {
			context.add(new RescuedSurvivor(space, grid));
		}
		// Distribution Center
		int distributionCenterCount = params.getInteger("distributionCenter_count");;
		for(int i=0; i<distributionCenterCount; i++) {
			context.add(new DistributionCenter(space, grid));
		}
		
		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj,  (int)pt.getX(), (int)pt.getY());
		}
		return context;
	}

}
