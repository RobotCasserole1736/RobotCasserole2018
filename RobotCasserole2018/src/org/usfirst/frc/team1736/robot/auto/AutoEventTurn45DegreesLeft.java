package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.robot.Drivetrain;
import org.usfirst.frc.team1736.robot.Gyro;

import edu.wpi.first.wpilibj.Timer;

public class AutoEventTurn45DegreesLeft extends AutoEvent {
	
	private double targetAngle;
	private boolean weAreDone;
	private double currentTime = 0.0;
	private double startTime = 0.0;
	private double elapsedTime = 0.0;
	
	final double TURN_SPEED_RPM = 100;
	final double TIMEOUT_S = 0.5;
	
	@Override
	public void userStart() {
		// get gyro
		targetAngle = Gyro.getInstance().getAngle() - 45;
		startTime = Timer.getFPGATimestamp();
	}

	@Override
	public void userUpdate() {
		//100 rpm to left
		//-110 rpm to right
		//is done = gyro read
		// gyro greater than target
		currentTime = Timer.getFPGATimestamp();
		elapsedTime = currentTime - startTime;
		Drivetrain.getInstance().disableHeadingCmd();
		Drivetrain.getInstance().setLeftWheelSpeed(-1 * TURN_SPEED_RPM);
		Drivetrain.getInstance().setRightWheelSpeed(TURN_SPEED_RPM);
		if(Gyro.getInstance().getAngle() < targetAngle || elapsedTime > TIMEOUT_S) {
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
