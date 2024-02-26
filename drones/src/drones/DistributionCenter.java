package drones;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class DistributionCenter {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	public DistributionCenter(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
}
