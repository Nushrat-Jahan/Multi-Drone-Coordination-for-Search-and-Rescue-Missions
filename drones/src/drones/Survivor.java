package drones;

import java.util.List;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class Survivor {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	int energy, startingEnergy;
	
	public Survivor(ContinuousSpace<Object> space, Grid<Object> grid, int energy) {
		this.space = space;
		this.grid = grid;
		this.energy = startingEnergy = energy;
	}
	
	@Watch(watcheeClassName = "drones.Drone",
			watcheeFieldNames = "moved",
			query = "within_moore 1",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void run() {
		// get the grid location of Survivor
		GridPoint pt = grid.getLocation(this);
		
		// Using the GridCellNgh class to create GridCells for the surrounding neighborhood
		GridCellNgh<Drone> nghCreator = new GridCellNgh<Drone> (grid, pt, Drone.class, 1, 1);
		List<GridCell<Drone>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridPoint pointWithMostDrones = null;
		int maxCount = -1;
		for (GridCell<Drone> cell: gridCells) {
			if(cell.size() > maxCount) {
				pointWithMostDrones = cell.getPoint();
				maxCount = cell.size();
			}
		}
		if (energy > 0) {
			moveTowards(pointWithMostDrones);
		}
		else {
			energy = startingEnergy;
		}
		
	}
	
	
	public void moveTowards(GridPoint pt)
	{
		// only move if we are not already in this grid location
		if(!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
			energy--;
		}
	}

}
