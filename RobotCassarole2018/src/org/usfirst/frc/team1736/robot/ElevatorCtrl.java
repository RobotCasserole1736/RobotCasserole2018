package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Spark;

public class ElevatorCtrl {
	private static ElevatorCtrl  singularInstance = null;
	
	private boolean continuousModeDesired;
	private double continuousModeCmd;
	private double curMotorCmd;
	private Spark motor1;
	private Spark motor2;
	
	public static synchronized ElevatorCtrl getInstance() {
		if ( singularInstance == null)
			singularInstance = new ElevatorCtrl();
		return singularInstance;
	}
	
	private ElevatorCtrl() {
		motor1 = new Spark(RobotConstants.PWM_ELEVATOR_ONE);
		motor2 = new Spark(RobotConstants.PWM_ELEVATOR_TWO);
	}
	
	
	public void update() {
		if (continuousModeDesired == true) {
			curMotorCmd = continuousModeCmd;

		} else {
			//Todo - Add Indexed mode (closed-loop). Assign curMotorCmd here.
		}
		
		motor1.set(curMotorCmd);
		motor2.set(curMotorCmd);
	}
	
	public void setIndexDesired (Elevator_index cmd) {
		//Todo: Fill me in
	}
	
	public void setContMode (boolean modecommand) {
		continuousModeDesired = modecommand;
	}
	
	public void setContModeCmd (double cmd) {
		continuousModeCmd = cmd;
	}
	
	public double getMotorCmd() {
		return curMotorCmd;
	};
}
