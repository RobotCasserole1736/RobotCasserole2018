package org.usfirst.frc.team1736.robot;

public interface Gearbox {
	
	
	public abstract void updateCalibrations();
	
	
	public abstract void setMotorSpeed(double speed_RPM);
	
	public abstract void setMotorCommand(double command);
	
	public abstract double getSpeedRPM();
	
	public abstract void setInverted(boolean invert);
	
	public abstract void setCurrentLimit_A(double limit_A);
	
	public abstract double getTotalCurrent();
	public abstract double getMasterMotorCurrent();
	public abstract double getSlave1MotorCurrent();
	public abstract double getSlave2MotorCurrent();
	
	public abstract double getMotorCommand();
	
	public abstract void sampleSensors();

}
