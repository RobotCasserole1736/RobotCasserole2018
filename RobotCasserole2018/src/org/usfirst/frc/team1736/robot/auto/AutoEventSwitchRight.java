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
		{0, 145}, 
		{-12, 150}, 
		{-24, 154}, 
		{-30, 154}, 
		{-35, 154}
	};
	
	private final double time = 4.0;

	public AutoEventSwitchRight() {
		driveForward = new PathPlannerAutoEvent(waypoints, time, false, 0.7, 0.1, 0.005, 0.9);
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