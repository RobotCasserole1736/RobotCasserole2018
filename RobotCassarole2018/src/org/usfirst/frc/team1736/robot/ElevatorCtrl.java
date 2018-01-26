package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Spark;

public class ElevatorCtrl {
	private static ElevatorCtrl  singularInstance = null;
	
	private Elevator_index indexModeDesired;
	private boolean continuousModeDesired;
	private double continuousModeCmd;
	private double curMotorCmd;
	private Spark motor1;
	private Spark motor2;
	boolean upperLimitSwitchReached = false;
	boolean lowerLimitSwitchReached = false;
	DigitalInput upperLimitSwitch = null;
	DigitalInput lowerLimitSwitch = null;
	Calibration FloorPos = null;
	Calibration SwitchPos = null;
	Calibration ScaleDownPos = null;
	Calibration ScaleBalancedPos = null;
	Calibration ScaleUpPos = null;
	Calibration ExchangePos = null;
	
	
	public static synchronized ElevatorCtrl getInstance() {
		if ( singularInstance == null)
			singularInstance = new ElevatorCtrl();
		return singularInstance;
	}
	
	private ElevatorCtrl() {
		motor1 = new Spark(RobotConstants.PWM_ELEVATOR_ONE);
		motor2 = new Spark(RobotConstants.PWM_ELEVATOR_TWO);
		upperLimitSwitch = new DigitalInput(RobotConstants.DI_ELEVATER_UPPER_LIMIT_SW);
		lowerLimitSwitch = new DigitalInput(RobotConstants.DI_ELEVATER_LOWER_LIMIT_SW);	
		FloorPos = new Calibration("Floor position", 0.0, 84.0, 0.0);
		SwitchPos = new Calibration("Switch position", 0.0, 84.0,20.0);
		ScaleDownPos = new Calibration("Scale down Position", 0.0, 84.0, 55.0);
		ScaleBalancedPos = new Calibration("Scale balanced postion", 0.0, 84.0, 66.0);
		ScaleUpPos = new Calibration ("Scale up position", 0.0, 84.0, 77.0);
		ExchangePos = new Calibration("Exchange position", 0.0, 84.0, 4.0);
		
		
		
		
	}
	
	
	public void update() {
		if (continuousModeDesired == true) {
			curMotorCmd = continuousModeCmd;

		} else {
			//Todo - Add Indexed mode (closed-loop). Assign curMotorCmd here.
		}
		
		if(upperLimitSwitch.get()) {
			upperLimitSwitchReached = true;
		} else {
			upperLimitSwitchReached = false;
		}
		
		if(lowerLimitSwitch.get()) {
			lowerLimitSwitchReached = true;
		} else {
			lowerLimitSwitchReached = false;
		}
		
		if(upperLimitSwitchReached = true) {
			if(curMotorCmd >= 0) {
				curMotorCmd = 0;
		} else if(lowerLimitSwitchReached = true){
			if(curMotorCmd <= 0) {
				curMotorCmd = 0;
		} else {
			curMotorCmd = 0;
		}
	}
		
		}
		
		motor1.set(curMotorCmd);
		motor2.set(curMotorCmd);
	}
	
	public void setIndexDesired (Elevator_index cmd) {
		indexModeDesired = cmd;
	}
	
	
	public void setContMode (boolean modecommand) {
		continuousModeDesired = modecommand;
	}
	
	public void setContModeCmd (double cmd) {
		continuousModeCmd = cmd;
	}
	
	public double getMotorCmd() {
		return curMotorCmd;
	};
	
	private double enumToDesiredHeight(Elevator_index cmd) {
		if(cmd == Elevator_index.Bottom) {
			return FloorPos.get();
		}
		else if(cmd == Elevator_index.Exchange) {
			return ExchangePos.get();
		}
		else if(cmd == Elevator_index.ScaleUnderscoreDown) {
			return ScaleDownPos.get();
		}
		else if(cmd == Elevator_index.ScaleUnderscoreBalanced) {
			return ScaleBalancedPos.get();
		}
		else if(cmd == Elevator_index.ScaleUnderscoreUp) {
			return ScaleUpPos.get();
		}
		else if(cmd == Elevator_index.Switch1) {
			return SwitchPos.get();
		}
		else {
			return 0;
		}
	}
		
		Elevator_index desiredHightToEmun(double height) {
			if(height <= 1.9) {
				return Elevator_index.Bottom;
			}
			else if(height >= 2.0 && height >= 9.9) {
				return Elevator_index.Exchange;
			}
			else if(height >= 10.0 && height >= 33.0) {
				return Elevator_index.Switch1;
			}
			else if(height >= 33.1 && height >= 59.9) {
				return Elevator_index.ScaleUnderscoreDown;
			}
			else if(height >= 60.0 && height >= 70.9) {
				return Elevator_index.ScaleUnderscoreBalanced;
			}
			else if(height >= 71.0) {
				return Elevator_index.ScaleUnderscoreUp;
			}
			return indexModeDesired;
		}
		
	}
	

