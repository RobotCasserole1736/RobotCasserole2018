package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class OperatorController {
	private static OperatorController clank = null;
	
	private XboxController operatorController;
	
	//Singelton boilerplate
	public static synchronized OperatorController getInstance() {
		if(clank == null)
			clank = new OperatorController();
		return clank;
		
	}
	
	//Constructor - private because singleton
	private OperatorController() {
		CrashTracker.logClassInitStart(this.getClass());
		operatorController = new XboxController(1);
		CrashTracker.logClassInitEnd(this.getClass());

	}
	
	
	/**
	 * 
	 * @return True if the operator wants the run the elevator in continuous mode, false otherwise
	 */
	public boolean getElevCntrlModeCmd() {
		boolean operaterBumper = operatorController.getBumper(Hand.kRight);
		boolean triggersPressed = false;
		
		if( 
			operatorController.getTriggerAxis(Hand.kRight) > 0.05 ||
			operatorController.getTriggerAxis(Hand.kLeft) > 0.05
		  ) {
			triggersPressed = true;
		}
		
		return operaterBumper || triggersPressed;
	}
	
	
	/**
	 * 
	 * @return The continuous motor command from the driver. -1 is full down, 1 is full up, 0 is stop.
	 */
	public double getElevCntrlModeCmdSpeed() {

		double operaterRightTrigger = operatorController.getTriggerAxis(Hand.kRight);
		double operaterLeftTrigger = operatorController.getTriggerAxis(Hand.kLeft);
		double out = operaterRightTrigger - operaterLeftTrigger;
		return out;

	}
	
	public boolean getIntakeCmd() {
		boolean operaterAbutton = operatorController.getAButton();
		return operaterAbutton;
	}
	
	public boolean getEjectCmd() {
		boolean operaterXbutton = operatorController.getXButton();
		return operaterXbutton;
	}
	
	public boolean getThrowCmd() {
		boolean operaterBbutton = operatorController.getBButton();
		return operaterBbutton;
	}
	
	public boolean getIntakeOverideCmd() {
		boolean operaterStart = operatorController.getStartButton();
		return operaterStart;
	}
	
	public boolean getPlatformLatchReleaseCmd() {
		boolean operaterL3 = operatorController.getStickButton(Hand.kLeft);
		return operaterL3;
	}

	public boolean getClimbEnabledCmd() {
		boolean operaterLeftBumper = operatorController.getBumper(Hand.kLeft);
		return operaterLeftBumper;
		
	}
	
	public double getPullLeftWinchCmd() {
		double operaterleftJoy = operatorController.getY(Hand.kLeft);
		return operaterleftJoy;
		
	}
	

	public double getPullRightWinchCmd() {
		double operaterRightJoy = operatorController.getY(Hand.kRight);
		return operaterRightJoy;
	}
	
	public ElevatorIndex getElevaterCmd() {
		int operaterDownArrow = operatorController.getPOV();
		ElevatorIndex ReturnValue;
		ReturnValue = ElevatorIndex.NO_NEW_SELECTION;
		if(operaterDownArrow == 0) {
			ReturnValue = ElevatorIndex.SCALE_UP;
		}
		if(operaterDownArrow == 90) {
			ReturnValue = ElevatorIndex.SWITCH;
		}
		if(operaterDownArrow == 180) {
			ReturnValue = ElevatorIndex.BOTTOM;
		}
		if(operaterDownArrow == 270) {
			ReturnValue = ElevatorIndex.EXCHANGE;
		}
		return ReturnValue;
	}

}
