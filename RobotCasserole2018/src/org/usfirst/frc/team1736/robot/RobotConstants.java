package org.usfirst.frc.team1736.robot;

public class RobotConstants {

	//Indicates which ports are being used and which components they are being used for
	
	/////////////////////////////////////////////////////////////////////////////////////
	// RoboRIO Output Ports
	/////////////////////////////////////////////////////////////////////////////////////

	// PWM Outputs
	public static final int PWM_INTAKE_LEFT = 0;
	public static final int PWM_INTAKE_RIGHT = 1;
	public static final int PWM_ELEVATOR_ONE = 2;
	// public static final int PWM_UNUSED = 3; UNUSED
	public static final int PWM_CLIMBER_RIGHT_ONE = 4;
	public static final int PWM_CLIMBER_RIGHT_TWO = 5;
	public static final int PWM_CLIMBER_LEFT_ONE = 6;
	public static final int PWM_CLIMBER_LEFT_TWO = 7;
	public static final int PWM_ELBOW = 8;
	public static final int PWM_RELEASE_LATCH = 9;

	// CAN ID's
	public static final int CANID_DRIVETRAIN_LEFT_MASTER_SRX = 1;
	public static final int CANID_DRIVETRAIN_LEFT_SLAVE1_SRX = 0;
	public static final int CANID_DRIVETRAIN_LEFT_SLAVE2_SRX = 12;
	public static final int CANID_DRIVETRAIN_RIGHT_MASTER_SRX = 15;
	public static final int CANID_DRIVETRAIN_RIGHT_SLAVE1_SRX = 13;
	public static final int CANID_DRIVETRAIN_RIGHT_SLAVE2_SRX = 14;

	/////////////////////////////////////////////////////////////////////////////////////
	// RoboRIO Input Ports
	/////////////////////////////////////////////////////////////////////////////////////

	// Digital Inputs
	public static final int DI_INTAKE_CUBE_PRESENT_SENSOR = 3;
	public static final int DI_ELBOW_UPPER_LIMIT_SW = 4;
	public static final int DI_ELEVATOR_ENCODER_A = 5;
	public static final int DI_ELEVATOR_ENCODER_B = 6;
	public static final int DI_ELEVATOR_LOWER_LIMIT_SW = 7;
	public static final int DI_ELEVATOR_UPPER_LIMIT_SW_STG1 = 8;
	public static final int DI_ELEVATOR_UPPER_LIMIT_SW_STG2 = 9;

	/////////////////////////////////////////////////////////////////////////////////////
	// PDP Ports
	/////////////////////////////////////////////////////////////////////////////////////

	// PDP Hookups
	public static final int PDP_LATCH = 10;
	public static final int PDP_ELEVATOR_ONE = 7;
	public static final int PDP_INTAKE_LEFT = 3;
	public static final int PDP_INTAKE_RIGHT = 12;
	public static final int PDP_CLIMBER_LEFT_ONE = 9;
	public static final int PDP_CLIMBER_LEFT_TWO = 8;
	public static final int PDP_CLIMBER_RIGHT_ONE = 4;
	public static final int PDP_CLIMBER_RIGHT_TWO = 5;
	public static final int PDP_ELBOW = 6;

	/////////////////////////////////////////////////////////////////////////////////////
	// Other Constants
	/////////////////////////////////////////////////////////////////////////////////////

	// Camera View URL. Requires the roboRIO be set to a fixed IP address of
	// 10.17.36.2
	public static final String DRIVER_CAMERA_URL = "http://10.17.36.2:1180/stream.mjpg";

	// LED Stuff
	public static final int NUM_LEDS_TOTAL = 50;
}
