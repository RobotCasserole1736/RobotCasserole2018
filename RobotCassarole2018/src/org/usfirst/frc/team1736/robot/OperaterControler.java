package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class OperaterControler {
	private static OperaterControler clank = null;
	
	private XboxController operaterControler;
	
	//Singelton boilerplate
	public static synchronized OperaterControler getInstance() {
		if(clank == null)
			clank = new OperaterControler();
		return clank;
		
	}
	
	//Constructor - private because singleton
	private OperaterControler() {
		operaterControler = new XboxController(1);
	}
	
	
	/**
	 * 
	 * @return True if the operator wants the run the elevator in continuous mode, false otherwise
	 */
	public boolean getElevCntrlModeCmd() {
		boolean operaterBumper = operaterControler.getBumper(Hand.kRight);
		return operaterBumper;
	}
	
	
	/**
	 * 
	 * @return The continuous motor command from the driver. -1 is full down, 1 is full up, 0 is stop.
	 */
	public double getElevCntrlModeCmdSpeed() {
		double operaterRightTrigger = operaterControler.getTriggerAxis(Hand.kRight);
		double operaterLeftTrigger = operaterControler.getTriggerAxis(Hand.kLeft);
		double out = operaterRightTrigger - operaterLeftTrigger;
		System.out.println(out);
		return out;
		
	}
	
	public boolean getIntakeCmd() {
		boolean operaterAbutton = operaterControler.getAButton();
		return operaterAbutton;
	}
	
	public boolean getEjectCmd() {
		boolean operaterXbutton = operaterControler.getXButtonPressed();
		return operaterXbutton;
	}
	
	public boolean getIntakeOverideCmd() {
		boolean operaterStart = operaterControler.getStartButton();
		return operaterStart;
	}
	
	public boolean getPlatformLatchReleaceCmd() {
		boolean operaterL3 = operaterControler.getStickButtonPressed(Hand.kLeft);
		return operaterL3;
	}
	//still waiting on confirmation that we need this
	public boolean getRaiseCLimberCmd() {
		boolean operaterLeftBumper = operaterControler.getBumper(Hand.kLeft);
		return operaterLeftBumper;	
		
	}
	
	public double getPullLeftWinchCmd() {
		double operaterleftJoy = operaterControler.getY(Hand.kLeft);
		return operaterleftJoy;
		
	}
	
	public double getPullRightWinchCmd() {
		double operaterRightJoy = operaterControler.getY(Hand.kRight);
		return operaterRightJoy;
		}
	
	public Elevator_index getElevaterCmd() {
		int operaterDownArrow = operaterControler.getPOV();
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
