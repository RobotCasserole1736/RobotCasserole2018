package org.usfirst.frc.team1736.robot;

public class RobotConstants {
	
	//PWM Outputs
	public static final int PWM_INTAKE_LEFT = 0;
	public static final int PWM_INTAKE_RIGHT = 1;
	public static final int PWM_ELEVATOR_ONE = 2;
	public static final int PWM_ELEVATOR_TWO = 3;
	public static final int PWM_CLIMBER_LEFT_ONE = 4;
	public static final int PWM_CLIMBER_LEFT_TWO = 5;
	public static final int PWM_CLIMBER_RIGHT_ONE = 6;
	public static final int PWM_CLIMBER_RIGHT_TWO = 7;
	public static final int PWM_ELBOW = 8;
	
	
	//CAN ID's
	public static final int CANID_DRIVETRAIN_LEFT_MASTER_SRX = 0;
	public static final int CANID_DRIVETRAIN_LEFT_SLAVE1_SRX = 0;
	public static final int CANID_DRIVETRAIN_LEFT_SLAVE2_SRX = 0;
	public static final int CANID_DRIVETRAIN_RIGHT_MASTER_SRX = 0;
	public static final int CANID_DRIVETRAIN_RIGHT_SLAVE1_SRX = 0;
	public static final int CANID_DRIVETRAIN_RIGHT_SLAVE2_SRX = 0;
	
	
	//Digital Inputs
	public static final int DI_ELBOW_UP_LIMIT_SW = 0;
	public static final int DI_ELBOW_DOWN_LIMIT_SW = 1;
	public static final int DI_INTAKE_CUBE_PRESENT_SENSOR = 4;
	public static final int DI_ELEVATER_ENCODER_A = 5;
	public static final int DI_ELEVATER_ENCODER_B = 6;
	public static final int DI_ELEVATER_LOWER_LIMIT_SW = 7;
	public static final int DI_ELEVATER_UPPER_LIMIT_SW = 8;
	
	//Analog Inputs
	
	//PDP Hookups
	public static final int PDP_INTAKE_LEFT = 0; //ALL TBD
	public static final int PDP_INTAKE_RIGHT = 1;
	public static final int PDP_CLIMBER_LEFT_ONE = 2;
	public static final int PDP_CLIMBER_LEFT_TWO = 3;
	public static final int PDP_CLIMBER_RIGHT_ONE = 4;
	public static final int PDP_CLIMBER_RIGHT_TWO = 5;
	public static final int PDP_ELBOW = 6;
}

