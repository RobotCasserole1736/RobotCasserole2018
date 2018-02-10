package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.robot.ElevatorCtrl;
import org.usfirst.frc.team1736.robot.Elevator_index;

import edu.wpi.first.wpilibj.Timer;

public class AutoEventRaiseElevatorScale extends AutoEvent {
	
	private double startTime = 0.0;
	private double currentTime = 0.0;
	private double elapsedTime = 0.0;
	private boolean weAreDone;


	@Override
	public void userStart() {
		startTime = Timer.getFPGATimestamp();
		
	}

	@Override
	public void userUpdate() {
		currentTime = Timer.getFPGATimestamp();
		elapsedTime = currentTime - startTime;
		if(elapsedTime > 1.5) {
			ElevatorCtrl.getInstance().setIndexDesired(Elevator_index.ScaleUnderscoreUp);
			if(ElevatorCtrl.getInstance().getUpperlimitSwitch() || ElevatorCtrl.getInstance().isAtDesiredHeight()) {
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTriggered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {

		return weAreDone;
	}

}
