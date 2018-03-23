 package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;

/**
 * drive straight and stuff. Step response check (with typical smoothing)
 */
public class AutoEventBackUpFromPyramid extends AutoEvent {
	PathPlannerAutoEvent driveBackward;

	private final double[][] waypoints = new double[][] {
		{0,0},
		{0,-71}
	};
	
	private final double time = 2.0;

	public AutoEventBackUpFromPyramid() {
		driveBackward = new PathPlannerAutoEvent(waypoints, time, true, 0.2, 0.5, 0.001, 0.9);
	}

	@Override
	public void userUpdate() {
		driveBackward.userUpdate();
		// shotCTRL.setDesiredShooterState(ShooterStates.PREP_TO_SHOOT);
	}

	@Override
	public void userForceStop() {
		driveBackward.userForceStop();
	}

	@Override
	public boolean isTriggered() {
		return driveBackward.isTriggered();
	}

	@Override
	public boolean isDone() {
		return driveBackward.isDone();
	}

	@Override
	public void userStart() {
		driveBackward.userStart();
	}
    public static void main(String[] args) {
    	AutoEventBackUpFromPyramid autoEvent = new AutoEventBackUpFromPyramid();
		FalconPathPlanner.plotPath(autoEvent.driveBackward.path);
	}
}
