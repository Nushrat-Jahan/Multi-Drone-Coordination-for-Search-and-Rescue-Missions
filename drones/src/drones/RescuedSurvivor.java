package drones;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class RescuedSurvivor {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	public RescuedSurvivor(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
}
