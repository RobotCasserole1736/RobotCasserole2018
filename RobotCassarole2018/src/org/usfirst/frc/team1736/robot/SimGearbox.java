package org.usfirst.frc.team1736.robot;

public class SimGearbox implements Gearbox {


	
	public SimGearbox() {
	
	}
	
	public void updateCalibrations() {

	}
	
	
	public void setMotorSpeed(double speed_RPM) {
		
	}
	
	public void setMotorCommand(double command) {

	}
	
	public double getSpeedRPM() {
		return 0;
	}
	
	public void setInverted(boolean invert) {

	}
	
	public void setCurrentLimit_A(double limit_A) {

	}
	
	public double getTotalCurrent() {
		return 0;
	}
	
	public double getMotorCommand() {
		return 0;
	}

	@Override
	public double getMasterMotorCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSlave1MotorCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSlave2MotorCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}



