package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;
import org.usfirst.frc.team1736.robot.Drivetrain;

import edu.wpi.first.wpilibj.Timer;

public class AutoEventBackUp extends AutoEvent {
	PathPlannerAutoEvent driveBackward;
	
	double startTime;
	
	private  double time = 2.0;
	boolean weAreDone = false;

	public AutoEventBackUp(double duration_in) {
		time = duration_in;
		
	}
	
	public AutoEventBackUp() {
		time = 2.0; //default
	}

	@Override
	public void userUpdate() {
		if(Timer.getFPGATimestamp() - startTime < time) {
			Drivetrain.getInstance().setLeftWheelSpeed(-50);
			Drivetrain.getInstance().setRightWheelSpeed(-50);
			Drivetrain.getInstance().disableHeadingCmd();
			weAreDone = false;
		} else {
			Drivetrain.getInstance().setForwardReverseCommand(0);
			Drivetrain.getInstance().setRotateCommand(0);
			weAreDone = true;
		}
		
		
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
		return weAreDone;
	}
	

	@Override
	public void userStart() {
		startTime = Timer.getFPGATimestamp();
		weAreDone = false;
		
	}

}
