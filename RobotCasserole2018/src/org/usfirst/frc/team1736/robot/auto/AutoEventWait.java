package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.robot.Drivetrain;

import edu.wpi.first.wpilibj.Timer;

public class AutoEventWait extends AutoEvent {
	
	double duration_s;
	double endTime;
	
	public AutoEventWait(double duration_s_in) {
		duration_s = duration_s_in;
	}

	@Override
	public void userStart() {
		endTime = Timer.getFPGATimestamp() + duration_s;
		
	}

	@Override
	public void userUpdate() {
    	Drivetrain.getInstance().setForwardReverseCommand(0);
    	Drivetrain.getInstance().setRotateCommand(0);
		
	}

	@Override
	public void userForceStop() {
    	Drivetrain.getInstance().setForwardReverseCommand(0);
    	Drivetrain.getInstance().setRotateCommand(0);
		
	}

	@Override
	public boolean isTriggered() {
		return true;
	}

	@Override
	public boolean isDone() {
		return Timer.getFPGATimestamp() >= endTime;
	}
}
