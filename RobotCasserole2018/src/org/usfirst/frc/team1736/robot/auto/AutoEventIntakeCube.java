package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.robot.IntakeControl;

import edu.wpi.first.wpilibj.Timer;

public class AutoEventIntakeCube extends AutoEvent {
	
	
	private double currentTime = 0.0;
	private double startTime = 0.0;
	private double elapsedTime = 0.0;
	boolean weAreDone = false;
	

	@Override
	public void userStart() {
		startTime = Timer.getFPGATimestamp();
		
	}

	@Override
	public void userUpdate() {
		currentTime = Timer.getFPGATimestamp();
		elapsedTime = currentTime - startTime;
		if(elapsedTime > 1.0) {
			IntakeControl.getInstance().setIntakeDesired(false);
			weAreDone = true;
		} else {
			IntakeControl.getInstance().setIntakeDesired(true);
			weAreDone = false;
		}
		
	}

	@Override
	public void userForceStop() {
		IntakeControl.getInstance().setIntakeDesired(false);
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
