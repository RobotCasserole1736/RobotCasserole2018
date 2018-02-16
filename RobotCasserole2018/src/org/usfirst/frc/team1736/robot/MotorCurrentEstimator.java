package org.usfirst.frc.team1736.robot;


public class MotorCurrentEstimator {
	
	//Known motor constants - from VEX
	private static final double CIM_stallCurrent_A = 131.0;
	private static final double CIM_operatingVoltage = 12.0;
	private static final double CIM_freewheelSpeed_RadperSec = 5330 * 0.1049;
	private static final double CIM_freewheelCurrent_A = 2.7;
	
	private static final double MiniCIM_stallCurrent_A = 89.0;
	private static final double MiniCIM_operatingVoltage = 12.0;
	private static final double MiniCIM_freewheelSpeed_RadperSec = 5840 * 0.1049;
	private static final double MiniCIM_freewheelCurrent_A = 3.0;
	
	//Guessed Constants
	private static final double motorWiringResistance = 0.131; //total guess to make the numbers work? This accounts for errors in windings, wiring, connectors, etc.
	
	//Derived motor constants
	private static final double CIM_ESR = CIM_operatingVoltage/CIM_stallCurrent_A + motorWiringResistance;
	private static final double CIM_Ki = (CIM_operatingVoltage - CIM_freewheelCurrent_A*CIM_ESR)/CIM_freewheelSpeed_RadperSec;
	
	private static final double MiniCIM_ESR = MiniCIM_operatingVoltage/MiniCIM_stallCurrent_A + motorWiringResistance;
	private static final double MiniCIM_Ki = (MiniCIM_operatingVoltage - MiniCIM_freewheelCurrent_A*CIM_ESR)/MiniCIM_freewheelSpeed_RadperSec;
	
	
	//configurable constants
	int numCIMMotorsInSystem; //Number of CIM motors driving this system
	int numMiniCIMMotorsInSystem; //Number of MiniCIM motors driving this system
	double contVDrop;
	
	
	/**
	 * init - Sets up the current estimator with the system parameters.
	 * Input - 
     *      numMotors = Integer number of motors in the gearbox system. Usually 2 or 3, depending on your setup.
     *      motorEncRatio = ratio of motor gear teeth divided by encoder gear teeth. A number smaller than one means the motor spins slower than the encoder.
     *      controllerVDrop_V = voltage drop induced by the motor controller, in V. 
	 */
	public MotorCurrentEstimator(int numCIMMotors, int numMiniCIMMotors, double controllerVDrop_V) {
		this.numCIMMotorsInSystem = numCIMMotors;
		this.numMiniCIMMotorsInSystem = numMiniCIMMotors;
		this.contVDrop = controllerVDrop_V;
	}
	
	/**
	 * getCurrentEstimate - returns the estimate of the motor current draw
	 * Input - 
	 */
	public double getCurrentEstimate(double motorSpeed_radpersec, double motorCommand, double systemVoltage) {
		double CIMMotorCurrent = 0;
		double miniCIMMotorCurrent = 0;
		if(motorCommand > 0.05) {
			CIMMotorCurrent = Math.max((numCIMMotorsInSystem)*(((systemVoltage-contVDrop)*motorCommand)-CIM_Ki*motorSpeed_radpersec)/CIM_ESR, 0);
			miniCIMMotorCurrent = Math.max((numMiniCIMMotorsInSystem)*(((systemVoltage-contVDrop)*motorCommand)-MiniCIM_Ki*motorSpeed_radpersec)/MiniCIM_ESR, 0);
		} else if(motorCommand < -0.05) {
			CIMMotorCurrent = -Math.min((numCIMMotorsInSystem)*(((systemVoltage-contVDrop)*motorCommand)-CIM_Ki*motorSpeed_radpersec)/CIM_ESR, 0);
			miniCIMMotorCurrent = -Math.min((numMiniCIMMotorsInSystem)*(((systemVoltage-contVDrop)*motorCommand)-MiniCIM_Ki*motorSpeed_radpersec)/MiniCIM_ESR, 0);
		} else {
			CIMMotorCurrent = 0.0;
		}
		
		return CIMMotorCurrent + miniCIMMotorCurrent;
	}

}
