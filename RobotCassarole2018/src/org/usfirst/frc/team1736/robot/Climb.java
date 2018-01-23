package org.usfirst.frc.team1736.robot;

public class Climb {
	private static Climb singularInstance = null;
	private double currLeftWinchCmd = 0;
	private double currRightWinchCmd = 0;
	private boolean currReleaseLatchCmd = false;
	private boolean currHookReleaseCmd = false;
	
	public static synchronized Climb getInstance() {
		if ( singularInstance == null)
			singularInstance = new Climb();
		return singularInstance;
	}
	private Climb() {
		
	}
	public void update(){
		if(currReleaseLatchCmd = true) {
			
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
