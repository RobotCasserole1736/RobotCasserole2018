package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.CasserolePath;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;
import org.usfirst.frc.team1736.robot.Drivetrain;

/**
 * drive straight and stuff.
 */
public class AutoEventTest2 extends AutoEvent {
	PathPlannerAutoEvent driveForward;

	private static final double[][] waypoints = new double[][] {
		{0, 0},
		{0, 20},
		{0, 0},
		{0, 20}
	};
	
	private static final double time = 12.0;

	public AutoEventTest2() {
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

}