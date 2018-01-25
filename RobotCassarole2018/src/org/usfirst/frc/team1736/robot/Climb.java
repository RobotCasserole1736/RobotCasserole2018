package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Servo;

public class Climb {
	private static Climb singularInstance = null;
	private double currLeftWinchCmd = 0;
	private double currRightWinchCmd = 0;
	private boolean currClimbEnabledCmd = false;
	private boolean currReleaseLatchCmd = false;
	private boolean currHookReleaseCmd = false;
	private final Servo releaseLatch;
	private final Servo hookRelease;
	private double latchAngleReleased = 90; 
	private double latchAngleClosed = 0;
	private double hookAngleReleased = 90;
	private double hookAngleClosed = 0;
	
	public static synchronized Climb getInstance() {
		if ( singularInstance == null)
			singularInstance = new Climb();
		return singularInstance;
	}
	private Climb() {
		releaseLatch = new Servo(0); 
		hookRelease = new Servo(1);
		releaseLatch.set(latchAngleClosed);
		hookRelease.set(hookAngleClosed);
		
		
	}
	public void update(){
		
		if(currReleaseLatchCmd = true) {
			releaseLatch.set(latchAngleReleased);
		}else {
			releaseLatch.set(latchAngleClosed);
		}
		
		if(currHookReleaseCmd = true) {
			hookRelease.set(hookAngleReleased);
		}else {
			hookRelease.set(hookAngleClosed);
		}
		
		if(currClimbEnabledCmd) {
			
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
