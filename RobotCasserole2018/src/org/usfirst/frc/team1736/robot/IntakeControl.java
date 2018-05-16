package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.DigitalInput;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;

import edu.wpi.first.wpilibj.Spark;
import org.usfirst.frc.team1736.lib.Calibration.Calibration;

public class IntakeControl {
	private static IntakeControl singularInstance = null;

	// input commands
	private boolean intakeDesired;
	private boolean ejectDesired;
	private boolean intakeOvrdDesired;
	private boolean throwDesired;

	// State variables
	private int sensorDbncCount;
	private double leftCurrent;
	private double rightCurrent;
	private boolean currentLimitExceeded = false;
	private boolean cubeInIntake = false;
	private double leftMotorCmd = 0;
	private double rightMotorCmd = 0;
	private int loopsAboveLimit = 0;

	// Physical Device IO
	DigitalInput sensor;
	Spark leftMotor;
	Spark rightMotor;

	// Calibrations
	double leftMotorCurrent;
	double rightMotorCurrent;
	Calibration leftIntakeMotorCal;
	Calibration rightIntakeMotorCal;
	Calibration leftEjectMotorCal;
	Calibration rightEjectMotorCal;
	Calibration leftThrowMotorCal;
	Calibration rightThrowMotorCal;

	// constants
	private final double INTAKE_MAX_MOTOR_CURRENT_A = 30;
	private final double INTAKE_MOTOR_CURRENT_DEBOUNCE_S = 0.5;
	private final int SENSOR_DEBOUNCE_TIME_LOOPS = 10;
	private final double MAX_ELEV_HEIGHT_FOR_INTAKE_in = 12.0;

	// derived constants
	private final double INTAKE_MOTOR_CURRENT_DEBOUNCE_LOOPS = INTAKE_MOTOR_CURRENT_DEBOUNCE_S / 0.02;

	public static synchronized IntakeControl getInstance() {
		if (singularInstance == null)
			singularInstance = new IntakeControl();
		return singularInstance;
	}

	private IntakeControl() {
		CrashTracker.logClassInitStart(this.getClass());
		sensor = new DigitalInput(RobotConstants.DI_INTAKE_CUBE_PRESENT_SENSOR);
		leftMotor = new Spark(RobotConstants.PWM_INTAKE_LEFT);
		rightMotor = new Spark(RobotConstants.PWM_INTAKE_RIGHT);

		leftIntakeMotorCal = new Calibration("Intake Left Intake Cmd", 1.0, 0.0, 1.0);
		rightIntakeMotorCal = new Calibration("Intake Right Intake Cmd", 1.0, 0.0, 1.0);
		leftEjectMotorCal = new Calibration("Intake Left Eject Cmd", 0.4, 0.0, 1.0);
		rightEjectMotorCal = new Calibration("Intake Right Eject Cmd", 0.4, 0.0, 1.0);
		leftThrowMotorCal = new Calibration("Intake Left Throw Cmd", 1.0, 0.0, 1.0);
		rightThrowMotorCal = new Calibration("Intake Right Throw Cmd", 1.0, 0.0, 1.0);

		CrashTracker.logClassInitEnd(this.getClass());
	}

	public void setIntakeDesired(boolean Cmd) {
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

	public void setMotorCurrents(double leftCurrent_in, double rightCurrent_in) {
		leftCurrent_in = leftCurrent;
		rightCurrent_in = rightCurrent;
	}

	public boolean cubeInIntake() {
		return cubeInIntake;
	}

	public double getRightMotorCmd() {
		return rightMotorCmd;
	}

	public double getLeftMotorCmd() {
		return leftMotorCmd;
	}

	public boolean getCurrentLimitExceeded() {
		return currentLimitExceeded;
	}

	public void sampleSensors() {

		if (sensor.get() == true) {
			cubeInIntake = true;
			sensorDbncCount = SENSOR_DEBOUNCE_TIME_LOOPS;
		} else {
			if (sensorDbncCount > 0) {
				sensorDbncCount--;
			} else {
				cubeInIntake = false;
			}
		}
	}

	public void update() {

		boolean dontIntake;

		// Debounce the current limit
		if (currentLimitExceeded == false) {
			if (leftCurrent > INTAKE_MAX_MOTOR_CURRENT_A || rightCurrent > INTAKE_MAX_MOTOR_CURRENT_A) {
				loopsAboveLimit++;
			} else {
				loopsAboveLimit = 0;
			}
		}

		// Check if we're allowed to intake or not
		if (cubeInIntake == true) {
			// If we have a cube, definitely don't intake
			dontIntake = true;
		} else {

			if (loopsAboveLimit >= INTAKE_MOTOR_CURRENT_DEBOUNCE_LOOPS) {
				// If we're above the current limit for the debounce time,
				currentLimitExceeded = true;
				dontIntake = true;
			} else {
				// No cube, and current is fine
				dontIntake = false;
			}
		}

		// Calculate motor commands
		if (intakeDesired && !dontIntake
				&& (ElevatorCtrl.getInstance().getElevActualHeight_in() < MAX_ELEV_HEIGHT_FOR_INTAKE_in)) {
			leftMotorCmd = leftIntakeMotorCal.get();
			rightMotorCmd = rightIntakeMotorCal.get();
		} else if (ejectDesired) {
			leftMotorCmd = -1 * leftEjectMotorCal.get();
			rightMotorCmd = -1 * rightEjectMotorCal.get();
		} else if (intakeOvrdDesired) {
			leftMotorCmd = leftIntakeMotorCal.get();
			rightMotorCmd = rightIntakeMotorCal.get();
		} else if (throwDesired) {
			leftMotorCmd = -1 * leftThrowMotorCal.get();
			rightMotorCmd = -1 * rightThrowMotorCal.get();
		} else {
			leftMotorCmd = 0;
			rightMotorCmd = 0;

			// Reset debouonce
			currentLimitExceeded = false;
			loopsAboveLimit = 0;
		}

		leftMotor.set(leftMotorCmd);
		rightMotor.set(-1 * rightMotorCmd);
	}
}