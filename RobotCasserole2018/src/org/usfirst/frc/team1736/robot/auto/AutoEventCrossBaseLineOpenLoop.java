package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.robot.Drivetrain;

import edu.wpi.first.wpilibj.Timer;


/**
 * drive straight and stuff.
 */
public class AutoEventCrossBaseLineOpenLoop extends AutoEvent {
	final double drive_speed = 0.4; //motor command
	final double drive_time = 4.0; //seconds
	
	double start_time = 0;
	
	boolean finished = false;


	public AutoEventCrossBaseLineOpenLoop() {
		//nothing to do
	}

	@Override
	public void userUpdate() {
		if(Timer.getFPGATimestamp() - start_time > drive_time) {
			finished = true;
		} else {
			finished = false;
		}
		
		if(finished) {
			Drivetrain.getInstance().setForwardReverseCommand(0.0);
		} else {
			Drivetrain.getInstance().setForwardReverseCommand(drive_speed);
		}
	}

	@Override
	public void userForceStop() {
		Drivetrain.getInstance().setForwardReverseCommand(0.0);
		finished = true;
	}

	@Override
	public boolean isTriggered() {
		return true;
	}

	@Override
	public boolean isDone() {
		return finished;
	}

	@Override
	public void userStart() {
		start_time = Timer.getFPGATimestamp();
		finished = false;
	}

}