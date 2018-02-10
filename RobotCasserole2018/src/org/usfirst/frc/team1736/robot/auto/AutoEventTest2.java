package org.usfirst.frc.team1736.robot.auto;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.PathPlanner.CasserolePath;
import org.usfirst.frc.team1736.lib.PathPlanner.FalconPathPlanner;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;
import org.usfirst.frc.team1736.robot.Drivetrain;

import edu.wpi.first.wpilibj.Timer;

/**
 * Frequency response check for drivetrain PID's
 */
public class AutoEventTest2 extends AutoEvent {

	//Update these constants to define the testcase
	final double CYCLE_PERIOD_S = 7.0;
	final double AMP_RPM = 200.0;
	
	//derived constants
	final double FREQ_HZ = 1.0/CYCLE_PERIOD_S;
	
	double startTime = 0;

	public AutoEventTest2() {

	}

	@Override
	public void userUpdate() {
		
		//Generate sine wave commands
		double rpmCmd = AMP_RPM*Math.sin(2 * Math.PI * FREQ_HZ * (Timer.getFPGATimestamp()-startTime));
		
        Drivetrain.getInstance().setLeftWheelSpeed(rpmCmd);
        Drivetrain.getInstance().setRightWheelSpeed(rpmCmd);
        Drivetrain.getInstance().setDesiredPose(0.0);
	}

	@Override
	public void userForceStop() {
    	Drivetrain.getInstance().setForwardReverseCommand(0);
    	Drivetrain.getInstance().setRotateCommand(0);
	}

	@Override
	public boolean isTriggered() {
		return true; //always ready to go
	}

	@Override
	public boolean isDone() {
		return false; //never stops
	}

	@Override
	public void userStart() {
		startTime = Timer.getFPGATimestamp();

	}
}