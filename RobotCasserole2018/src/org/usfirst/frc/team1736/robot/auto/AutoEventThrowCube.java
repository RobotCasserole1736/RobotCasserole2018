package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.robot.IntakeControl;

import edu.wpi.first.wpilibj.Timer;

public class AutoEventThrowCube extends AutoEvent {

	private double currentTime = 0.0;
	private double startTime = 0.0;
	private double elapsedTime = 0.0;
	private double delayTime = 0.0;
	boolean weAreDone = false;
	private final double EJECT_TIME = 2.0;

	public AutoEventThrowCube() {
		delayTime = 0.0;
	}

	public AutoEventThrowCube(double delay_in) {
		delayTime = Math.min(EJECT_TIME, delay_in);
	}

	@Override
	public void userStart() {
		startTime = Timer.getFPGATimestamp();

	}

	@Override
	public void userUpdate() {
		currentTime = Timer.getFPGATimestamp();
		elapsedTime = currentTime - startTime;
		if (elapsedTime < delayTime) {
			IntakeControl.getInstance().setThrowDesired(false);
			weAreDone = false;
		} else if (elapsedTime > EJECT_TIME) {
			IntakeControl.getInstance().setThrowDesired(false);
			weAreDone = true;
		} else {
			IntakeControl.getInstance().setThrowDesired(true);
			weAreDone = false;
		}

	}

	@Override
	public void userForceStop() {
		IntakeControl.getInstance().setEjectDesired(false);
	}

	@Override
	public boolean isTriggered() {
		return true;
	}

	@Override
	public boolean isDone() {
		return weAreDone;
	}

}
