package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;

public class AutoEventMoveForward228 extends AutoEvent{
	PathPlannerAutoEvent driveForward;
	
	private final double[][] waypoints = new double[][] {
		{0, 0},
		{0, 228.75}
	};
	
	private final double time = 3.0;
	
	public AutoEventMoveForward228() {
		driveForward = new PathPlannerAutoEvent(waypoints, time);
	}
	
	@Override
	public void userStart() {
		driveForward.userStart();
		
	}

	@Override
	public void userUpdate() {
		driveForward.userUpdate();
		
	}

	@Override
	public void userForceStop() {
		driveForward.userForceStop();
		
	}

	@Override
	public boolean isTriggered() {
		return driveForward.isTriggered();
		
	}

	@Override
	public boolean isDone() {
		return driveForward.isDone();
	}
	
	public static void main(String[] args) {
		AutoEventMoveForward228 autoEvent = new AutoEventMoveForward228();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
		autoEvent = new AutoEventMoveForward228();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
	}

}
