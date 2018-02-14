package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;

public class AutoEventBackUp extends AutoEvent {
	PathPlannerAutoEvent driveBackward;
	
	private static final double[][] waypoints = new double[][] {
		{0,0},
		{0,-24}
	};
	
	private final double time = 2.0;

	public AutoEventBackUp() {
		driveBackward = new PathPlannerAutoEvent(waypoints, time, true);
		
	}

	@Override
	public void userUpdate() {
		driveBackward.userUpdate();
		
		
	}

	@Override
	public void userForceStop() {
		 driveBackward.userForceStop();
		
	}

	@Override
	public boolean isTriggered() {
		// TODO Auto-generated method stub
		return driveBackward.isTriggered();
	}

	@Override
	public boolean isDone() {
		return driveBackward.isDone();
	}
	
	public static void main(String[] args) {
		AutoEventBackUp autoEvent = new AutoEventBackUp();
		FalconPathPlanner.plotPath(autoEvent.driveBackward.path);
	}

	@Override
	public void userStart() {
		driveBackward.userStart();
		
	}

}
