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
	public double currentHeightCmd = 0;
	public double desiredHeight;
	public boolean isZeroed = false;
	
	
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
		
		//Calibrations for positions & speeds
		FloorPos = new Calibration("Floor position", 0.0, 0.0, 84.0);
		SwitchPos = new Calibration("Switch position", 20.0, 0.0,84.0);
		ScaleDownPos = new Calibration("Scale down Position", 55.0, 0.0, 84.0);
		ScaleBalancedPos = new Calibration("Scale balanced postion", 66.0, 0.0, 84.0);
		ScaleUpPos = new Calibration ("Scale up position", 77.0, 0.0, 84.0);
		ExchangePos = new Calibration("Exchange position", 4.0, 0.0, 84.0);
		UpSpeed = new Calibration("Elevator Closed-Loop up speed", 0.5, 0.0, 1.0);
		DownSpeed = new Calibration("Elevator Closed-Loop down speed", 0.5, 0.0, 1.0);
		
		
	}
	
	
	public void update() {
		if (continuousModeDesired == true) {
			
			//Open Loop control - Operator commands motor directly
			curMotorCmd = continuousModeCmd;
			
			//Keep the closed loop command set to the nearest height
			indexModeDesired = desiredHightToEmun(getElevHeight_in());

		} else {
			
			//Super-de-duper simple bang-bang control of elevator in closed loop
			desiredHeight = enumToDesiredHeight(indexModeDesired);
			double actualHeight = getElevHeight_in();
			if(desiredHeight >= actualHeight) {
				curMotorCmd = UpSpeed.get();
			}else if(desiredHeight <= actualHeight) {
				curMotorCmd = DownSpeed.get();
			}
		}
		
		
		//Check if we've hit the upper or lower limits of travel yet
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
		
		
		//Limit motor speed if we've hit either limit.
		if(upperLimitSwitchReached == true) {
			if(curMotorCmd >= 0) {
				curMotorCmd = 0;
			}
		}
		
		if(lowerLimitSwitchReached == true){
			if(curMotorCmd <= 0) {
				curMotorCmd = 0;
			}
		}
		
		//Actually output command tomotors
		motor1.set(curMotorCmd);
		motor2.set(curMotorCmd);
	}
	
	public void setIndexDesired (Elevator_index cmd) {
		if(cmd != Elevator_index.nothingUnderscoreNew) {
			indexModeDesired = cmd;
		}
	}
	
	public void setContMode (boolean modecommand) {
		continuousModeDesired = modecommand;
	}
	
	public void setContModeCmd (double cmd) {
		continuousModeCmd = cmd;
	}

	
	public double getMotorCmd() {
		return curMotorCmd;
	}
	
	
	/**
	 * Conversion between an elevator height (from an enum value) to the
	 * presently configured height (in inches)
	 * @param cmd enum height command
	 * @return height, in inches.
	 */
	private double enumToDesiredHeight(Elevator_index cmd) {
		if(cmd == Elevator_index.Bottom) {
			currentHeightCmd = FloorPos.get();
		}
		else if(cmd == Elevator_index.Exchange) {
			currentHeightCmd = ExchangePos.get();
		}
		else if(cmd == Elevator_index.ScaleUnderscoreDown) {
			currentHeightCmd = ScaleDownPos.get();
		}
		else if(cmd == Elevator_index.ScaleUnderscoreBalanced) {
			currentHeightCmd = ScaleBalancedPos.get();
		}
		else if(cmd == Elevator_index.ScaleUnderscoreUp) {
			currentHeightCmd = ScaleUpPos.get();
		}
		else if(cmd == Elevator_index.Switch1) {
			currentHeightCmd = SwitchPos.get();
		}
		else if(cmd == Elevator_index.nothingUnderscoreNew){
			//do nothing
		}
		else {
			currentHeightCmd = 0;
		}
		return currentHeightCmd;
	}
		
	/**
	 * takes in a given height and returns the nearest enum height
	 * @param height height in inches
	 * @return the enum of the nearest level to the given height
	 */
	Elevator_index desiredHightToEmun(double height) {
		Elevator_index returnValue = (Elevator_index.Bottom);
		double mindist = 100; //a sufficiently large number
		
		//Check every possible height to see which one the 
		// current height is closes to.
		double calculation1 = Math.abs(FloorPos.get() - height);
		if(calculation1 < mindist) {
			mindist = calculation1;
			returnValue = Elevator_index.Bottom;
		}
		
		double calculation2 = Math.abs(ExchangePos.get() - height);
		if(calculation2 < mindist) {
			mindist = calculation2; 
			returnValue = Elevator_index.Exchange;
		}
		 
	 	double calculation3 = Math.abs(SwitchPos.get() - height);
		if(calculation3 > mindist) {
			mindist = calculation3;
			returnValue =  Elevator_index.Switch1;
		}
		 
		double calculation4 = Math.abs(ScaleDownPos.get() - height);
		if(calculation4 > mindist) {
			mindist = calculation4; 
			returnValue =  Elevator_index.ScaleUnderscoreDown;
		}
		 
		double calculation5 = Math.abs(ScaleBalancedPos.get() - height);
		if(calculation5 > mindist) {
			mindist = calculation5; 
			returnValue =  Elevator_index.ScaleUnderscoreBalanced;
		}
		 
		double calculation6 = Math.abs(ScaleUpPos.get() - height);
		if(calculation6 > mindist) {
			 mindist = calculation6;
			 returnValue =  Elevator_index.ScaleUnderscoreUp;
		}
		 
		 
		return returnValue;
	}
	
	
	
	// Public getters and setters
	
	public double getElevHeight_in() {
		elevatorEncoder.get();
		return elevatorEncoder.get() * Encoder_Pulse_Pur_Rev * Elevator_Inches_Pur_Rev;
	}
	
	public boolean getUpperlimitSwitch() {
		return upperLimitSwitch.get();
	}
	
	public boolean getLowerLimitSwitch() {
		return lowerLimitSwitch.get();
	}
		
	public boolean getIsZeroed(){
		if(isZeroed = false) {
		curMotorCmd = DownSpeed.get();
			if(lowerLimitSwitchReached == true) {
				elevatorEncoder.reset();
				isZeroed = true;
			}
		}
		return isZeroed;	
	}
}

	

