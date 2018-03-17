package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Spark;


// Climber mechanism consists of two winches (each powered by two motors). 
//  One winch on the left, one winch on the right. The operator currently manually controls both.
//  Since the winches are on 1-way ratchets, there is a safety button which must be held in order for
//  the winch to retract.
// One solenoid is used to hold the hooks for the climber in place. At endgame, the operator will command
//  the hooks to be released and carried up by the elevator.
// One servo is used to release a platform at endgame for a partner to climb with us.
public class Climb {
	private static Climb singularInstance = null;
	
	
	private double currLeftWinchCmd = 0;
	private double currRightWinchCmd = 0;
	private boolean currClimbEnabledCmd = false;
	private boolean currReleaseLatchCmd = false;
	private double latchAngleCmd = 0;
	
	private final double LATCH_ANGLE_RELEASED = 90; 
	private final double LATCH_ANGLE_CLOSED = 0;
	
	
	private Spark leftWinchMotor1;
	private Spark leftWinchMotor2;
	private Spark rightWinchMotor1;
	private Spark rightWinchMotor2;
	private Servo releaseLatch;
	
	
	public static synchronized Climb getInstance() {
		if ( singularInstance == null)
			singularInstance = new Climb();
		return singularInstance;
	}
	
	
	private Climb() {
		CrashTracker.logClassInitStart(this.getClass());
		releaseLatch = new Servo(RobotConstants.PWM_RELEASE_LATCH); 
		
		//Init latches and hook release to unreleased
		latchAngleCmd = LATCH_ANGLE_CLOSED;
		releaseLatch.set(latchAngleCmd);
		
		leftWinchMotor1 = new Spark (RobotConstants.PWM_CLIMBER_LEFT_ONE);
		leftWinchMotor2 = new Spark (RobotConstants.PWM_CLIMBER_LEFT_TWO);
		rightWinchMotor1 = new Spark (RobotConstants.PWM_CLIMBER_RIGHT_ONE);
		rightWinchMotor2 = new Spark (RobotConstants.PWM_CLIMBER_RIGHT_TWO);
		
		//Init motors to off.
		leftWinchMotor1.set(0);
		leftWinchMotor2.set(0);
		rightWinchMotor1.set(0);
		rightWinchMotor2.set(0);
		CrashTracker.logClassInitEnd(this.getClass());
	}
	
	
	public void update(){
		
		
		//Interpret commands for latch/hook release 
		if(currReleaseLatchCmd == true) {
			latchAngleCmd = LATCH_ANGLE_RELEASED;
		}else {
			latchAngleCmd = LATCH_ANGLE_CLOSED;
		}
		
		releaseLatch.set(latchAngleCmd);

		
		if(!currClimbEnabledCmd) {
			//Inhibit climb if not enabled
			currLeftWinchCmd = 0;
			currRightWinchCmd = 0;
		} else {
			//Ensure we have the absolute value of the commands
			currLeftWinchCmd = Math.abs(currLeftWinchCmd);
			currRightWinchCmd = Math.abs(currRightWinchCmd);
		}
		
		//Assign outputs to motors
		leftWinchMotor1.set(currLeftWinchCmd);
		leftWinchMotor2.set(currLeftWinchCmd);
		rightWinchMotor1.set(currRightWinchCmd);
		rightWinchMotor2.set(currRightWinchCmd);
		
	}
	
	//Public getters and setters
	public void setLeftWinchCmd(double cmd) {
		currLeftWinchCmd = cmd;	
	}
	public void setRightWinchCmd(double cmd) {
		currRightWinchCmd = cmd;
	}
	public double getLeftWinchCmd() {
		return currLeftWinchCmd;	
	}
	public double getRightWinchCmd() {
		return currRightWinchCmd;
	}
	public void setReleaseLatchCmd(boolean cmd) {
		currReleaseLatchCmd = cmd;
	}
	public void setClimbEnabledCmd(boolean cmd) {
		currClimbEnabledCmd = cmd;
	}
	public boolean getClimbEnabledCmd() {
		return currClimbEnabledCmd;
	}
	public double getLatchAngleCmd() {
		return latchAngleCmd;
	}
	
}
