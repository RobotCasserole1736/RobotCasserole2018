package org.usfirst.frc.team1736.robot;

public class MotorCurrentEstimator {

	// Known motor constants - from VEX
	private final double CIM_stallCurrent_A = 131.0;
	private final double CIM_operatingVoltage = 12.0;
	private static final double CIM_freewheelSpeed_RadperSec = 5330 * 0.1049;
	private static final double CIM_freewheelCurrent_A = 2.7;

	private final double MiniCIM_stallCurrent_A = 89.0;
	private final double MiniCIM_operatingVoltage = 12.0;
	private final double MiniCIM_freewheelSpeed_RadperSec = 5840 * 0.1049;
	private final double MiniCIM_freewheelCurrent_A = 3.0;

	// Guessed Constants
	private double motorWiringResistance = 0.131; // total guess to make the numbers work? This accounts for errors in
													// windings, wiring, connectors, etc.

	// Derived motor constants
	private final double CIM_ESR = CIM_operatingVoltage / CIM_stallCurrent_A;
	private final double CIM_Ki = (CIM_operatingVoltage - CIM_freewheelCurrent_A * CIM_ESR)
			/ CIM_freewheelSpeed_RadperSec;

	private final double MiniCIM_ESR = MiniCIM_operatingVoltage / MiniCIM_stallCurrent_A;
	private final double MiniCIM_Ki = (MiniCIM_operatingVoltage - MiniCIM_freewheelCurrent_A * CIM_ESR)
			/ MiniCIM_freewheelSpeed_RadperSec;

	// configurable constants
	int numCIMMotorsInSystem; // Number of CIM motors driving this system
	int numMiniCIMMotorsInSystem; // Number of MiniCIM motors driving this system
	double Ki_fudge;
	double Cmd_fudge;

	/**
	 * init - Sets up the current estimator with the system parameters. Input -
	 * numMotors = Integer number of motors in the gearbox system. Usually 2 or 3,
	 * depending on your setup. motorEncRatio = ratio of motor gear teeth divided by
	 * encoder gear teeth. A number smaller than one means the motor spins slower
	 * than the encoder. wiringResistance_ohm = voltage drop induced by the motor
	 * controller, in V.
	 */
	public MotorCurrentEstimator(int numCIMMotors, int numMiniCIMMotors, double cmd_fudge_in,
			double speedGainFudgeFactor_in) {
		this.numCIMMotorsInSystem = numCIMMotors;
		this.numMiniCIMMotorsInSystem = numMiniCIMMotors;
		this.Cmd_fudge = cmd_fudge_in;
		this.Ki_fudge = speedGainFudgeFactor_in;
	}

	/**
	 * getCurrentEstimate - returns the estimate of the motor current draw Input -
	 */
	public double getCurrentEstimate(double motorSpeed_radpersec, double motorCommand, double systemVoltage) {
		double CIMMotorCurrent = 0;
		double miniCIMMotorCurrent = 0;
		if (motorCommand > 0.05) {
			CIMMotorCurrent = Math.max((numCIMMotorsInSystem)
					* (((systemVoltage) * motorCommand * Cmd_fudge) - Ki_fudge * CIM_Ki * motorSpeed_radpersec)
					/ (motorWiringResistance + CIM_ESR), 0);
			miniCIMMotorCurrent = Math.max((numMiniCIMMotorsInSystem)
					* (((systemVoltage) * motorCommand * Cmd_fudge) - Ki_fudge * MiniCIM_Ki * motorSpeed_radpersec)
					/ (motorWiringResistance + MiniCIM_ESR), 0);
		} else if (motorCommand < -0.05) {
			CIMMotorCurrent = -Math.min((numCIMMotorsInSystem)
					* (((systemVoltage) * motorCommand * Cmd_fudge) - Ki_fudge * CIM_Ki * motorSpeed_radpersec)
					/ (motorWiringResistance + CIM_ESR), 0);
			miniCIMMotorCurrent = -Math.min((numMiniCIMMotorsInSystem)
					* (((systemVoltage) * motorCommand * Cmd_fudge) - Ki_fudge * MiniCIM_Ki * motorSpeed_radpersec)
					/ (motorWiringResistance + MiniCIM_ESR), 0);
		} else {
			CIMMotorCurrent = 0.0;
		}

		return CIMMotorCurrent + miniCIMMotorCurrent;
	}

	/**
	 * If there is need for some specific offset factor (not symmetrical wiring?)
	 * then this is how it can be added.
	 * 
	 * @param r_in
	 */
	public void setExtraResistance(double r_in) {
		motorWiringResistance = r_in;
	}

}
