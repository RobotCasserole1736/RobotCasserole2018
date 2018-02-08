package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.CasserolePath;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;
import org.usfirst.frc.team1736.robot.Drivetrain;

/**
 * drive straight and stuff. Step response check (with typical smoothing)
 */
public class AutoEventTest1Reversed extends AutoEvent {
	PathPlannerAutoEvent driveForward;

	private static final double[][] waypoints = new double[][] {
		{0,0},
		{-0.5,-1},
		{-1.5,-3},
		{-1.5,-23}

	};
	
	private static final double time = 5.0;

	public AutoEventTest1Reversed() {
		driveForward = new PathPlannerAutoEvent(waypoints, time, true);

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