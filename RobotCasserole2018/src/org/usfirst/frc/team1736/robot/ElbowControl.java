package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;
import org.usfirst.frc.team1736.lib.Util.CrashTracker;


import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.AnalogInput;

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
	double potentiometerVoltage;
	
	//Present value passed to motor. Positive means raise, negative means lower.
	double curMotorCmd = 0;
	
	

	
	Spark elbowMotor = null;
	AnalogInput potentiometer;
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
		potentiometer = new AnalogInput(1);
		CrashTracker.logClassInitEnd(this.getClass());
	}
	
	
	public void sampleSensors() {
	potentiometerVoltage = potentiometer.getVoltage();
	
		//Read Potentiometer
			
		
	if(potentiometerVoltage >= 5) {
		
			upperLimitReached = true;
		} else {
			upperLimitReached = false;
		}
		
	if(potentiometerVoltage <= 0) {
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
