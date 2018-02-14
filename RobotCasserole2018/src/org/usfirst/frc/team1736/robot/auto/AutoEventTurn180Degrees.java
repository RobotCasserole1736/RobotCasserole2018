package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.robot.Drivetrain;
import org.usfirst.frc.team1736.robot.Gyro;

public class AutoEventTurn180Degrees extends AutoEvent {
	
	private double targetAngle;
	private boolean weAreDone;
	@Override
	public void userStart() {
		// get gyro
		targetAngle = Gyro.getInstance().getAngle() + 180;
	}

	@Override
	public void userUpdate() {
		//100 rpm to left
		//-110 rpm to right
		//is done = gyro read
		// gyro greater than target
		Drivetrain.getInstance().setLeftWheelSpeed(100);
		Drivetrain.getInstance().setRightWheelSpeed(-100);
		if(Gyro.getInstance().getAngle() > targetAngle) {
			weAreDone = true;
			Drivetrain.getInstance().setLeftWheelSpeed(0);
			Drivetrain.getInstance().setRightWheelSpeed(0);
		}else {
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
		return true;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return weAreDone;
	}

}
