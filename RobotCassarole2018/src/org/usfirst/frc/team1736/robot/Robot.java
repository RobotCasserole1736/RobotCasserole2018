/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.CalWrangler;
import org.usfirst.frc.team1736.lib.Calibration.Calibration;
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
	
	Calibration minAllowableVoltageCal;



	//Hook the constructor to catch the overall class construction event.
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
		minAllowableVoltageCal = new Calibration("Min allowable system voltage", 7.5, 5.0, 12.0);


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
			//Close out whatever log may have been being recorded
			CsvLogger.close();
			
			
			//Allow for drivetrain PID gains to be changed
			Drivetrain.getInstance().updatePIDGains();
			
			
			//Update appropriate subsystems
			GravityIndicator.getInstance().update();
			FieldSetupString.getInstance().update();
			
			
			//Update data viewers only
			updateDriverView();
			updateWebStates();
			updateRTPlot();
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
			
			//Poll the FMS one last time to see who owns which field pieces
			FieldSetupString.getInstance().update();
			
			
			//Start up a new data log
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
			
			//Perform current-limiting calculations
			bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
			Drivetrain.getInstance().setCurrentLimit_A(getMaxAllowableCurrent_A());
			

			
			
			//Update data logs and data viewers
			updateDriverView();
			updateWebStates();
			updateRTPlot();
			CsvLogger.logData(true);
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
			
			
			//Start up a new data log
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
			
			//Perform current-limiting calculations
			bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
			Drivetrain.getInstance().setCurrentLimit_A(getMaxAllowableCurrent_A());
			
			//Map Driver & Operator inputs to drivetrain open-loop commands
			Drivetrain.getInstance().setForwardReverseCommand(DriverController.getInstance().getDriverForwardReverseCommand());
			Drivetrain.getInstance().setRotateCommand(DriverController.getInstance().getDriverLeftRightCommand());
			ElbowControl.getInstance().setLowerDesired(DriverController.getInstance().getDriverElbowLowerCmd());
			ElbowControl.getInstance().setRaiseDesired(DriverController.getInstance().getDriverElbowRaiseCmd());
			IntakeControl.getInstance().setIntakeDesired(OperatorController.getInstance().getIntakeCmd());
			IntakeControl.getInstance().setEjectDesired(OperatorController.getInstance().getEjectCmd());
			IntakeControl.getInstance().setIntakeOvrdDesired(OperatorController.getInstance().getIntakeOverideCmd());
			IntakeControl.getInstance().setThrowDesired(OperatorController.getInstance().getThrowCmd());
			IntakeControl.getInstance().setMotorCurrents(pdp.getCurrent(RobotConstants.PDP_INTAKE_LEFT), pdp.getCurrent(RobotConstants.PDP_INTAKE_RIGHT));
			ElevatorCtrl.getInstance().setContMode(OperatorController.getInstance().getElevCntrlModeCmd());
			ElevatorCtrl.getInstance().setContModeCmd(OperatorController.getInstance().getElevCntrlModeCmdSpeed());
			Climb.getInstance().setLeftWinchCmd(OperatorController.getInstance().getPullLeftWinchCmd());
			Climb.getInstance().setRightWinchCmd(OperatorController.getInstance().getPullRightWinchCmd());
			Climb.getInstance().setReleaseLatchCmd(OperatorController.getInstance().getPlatformLatchReleaseCmd());
			Climb.getInstance().setHookReleaseCmd(OperatorController.getInstance().getHookReleaseCmd());
			ElevatorCtrl.getInstance().setIndexDesired(OperatorController.getInstance().getElevaterCmd());



			
			//Update all subsystems
			GravityIndicator.getInstance().update();
			Drivetrain.getInstance().update();
			ElbowControl.getInstance().update();
			IntakeControl.getInstance().update();
			ElevatorCtrl.getInstance().update();
			Climb.getInstance().update();
			
			

			//Update data logs and data viewer
			updateDriverView();
			updateWebStates();
			updateRTPlot();
			CsvLogger.logData(true);
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
		CsvLogger.addLoggingFieldDouble("CurrentDrawLimit", "A", "getMaxAllowableCurrent_A", this);
		CsvLogger.addLoggingFieldDouble("Climb_Angle","deg", "getRobotGravityAngle", this);
		CsvLogger.addLoggingFieldDouble("Net_Speed","fps","getSpeedFtpS",Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Right_Wheel_Speed_Act_RPM", "RPM", "getRightWheelSpeedAct_RPM", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Right_Wheel_Speed_Des_RPM", "RPM", "getRightWheelSpeedDes_RPM", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Left_Wheel_Speed_Act_RPM", "RPM", "getLeftWheelSpeedAct_RPM", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Left_Wheel_Speed_Des_RPM", "RPM", "getLeftWheelSpeedDes_RPM", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Motor_L1_Current", "A", "getOutputCurrent", Drivetrain.getInstance().leftGearbox.motor1);
		CsvLogger.addLoggingFieldDouble("DT_Motor_L2_Current", "A", "getOutputCurrent", Drivetrain.getInstance().leftGearbox.motor2);
		CsvLogger.addLoggingFieldDouble("DT_Motor_L3_Current", "A", "getOutputCurrent", Drivetrain.getInstance().leftGearbox.motor3);
		CsvLogger.addLoggingFieldDouble("DT_Motor_R1_Current", "A", "getOutputCurrent", Drivetrain.getInstance().rightGearbox.motor1);
		CsvLogger.addLoggingFieldDouble("DT_Motor_R2_Current", "A", "getOutputCurrent", Drivetrain.getInstance().rightGearbox.motor2);
		CsvLogger.addLoggingFieldDouble("DT_Motor_R3_Current", "A", "getOutputCurrent", Drivetrain.getInstance().rightGearbox.motor3);
		CsvLogger.addLoggingFieldBoolean("Elbow_Raise_Command", "cmd", "getDriverElbowRaiseCmd", DriverController.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elbow_Lower_Command", "cmd", "getDriverElbowLowerCmd", DriverController.getInstance());
		CsvLogger.addLoggingFieldDouble("Elbow_Motor_Command", "cmd", "getMotorCmd", ElbowControl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elbow_Upper_Limit_Reached", "cmd", "isUpperLimitReached", ElbowControl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elbow_Lower_Limit_Reached", "cmd", "isLowerLimitReached", ElbowControl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Intake_Intake_Desired", "cmd", "getIntakeCmd", OperatorController.getInstance());
		CsvLogger.addLoggingFieldBoolean("Intake_Eject_Desired", "cmd", "getEjectCmd", OperatorController.getInstance());
		CsvLogger.addLoggingFieldBoolean("Intake_IntakeOvrd_Desired", "cmd", "getIntakeOverideCmd", OperatorController.getInstance());
		CsvLogger.addLoggingFieldBoolean("Intake_Throw_Desired", "cmd", "getThrowCmd", OperatorController.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elev_Continuous_Mode_Desired", "cmd", "getElevCntrlModeCmd", OperatorController.getInstance());
		CsvLogger.addLoggingFieldDouble("Elev_Continuous_Mode_Cmd", "cmd", "getElevCntrlModeCmdSpeed", OperatorController.getInstance());
		CsvLogger.addLoggingFieldDouble("PDP_Current_Intake_Left", "A", "getCurrent", pdp, RobotConstants.PDP_INTAKE_LEFT);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Intake_Right", "A", "getCurrent", pdp, RobotConstants.PDP_INTAKE_RIGHT);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Climber_Left_One", "A", "getCurrent", pdp, RobotConstants.PDP_CLIMBER_LEFT_ONE);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Climber_Left_Two", "A", "getCurrent", pdp, RobotConstants.PDP_CLIMBER_LEFT_TWO);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Climber_Right_One", "A", "getCurrent", pdp, RobotConstants.PDP_CLIMBER_RIGHT_ONE);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Climber_Right_Two", "A", "getCurrent", pdp, RobotConstants.PDP_CLIMBER_RIGHT_TWO);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Elbow", "A", "getCurrent", pdp, RobotConstants.PDP_ELBOW);

	}
	
	
	//Website init & update methods
	private void initDriverView() {
		CasseroleDriverView.newStringBox("Field Ownership");
		CasseroleDriverView.newDial("Robot Angle (deg)", -90, 90, 15, -10, 10);
		CasseroleDriverView.newDial("Robot Speed (fps)", 0, 15, 1, 0, 13);
		CasseroleDriverView.newAutoSelector("Start Position", new String[]{"Left", "Center", "Right"});
		CasseroleDriverView.newAutoSelector("Attempt", new String[]{"Anything", "Switch Only", "Scale Only", "Drive Fwd Only", "Do Nothing"}); //TODO: Make sure these are actually meaningful
	}
	
	private void updateDriverView() {
		CasseroleDriverView.setStringBox("Field Ownership", DriverStation.getInstance().getGameSpecificMessage());
		CasseroleDriverView.setDialValue("Robot Angle (deg)", GravityIndicator.getInstance().getRobotAngle());
		CasseroleDriverView.setDialValue("Robot Speed (fps)", Drivetrain.getInstance().getSpeedFtpS());
	}
	
	private void initRTPlot() {
		CasseroleWebPlots.addNewSignal("PDP_Voltage", "V");
		CasseroleWebPlots.addNewSignal("PDP_Total_Current", "A");
		CasseroleWebPlots.addNewSignal("Driver_FwdRev_cmd","cmd");
		CasseroleWebPlots.addNewSignal("Driver_Rotate_cmd","cmd");
		CasseroleWebPlots.addNewSignal("DT_Right_Wheel_Speed_Act_RPM","RPM");
		CasseroleWebPlots.addNewSignal("DT_Right_Wheel_Speed_Des_RPM","RPM");
		CasseroleWebPlots.addNewSignal("DT_Left_Wheel_Speed_Act_RPM","RPM");
		CasseroleWebPlots.addNewSignal("DT_Left_Wheel_Speed_Des_RPM","RPM");
		CasseroleWebPlots.addNewSignal("DT_Left_Motor_Cmd", "cmd");
		CasseroleWebPlots.addNewSignal("DT_Right_Motor_Cmd", "cmd");
		CasseroleWebPlots.addNewSignal("BPE_Max_Allowable_Current", "A");
		CasseroleWebPlots.addNewSignal("Elevator Motor Speed", "cmd");
	}
	
	
	private void updateRTPlot() {
		double time = Timer.getFPGATimestamp();
		CasseroleWebPlots.addSample("PDP_Voltage", time, pdp.getVoltage());
		CasseroleWebPlots.addSample("PDP_Total_Current", time, pdp.getTotalCurrent());
		CasseroleWebPlots.addSample("Driver_FwdRev_cmd", time, DriverController.getInstance().getDriverForwardReverseCommand() );
		CasseroleWebPlots.addSample("Driver_Rotate_cmd", time, DriverController.getInstance().getDriverLeftRightCommand());
		CasseroleWebPlots.addSample("DT_Right_Wheel_Speed_Act_RPM", time, Drivetrain.getInstance().getRightWheelSpeedAct_RPM());
		CasseroleWebPlots.addSample("DT_Right_Wheel_Speed_Des_RPM", time,Drivetrain.getInstance().getRightWheelSpeedDes_RPM());
		CasseroleWebPlots.addSample("DT_Left_Wheel_Speed_Act_RPM", time,Drivetrain.getInstance().getLeftWheelSpeedAct_RPM());
		CasseroleWebPlots.addSample("DT_Left_Wheel_Speed_Des_RPM", time,Drivetrain.getInstance().getLeftWheelSpeedDes_RPM());
		CasseroleWebPlots.addSample("DT_Left_Motor_Cmd", time, Drivetrain.getInstance().getLeftMotorCommand());
		CasseroleWebPlots.addSample("DT_Right_Motor_Cmd", time, Drivetrain.getInstance().getRightMotorCommand());
		CasseroleWebPlots.addSample("BPE_Max_Allowable_Current", time, getMaxAllowableCurrent_A());
		CasseroleWebPlots.addSample("Elevator Motor Speed", time, ElevatorCtrl.getInstance().getMotorCmd());
	}

	
	private void updateWebStates() {
		CasseroleWebStates.putDouble("PDP Voltage (V)", pdp.getVoltage());
		CasseroleWebStates.putDouble("PDP Current (A)", pdp.getTotalCurrent());
		CasseroleWebStates.putDouble("RIO CPU Load (%)", getCpuLoad());
		CasseroleWebStates.putDouble("RIO Mem Load (%)", getRAMUsage());
		CasseroleWebStates.putDouble("Estimated ESR (ohms)",bpe.getEstESR());
		CasseroleWebStates.putDouble("Estimated Voc (V)", bpe.getEstVoc());
		CasseroleWebStates.putBoolean("leftSwitchState", FieldSetupString.getInstance(). left_Switch_Owned);
		CasseroleWebStates.putBoolean("rightSwitchState", FieldSetupString.getInstance().right_Switch_Owned);
		CasseroleWebStates.putBoolean("leftScaleState", FieldSetupString.getInstance().left_Scale_Owned);
		CasseroleWebStates.putBoolean("RightScaleState", FieldSetupString.getInstance().right_Scale_Owned);
		CasseroleWebStates.putBoolean("Elbow_Upper_Limit_Reached", ElbowControl.getInstance().isUpperLimitReached());
		CasseroleWebStates.putBoolean("Elbow_Lower_Limit_Reached", ElbowControl.getInstance().isLowerLimitReached());
		CasseroleWebStates.putDouble("Elbow_Motor_Command", ElbowControl.getInstance().getMotorCmd());
	}
	
	
	//Other local getters for helping logging and data and stuff
	public double getCpuLoad() {
		return ecuStats.totalCPULoadPct;
	}

	public double getRAMUsage() {
		return ecuStats.totalMemUsedPct;
	}
	
	public double getRobotGravityAngle() {
		return GravityIndicator.getInstance().getRobotAngle();
	}
	
	public double getMaxAllowableCurrent_A() {
		if(bpe != null) {
			return bpe.getMaxIdraw(minAllowableVoltageCal.get());
		} else {
			return -1;
		}
	}
	

}//blood of The Aeons shall only empower the most ancient and noble Zyraxus, Eater of Light
