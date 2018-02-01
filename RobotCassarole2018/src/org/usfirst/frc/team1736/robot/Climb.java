package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;

import edu.wpi.first.wpilibj.Relay;
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
	private boolean currHookReleaseCmd = false;
	private double latchAngleReleased = 90; 
	private double latchAngleClosed = 0;
	
	
	private Spark leftWinchMotor1;
	private Spark leftWinchMotor2;
	private Spark rightWinchMotor1;
	private Spark rightWinchMotor2;
	private Servo releaseLatch;
	private Relay hookRelease;
	
	
	public static synchronized Climb getInstance() {
		if ( singularInstance == null)
			singularInstance = new Climb();
		return singularInstance;
	}
	
	
	private Climb() {
		CrashTracker.logGenericMessage("start of"+(this.getClass().getSimpleName()));
		releaseLatch = new Servo(RobotConstants.PWM_RELEASE_LATCH); 
		hookRelease = new Relay(RobotConstants.RELAY_HOOK_RELEASE, Relay.Direction.kForward);
		
		//Init latches and hook release to unreleased
		releaseLatch.set(latchAngleClosed);
		hookRelease.set(Relay.Value.kOff);
		
		leftWinchMotor1 = new Spark (RobotConstants.PWM_CLIMBER_LEFT_ONE);
		leftWinchMotor2 = new Spark (RobotConstants.PWM_CLIMBER_LEFT_TWO);
		rightWinchMotor1 = new Spark (RobotConstants.PWM_CLIMBER_RIGHT_ONE);
		rightWinchMotor2 = new Spark (RobotConstants.PWM_CLIMBER_RIGHT_TWO);
		
		//Init motors to off.
		leftWinchMotor1.set(0);
		leftWinchMotor2.set(0);
		rightWinchMotor1.set(0);
		rightWinchMotor2.set(0);
		CrashTracker.logGenericMessage("End of"+(this.getClass().getSimpleName()));
	}
	
	
	public void update(){
		
		
		//Interpret commands for latch/hook release 
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
		
		//Once climb is enabled, set climbing winch motors
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
	
	//Public getters and setters
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
