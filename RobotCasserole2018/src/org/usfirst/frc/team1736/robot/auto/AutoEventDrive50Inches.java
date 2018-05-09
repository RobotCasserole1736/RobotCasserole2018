package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;

/**
 * drive straight and stuff.
 */
public class AutoEventDrive50Inches extends AutoEvent {
	PathPlannerAutoEvent driveForward;

	private final double[][] waypoints = new double[][] { { 0, 0 }, { 0, 50 } };

	private final double time = 1.0;

	public AutoEventDrive50Inches() {
		driveForward = new PathPlannerAutoEvent(waypoints, time);
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

	@Override
	public void userStart() {
		driveForward.userStart();
	}

	public static void main(String[] args) {
		AutoEventDrive50Inches autoEvent = new AutoEventDrive50Inches();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
		autoEvent = new AutoEventDrive50Inches();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
	}
}