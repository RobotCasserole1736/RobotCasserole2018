/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.CalWrangler;
import org.usfirst.frc.team1736.lib.LoadMon.CasseroleRIOLoadMonitor;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebPlots;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebServer;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebStates;

import edu.wpi.first.wpilibj.DriverStation;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;
import org.usfirst.frc.team1736.lib.Logging.CsvLogger;
import edu.wpi.first.wpilibj.PowerDistributionPanel;


import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

/*
 *******************************************************************************************
 * Copyright (C) 2018 FRC Team 1736 Robot Casserole - www.robotcasserole.org
 *******************************************************************************************
 *
 * This software is released under the MIT license - see the license.txt
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
	
	PowerDistributionPanel pdp;
	CasseroleRIOLoadMonitor ecuStats;
	BatteryParamEstimator bpe;
	
	final static int BPE_length = 200; 
	final static double BPE_confidenceThresh_A = 10.0;

	public Robot() {
		CrashTracker.logRobotConstruction();
		
	
	}
	

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//Log that we are starting the robot code
		CrashTracker.logRobotInit();	

		//Init physical robot devices
		pdp = new PowerDistributionPanel(0);
		
		bpe = new BatteryParamEstimator(BPE_length); 
		bpe.setConfidenceThresh(BPE_confidenceThresh_A);
		
		//Init Software Helper libraries
		ecuStats = new CasseroleRIOLoadMonitor();


		// Set up and start web server (must be after all other website init functions)
		webServer = new CasseroleWebServer();
		webServer.startServer();


		// Load any saved calibration values (must be last to ensure all calibrations have been initialized first)
		CalWrangler.loadCalValues();
		
		//Add all visual items to the website and data logs
		initDriverView();
		initRTPlot();
		initLoggingChannels();

	}
	
	/**
	 * This function is called just before the robot enters disabled
	 */
	@Override
	public void disabledInit() {
		try {
			CrashTracker.logDisabledInit();	
			
			
			
		}
		catch(Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}

		
	}
	

	/**
	 * This function is called periodically while disabled. 
	 */
	@Override
	public void disabledPeriodic() {


		try {
			
			Field_setup_string.getInstance().update();
			updateDriverView();
			updateWebStates();
			updateRTPlot();
			CsvLogger.close();
			GravityIndicator.getInstance().update();
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
			CrashTracker.logMatchInfo();
			Field_setup_string.getInstance().update();

		
			CsvLogger.init();
			
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
			CrashTracker.logAutoPeriodic();	
			GravityIndicator.getInstance().update();
			
			//Add auto periodic code here
			
			
			updateDriverView();
			updateWebStates();
			updateRTPlot();
			CsvLogger.logData(true);
			
			bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
			
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
		try {
			CrashTracker.logTeleopInit();	
			CrashTracker.logMatchInfo();
			
			//Add Teleop init code here
			CsvLogger.init();

		}
		catch(Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
		
	}
	

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {

		try {
			CrashTracker.logTeleopPeriodic();
			GravityIndicator.getInstance().update();
			//Map Driver inputs to drivetrain open-loop commands
			Drivetrain.getInstance().setForwardReverseCommand(DriverController.getInstance().getDriverForwardReverseCommand());
			Drivetrain.getInstance().setRotateCommand(DriverController.getInstance().getDriverLeftRightCommand());
			
			
			
			Drivetrain.getInstance().update();

			
		
			updateDriverView();
			updateWebStates();
			updateRTPlot();
			CsvLogger.logData(true);
			
			bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
			
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
		GravityIndicator.getInstance().update();
	}

	
	
	///////////////////////////////////////////////////////////////////////
	// Private helper methods to group things nicely
    ///////////////////////////////////////////////////////////////////////
	
	//Sets up all data channels to be logged
	public void initLoggingChannels() {
		CsvLogger.addLoggingFieldDouble("TIME", "sec", "getFPGATimestamp", Timer.class);
		CsvLogger.addLoggingFieldDouble("PDP_Voltage", "V", "getVoltage", pdp);
		CsvLogger.addLoggingFieldDouble("PDP_Total_Current", "A", "getTotalCurrent", pdp);
		CsvLogger.addLoggingFieldDouble("RIO_Cpu_Load", "%", "getCpuLoad", this);
		CsvLogger.addLoggingFieldDouble("RIO_RAM_Usage", "%", "getRAMUsage", this);
		CsvLogger.addLoggingFieldDouble("ESR", "ohms", "getEstESR", bpe);
		CsvLogger.addLoggingFieldDouble("EstVoc", "V", "getEstVoc", bpe);
		CsvLogger.addLoggingFieldDouble("Angle_of_Robot","deg", "getRobotGravityAngle", this);



	}
	
	
	
	private void initDriverView() {
		CasseroleDriverView.newStringBox("Field Ownership");
		CasseroleDriverView.newDial("Robot Angle", -90, 90, 15, -10, 10);
		
		CasseroleDriverView.newAutoSelector("Start Position", new String[]{"Left", "Center", "Right"});
		CasseroleDriverView.newAutoSelector("Action", new String[]{"Do Nothing", "Drive Fwd", "Scale", "Switch"}); //TODO: Make sure these are actually meaningful
		String result = CasseroleDriverView.getAutoSelectorVal("Start Position");
		
	}
	
	private void initRTPlot() {
		CasseroleWebPlots.addNewSignal("PDP_Voltage", "V");
		CasseroleWebPlots.addNewSignal("PDP_Total_Current", "A");
		CasseroleWebPlots.addNewSignal("curFwdRevCmd","Cmd");
		CasseroleWebPlots.addNewSignal("curRotCmd", "Cmd");
	}
	
	private void updateRTPlot() {
		double time = Timer.getFPGATimestamp();
		CasseroleWebPlots.addSample("PDP_Voltage", time, pdp.getVoltage());
		CasseroleWebPlots.addSample("PDP_Total_Current", time, pdp.getTotalCurrent());
		CasseroleWebPlots.addSample("curFwdRevCmd", time, DriverController.getInstance().getDriverForwardReverseCommand() );
		CasseroleWebPlots.addSample("curRotCmd", time, DriverController.getInstance().getDriverLeftRightCommand());
	}

	private void updateDriverView() {
		CasseroleDriverView.setStringBox("Field Ownership", DriverStation.getInstance().getGameSpecificMessage());
		CasseroleDriverView.setDialValue("Robot Angle", GravityIndicator.getInstance().getRobotAngle());
	}
	
	private void updateWebStates() {
		CasseroleWebStates.putDouble("PDP Voltage (V)", pdp.getVoltage());
		CasseroleWebStates.putDouble("PDP Current (A)", pdp.getTotalCurrent());
		CasseroleWebStates.putDouble("RIO CPU Load (%)", getCpuLoad());
		CasseroleWebStates.putDouble("RIO Mem Load (%)", getRAMUsage());
		CasseroleWebStates.putDouble("Estimated ESR (ohms)",bpe.getEstESR());
		CasseroleWebStates.putDouble("Estimated Voc (V)", bpe.getEstVoc());
		CasseroleWebStates.putBoolean("leftSwitchState", Field_setup_string.getInstance(). left_Switch_Owned);
		CasseroleWebStates.putBoolean("rightSwitchState", Field_setup_string.getInstance().right_Switch_Owned);
		CasseroleWebStates.putBoolean("leftScaleState", Field_setup_string.getInstance().left_Scale_Owned);
		CasseroleWebStates.putBoolean("RightScaleState", Field_setup_string.getInstance().right_Scale_Owned);
	}
	
	public double getCpuLoad() {
		return ecuStats.totalCPULoadPct;
	}

	public double getRAMUsage() {
		return ecuStats.totalMemUsedPct;
	}
	public double getRobotGravityAngle() {
		return GravityIndicator.getInstance().getRobotAngle();
	}


}//blood of aeons shall only empower the most ancient Zyraxus, Eater of Light
