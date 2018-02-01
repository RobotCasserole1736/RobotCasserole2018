package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;


public class ElevatorCtrl {
	private static ElevatorCtrl  singularInstance = null;
	
	//Input Commands
	private Elevator_index indexModeDesired;
	private boolean continuousModeDesired;
	private double continuousModeCmd;
	private double curMotorCmd;
	
	//State variables
	public double currentHeightCmd = 0;
	public double desiredHeight = 0;
	public double actualHeight = 0;
	public boolean isZeroed = false;

	//Travel limit reached check booleans
	boolean upperLimitSwitchReached = false;
	boolean lowerLimitSwitchReached = false;
	
	
	//Physical devices
	private Spark motor1;
	private Spark motor2;
	DigitalInput upperLimitSwitch = null;
	DigitalInput lowerLimitSwitch = null;
	private Encoder elevatorEncoder;
	
	//Height calibrations
	Calibration FloorPosCal = null;
	Calibration SwitchPosCal = null;
	Calibration ScaleDownPosCal = null;
	Calibration ScaleBalancedPosCal = null;
	Calibration ScaleUpPosCal = null;
	Calibration ExchangePosCal = null;
	
	//Closed-loop calibrations
	Calibration UpMotorCmdCal = null;
	Calibration DownMotorCmdCal = null;
	Calibration ElevCtrlDeadzoneCal = null;

	//Constants
	public final double ELEV_ENC_PULSES_PER_REV = 1024;
	public final double ELEV_WINCH_DIAMETER_IN = 1.5;
	
	//Derived Constants
	public final double ELEV_HEIGHT_IN_PER_WINCH_REV = 2*Math.PI*(ELEV_WINCH_DIAMETER_IN/2.0); //Linear rope distance is 2*pi*r_winch


	
	
	public static synchronized ElevatorCtrl getInstance() {
		if ( singularInstance == null)
			singularInstance = new ElevatorCtrl();
		return singularInstance;
	}
	
