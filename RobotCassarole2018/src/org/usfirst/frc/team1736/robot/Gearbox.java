package org.usfirst.frc.team1736.robot;
import org.usfirst.frc.team1736.lib.Calibration.Calibration;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Gearbox {
	
	private TalonSRX motor1;
	private TalonSRX motor2;
	private TalonSRX motor3;
	
	Calibration kP;
	Calibration kI;
	Calibration kD;
	Calibration kF;
	
	//Update this to match the actual encoders we put on the drivetrain.
	private static final double ENCODER_CYCLES_PER_REV = 1024;
	
	// TALON Can Bus Read timeouts
	private static final int TIMEOUT_MS = 1000;
	
	public Gearbox(int canid1, int canid2, int canid3, String name) {
		
		motor1 = new TalonSRX(canid1);
		motor2 = new TalonSRX(canid2);
		motor3 = new TalonSRX(canid3);
		
		kP = new Calibration("Gearbox_"+name+"_velocity_kP", 0);
		kI = new Calibration("Gearbox_"+name+"_velocity_kI", 0);
		kD = new Calibration("Gearbox_"+name+"_velocity_kD", 0);
		kF = new Calibration("Gearbox_"+name+"_velocity_kF", 0);
		
		updateCalibrations();
		
		//Motor 1 is presumed to be the one with a sensor hooked up to it.
		motor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, TIMEOUT_MS);
		
		//We need to indicate to the controller if positive control effort to the motor 
		// produces positive sensor readings (in-phase) or negative sensor readings (out-of-phase)
		// "true" means out-of-phase, "false" means in-phase (???!?!?!?!)
		motor1.setSensorPhase(false); 
	}
	
	public void updateCalibrations() {
		motor1.config_kP(0, kP.get(), TIMEOUT_MS);
		motor1.config_kI(0, kP.get(), TIMEOUT_MS);
		motor1.config_kD(0, kP.get(), TIMEOUT_MS);
		motor1.config_kF(0, kP.get(), TIMEOUT_MS);
	}
	
	
	public void setMotorSpeed(double speed_RPM) {
		
		motor1.set(ControlMode.Velocity,RPM_TO_CTRE_UNITS(speed_RPM));
		motor2.follow(motor1);
		motor3.follow(motor1);
	}
	
	public void setMotorCommand(double command) {
		
		motor1.set(ControlMode.PercentOutput,command);
		motor2.follow(motor1);
		motor3.follow(motor1);
	}
	
	public double getSpeedRPM() {
		return CTRE_UNITS_TO_RPM(motor1.getSelectedSensorVelocity(0));
	}
	
	public void setInverted(boolean invert) {
		motor1.setInverted(invert);
		motor2.setInverted(invert);
		motor3.setInverted(invert);
	}
	
	public void setCurrentLimit_A(double limit_A) {
		motor1.configContinuousCurrentLimit((int)Math.round(limit_A/3.0), TIMEOUT_MS);
		motor2.configContinuousCurrentLimit((int)Math.round(limit_A/3.0), TIMEOUT_MS);
		motor3.configContinuousCurrentLimit((int)Math.round(limit_A/3.0), TIMEOUT_MS);
	}
	
	public double getTotalCurrent_A() {
		return motor1.getOutputCurrent() + motor2.getOutputCurrent() + motor3.getOutputCurrent();
	}
	
	public double getMotorCommand() {
		return motor1.getMotorOutputPercent();
	}
	
	
	//Conversion Functions
	// CTRE measures velocity in terms of "per 100ms" (???!?!) - hence factor of 600 to get to/from "per-minute"
	// CTRE does quadrature decoding of every edge, so each encoder full period is counted as 4 pulses
	// ENCODER_CYCLES_PER_REV defines how county the encoder is
	private double RPM_TO_CTRE_UNITS(double rpm) {
		return rpm / 600.0 * ENCODER_CYCLES_PER_REV *4.0;
	}
	private double CTRE_UNITS_TO_RPM(double ctre_units) {
		return ctre_units*600.0 / ENCODER_CYCLES_PER_REV / 4.0;
	}
	
}



