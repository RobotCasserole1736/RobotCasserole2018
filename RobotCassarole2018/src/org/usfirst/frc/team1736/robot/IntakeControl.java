package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.PWMSpeedController;
import org.usfirst.frc.team1736.lib.Calibration.Calibration;
public class IntakeControl {
	private static IntakeControl singularInstance = null;
	private boolean intakeDesired;
	private boolean ejectDesired;
	private boolean intakeOvrdDesired;
	private boolean throwDesired;
	private double leftCurrent;
	private double rightCurrent;
	private boolean sensorState;
	private DigitalInput sensor;
	Spark leftMotor;
	Spark rightMotor;
	Calibration leftIntakeMotorCal;
	Calibration rightIntakeMotorCal;
	Calibration leftEjectMotorCal;
	Calibration rightEjectMotorCal;
	Calibration leftThrowMotorCal;
	Calibration rightThrowMotorCal;

	
	
	public static synchronized IntakeControl getInstance() {
		if ( singularInstance == null)
				singularInstance = new IntakeControl();
		return singularInstance;
			
	}
	
	private IntakeControl () {
		sensor = new DigitalInput(RobotConstants.DI_INTAKE_CUBE_PRESENT_SENSOR);
		
	}
	
	public void setIntakeDesired(boolean Cmd ) {
		
			intakeDesired = Cmd;
						
		
	}
		
	public void setEjectDesired(boolean Cmd) {
					
			ejectDesired = Cmd;			
						
	}
			
			
	public void setIntakeOvrdDesired(boolean Cmd) {
			
			intakeOvrdDesired = Cmd;
		
	
	
	}
	
	public void setThrowDesired(boolean Cmd) {
		
			throwDesired = Cmd;
		
	}
	
	
	public  void setMotorCurrents(double leftCurrent_in, double rightCurrent_in) {
		leftCurrent_in = leftCurrent;
		rightCurrent_in = rightCurrent;
	}
	
	
	public boolean intakeSensorState() {
		
		boolean sensorState = sensor.get();
		 
		
		return sensorState;
			
	}
	public boolean intakeFlag() {
		//intake cap = 30
		boolean intakeCapReached = false;
		if(leftCurrent > 30 || rightCurrent >30) {
			intakeCapReached = true;
		}
			else if(sensorState == true) {
				intakeCapReached = true;
			}
			else {
				intakeCapReached = false;
			}
			
			 {
				intakeCapReached = false;		
			}
		return intakeCapReached;
	}
	
			
		
			
			
			
	public void update() {
		if(intakeDesired) {
		leftMotor.set(leftIntakeMotorCal.get());
		rightMotor.set(leftIntakeMotorCal.get());
		}
		if(ejectDesired) {
			leftMotor.set(leftEjectMotorCal.get());
			leftMotor.set(rightEjectMotorCal.get());
		}
		if(intakeOvrdDesired) {
			leftMotor.set(leftIntakeMotorCal.get());
			rightMotor.set(leftIntakeMotorCal.get());
		}
		if(throwDesired) {
			leftMotor.set(leftThrowMotorCal.get());
			leftMotor.set(rightThrowMotorCal.get());
		}
	}
}