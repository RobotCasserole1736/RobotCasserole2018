package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;

/**
 * go to scale on right.
 */
public class AutoEventScaleRight extends AutoEvent {
	PathPlannerAutoEvent driveForward; 

	private final double[][] waypoints = new double[][] {
		{0, 0},
		{0, 100}, 
		{0, 200}, 
		{-10, 220},
		{-25.0, 245}, 
		{-25.0, 258}
	};
	
	private final double time = 10.0;

	public AutoEventScaleRight() {
		driveForward = new PathPlannerAutoEvent(waypoints, time, false, 0.5, 0.5, 0.001, 0.9);
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
		AutoEventScaleRight autoEvent = new AutoEventScaleRight();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
	}
}