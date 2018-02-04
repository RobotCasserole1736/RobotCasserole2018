package org.usfirst.frc.team1736.robot;

public class SimGearbox implements Gearbox {

	double presentCommand = 0;
	double presentSpeed = 0;
	
	
	public SimGearbox() {
	
	}
	
	public void updateCalibrations() {

	}
	
	
	public void setMotorSpeed(double speed_RPM) {
		presentSpeed = speed_RPM;
	}
	
	public void setMotorCommand(double command) {
		presentCommand = command;
	}
	
	public double getSpeedRPM() {
		return presentSpeed + (Math.random()-0.5)*30; //"simulate" sensor noise
	}
	
	public void setInverted(boolean invert) {

	}
	
	public void setCurrentLimit_A(double limit_A) {

	}
	
	public double getTotalCurrent() {
		return 0;
	}
	
	public double getMotorCommand() {
		return presentCommand;
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

	@Override
	public void sampleSensors() {
		// TODO Auto-generated method stub
		
	}
	
}



