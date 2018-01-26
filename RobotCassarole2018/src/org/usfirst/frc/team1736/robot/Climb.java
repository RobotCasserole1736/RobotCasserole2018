package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Spark;

public class Climb {
	private static Climb singularInstance = null;
	private double currLeftWinchCmd = 0;
	private double currRightWinchCmd = 0;
	private boolean currClimbEnabledCmd = false;
	private boolean currReleaseLatchCmd = false;
	private boolean currHookReleaseCmd = false;
	private Servo releaseLatch;
	private Relay hookRelease;
	private double latchAngleReleased = 90; 
	private double latchAngleClosed = 0;
	private Spark leftWinchMotor1;
	private Spark leftWinchMotor2;
	private Spark rightWinchMotor1;
	private Spark rightWinchMotor2;
	
	
	public static synchronized Climb getInstance() {
		if ( singularInstance == null)
			singularInstance = new Climb();
		return singularInstance;
	}
	private Climb() {
		releaseLatch = new Servo(RobotConstants.PWM_RELEASE_LATCH); 
		hookRelease = new Relay(RobotConstants.RELAY_HOOK_RELEASE, Relay.Direction.kForward);
		releaseLatch.set(latchAngleClosed);
		hookRelease.set(Relay.Value.kOff);
		
		leftWinchMotor1 = new Spark (RobotConstants.PWM_CLIMBER_LEFT_ONE);
		leftWinchMotor2 = new Spark (RobotConstants.PWM_CLIMBER_LEFT_TWO);
		rightWinchMotor1 = new Spark (RobotConstants.PWM_CLIMBER_RIGHT_ONE);
		rightWinchMotor2 = new Spark (RobotConstants.PWM_CLIMBER_RIGHT_TWO);
		leftWinchMotor1.set(0);
		leftWinchMotor2.set(0);
		rightWinchMotor1.set(0);
		rightWinchMotor2.set(0);
	}
	public void update(){
		
		if(currReleaseLatchCmd == true) {
			releaseLatch.set(latchAngleReleased);
		}else {
			releaseLatch.set(latchAngleClosed);
		}
		
		if(currHookReleaseCmd == true) {
			hookRelease.set(Relay.Value.kOn);
		}else {
			hookRelease.set(Relay.Value.kOff);
		}
		
		if(currClimbEnabledCmd) {
			leftWinchMotor1.set(Math.abs(currLeftWinchCmd));
			leftWinchMotor2.set(Math.abs(currLeftWinchCmd));
			rightWinchMotor1.set(Math.abs(currRightWinchCmd));
			rightWinchMotor2.set(Math.abs(currRightWinchCmd));
		}else {
			leftWinchMotor1.set(0);
			leftWinchMotor2.set(0);
			rightWinchMotor1.set(0);
			rightWinchMotor2.set(0);
		}
		
	}
	public void setLeftWinchCmd(double cmd) {
		currLeftWinchCmd = cmd;
		
	}
	public void setRightWinchCmd(double cmd) {
		currRightWinchCmd = cmd;
	}
	public void setReleaseLatchCmd(boolean cmd) {
		currReleaseLatchCmd = cmd;
	}
	public void setHookReleaseCmd(boolean cmd) {
		currHookReleaseCmd = cmd;
	}
	
}
