package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;

/**
 * drive straight and stuff.
 */
public class AutoEventRightScaleToRightSwitch extends AutoEvent {
	PathPlannerAutoEvent driveForward;

	private static final double[][] waypoints = new double[][] { { 0, 0 }, { 0, 10 }, { 7, 35 }, { 7, 37 } };

	public static final double time = 4.0;

	public AutoEventRightScaleToRightSwitch() {
		driveForward = new PathPlannerAutoEvent(waypoints, time);
	}

	@Override
	public void userUpdate() {
		driveForward.userUpdate();
		// shotCTRL.setDesiredShooterState(ShooterStates.PREP_TO_SHOOT);
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
		AutoEventRightScaleToRightSwitch autoEvent = new AutoEventRightScaleToRightSwitch();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
	}
}