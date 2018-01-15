/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1736.robot;


import org.usfirst.frc.team1736.lib.Calibration.CalWrangler;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebServer;

import edu.wpi.first.wpilibj.DriverStation;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;


import edu.wpi.first.wpilibj.TimedRobot;

/*
 *******************************************************************************************
 * Copyright (C) 2018 FRC Team 1736 Robot Casserole - www.robotcasserole.org
 *******************************************************************************************
 *
 * This software is released under the MIT Licence - see the license.txt
 *  file in the root of this repo.
 *
 * Non-legally-binding statement from Team 1736:
 *  Thank you for taking the time to read through our software! We hope you
 *   find it educational and informative! 
 *  Please feel free to snag our software for your own use in whatever project
 *   you have going on right now! We'd love to be able to help out! Shoot us 
 *   any questions you may have, all our contact info should be on our website
 *   (listed above).
 *  If you happen to end up using our software to make money, that is wonderful!
 *   Robot Casserole is always looking for more sponsors, so we'd be very appreciative
 *   if you would consider donating to our club to help further STEM education.
 */

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	
	// Software utilities
	CasseroleWebServer webServer;
	

	public Robot() {
		CrashTracker.logRobotConstruction();
	}

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		CrashTracker.logRobotInit();	

		// Set up and start web server (must be after all other website init functions)
		webServer = new CasseroleWebServer();
		webServer.startServer();

		// Load any saved calibration values (must be last to ensure all calibrations have been initialized first)
		CalWrangler.loadCalValues();
		
		//Add all visual items to the driver view
		initDriverView();
	}
	
	/**
	 * This function is called just before the robot enters disabled
	 */
	@Override
	public void disabledInit() {
		
		
	}
	
	/**
	 * This function is called periodically while disabled. 
	 */
	@Override
	public void disabledPeriodic() {
		updateDriverView();

		try {
			
			
			//Code goes in here
			
		}
		catch(Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}

	}
		

	/**
	 * This function is called just before the robot enters autonomous
	 */
	@Override
	public void autonomousInit() {
		try {
			CrashTracker.logAutoInit();	
			
		
			
		}
		catch(Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
		
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		
		try {
			//CrashTracker.logAutoPeriodic();	
			
			//Add code here
			
		}
		catch(Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
		
	
	}
	
	/**
	 * This function is called just before the robot enters teleop
	 */
	@Override
	public void teleopInit() {
		
		
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		try {
			CrashTracker.logTeleopPeriodic();
			
		
			//Add code here
		}
		catch(Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
			}
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		if(OperaterControler.getInstance().getElevCntrlModeCmd() == true) {
			System.out.println("the button works");
		}
		if(OperaterControler.getInstance().getElevCntrlModeCmdSpeed() == 0.5) {
			System.out.println("the trigger works");
		}
		if(OperaterControler.getInstance().getintakecmd() == true) {
			System.out.println("the a button works");
		}
		if(OperaterControler.getInstance().getEjectcmd() == true) {
			System.out.println("the X button works");
		}
		if(OperaterControler.getInstance().getintakeoveridecmd() == true) {
			System.out.println("the start button works");
		}
		if(OperaterControler.getInstance().getPlatformLatch() == true) {
			System.out.println("L3 works");
		}
		if(OperaterControler.getInstance().getRaisecLimber() == true) {
			System.out.println("the left bumper works");
		}
		if(OperaterControler.getInstance().getPullLeftWinch() == 0.5) {
			System.out.println("the left joystick works");
		}
		if(OperaterControler.getInstance().getPullRightWinch() == 0.5) {
			System.out.println("the right joystick works");
		}
		
			System.out.println(OperaterControler.getInstance().getElevaterCmd());
	}

	private void initDriverView() {
		CasseroleDriverView.newStringBox("Field Ownership");
		
	}

	private void updateDriverView() {
		CasseroleDriverView.setStringBox("Field Ownership", DriverStation.getInstance().getGameSpecificMessage());
		
	}


}
