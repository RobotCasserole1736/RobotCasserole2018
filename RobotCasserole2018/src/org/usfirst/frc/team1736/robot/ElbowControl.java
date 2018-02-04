package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;
import org.usfirst.frc.team1736.lib.Util.CrashTracker;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Spark;


/**
 * Class to control the elbow motor, which raises and lowers the intake arms
 * @author Chris Gerth
 *
 */
public class ElbowControl {
	private static ElbowControl singularInstance = null;
	
	boolean curRaiseCmd = false;
	boolean curLowerCmd = false;
	boolean upperLimitReached = false;
	boolean lowerLimitReached = false;
	
	//Present value passed to motor. Positive means raise, negative means lower.
	double curMotorCmd = 0;
	
	
	DigitalInput upperLimitSw = null;
	DigitalInput lowerLimitSw = null;
	Spark elbowMotor = null;
	
	Calibration raiseSpeedCal = null;
	Calibration lowerSpeedCal = null;
	
	
	
	public static synchronized ElbowControl getInstance() {
		if ( singularInstance == null)
			singularInstance = new ElbowControl();
		return singularInstance;
	}
	
	private ElbowControl() {
		CrashTracker.logGenericMessage("start of"+(this.getClass().getSimpleName()));
		elbowMotor = new Spark(RobotConstants.PWM_ELBOW);
		upperLimitSw = new DigitalInput(RobotConstants.DI_ELBOW_UP_LIMIT_SW);
		lowerLimitSw = new DigitalInput(RobotConstants.DI_ELBOW_DOWN_LIMIT_SW);
		raiseSpeedCal = new Calibration("Elbow Raise Speed", 0.5, 0.0, 1.0);
		lowerSpeedCal = new Calibration("Elbow Lower Speed", 0.5, 0.0, 1.0);
		CrashTracker.logGenericMessage("End of"+(this.getClass().getSimpleName()));
	}
	
	public void sampleSensors() {
		
		//Read Limit Switches
		if(upperLimitSw.get()) {
			upperLimitReached = true;
		} else {
			upperLimitReached = false;
		}
		
		if(lowerLimitSw.get()) {
			lowerLimitReached = true;
		} else {
			lowerLimitReached = false;
		}
	}
	
	public void update() {

		//calculate motor command
		if(upperLimitReached == false && curRaiseCmd == true) {
			curMotorCmd = raiseSpeedCal.get();
		} else if(lowerLimitReached == false && curLowerCmd == true) {
			curMotorCmd = -1*lowerSpeedCal.get();
		} else {
			curMotorCmd = 0;
		}
		
		//Set the motor command to the motor
		elbowMotor.set(curMotorCmd);
		
	}
	
	
	//Public Getters and Setters
	public void setRaiseDesired(boolean cmd) {
		curRaiseCmd = cmd;
	}
	
	public void setLowerDesired(boolean cmd) {
		curLowerCmd = cmd;
	}
	
	public boolean isUpperLimitReached() {
		return upperLimitReached;
	}
	
	public boolean isLowerLimitReached() {
		return lowerLimitReached;
	}
	
	public double getMotorCmd() {
		return curMotorCmd;
	}

}