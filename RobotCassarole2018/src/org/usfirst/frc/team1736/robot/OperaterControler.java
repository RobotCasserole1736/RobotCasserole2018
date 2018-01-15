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
		double operaterRightTrigger = operaterControler.getRawAxis(3);
		return operaterRightTrigger;
	}
	
	public boolean getintakecmd() {
		boolean operaterAbutton = operaterControler.getAButton();
		return operaterAbutton;
	}
	
	public boolean getEjectcmd() {
		boolean operaterXbutton = operaterControler.getXButtonPressed();
		return operaterXbutton;
	}
	
	public boolean getintakeoveridecmd() {
		boolean operaterstart = operaterControler.getStartButton();
		return operaterstart;
	}
	
	public boolean getPlatformLatch() {
		boolean operaterL3 = operaterControler.getYButtonPressed();
		return operaterL3;
	}
	
	public boolean getClimberEnable() {
		boolean operaterLeftbumper = operaterControler.getBumper(Hand.kLeft);
		return operaterLeftbumper;	
	}
	
	public double getPullLeftWinch() {
		double operaterleftJoy = operaterControler.getY(Hand.kLeft);
		return operaterleftJoy;
		
	}
	
	public double getPullRightWinch() {
		double operaterRightJoy = operaterControler.getY(Hand.kLeft);
		return operaterRightJoy;	
	}

}
