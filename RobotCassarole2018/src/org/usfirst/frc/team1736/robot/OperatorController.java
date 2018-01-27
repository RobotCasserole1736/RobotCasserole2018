package org.usfirst.frc.team1736.robot;

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
		operatorController = new XboxController(1);
	}
	
	
	/**
	 * 
	 * @return True if the operator wants the run the elevator in continuous mode, false otherwise
	 */
	public boolean getElevCntrlModeCmd() {
		boolean operaterBumper = operatorController.getBumper(Hand.kRight);
		return operaterBumper;
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
		boolean operaterXbutton = operatorController.getXButtonPressed();
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
		boolean operaterL3 = operatorController.getStickButtonPressed(Hand.kLeft);
		return operaterL3;
	}
	
	public boolean getHookReleaseCmd() {
		boolean operatorR3 = operatorController.getStickButtonPressed(Hand.kRight);
		return operatorR3;
	}

	//Right now this both getClimbEnabledCmd and getRaiseClimberCmd are on the Left Bumper. This will probably change later.
	public boolean getClimbEnabledCmd() {
		boolean operaterLeftBumper = operatorController.getBumper(Hand.kLeft);
		return operaterLeftBumper;
		
	}
	//still waiting on confirmation that we need this
	public boolean getRaiseCLimberCmd() {
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
	
	public Elevator_index getElevaterCmd() {
		int operaterDownArrow = operatorController.getPOV();
		Elevator_index ReturnValue;
		ReturnValue = Elevator_index.nothingUnderscoreNew;
		if(operaterDownArrow == 0) {
			ReturnValue = Elevator_index.ScaleUnderscoreUp;
		}
		if(operaterDownArrow == 90) {
			ReturnValue = Elevator_index.Switch1;
		}
		if(operaterDownArrow == 180) {
			ReturnValue = Elevator_index.Bottom;
		}
		if(operaterDownArrow == 270) {
			ReturnValue = Elevator_index.Exchange;
		}
		return ReturnValue;
	}

}
