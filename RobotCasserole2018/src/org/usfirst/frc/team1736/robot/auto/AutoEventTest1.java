package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;

/**
 * drive straight and stuff. Step response check (with typical smoothing)
 */
public class AutoEventTest1 extends AutoEvent {
	PathPlannerAutoEvent driveForward;

	private final double[][] waypoints = new double[][] {
		{0, 0},
		{0, 240}, 
		{12, 264}, 
		{18, 276}
	};
	
	private final double time = 5.0;

	public AutoEventTest1() {
		driveForward = new PathPlannerAutoEvent(waypoints, time, false, 0.2, 0.5, 0.001, 0.9);

		driveForward.path.setVelocityAlpha(0.001);

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
		AutoEventTest1 autoEvent = new AutoEventTest1();
		FalconPathPlanner.plotPath(autoEvent.driveForward.path);
	}
}