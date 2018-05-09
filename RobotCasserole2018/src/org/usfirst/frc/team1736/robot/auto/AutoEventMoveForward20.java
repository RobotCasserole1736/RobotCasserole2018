package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;

public class AutoEventMoveForward20 extends AutoEvent {
	PathPlannerAutoEvent driveForward;

	private final double[][] waypoints = new double[][] { { 0, 0 }, { 0, 20 } };

	private final double time = 1.0;

	public AutoEventMoveForward20() {
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
		AutoEventMoveForward20 autoEvent = new AutoEventMoveForward20();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
		autoEvent = new AutoEventMoveForward20();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
	}
}