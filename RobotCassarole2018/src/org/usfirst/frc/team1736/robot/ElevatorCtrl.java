package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;

public class ElevatorCtrl {
	private static ElevatorCtrl  singularInstance = null;
	
	private Elevator_index indexModeDesired;
	private boolean continuousModeDesired;
	private double continuousModeCmd;
	private Elevator_index enumModeCmd;
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
	private Encoder elevatorEncoder;
	public final double Encoder_Pulse_Pur_Rev = 1024;
	public final double Elevator_Inches_Pur_Rev = 2;
	Calibration UpSpeed = null;
	Calibration DownSpeed = null;
	
	
	
	public static synchronized ElevatorCtrl getInstance() {
		if ( singularInstance == null)
			singularInstance = new ElevatorCtrl();
		return singularInstance;
	}
	
	private ElevatorCtrl() {
		elevatorEncoder = new Encoder(RobotConstants.DI_ELEVATER_ENCODER_A, RobotConstants.DI_ELEVATER_ENCODER_B );
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
		UpSpeed = new Calibration("up speed", 10);
		DownSpeed = new Calibration("down speed", -10);
		
		
		
	}
	
	
	public void update() {
		if (continuousModeDesired == true) {
			curMotorCmd = continuousModeCmd;

		} else {
			double desiredHeight = enumToDesiredHeight(enumModeCmd);
			double actualHeight = getElevHeight_in();
			if(desiredHeight >= actualHeight) {
				curMotorCmd = UpSpeed.get();
			}else if(desiredHeight <= actualHeight) {
				curMotorCmd = DownSpeed.get();
			}
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
		;
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
	

	
	public void setEnumModeCmd (Elevator_index cmd) {
		enumModeCmd = cmd;
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
			Elevator_index returnValue = (Elevator_index.Bottom);
			double mindist = 100;
			Double calculation1 = FloorPos.get() - height;
			if(calculation1 < mindist) {
				returnValue = Elevator_index.Bottom;
			}
			Double calculation2 = ExchangePos.get() - height;
			 if(calculation2 < mindist) {
				return Elevator_index.Exchange;
			}
			 Double calculation3 = SwitchPos.get() - height;
			 if(calculation3 > mindist) {
				return Elevator_index.Switch1;
			}
			 Double calculation4 = ScaleDownPos.get() - height;
			 if(calculation4 > mindist) {
				return Elevator_index.ScaleUnderscoreDown;
			}
			 Double calculation5 = ScaleBalancedPos.get() - height;
			 if(calculation5 > mindist) {
				return Elevator_index.ScaleUnderscoreBalanced;
			}
			 Double calculation6 = ScaleUpPos.get() - height;
			 if(calculation6 > mindist) {
				return Elevator_index.ScaleUnderscoreUp;
			}
			return indexModeDesired;
		}
		
		public double getElevHeight_in() {
			elevatorEncoder.get();
			return elevatorEncoder.get() * Encoder_Pulse_Pur_Rev * Elevator_Inches_Pur_Rev;
		}
	}
	

