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
		if(operaterBumper == true) {
			System.out.println("the button works");
		}
		return operaterBumper;
	}
	
	/**
	 * 
	 * @return The continuous motor command from the driver. -1 is full down, 1 is full up, 0 is stop.
	 */
	public double getElevCntrlModeCmdSpeed() {
		double operaterRightTrigger = operaterControler.getRawAxis(3);
		if(operaterRightTrigger == 0.5) {
			System.out.println("the trigger works");
		}
		return operaterRightTrigger;
		
	}
	
	public boolean getintakecmd() {
		boolean operaterAbutton = operaterControler.getAButton();
		if(operaterAbutton == true) {
			System.out.println("the a button works");
		}
		return operaterAbutton;
	}
	
	public boolean getEjectcmd() {
		boolean operaterXbutton = operaterControler.getXButtonPressed();
		if(operaterXbutton == true) {
			System.out.println("the X button works");
		}
		return operaterXbutton;
	}
	
	public boolean getintakeoveridecmd() {
		boolean operaterstart = operaterControler.getStartButton();
		if(operaterstart == true) {
			System.out.println("the start button works");
		}
		return operaterstart;
	}
	
	public boolean getPlatformLatch() {
		boolean operaterL3 = operaterControler.getYButtonPressed();
		if(operaterL3 == true) {
			System.out.println("L3 works");
		}
		return operaterL3;
	}
	
	public boolean getRaisecLimber() {
		boolean operaterLeftbumper = operaterControler.getBumper(Hand.kLeft);
		if(operaterLeftbumper == true) {
			System.out.println("the left bumper works");
		}
		return operaterLeftbumper;	
		
	}
	
	public double getPullLeftWinch() {
		double operaterleftJoy = operaterControler.getY(Hand.kLeft);
		if(operaterleftJoy == 0.5) {
			System.out.println("the left joystick works");
		}
		return operaterleftJoy;
		
	}
	
	public double getPullRightWinch() {
		double operaterRightJoy = operaterControler.getY(Hand.kLeft);
		if(operaterRightJoy == 0.5) {
			System.out.println("the right joystick works");
		}
		return operaterRightJoy;
		}
	
	public Elevater_index getElevaterCmd() {
		int operaterDownArrow = operaterControler.getPOV();
		Elevater_index ReturnValue;
		ReturnValue = Elevater_index.nothingUnderscoreNew;
		if(operaterDownArrow == 0) {
			ReturnValue = Elevater_index.ScaleUnderscoreUp;
		}
		if(operaterDownArrow == 90) {
			ReturnValue = Elevater_index.Switch1;
		}
		if(operaterDownArrow == 180) {
			ReturnValue = Elevater_index.Bottom;
		}
		if(operaterDownArrow == 270) {
			ReturnValue = Elevater_index.Exchange;
		}
		return ReturnValue;
	}

}
