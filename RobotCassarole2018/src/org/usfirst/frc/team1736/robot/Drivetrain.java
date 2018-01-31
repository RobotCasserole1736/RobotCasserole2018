package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;

public class Drivetrain {
	private static Drivetrain singularInstance = null;
	
	
	public Gearbox leftGearbox;
	public Gearbox rightGearbox;
	private double curFwdRevCmd = 0; 
	private double curRotCmd = 0;
	private double rightMotorCommand;
	private double leftMotorCommand;
	private double curLeftSpeedCmd_RPM = 0;
	private double curRightSpeedCmd_RPM = 0;
	private boolean isClosedLoop = false;
	private double speedFtpS = 0;
	double leftWheelRPM = 0;
	double rightWheelRPM = 0;
	double perGearboxCurrentLimit = 1000;
	
	public static final double SPROCKET_RATIO = 15.0/26.0; //15 tooth sprocket on gearbox, 26 tooth sprocket on wheels
	public static final double WHEEL_ROLLING_RADIUS_FT = 0.45; //6 inch pneumatic wheels with a bit of squish
	
	Calibration curLimitEnable;
	

	public static synchronized Drivetrain getInstance() {
		if ( singularInstance == null)
			singularInstance = new Drivetrain();
		return singularInstance;
	}

	
	
	private Drivetrain() {
		
		boolean useRealGearbox = false;
		
		if(useRealGearbox) {
			leftGearbox = new RealGearbox(RobotConstants.CANID_DRIVETRAIN_LEFT_MASTER_SRX, 
					                  RobotConstants.CANID_DRIVETRAIN_LEFT_SLAVE1_SRX, 
					                  RobotConstants.CANID_DRIVETRAIN_LEFT_SLAVE2_SRX,
					                  "left");
			rightGearbox = new RealGearbox(RobotConstants.CANID_DRIVETRAIN_RIGHT_MASTER_SRX, 
					                   RobotConstants.CANID_DRIVETRAIN_RIGHT_SLAVE1_SRX,
					                   RobotConstants.CANID_DRIVETRAIN_RIGHT_SLAVE2_SRX,
					                   "right");
		} else {
			leftGearbox  = new SimGearbox();
			rightGearbox = new SimGearbox();
		}
		
		rightGearbox.setInverted(true);
		curLimitEnable = new Calibration("Enable DT Current Limit", 0, 0, 1.0);
		
	}
		
	public void setForwardReverseCommand(double command) {
		curFwdRevCmd = command;
		isClosedLoop = false;
	}
	
	public void setRotateCommand(double command) {
		curRotCmd = command;
		isClosedLoop = false;
	}
	
	public void setLeftWheelSpeed(double speed_RPM) {
		curLeftSpeedCmd_RPM = speed_RPM/SPROCKET_RATIO;
		isClosedLoop = true;
	}
	
	public void setRightWheelSpeed(double speed_RPM) {
		curRightSpeedCmd_RPM = speed_RPM/SPROCKET_RATIO;
		isClosedLoop = true;
	}
	
	public void update() {
	
		if(!isClosedLoop) {
			//Open loop logic - calculate motor commands from driver inputs
			if (curFwdRevCmd > 0.0) {
				if (curRotCmd > 0.0) {
					leftMotorCommand = curFwdRevCmd - curRotCmd;
		            rightMotorCommand = Math.max(curFwdRevCmd, curRotCmd);
				} else {
					leftMotorCommand = Math.max(curFwdRevCmd, -curRotCmd);
		            rightMotorCommand = curFwdRevCmd + curRotCmd;
				}
			} else {
				if (curRotCmd > 0.0) {
					leftMotorCommand = -Math.max(-curFwdRevCmd, curRotCmd);
				    rightMotorCommand = curFwdRevCmd + curRotCmd;
				  } else {
				    leftMotorCommand = curFwdRevCmd - curRotCmd;
				    rightMotorCommand = -Math.max(-curFwdRevCmd, -curRotCmd);
				  }
			}
			leftGearbox.setMotorCommand(leftMotorCommand);
			rightGearbox.setMotorCommand(rightMotorCommand);
			
		} else {
			//Closed loop logic - set closed loop speed commands
			leftGearbox.setMotorSpeed(curLeftSpeedCmd_RPM);
			rightGearbox.setMotorSpeed(curRightSpeedCmd_RPM);
		}
		
		//Update present wheel speeds
		leftWheelRPM = leftGearbox.getSpeedRPM()*SPROCKET_RATIO;
		rightWheelRPM = rightGearbox.getSpeedRPM()*SPROCKET_RATIO;
		
		//Update net robot speed calculation.
		//ft/sec = rev/min * ft/rev * min/sec
		double leftSpeedFtpS = leftWheelRPM*(2*Math.PI*WHEEL_ROLLING_RADIUS_FT)/60.0;
		double rightSpeedFtpS = rightWheelRPM*(2*Math.PI*WHEEL_ROLLING_RADIUS_FT)/60.0;
		speedFtpS = (leftSpeedFtpS + rightSpeedFtpS / 2.0);
		
	}
	
	public void updatePIDGains() {
		leftGearbox.updateCalibrations();
		rightGearbox.updateCalibrations();
	}
	
	public double getSpeedFtpS() {
		return speedFtpS;
	}
	
	public double getRightWheelSpeedAct_RPM() {
		return rightWheelRPM;
	}
	
	public double getLeftWheelSpeedAct_RPM() {
		return leftWheelRPM;
	}
	
	public double getRightWheelSpeedDes_RPM() {
		if(isClosedLoop) {
			return curRightSpeedCmd_RPM;
		} else {
			return 0;
		}
	}
	
	public double getLeftWheelSpeedDes_RPM() {
		if(isClosedLoop) {
			return curLeftSpeedCmd_RPM;
		} else {
			return 0;
		}
	}
	
	public double getLeftMotorCommand() {
		return leftGearbox.getMotorCommand();
	}
	
	public double getRightMotorCommand() {
		return rightGearbox.getMotorCommand();
	}
	
	public boolean getCurrentHigh() {
		if(
		   leftGearbox.getTotalCurrent() > perGearboxCurrentLimit*0.9
		    ||
		   rightGearbox.getTotalCurrent() > perGearboxCurrentLimit*0.9 	
		  ) {
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * Sets the current limit for the whole drivetrain.
	 * @param limit_A
	 */
	public void setCurrentLimit_A(double limit_A) {
		if(curLimitEnable.get() == 1) {
			perGearboxCurrentLimit = limit_A/2.0;
		} else {
			perGearboxCurrentLimit = 1000; //Effectively remove any reasonable limit
		}
		leftGearbox.setCurrentLimit_A(perGearboxCurrentLimit);
		rightGearbox.setCurrentLimit_A(perGearboxCurrentLimit);
	}
	
	/**
	 * 
	 * @param x Input value
	 * @return x, but limited to the range [-1,1]
	 */
	public double cap(double x) {
		double y;	
		
		if(x<-1) {
			y=-1;
		}
		else if(x>-1 & x<1 ) {
			y=x;
		}
		else {
			y=1;
		}
		return y;
		
	}
	
}