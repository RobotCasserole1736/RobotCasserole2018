package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.robot.ElevatorCtrl;
import org.usfirst.frc.team1736.robot.ElevatorIndex;

import edu.wpi.first.wpilibj.Timer;

public class AutoEventMoveElevator extends AutoEvent {
	
	private double startTime = 0.0;
	private double currentTime = 0.0;
	private double elapsedTime = 0.0;
	private final ElevatorIndex targetLevel;
	private boolean weAreDone = false;
	private final double delayTime;
	
	private final double HEIGHT_SEEK_TIMEOUT_S = 8.0;
	
	
	public AutoEventMoveElevator(double delay_in, ElevatorIndex level_in) {
		delayTime = delay_in;
		targetLevel = level_in;
	}
	
	@Override
	public void userStart() {
		startTime = Timer.getFPGATimestamp();
	}

	@Override
	public void userUpdate() {
		currentTime = Timer.getFPGATimestamp();
		elapsedTime = currentTime - startTime;
		
		if(elapsedTime > delayTime) {
			ElevatorCtrl.getInstance().setContModeDesired(false);
			ElevatorCtrl.getInstance().setIndexDesired(targetLevel);
			if(ElevatorCtrl.getInstance().isAtDesiredHeight() || (elapsedTime) > (delayTime + HEIGHT_SEEK_TIMEOUT_S)) {
				weAreDone = true;
			} else {
				weAreDone = false;
			}
		} else {
			weAreDone = false;
		}
		
	}

	@Override
	public void userForceStop() {
		ElevatorCtrl.getInstance().setContModeDesired(true);
		ElevatorCtrl.getInstance().setContModeCmd(0);
		
	}

	@Override
	public boolean isTriggered() {
		return true; //always ready to go!
	}

	@Override
	public boolean isDone() {
		return weAreDone;
	}

}