	private ElevatorCtrl() {
		//Init physical devices
		elevatorEncoder = new Encoder(RobotConstants.DI_ELEVATER_ENCODER_A, RobotConstants.DI_ELEVATER_ENCODER_B );
		motor1 = new Spark(RobotConstants.PWM_ELEVATOR_ONE);
		motor2 = new Spark(RobotConstants.PWM_ELEVATOR_TWO);
		upperLimitSwitch = new DigitalInput(RobotConstants.DI_ELEVATER_UPPER_LIMIT_SW);
		lowerLimitSwitch = new DigitalInput(RobotConstants.DI_ELEVATER_LOWER_LIMIT_SW);	
		
		//Init Calibrations for positions & speeds
		FloorPosCal = new Calibration("Elev Floor position (in)", 0.0, 0.0, 84.0);
		SwitchPosCal = new Calibration("Elev Switch position (in)", 20.0, 0.0,84.0);
		ScaleDownPosCal = new Calibration("Elev Scale down Position (in)", 55.0, 0.0, 84.0);
		ScaleBalancedPosCal = new Calibration("Elev Scale balanced postion (in)", 66.0, 0.0, 84.0);
		ScaleUpPosCal = new Calibration ("Elev Scale up position (in)", 77.0, 0.0, 84.0);
		ExchangePosCal = new Calibration("Elev Exchange position (in)", 4.0, 0.0, 84.0);
		UpMotorCmdCal = new Calibration("Elev Closed-Loop up speed (cd)", 0.5, 0.0, 1.0);
		DownMotorCmdCal = new Calibration("Elev Closed-Loop down speed (cmd)", 0.5, 0.0, 1.0);
		ElevCtrlDeadzoneCal = new Calibration("Elev Closed-Loop deadzone (in)", 1.0, 0.0, 20.0);
		
		
	}
	
	
	public void update() {
		//Check for zeroed condition
		if(lowerLimitSwitchReached == true) {
			elevatorEncoder.reset();
			isZeroed = true;
		}

		
		if (continuousModeDesired == true || isZeroed == false) {
			
			//Continuous mode - used whenever the driver wants control, or the encoder has not yet been zeroed.
			
			//Open Loop control - Operator commands motor directly
			curMotorCmd = continuousModeCmd;
			
			//Keep the closed loop command set to the nearest height
			indexModeDesired = desiredHightToEmun(getElevHeight_in());

		} else {
			
			//Indexed mode - the default case where the driver just presses buttons.
			
			//Super-de-duper simple bang-bang control of elevator in closed loop
			desiredHeight = enumToDesiredHeight(indexModeDesired);
			actualHeight = getElevHeight_in();
			if(isInDeadzone()) {
				//Deadzone, don't run motor.
				curMotorCmd = 0;
			}else if(desiredHeight >= actualHeight) {
				//Too low, run motor up.
				curMotorCmd = UpMotorCmdCal.get();
			}else if(desiredHeight < actualHeight) {
				//Too high, run motor down.
				curMotorCmd = DownMotorCmdCal.get();
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
		
		//Actually output command to motors
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
			currentHeightCmd = FloorPosCal.get();
		}
		else if(cmd == Elevator_index.Exchange) {
			currentHeightCmd = ExchangePosCal.get();
		}
		else if(cmd == Elevator_index.ScaleUnderscoreDown) {
			currentHeightCmd = ScaleDownPosCal.get();
		}
		else if(cmd == Elevator_index.ScaleUnderscoreBalanced) {
			currentHeightCmd = ScaleBalancedPosCal.get();
		}
		else if(cmd == Elevator_index.ScaleUnderscoreUp) {
			currentHeightCmd = ScaleUpPosCal.get();
		}
		else if(cmd == Elevator_index.Switch1) {
			currentHeightCmd = SwitchPosCal.get();
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
		double calculation1 = Math.abs(FloorPosCal.get() - height);
		if(calculation1 < mindist) {
			mindist = calculation1;
			returnValue = Elevator_index.Bottom;
		}
		
		double calculation2 = Math.abs(ExchangePosCal.get() - height);
		if(calculation2 < mindist) {
			mindist = calculation2; 
			returnValue = Elevator_index.Exchange;
		}
		 
	 	double calculation3 = Math.abs(SwitchPosCal.get() - height);
		if(calculation3 > mindist) {
			mindist = calculation3;
			returnValue =  Elevator_index.Switch1;
		}
		 
		double calculation4 = Math.abs(ScaleDownPosCal.get() - height);
		if(calculation4 > mindist) {
			mindist = calculation4; 
			returnValue =  Elevator_index.ScaleUnderscoreDown;
		}
		 
		double calculation5 = Math.abs(ScaleBalancedPosCal.get() - height);
		if(calculation5 > mindist) {
			mindist = calculation5; 
			returnValue =  Elevator_index.ScaleUnderscoreBalanced;
		}
		 
		double calculation6 = Math.abs(ScaleUpPosCal.get() - height);
		if(calculation6 > mindist) {
			 mindist = calculation6;
			 returnValue =  Elevator_index.ScaleUnderscoreUp;
		}
		 
		 
		return returnValue;
	}
	
	
	
	// Public getters and setters
	
	public double getElevHeight_in() {
		elevatorEncoder.get();
		return elevatorEncoder.get() * (1.0/ELEV_ENC_PULSES_PER_REV) * ELEV_HEIGHT_IN_PER_WINCH_REV;
	}
	
	public boolean getUpperlimitSwitch() {
		return upperLimitSwitch.get();
	}
	
	public boolean getLowerLimitSwitch() {
		return lowerLimitSwitch.get();
	}
		
	public boolean getIsZeroed(){
		return isZeroed;	
	}
	
	public boolean isInDeadzone() {
		return (Math.abs(desiredHeight - actualHeight) < ElevCtrlDeadzoneCal.get());
	}
}

	

