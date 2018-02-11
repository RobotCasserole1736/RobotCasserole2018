package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;

/**
 * go to switch on right.
 */
public class AutoEventSwitchRight extends AutoEvent {
	PathPlannerAutoEvent driveForward;

	private final double[][] waypoints = new double[][] {
		{0, 0},
		{0, 2},
		{-2, 5},
		{-3, 5},
		{-5, 9},
		{-5, 11},
	};
	
	private final double time = 4.0;

	public AutoEventSwitchRight() {
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
		AutoEventSwitchRight autoEvent = new AutoEventSwitchRight();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
	}
}