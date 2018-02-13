package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;
import org.usfirst.frc.team1736.lib.Util.CrashTracker;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;


public class ElevatorCtrl {
	private static ElevatorCtrl  singularInstance = null;
	
	//Input Commands
	private ElevatorIndex indexModeDesired;
	private boolean continuousModeDesired;
	private double continuousModeCmd;
	private double curMotorCmd;
	
	//State variables
	public double currentHeightCmd = 0;
	public double desiredHeight = 0;
	public double actualHeight = 0;
	public boolean isZeroed = false;

	//Travel limit reached check booleans
	boolean upperTravelLimitReached = false;
	boolean lowerTravelLimitReached = false;
	
	
	//Physical devices
	private Spark motor1;
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
		CrashTracker.logClassInitStart(this.getClass());

		//Init physical devices
		elevatorEncoder = new Encoder(RobotConstants.DI_ELEVATER_ENCODER_A, RobotConstants.DI_ELEVATER_ENCODER_B );
		motor1 = new Spark(RobotConstants.PWM_ELEVATOR_ONE);
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
		

		CrashTracker.logClassInitEnd(this.getClass());

		
	}
	
	public void sampleSensors() {
		//Check if we've hit the upper or lower limits of travel yet
		if(upperLimitSwitch.get()) {
			upperTravelLimitReached = true;
		} else {
			upperTravelLimitReached = false;
		}
		
		if(lowerLimitSwitch.get()) {
			lowerTravelLimitReached = true;
		} else {
			lowerTravelLimitReached = false;
		}
		
		//Read in present elevator height
		actualHeight = elevatorEncoder.get()* (1.0/ELEV_ENC_PULSES_PER_REV) * ELEV_HEIGHT_IN_PER_WINCH_REV;;
	}
	
	
	public void update() {
		//Check for zeroed condition
		if(lowerTravelLimitReached == true) {
			elevatorEncoder.reset();
			isZeroed = true;
		}

		if(isZeroed == false) {
			//Uncalibrated mode - like continuous, but don't trust the encoder reading
			
			//Open Loop control - Operator commands motor directly
			curMotorCmd = continuousModeCmd;
			
		} else if (continuousModeDesired == true ) {
			
			//Continuous mode - used whenever the driver wants control, or the encoder has not yet been zeroed.
			
			//Open Loop control - Operator commands motor directly
			curMotorCmd = continuousModeCmd;
			
			//Keep the closed loop command set to the nearest height
			indexModeDesired = desiredHightToEmun(getElevActualHeight_in());

		} else {
			
			//Indexed mode - the default case where the driver just presses buttons.
			
			//Super-de-duper simple bang-bang control of elevator in closed loop

			if(isAtDesiredHeight()) {
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
		
		
		//Limit motor speed if we've hit either limit.
		if(upperTravelLimitReached == true) {
			if(curMotorCmd >= 0) {
				curMotorCmd = 0;
			}
		}

		
		if(lowerTravelLimitReached == true){
			if(curMotorCmd <= 0) {
				curMotorCmd = 0;
			}
		}
		
		//Actually output command to motors
		motor1.set(curMotorCmd);
	}
	
	public void setIndexDesired (ElevatorIndex cmd) {
		if(cmd != ElevatorIndex.NO_NEW_SELECTION) {
			indexModeDesired = cmd;
			desiredHeight = enumToDesiredHeight(indexModeDesired);
		}
	}
	
	public void setContModeDesired (boolean modecommand) {
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
	private double enumToDesiredHeight(ElevatorIndex cmd) {
		if(cmd == ElevatorIndex.BOTTOM) {
			currentHeightCmd = FloorPosCal.get();
		}
		else if(cmd == ElevatorIndex.EXCHANGE) {
			currentHeightCmd = ExchangePosCal.get();
		}
		else if(cmd == ElevatorIndex.SCALE_DOWN) {
			currentHeightCmd = ScaleDownPosCal.get();
		}
		else if(cmd == ElevatorIndex.SCALE_BALANCED) {
			currentHeightCmd = ScaleBalancedPosCal.get();
		}
		else if(cmd == ElevatorIndex.SCALE_UP) {
			currentHeightCmd = ScaleUpPosCal.get();
		}
		else if(cmd == ElevatorIndex.SWITCH) {
			currentHeightCmd = SwitchPosCal.get();
		}
		else if(cmd == ElevatorIndex.NO_NEW_SELECTION){
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
	ElevatorIndex desiredHightToEmun(double height) {
		ElevatorIndex returnValue = (ElevatorIndex.BOTTOM);
		double mindist = 100; //a sufficiently large number
		
		//Check every possible height to see which one the 
		// current height is closes to.
		double calculation1 = Math.abs(FloorPosCal.get() - height);
		if(calculation1 < mindist) {
			mindist = calculation1;
			returnValue = ElevatorIndex.BOTTOM;
		}
		
		double calculation2 = Math.abs(ExchangePosCal.get() - height);
		if(calculation2 < mindist) {
			mindist = calculation2; 
			returnValue = ElevatorIndex.EXCHANGE;
		}
		 
	 	double calculation3 = Math.abs(SwitchPosCal.get() - height);
		if(calculation3 > mindist) {
			mindist = calculation3;
			returnValue =  ElevatorIndex.SWITCH;
		}
		 
		double calculation4 = Math.abs(ScaleDownPosCal.get() - height);
		if(calculation4 > mindist) {
			mindist = calculation4; 
			returnValue =  ElevatorIndex.SCALE_DOWN;
		}
		 
		double calculation5 = Math.abs(ScaleBalancedPosCal.get() - height);
		if(calculation5 > mindist) {
			mindist = calculation5; 
			returnValue =  ElevatorIndex.SCALE_BALANCED;
		}
		 
		double calculation6 = Math.abs(ScaleUpPosCal.get() - height);
		if(calculation6 > mindist) {
			 mindist = calculation6;
			 returnValue =  ElevatorIndex.SCALE_UP;
		}
		 
		 
		return returnValue;
	}
	
	
	
	// Public getters and setters
	
	public double getElevActualHeight_in() {
		return actualHeight;
	}
	
	public double getElevDesiredHeight_in() {
		return desiredHeight;
	}
	
	public boolean getLowerTravelLimitReached() {
		return lowerTravelLimitReached;
	}
	
	public boolean getUpperTravelLimitReached() {
		return upperTravelLimitReached;
	}
		
	public boolean getIsZeroed(){
		return isZeroed;	
	}
	
	public boolean isAtDesiredHeight() {
		return (Math.abs(desiredHeight - actualHeight) < ElevCtrlDeadzoneCal.get());
	}
}

	

