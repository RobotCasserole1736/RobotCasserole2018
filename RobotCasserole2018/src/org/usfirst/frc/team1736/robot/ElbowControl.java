package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;
import org.usfirst.frc.team1736.lib.Util.CrashTracker;


import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Class to control the elbow motor, which raises and lowers the intake arms
 * @author Chris Gerth
 *
 */
public class ElbowControl {
	private static ElbowControl singularInstance = null;
	
	boolean curRaiseCmd = false;
	boolean curLowerCmd = false;
	boolean prevLowerCmd = false;
	boolean upperLimitReached = false;
	boolean lowerLimitReached = false;
	DigitalInput upperLimitSwitch;
	private double startTime = 0.0;
	private double currentTime = 0.0;
	private double elapsedTime = 0.0;
	
	//Cutoffs
	
	
	//Present value passed to motor. Positive means raise, negative means lower.
	double curMotorCmd = 0;
	
	Spark elbowMotor = null;
	Calibration raiseSpeedCal = null;
	Calibration lowerSpeedCal = null;
	
	
	
	public static synchronized ElbowControl getInstance() {
		if ( singularInstance == null)
			singularInstance = new ElbowControl();
		return singularInstance;
	}
	
	private ElbowControl() {
		CrashTracker.logClassInitStart(this.getClass());
		elbowMotor = new Spark(RobotConstants.PWM_ELBOW);
		raiseSpeedCal = new Calibration("Elbow Raise Speed", 0.5, 0.0, 1.0);
		lowerSpeedCal = new Calibration("Elbow Lower Speed", 0.5, 0.0, 1.0);
		upperLimitSwitch = new DigitalInput(RobotConstants.DI_ELBOW_UPPER_LIMIT_SW);
		CrashTracker.logClassInitEnd(this.getClass());
	}
	
		
	public void sampleSensors() {
		//Read Potentiometer

		if(upperLimitSwitch.get() == true) {
			upperLimitReached = true;
		} else {
			upperLimitReached = false;
		}
		
		
	}
	
	public void update() {

		//calculate motor command
		if(upperLimitReached == false && curRaiseCmd == true) {
			curMotorCmd = raiseSpeedCal.get(); 
		} else {
			curMotorCmd = 0;
		}
		
		if(curLowerCmd == true && prevLowerCmd == false) {
			startTime = Timer.getFPGATimestamp();
		}
		
		if(curLowerCmd == true) {
			elapsedTime = Timer.getFPGATimestamp() - startTime;
			if(elapsedTime < 0.25) {
				curMotorCmd = -1*raiseSpeedCal.get();
			} else {
				curMotorCmd = 0;
			}
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
		prevLowerCmd = curLowerCmd;
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
