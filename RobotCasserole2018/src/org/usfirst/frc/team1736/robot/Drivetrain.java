package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;
import org.usfirst.frc.team1736.lib.Util.CrashTracker;

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
	private double curHeadingCmd_deg = 0;
	double systemVoltage_V;
	double systemCurrent_A;
	private boolean isClosedLoop = false;
	private double speedFtpS = 0;
	double leftWheelRPM = 0;
	double rightWheelRPM = 0;
	double perGearboxCurrentLimit = 1000;

	

	
	public static final double SPROCKET_RATIO = 15.0/26.0; //15 tooth sprocket on gearbox, 26 tooth sprocket on wheels
	public static final double WHEEL_ROLLING_RADIUS_FT = 0.26; //~6 inch pneumatic wheels with a bit of squish. Measured with a ruler.
	
	Calibration curLimitEnable;
	Calibration headingGainCal;
	
	//Current Limiting Verification
	CIMCurrentEstimator leftCurrentEst;
	CIMCurrentEstimator rightCurrentEst;
	double reductionFactor = 1.0;
	BatteryParamEstimator bpe;
	final static int BPE_length = 200; 
	final static double BPE_confidenceThresh_A = 10.0;
	Calibration minAllowableVoltageCal;
	final static double REDUCTION_ITER_STEP = 0.1;
	double leftCurrentEst_A = 0;
	double rightCurrentEst_A = 0;
	

	public static synchronized Drivetrain getInstance() {
		if ( singularInstance == null)
			singularInstance = new Drivetrain();
		return singularInstance;
	}

	
	
	private Drivetrain() {
		CrashTracker.logClassInitStart(this.getClass());
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
		
		leftGearbox.setInverted(true);
		
		rightGearbox.setInverted(false);
		
		//Set up current limiting & battery estimation
		leftCurrentEst = new CIMCurrentEstimator(3,0);
		rightCurrentEst = new CIMCurrentEstimator(3,0);
		bpe = new BatteryParamEstimator(BPE_length); 
		bpe.setConfidenceThresh(BPE_confidenceThresh_A);
		minAllowableVoltageCal = new Calibration("Min allowable system voltage", 7.5, 5.0, 12.0);
		
		curLimitEnable = new Calibration("DT Enable Current Limit", 1.0, 0, 1.0);
		headingGainCal = new Calibration("DT Heading Comp P Gain", 4.0);

		CrashTracker.logClassInitEnd(this.getClass());
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
	
	public void setDesiredPose(double heading_deg) {
		curHeadingCmd_deg = heading_deg;
		isClosedLoop = true;
	}
	
	public void update() {
		
		//Update IO for gearbox
		leftGearbox.sampleSensors();
		rightGearbox.sampleSensors();
		
		//Perform current-limiting calculations
		bpe.updateEstimate(systemVoltage_V, systemCurrent_A);
	
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
			
			//If requested, perform current limiting
			calcCurrentLimitFactor(leftMotorCommand, rightMotorCommand);
			if(curLimitEnable.get() == 1.0) {
				leftMotorCommand  *= reductionFactor;
				rightMotorCommand *= reductionFactor;
			}
			
			//Run final motor current estimation for logging purposes
			leftCurrentEst_A = leftCurrentEst.getCurrentEstimate(leftGearbox.getMotorSpeedRadpSec(), leftMotorCommand, systemVoltage_V);
			rightCurrentEst_A = rightCurrentEst.getCurrentEstimate(rightGearbox.getMotorSpeedRadpSec(), rightMotorCommand, systemVoltage_V);
			
			//pass final commands to drivetrain
			leftGearbox.setMotorCommand(leftMotorCommand);
			rightGearbox.setMotorCommand(rightMotorCommand);
			
		} else {
			
			//Closed loop logic
			double headingCompVal = 0;
			
			//Calc heading compensation. Simple P controller. Sorta.
			if(Gyro.getInstance().isOnline()) {
				//Switch-mode gains since the in-motion correction is more agressive than stand still logic and causes instability
				if(Math.abs(curLeftSpeedCmd_RPM) > 5 ||Math.abs(curRightSpeedCmd_RPM) > 5 ) {
					headingCompVal = (Gyro.getInstance().getAngle() - curHeadingCmd_deg) * headingGainCal.get();
				} else {
					headingCompVal = 0;
				}
			}
			curLeftSpeedCmd_RPM -= headingCompVal;
			curRightSpeedCmd_RPM += headingCompVal;
			
			//Set values to gearboxes
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
		speedFtpS = Math.abs((leftSpeedFtpS + rightSpeedFtpS) / 2.0);
		
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
			return curRightSpeedCmd_RPM*SPROCKET_RATIO;
		} else {
			return 0;
		}
	}
	
	public double getLeftWheelSpeedDes_RPM() {
		if(isClosedLoop) {
			return curLeftSpeedCmd_RPM*SPROCKET_RATIO;
		} else {
			return 0;
		}
	}
	
	public double getHeadingDes_deg() {
		if(isClosedLoop) {
			return curHeadingCmd_deg;
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
	
	public double getLeftCurrent() {
		return leftGearbox.getTotalCurrent();
	}
	
	public double getRightCurrent() {
		return rightGearbox.getTotalCurrent();
	}
	
	public double getLeftCurrentEst() {
		return leftCurrentEst_A;
	}
	
	public double getRightCurrentEst() {
		return rightCurrentEst_A;
	}
	
	public double getLimitFactor() {
		return reductionFactor;
	}
	
	public boolean getCurrentHigh() {
		if(	reductionFactor != 1.0) {
			return true;
		} else {
			return false;
		}
	}
	
	public double getBattESR() {
		return bpe.getEstESR();
	}
	
	public double getBattVoc() {
		return bpe.getEstVoc();
	}
	
	public void setSystemVoltageCurrent(double voltage_v_in, double current_a_in) {
		systemVoltage_V = voltage_v_in;
		systemCurrent_A = current_a_in;
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
	
	//2016-style current limiter. Calculates an adjustment factor for drivetrain commands based on predicted current draw.
	private void calcCurrentLimitFactor(double leftOutput, double rightOutput) {
		//If motor induces acceptable voltage drop, just set it
		if(isAcceptableVoltage(leftOutput, rightOutput))
		{
			reductionFactor = 1;
		}
		else
		{
			reductionFactor = 0;
			//If not, iterate over a set of reduction factors
			//to get the drop acceptable it.
			for(double i = 1.0; i >= 0; i-=REDUCTION_ITER_STEP){
				if(isAcceptableVoltage(leftOutput*i, rightOutput*i)){
					reductionFactor = i;
					break;
				}
			}
			reductionFactor = Math.max(reductionFactor, REDUCTION_ITER_STEP);
		}
	}
	
	private boolean isAcceptableVoltage(double leftOutput, double rightOutput)
	{
		//handle when this is called without proper initialization
		if(leftCurrentEst == null || leftCurrentEst == null){
			return true;
		}
		
		double leftCurEst = leftCurrentEst.getCurrentEstimate(leftGearbox.getMotorSpeedRadpSec(), leftOutput, systemVoltage_V);
		double rightCurEst = rightCurrentEst.getCurrentEstimate(rightGearbox.getMotorSpeedRadpSec(), rightOutput, systemVoltage_V);
		
		if(bpe.getEstVsys(leftCurEst + rightCurEst+10) > minAllowableVoltageCal.get())
			return true;
		else
			return false;
	}
	
}