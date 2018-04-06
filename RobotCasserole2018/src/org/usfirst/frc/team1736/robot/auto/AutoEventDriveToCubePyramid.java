package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;
import org.usfirst.frc.team1736.robot.Gyro;



/**
 * drive straight and stuff.
 */
public class AutoEventDriveToCubePyramid extends AutoEvent {
	PathPlannerAutoEvent driveForward;

	private final double[][] waypoints = new double[][] {
		{0, 0},
		{0, 50}
	};
	
	public static final double time = 2.25;

	public AutoEventDriveToCubePyramid() {
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
		driveForward.setDesiredHeadingOverride(Gyro.getInstance().getAngle());
	}
	
	
    public static void main(String[] args) {
    	AutoEventDriveToCubePyramid autoEvent = new AutoEventDriveToCubePyramid();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
		autoEvent = new AutoEventDriveToCubePyramid();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
	}
}