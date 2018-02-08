package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;

/**
 * go to scale on right.
 */
public class AutoEventScaleRight extends AutoEvent {
	PathPlannerAutoEvent driveForward; 

	private static final double[][] waypoints = new double[][] {
		{0, 0},
		{0, 20}, 
		{-1, 22}, 
		{-1.5, 23}
	};
	
	private static final double time = 4.0;

	public AutoEventScaleRight() {
		driveForward = new PathPlannerAutoEvent(waypoints, time);
		driveForward.path.setVelocityAlpha(0.001);
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

}