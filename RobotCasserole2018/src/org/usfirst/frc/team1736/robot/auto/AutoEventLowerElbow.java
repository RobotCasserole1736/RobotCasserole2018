package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.robot.ElbowControl;
import edu.wpi.first.wpilibj.Timer;

public class AutoEventLowerElbow extends AutoEvent {
	
	
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
		

		if(elapsedTime > 2.0 || ElbowControl.getInstance().isLowerLimitReached()) {
			//We've finished lowering
			ElbowControl.getInstance().setLowerDesired(false);
			weAreDone = true;
		} else {
			//Still need to keep on lowering.
			ElbowControl.getInstance().setLowerDesired(true);
			weAreDone = false;
		}
		
	}

	@Override
	public void userForceStop() {
		ElbowControl.getInstance().setLowerDesired(false);
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
