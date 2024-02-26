package drones;

import java.util.ArrayList;
import java.util.List;


import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Drone {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private boolean moved;
	private boolean full=false;
	
	public Drone(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		// get the grid location of Drone
		GridPoint pt = grid.getLocation(this);
		if(full==true) {
			
			GridCellNgh<Survivor> nghCreator = new GridCellNgh<Survivor> (grid, pt, Survivor.class, 1, 1);
			List<GridCell<Survivor>> gridCells = nghCreator.getNeighborhood(true);
			SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
			GridPoint pointWithMostSurvivors = null;
			int maxCount = -1;
			for (GridCell<Survivor> cell: gridCells) {
				if(cell.size() > maxCount) {
					pointWithMostSurvivors = cell.getPoint();
					maxCount = cell.size();
				}
			}
			moveTowards(pointWithMostSurvivors);
			rescue();
		}
		else {
			GridCellNgh<DistributionCenter> nghCreatorDC = new GridCellNgh<DistributionCenter> (grid, pt, DistributionCenter.class, 1, 1);
			List<GridCell<DistributionCenter>> gridCellsDC = nghCreatorDC.getNeighborhood(true);
			SimUtilities.shuffle(gridCellsDC, RandomHelper.getUniform());
			GridPoint pointWithDistributionCenters = null;
			int maxCountDC = -1;
			for (GridCell<DistributionCenter> cell: gridCellsDC) {
				if(cell.size() > maxCountDC) {
					pointWithDistributionCenters = cell.getPoint();
					maxCountDC = cell.size();
				}
			}
			moveTowardsDistributionCentre(pointWithDistributionCenters);
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
			moved = true;
		}
	}
	
	public void moveTowardsDistributionCentre(GridPoint pt)
	{
		// only move if we are not already in this grid location
		if(!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		}
		else {
			full = true;
		}
	}
	
	public void rescue() {
		GridPoint pt = grid.getLocation(this);
		List<Object> survivors = new ArrayList<Object>();
		for(Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if(obj instanceof Survivor) {
				survivors.add(obj);
			}
		}
		if(survivors.size()>0) {
			int index = RandomHelper.nextIntFromTo(0, survivors.size() - 1);
			Object obj = survivors.get(index);
			NdPoint spacePt = space.getLocation(obj);
			Context<Object> context = ContextUtils.getContext(obj);
			context.remove(obj);
			RescuedSurvivor rs = new RescuedSurvivor(space, grid);
			context.add(rs);
			space.moveTo(rs, spacePt.getX(), spacePt.getY());
			grid.moveTo(rs, pt.getX(), pt.getY());
			
			// Drone gives the food and medicines to the survivors
			full = false;
			
			//Network<Object> net = (Network<Object>) context.getProjection("rescured network");
			//net.addEdge(this, rs);
		}
		
	}
	
	

}