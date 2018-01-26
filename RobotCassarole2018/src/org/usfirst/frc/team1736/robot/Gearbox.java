package org.usfirst.frc.team1736.robot;
import org.usfirst.frc.team1736.lib.Calibration.Calibration;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Gearbox {
	
	public TalonSRX motor1;
	//public TalonSRX motor2;
	//public TalonSRX motor3;
	
	Calibration kP;
	Calibration kI;
	Calibration kD;
	Calibration kF;
	
	//Update this to match the actual encoders we put on the drivetrain.
	// This should be total periods per rev - the quadrature 4x decoding
	// is accounted for elsewhere.
	private static final double ENCODER_CYCLES_PER_REV = 1024;
	
	// TALON Can Bus Read timeouts
	private static final int TIMEOUT_MS = 0;
	
	public Gearbox(int canid1, int canid2, int canid3, String name) {
		
		motor1 = new TalonSRX(canid1);
		//motor2 = new TalonSRX(canid2);
		//motor3 = new TalonSRX(canid3);
		
		kP = new Calibration("Gearbox_"+name+"_velocity_kP", 0);
		kI = new Calibration("Gearbox_"+name+"_velocity_kI", 0);
		kD = new Calibration("Gearbox_"+name+"_velocity_kD", 0);
		kF = new Calibration("Gearbox_"+name+"_velocity_kF", 0);
		
		updateCalibrations();
		
		//Enable current limits on all motors, with very large limits to start
		// We'll only use the continuous limiting for now
		motor1.enableCurrentLimit(true);
		//motor2.enableCurrentLimit(true);
		//motor3.enableCurrentLimit(true);
		motor1.configPeakCurrentDuration(0,TIMEOUT_MS);
		//motor2.configPeakCurrentDuration(0,TIMEOUT_MS);
		//motor3.configPeakCurrentDuration(0,TIMEOUT_MS);
		motor1.configPeakCurrentLimit(0,TIMEOUT_MS);
		//motor2.configPeakCurrentLimit(0,TIMEOUT_MS);
		//motor3.configPeakCurrentLimit(0,TIMEOUT_MS);
		
		//Config Min/Max output values. Not 100% sure if this is needed, but
		// CTRE put it in their example...
		motor1.configNominalOutputForward(0, TIMEOUT_MS);
		motor1.configNominalOutputReverse(0, TIMEOUT_MS);
		motor1.configPeakOutputForward(1,TIMEOUT_MS);
		motor1.configPeakOutputReverse(-1, TIMEOUT_MS);
		//motor2.configNominalOutputForward(0, TIMEOUT_MS);
		//motor2.configNominalOutputReverse(0, TIMEOUT_MS);
		//motor2.configPeakOutputForward(1,TIMEOUT_MS);
		//motor2.configPeakOutputReverse(-1, TIMEOUT_MS);
		//motor3.configNominalOutputForward(0, TIMEOUT_MS);
		//motor3.configNominalOutputReverse(0, TIMEOUT_MS);
		//motor3.configPeakOutputForward(1,TIMEOUT_MS);
		//motor3.configPeakOutputReverse(-1, TIMEOUT_MS);
		
		//Motor 1 is presumed to be the one with a sensor hooked up to it.
		motor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, TIMEOUT_MS);
		
		//We need a fairly high bandwidth on the velocity measurement, so keep
		// the averaging of velocity samples low to minimize phase shift
		motor1.configVelocityMeasurementWindow(4, TIMEOUT_MS);
		
		//We need to indicate to the controller if positive control effort to the motor 
		// produces positive sensor readings (in-phase) or negative sensor readings (out-of-phase)
		// "true" means out-of-phase, "false" means in-phase (???!?!?!?!)
		motor1.setSensorPhase(false); 
		
		//Configure motors 2 and 3 to be followers of the primary
		//motor2.follow(motor1);
		//motor3.follow(motor1);
	}
	
	public void updateCalibrations() {
		motor1.config_kP(0, kP.get(), TIMEOUT_MS);
		motor1.config_kI(0, kP.get(), TIMEOUT_MS);
		motor1.config_kD(0, kP.get(), TIMEOUT_MS);
		motor1.config_kF(0, kP.get(), TIMEOUT_MS);
	}
	
	
	public void setMotorSpeed(double speed_RPM) {
		
		motor1.set(ControlMode.Velocity,RPM_TO_CTRE_UNITS(speed_RPM));
	}
	
	public void setMotorCommand(double command) {
		
		motor1.set(ControlMode.PercentOutput,command);
	}
	
	public double getSpeedRPM() {
		return CTRE_UNITS_TO_RPM(motor1.getSelectedSensorVelocity(0));
	}
	
	public void setInverted(boolean invert) {
		motor1.setInverted(invert);
		//motor2.setInverted(invert);
		//motor3.setInverted(invert);
	}
	
	public void setCurrentLimit_A(double limit_A) {
		motor1.configContinuousCurrentLimit((int)Math.round(limit_A/3.0), TIMEOUT_MS);
		//motor2.configContinuousCurrentLimit((int)Math.round(limit_A/3.0), TIMEOUT_MS);
		//motor3.configContinuousCurrentLimit((int)Math.round(limit_A/3.0), TIMEOUT_MS);
	}
	
	public double getTotalCurrent() {
		return motor1.getOutputCurrent() + 
			   motor2.getOutputCurrent() + 
			   motor3.getOutputCurrent();
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



