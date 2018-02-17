/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/


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

package org.usfirst.frc.team1736.robot;

import java.util.Date;

import org.usfirst.frc.team1736.lib.Calibration.CalWrangler;
import org.usfirst.frc.team1736.lib.LoadMon.CasseroleRIOLoadMonitor;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebPlots;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebServer;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebStates;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode.PixelFormat;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;
import org.usfirst.frc.team1736.lib.Logging.CsvLogger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;


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
	CasseroleRIOLoadMonitor ecuStats;
	
	
	PowerDistributionPanel pdp;
	
	//Auto Routine objects
	Autonomous auto;
	
	//Camera stream objects
	UsbCamera driverAssistCam;
	MjpegServer driverStream;
	
	//Loop execution time metrics
	double loopStartTime_s = 0;
	double loopPrevStartTime_s = 0;
	double loopExecTime_ms = 20; //starting guess
	double loopPeriod_ms = 20; //starting guess


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

		//Make sure all singelton subsystems get init'ed once here
		// Two impacts: reduces 1st loop processor load, and ensures 
		// all calibrations are registered prior to loading calWrangler values.
		Drivetrain.getInstance();
		Climb.getInstance();
		ElbowControl.getInstance();
		ElevatorCtrl.getInstance();
		FieldSetupString.getInstance();
		IntakeControl.getInstance();
		GravityIndicator.getInstance();
		Gyro.getInstance();
		
		
		//Init physical robot devices
		pdp = new PowerDistributionPanel(0);
		Gyro.getInstance().reset();
		
		//Set up autonomous routine control
		auto = new Autonomous();
		
		//Init Software Helper libraries
		ecuStats = new CasseroleRIOLoadMonitor();

		//Set up and start webcam stream
		driverAssistCam = new UsbCamera("CheapWideAngleCam", 0);
		driverAssistCam.setVideoMode(PixelFormat.kMJPEG, 320, 240, 15);
		driverStream = new MjpegServer("DriverCamServer", 1180);
		driverStream.setSource(driverAssistCam);
		
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
			
			//Ensure Auto is not running
			auto.stop();
			
			
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
		
		//Do loop time sampling first
		loopPrevStartTime_s = loopStartTime_s;
		loopStartTime_s = Timer.getFPGATimestamp();
		loopPeriod_ms = (loopStartTime_s - loopPrevStartTime_s)*1000;
		
		try {
			//Close out whatever log may have been being recorded
			CsvLogger.close();
			
			//Allow for drivetrain PID gains to be changed
			Drivetrain.getInstance().updatePIDGains();
			
			//Update appropriate subsystem sensor inputs
			IntakeControl.getInstance().sampleSensors();
			ElevatorCtrl.getInstance().sampleSensors();
			ElbowControl.getInstance().sampleSensors();
			
			//Set zero-state inputs
			Drivetrain.getInstance().setForwardReverseCommand(0);
			Drivetrain.getInstance().setRotateCommand(0);
			ElbowControl.getInstance().setLowerDesired(false);
			ElbowControl.getInstance().setRaiseDesired(false);
			IntakeControl.getInstance().setIntakeDesired(false);
			IntakeControl.getInstance().setEjectDesired(false);
			IntakeControl.getInstance().setIntakeOvrdDesired(false);
			IntakeControl.getInstance().setThrowDesired(false);
			ElevatorCtrl.getInstance().setContModeDesired(true);
			ElevatorCtrl.getInstance().setContModeCmd(0);
			ElevatorCtrl.getInstance().setIndexDesired(ElevatorIndex.BOTTOM);
			Climb.getInstance().setClimbEnabledCmd(false);
			Climb.getInstance().setLeftWinchCmd(0);
			Climb.getInstance().setRightWinchCmd(0);
			Climb.getInstance().setReleaseLatchCmd(false);
			

			//Update the right subset of subsystems - remember we're disabled!
			GravityIndicator.getInstance().update();
			FieldSetupString.getInstance().update();
			
			//Recalculate auto paths
			auto.updateAutoSelection();
			auto.calculatePaths();
			
			//Update data viewers only
			updateDriverView();
			updateWebStates();
			updateRTPlot();
		}
		catch(Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
		
		loopExecTime_ms = (Timer.getFPGATimestamp() - loopStartTime_s)*1000;
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
			
			//Ensure gyro is starting at zero
			Gyro.getInstance().reset();
			
			// Update autonomous selection and start
			auto.updateAutoSelection();
			auto.calculatePaths();
			auto.start();
			
			
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
		
		//Do loop time sampling first
		loopPrevStartTime_s = loopStartTime_s;
		loopStartTime_s = Timer.getFPGATimestamp();
		loopPeriod_ms = (loopStartTime_s - loopPrevStartTime_s)*1000;
		
		try {
			//Log a new periodic loop
			CrashTracker.logAutoPeriodic();
			
			//Sample sensors
			GravityIndicator.getInstance().update();
			ElbowControl.getInstance().sampleSensors();
			ElevatorCtrl.getInstance().sampleSensors();
			IntakeControl.getInstance().sampleSensors();
			IntakeControl.getInstance().setMotorCurrents(pdp.getCurrent(RobotConstants.PDP_INTAKE_LEFT), pdp.getCurrent(RobotConstants.PDP_INTAKE_RIGHT));
			Drivetrain.getInstance().setSystemVoltageCurrent(pdp.getVoltage(), pdp.getTotalCurrent());
			
			//Update autonomous sequencer
			auto.update();


			//Update all subsystems
			Drivetrain.getInstance().update();
			ElbowControl.getInstance().update();
			IntakeControl.getInstance().update();
			ElevatorCtrl.getInstance().update();
			Climb.getInstance().update();

			
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
		
		loopExecTime_ms = (Timer.getFPGATimestamp() - loopStartTime_s)*1000;
	}

	
	
	/**
	 * This function is called just before the robot enters teleop
	 */
	@Override
	public void teleopInit() {
		try {
			CrashTracker.logTeleopInit();	
			CrashTracker.logMatchInfo();
			
			
			//Ensure Auto is not running
			auto.stop();
			
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
		
		//Do loop time sampling first
		loopPrevStartTime_s = loopStartTime_s;
		loopStartTime_s = Timer.getFPGATimestamp();
		loopPeriod_ms = (loopStartTime_s - loopPrevStartTime_s)*1000;
		
		try {
			//Log the start of a new teleop loop
			CrashTracker.logTeleopPeriodic();
			
			//Sample Sensors
			GravityIndicator.getInstance().update();
			ElevatorCtrl.getInstance().sampleSensors();
			ElbowControl.getInstance().sampleSensors();
			IntakeControl.getInstance().sampleSensors();
			IntakeControl.getInstance().setMotorCurrents(pdp.getCurrent(RobotConstants.PDP_INTAKE_LEFT), pdp.getCurrent(RobotConstants.PDP_INTAKE_RIGHT));
			Drivetrain.getInstance().setSystemVoltageCurrent( pdp.getVoltage(), pdp.getTotalCurrent());
			
			
			//Map Driver & Operator inputs to drivetrain open-loop commands
			Drivetrain.getInstance().setForwardReverseCommand(DriverController.getInstance().getDriverForwardReverseCommand());
			Drivetrain.getInstance().setRotateCommand(DriverController.getInstance().getDriverLeftRightCommand());
			ElbowControl.getInstance().setLowerDesired(DriverController.getInstance().getDriverElbowLowerCmd());
			ElbowControl.getInstance().setRaiseDesired(DriverController.getInstance().getDriverElbowRaiseCmd());
			IntakeControl.getInstance().setIntakeDesired(OperatorController.getInstance().getIntakeCmd());
			IntakeControl.getInstance().setEjectDesired(OperatorController.getInstance().getEjectCmd());
			IntakeControl.getInstance().setIntakeOvrdDesired(OperatorController.getInstance().getIntakeOverideCmd());
			IntakeControl.getInstance().setThrowDesired(OperatorController.getInstance().getThrowCmd());
			ElevatorCtrl.getInstance().setContModeDesired(OperatorController.getInstance().getElevCntrlModeCmd());
			ElevatorCtrl.getInstance().setContModeCmd(OperatorController.getInstance().getElevCntrlModeCmdSpeed());
			Climb.getInstance().setClimbEnabledCmd(OperatorController.getInstance().getClimbEnabledCmd());
			Climb.getInstance().setLeftWinchCmd(OperatorController.getInstance().getPullLeftWinchCmd());
			Climb.getInstance().setRightWinchCmd(OperatorController.getInstance().getPullRightWinchCmd());
			Climb.getInstance().setReleaseLatchCmd(OperatorController.getInstance().getPlatformLatchReleaseCmd());
			ElevatorCtrl.getInstance().setIndexDesired(OperatorController.getInstance().getElevaterCmd());
			

			//Update all subsystems
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
		
		loopExecTime_ms = (Timer.getFPGATimestamp() - loopStartTime_s)*1000;
	}


	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		//Do loop time sampling first
		loopPrevStartTime_s = loopStartTime_s;
		loopStartTime_s = Timer.getFPGATimestamp();
		loopPeriod_ms = (loopStartTime_s - loopPrevStartTime_s)*1000;
		
		GravityIndicator.getInstance().update();
		
		
		loopExecTime_ms = (Timer.getFPGATimestamp() - loopStartTime_s)*1000;
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
		CsvLogger.addLoggingFieldDouble("RIO_Main_Loop_Exec_Time", "ms", "getLoopExeTime_ms", this);
		CsvLogger.addLoggingFieldDouble("RIO_Main_Loop_Period", "ms", "getLoopPeriod_ms", this);
		CsvLogger.addLoggingFieldDouble("RIO_CAN_Bus_Load", "%", "getCANBusUtilizationPct", this);
		CsvLogger.addLoggingFieldBoolean("RIO_FPGA_Active", "bit", "getOutputsActive", this);
		CsvLogger.addLoggingFieldBoolean("RIO_DS_Connected", "bit", "getDSConnected", this);
		CsvLogger.addLoggingFieldDouble("Bat_ESR", "ohms", "getBattESR", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("Bat_Voc", "V", "getBattVoc", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("Climb_Angle","deg", "getRobotGravityAngle", this);
		CsvLogger.addLoggingFieldDouble("Net_Speed","fps","getSpeedFtpS", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Right_Wheel_Speed_Act_RPM", "RPM", "getRightWheelSpeedAct_RPM", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Right_Wheel_Speed_Des_RPM", "RPM", "getRightWheelSpeedDes_RPM", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Left_Wheel_Speed_Act_RPM", "RPM", "getLeftWheelSpeedAct_RPM", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Left_Wheel_Speed_Des_RPM", "RPM", "getLeftWheelSpeedDes_RPM", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Heading_Des", "deg", "getHeadingDes_deg", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Motor_L1_Current", "A", "getMasterMotorCurrent", Drivetrain.getInstance().leftGearbox);
		CsvLogger.addLoggingFieldDouble("DT_Motor_L2_Current", "A", "getSlave1MotorCurrent", Drivetrain.getInstance().leftGearbox);
		CsvLogger.addLoggingFieldDouble("DT_Motor_L3_Current", "A", "getSlave2MotorCurrent", Drivetrain.getInstance().leftGearbox);
		CsvLogger.addLoggingFieldDouble("DT_Motor_R1_Current", "A", "getMasterMotorCurrent", Drivetrain.getInstance().rightGearbox);
		CsvLogger.addLoggingFieldDouble("DT_Motor_R2_Current", "A", "getSlave1MotorCurrent", Drivetrain.getInstance().rightGearbox);
		CsvLogger.addLoggingFieldDouble("DT_Motor_R3_Current", "A", "getSlave2MotorCurrent", Drivetrain.getInstance().rightGearbox);
		CsvLogger.addLoggingFieldDouble("DT_Left_Meas_Current", "A", "getTotalCurrent", Drivetrain.getInstance().leftGearbox);
		CsvLogger.addLoggingFieldDouble("DT_Right_Meas_Current", "A", "getTotalCurrent", Drivetrain.getInstance().rightGearbox);
		CsvLogger.addLoggingFieldDouble("DT_Left_Motor_Cmd", "cmd", "getLeftMotorCommand", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Right_Motor_Cmd", "cmd", "getRightMotorCommand", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Left_Current_Est", "A", "getLeftCurrentEst", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Right_Current_Est", "A", "getRightCurrentEst", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Current_Limit_Factor", "factor", "getLimitFactor", Drivetrain.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_FwdRev_Cmd", "cmd", "getDriverForwardReverseCommand", DriverController.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Rotate_Cmd", "cmd", "getDriverLeftRightCommand", DriverController.getInstance());
		CsvLogger.addLoggingFieldDouble("DT_Pose_Angle", "deg", "getAngle", Gyro.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elbow_Raise_Command", "cmd", "getDriverElbowRaiseCmd", DriverController.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elbow_Lower_Command", "cmd", "getDriverElbowLowerCmd", DriverController.getInstance());
		CsvLogger.addLoggingFieldDouble("Elbow_Motor_Command", "cmd", "getMotorCmd", ElbowControl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elbow_Upper_Limit_Reached", "bool", "isUpperLimitReached", ElbowControl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Intake_Intake_Desired", "cmd", "getIntakeCmd", OperatorController.getInstance());
		CsvLogger.addLoggingFieldBoolean("Intake_Eject_Desired", "cmd", "getEjectCmd", OperatorController.getInstance());
		CsvLogger.addLoggingFieldBoolean("Intake_IntakeOvrd_Desired", "cmd", "getIntakeOverideCmd", OperatorController.getInstance());
		CsvLogger.addLoggingFieldBoolean("Intake_Throw_Desired", "cmd", "getThrowCmd", OperatorController.getInstance());
		CsvLogger.addLoggingFieldBoolean("Intake_Current_Limit_Exceeded", "bool", "getCurrentLimitExceeded", IntakeControl.getInstance());
		CsvLogger.addLoggingFieldDouble("Intake_Right_Motor_Cmd", "cmd", "getRightMotorCmd", IntakeControl.getInstance());
		CsvLogger.addLoggingFieldDouble("Intake_Left_Motor_Cmd", "cmd", "getLeftMotorCmd", IntakeControl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Intake_Cube_In", "bool", "cubeInIntake", IntakeControl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elev_Continuous_Mode_Desired", "cmd", "getElevCntrlModeCmd", OperatorController.getInstance());
		CsvLogger.addLoggingFieldDouble("Elev_Continuous_Mode_Cmd", "cmd", "getElevCntrlModeCmdSpeed", OperatorController.getInstance());
		CsvLogger.addLoggingFieldDouble("Elev_Motor_Cmd", "cmd", "getMotorCmd", ElevatorCtrl.getInstance());
		CsvLogger.addLoggingFieldDouble("Elev_Des_Height", "in", "getElevDesiredHeight_in", ElevatorCtrl.getInstance());
		CsvLogger.addLoggingFieldDouble("Elev_Act_Height", "in", "getElevActualHeight_in",  ElevatorCtrl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elev_Upper_Limit_Reached", "bit", "getUpperTravelLimitReached",  ElevatorCtrl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elev_Lower_Limit_Reached", "bit", "getLowerTravelLimitReached",  ElevatorCtrl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Elev_Zeroed", "bit", "getIsZeroed",  ElevatorCtrl.getInstance());
		CsvLogger.addLoggingFieldBoolean("Climb_Enabled_Cmd", "cmd", "getClimbEnabledCmd", Climb.getInstance());
		CsvLogger.addLoggingFieldDouble("Climb_Left_Winch_Cmd", "cmd", "getLeftWinchCmd", Climb.getInstance());
		CsvLogger.addLoggingFieldDouble("Climb_Right_Winch_Cmd", "cmd", "getRightWinchCmd", Climb.getInstance());
		CsvLogger.addLoggingFieldDouble("PDP_Current_Latch", "A", "getCurrent", pdp, RobotConstants.PDP_LATCH);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Elevator_One", "A", "getCurrent", pdp, RobotConstants.PDP_ELEVATOR_ONE);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Intake_Left", "A", "getCurrent", pdp, RobotConstants.PDP_INTAKE_LEFT);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Intake_Right", "A", "getCurrent", pdp, RobotConstants.PDP_INTAKE_RIGHT);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Climber_Left_One", "A", "getCurrent", pdp, RobotConstants.PDP_CLIMBER_LEFT_ONE);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Climber_Left_Two", "A", "getCurrent", pdp, RobotConstants.PDP_CLIMBER_LEFT_TWO);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Climber_Right_One", "A", "getCurrent", pdp, RobotConstants.PDP_CLIMBER_RIGHT_ONE);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Climber_Right_Two", "A", "getCurrent", pdp, RobotConstants.PDP_CLIMBER_RIGHT_TWO);
		CsvLogger.addLoggingFieldDouble("PDP_Current_Elbow", "A", "getCurrent", pdp, RobotConstants.PDP_ELBOW);
		CsvLogger.addLoggingFieldBoolean("Brownout_Active", "bit", "isBrownedOut", RobotController.class);

	}
	
	
	//Website init & update methods
	private void initDriverView() {
		CasseroleDriverView.newDial("Robot Angle (deg)", -90, 90, 15, -10, 10);
		CasseroleDriverView.newDial("Robot Speed (fps)", 0, 15, 1, 0, 13);
		CasseroleDriverView.newDial("Actual Elevator Height", 0, 80, 10, 0, 80);
		CasseroleDriverView.newBoolean("DT Current High", "yellow");
		CasseroleDriverView.newBoolean("Intake Current High", "red");
		CasseroleDriverView.newBoolean("Elevator In Transit", "green");
		CasseroleDriverView.newBoolean("Elbow Upper Limit", "green");
		CasseroleDriverView.newBoolean("Elevator Upper Limit", "yellow");
		CasseroleDriverView.newBoolean("Elevator Lower Limit", "yellow");
		CasseroleDriverView.newBoolean("Elevator Not Zeroed", "yellow");
		CasseroleDriverView.newBoolean("Cube In Intake", "green");
		
		
		CasseroleDriverView.newWebcam("Driver_cam", RobotConstants.DRIVER_CAMERA_URL,50,50,0); //must use firefox. No idea why.
		CasseroleDriverView.newAutoSelector("Start Position", Autonomous.START_POS_MODES);
		CasseroleDriverView.newAutoSelector("Action", Autonomous.ACTION_MODES);
		CasseroleDriverView.newAutoSelector("Delay", Autonomous.DELAY_OPTIONS);

	}
	
	private void updateDriverView() {
		CasseroleDriverView.setDialValue("Robot Angle (deg)", GravityIndicator.getInstance().getRobotAngle());
		CasseroleDriverView.setDialValue("Robot Speed (fps)", Drivetrain.getInstance().getSpeedFtpS());
		CasseroleDriverView.setBoolean("DT Current High", Drivetrain.getInstance().getCurrentHigh());
		CasseroleDriverView.setBoolean("Intake Current High", IntakeControl.getInstance().getCurrentLimitExceeded());
		CasseroleDriverView.setBoolean("Elevator In Transit", !ElevatorCtrl.getInstance().isAtDesiredHeight()); 
		CasseroleDriverView.setBoolean("Elevator Upper Limit", ElevatorCtrl.getInstance().getUpperTravelLimitReached());
		CasseroleDriverView.setBoolean("Elevator Lower Limit", ElevatorCtrl.getInstance().getLowerTravelLimitReached());
		CasseroleDriverView.setBoolean("Elevator Not Zeroed", !ElevatorCtrl.getInstance().getIsZeroed());
		CasseroleDriverView.setBoolean("Elbow Upper Limit", ElbowControl.getInstance().isUpperLimitReached());
		CasseroleDriverView.setBoolean("Cube In Intake", IntakeControl.getInstance().cubeInIntake());
		CasseroleDriverView.setDialValue("Actual Elevator Height", ElevatorCtrl.getInstance().getElevActualHeight_in());
		
	}
	
	private void initRTPlot() {
		CasseroleWebPlots.addNewSignal("PDP_Voltage", "V");
		CasseroleWebPlots.addNewSignal("PDP_Total_Current", "A");
		CasseroleWebPlots.addNewSignal("Driver_FwdRev_Cmd","cmd");
		CasseroleWebPlots.addNewSignal("Driver_Rotate_Cmd","cmd");
		CasseroleWebPlots.addNewSignal("DT_Right_Wheel_Speed_Act","RPM");
		CasseroleWebPlots.addNewSignal("DT_Right_Wheel_Speed_Des","RPM");
		CasseroleWebPlots.addNewSignal("DT_Left_Wheel_Speed_Act","RPM");
		CasseroleWebPlots.addNewSignal("DT_Left_Wheel_Speed_Des","RPM");
		CasseroleWebPlots.addNewSignal("DT_Left_Motor_Cmd", "cmd");
		CasseroleWebPlots.addNewSignal("DT_Right_Motor_Cmd", "cmd");
		CasseroleWebPlots.addNewSignal("DT_Heading_des", "deg");
		CasseroleWebPlots.addNewSignal("DT_Left_Current", "A");
		CasseroleWebPlots.addNewSignal("DT_Right_Current", "A");
		CasseroleWebPlots.addNewSignal("DT_Left_Current_Est", "A");
		CasseroleWebPlots.addNewSignal("DT_Right_Current_Est", "A");
		CasseroleWebPlots.addNewSignal("DT_Current_Limit_Factor", "factor");
		CasseroleWebPlots.addNewSignal("BPE_Est_Voc", "V");
		CasseroleWebPlots.addNewSignal("BPE_Est_ESR", "Ohm");
		CasseroleWebPlots.addNewSignal("Elevator Motor Speed", "cmd");
		CasseroleWebPlots.addNewSignal("Elevator_Height", "in");
		CasseroleWebPlots.addNewSignal("Elevator_Desired_Height", "in");
		CasseroleWebPlots.addNewSignal("Pose_Angle", "deg");
		CasseroleWebPlots.addNewSignal("Brownout", "bit");
		CasseroleWebPlots.addNewSignal("Intake_Left_Cmd", "cmd");
		CasseroleWebPlots.addNewSignal("Intake_Right_Cmd", "cmd");
		CasseroleWebPlots.addNewSignal("Elbow_Cmd", "cmd");
		CasseroleWebPlots.addNewSignal("Elbow_Pot_Val", "V");
	}
	
	
	private void updateRTPlot() {
		double time = Timer.getFPGATimestamp();
		CasseroleWebPlots.addSample("PDP_Voltage", time, pdp.getVoltage());
		CasseroleWebPlots.addSample("PDP_Total_Current", time, pdp.getTotalCurrent());
		CasseroleWebPlots.addSample("Driver_FwdRev_Cmd", time, DriverController.getInstance().getDriverForwardReverseCommand() );
		CasseroleWebPlots.addSample("Driver_Rotate_Cmd", time, DriverController.getInstance().getDriverLeftRightCommand());
		CasseroleWebPlots.addSample("DT_Right_Wheel_Speed_Act", time, Drivetrain.getInstance().getRightWheelSpeedAct_RPM());
		CasseroleWebPlots.addSample("DT_Right_Wheel_Speed_Des", time,Drivetrain.getInstance().getRightWheelSpeedDes_RPM());
		CasseroleWebPlots.addSample("DT_Left_Wheel_Speed_Act", time,Drivetrain.getInstance().getLeftWheelSpeedAct_RPM());
		CasseroleWebPlots.addSample("DT_Left_Wheel_Speed_Des", time,Drivetrain.getInstance().getLeftWheelSpeedDes_RPM());
		CasseroleWebPlots.addSample("DT_Left_Motor_Cmd", time, Drivetrain.getInstance().getLeftMotorCommand());
		CasseroleWebPlots.addSample("DT_Right_Motor_Cmd", time, Drivetrain.getInstance().getRightMotorCommand());
		CasseroleWebPlots.addSample("DT_Heading_des", time, Drivetrain.getInstance().getHeadingDes_deg());
		CasseroleWebPlots.addSample("DT_Left_Current", time, Drivetrain.getInstance().getLeftCurrent());
		CasseroleWebPlots.addSample("DT_Right_Current", time, Drivetrain.getInstance().getRightCurrent());
		CasseroleWebPlots.addSample("DT_Left_Current_Est", time, Drivetrain.getInstance().getLeftCurrentEst());
		CasseroleWebPlots.addSample("DT_Right_Current_Est", time, Drivetrain.getInstance().getRightCurrentEst());
		CasseroleWebPlots.addSample("DT_Current_Limit_Factor", time, Drivetrain.getInstance().getLimitFactor());
		CasseroleWebPlots.addSample("BPE_Est_Voc", time, Drivetrain.getInstance().getBattVoc());
		CasseroleWebPlots.addSample("BPE_Est_ESR", time, Drivetrain.getInstance().getBattESR());
		CasseroleWebPlots.addSample("Elevator Motor Speed", time, ElevatorCtrl.getInstance().getMotorCmd());
		CasseroleWebPlots.addSample("Elevator_Height", time, ElevatorCtrl.getInstance().getElevActualHeight_in());
		CasseroleWebPlots.addSample("Elevator_Desired_Height", time, ElevatorCtrl.getInstance().getElevDesiredHeight_in());
		CasseroleWebPlots.addSample("Pose_Angle", time, Gyro.getInstance().getAngle());
		CasseroleWebPlots.addSample("Brownout", time, RobotController.isBrownedOut()?1.0:0.0);
		CasseroleWebPlots.addSample("Intake_Left_Cmd", time, IntakeControl.getInstance().getLeftMotorCmd());
		CasseroleWebPlots.addSample("Intake_Right_Cmd", time, IntakeControl.getInstance().getRightMotorCmd());
		CasseroleWebPlots.addSample("Elbow_Cmd", time, ElbowControl.getInstance().getMotorCmd());
	}

	
	private void updateWebStates() {
		CasseroleWebStates.putDouble("PDP Voltage (V)", pdp.getVoltage());
		CasseroleWebStates.putDouble("PDP Current (A)", pdp.getTotalCurrent());
		CasseroleWebStates.putDouble("RIO CPU Load (%)", getCpuLoad());
		CasseroleWebStates.putDouble("RIO Mem Load (%)", getRAMUsage());
		CasseroleWebStates.putDouble("RIO Main Loop Exec Time (ms)", getLoopExeTime_ms());
		CasseroleWebStates.putString("RioTimeandDate", new Date().toString());
		CasseroleWebStates.putBoolean("Field Left Switch Owned", FieldSetupString.getInstance(). left_Switch_Owned);
		CasseroleWebStates.putBoolean("Field Right Switch Owned", FieldSetupString.getInstance().right_Switch_Owned);
		CasseroleWebStates.putBoolean("Field Left Scale Owned", FieldSetupString.getInstance().left_Scale_Owned);
		CasseroleWebStates.putBoolean("Field Right Scale Owned", FieldSetupString.getInstance().right_Scale_Owned);
		CasseroleWebStates.putDouble("Elevator Act Height (in)",ElevatorCtrl.getInstance().getElevActualHeight_in());
		CasseroleWebStates.putDouble("Elevator Des Height (in)",ElevatorCtrl.getInstance().getElevDesiredHeight_in());
		CasseroleWebStates.putBoolean("Elevator Zeroed",ElevatorCtrl.getInstance().getIsZeroed());
		CasseroleWebStates.putBoolean("Elevator Lower Limit",ElevatorCtrl.getInstance().getLowerTravelLimitReached());
		CasseroleWebStates.putBoolean("Elevator Upper Limit",ElevatorCtrl.getInstance().getUpperTravelLimitReached());
		CasseroleWebStates.putDouble("Elevator Motor Cmd",ElevatorCtrl.getInstance().getMotorCmd());
		CasseroleWebStates.putBoolean("Elbow_Upper_Limit_Reached", ElbowControl.getInstance().isUpperLimitReached());
		CasseroleWebStates.putDouble("Elbow_Motor_Command", ElbowControl.getInstance().getMotorCmd());
		CasseroleWebStates.putString("Auto Mode", auto.mode.toString());
		CasseroleWebStates.putDouble("Climber Latch Angle Cmd", Climb.getInstance().getLatchAngleCmd());
		CasseroleWebStates.putBoolean("Climber Enabled", Climb.getInstance().getClimbEnabledCmd());
		CasseroleWebStates.putDouble("Climber Left Motor Cmd", Climb.getInstance().getLeftWinchCmd());
		CasseroleWebStates.putDouble("Climber Right Motor Cmd", Climb.getInstance().getRightWinchCmd());
		CasseroleWebStates.putBoolean("Intake Sensor State", IntakeControl.getInstance().cubeInIntake());
	}
	
	
	//Other local getters for helping logging and data and stuff
	public double getCpuLoad() {
		return ecuStats.totalCPULoadPct;
	}

	public double getRAMUsage() {
		return ecuStats.totalMemUsedPct;
	}
	
	public boolean getDSConnected() {
		return DriverStation.getInstance().isDSAttached();
	}
	
	public boolean getOutputsActive() {
		return DriverStation.getInstance().isSysActive();
	}
	
	public double getRobotGravityAngle() {
		return GravityIndicator.getInstance().getRobotAngle();
	}
	
	public double getCANBusUtilizationPct() {
		return RobotController.getCANStatus().percentBusUtilization*100;
	}
	
	public double getLoopExeTime_ms() {
		return loopExecTime_ms;
	}
	
	public double getLoopPeriod_ms() {
		return loopPeriod_ms;
	}
	

}//blood of The Aeons shall only empower the most ancient and noble Zyraxus, Eater of Light
