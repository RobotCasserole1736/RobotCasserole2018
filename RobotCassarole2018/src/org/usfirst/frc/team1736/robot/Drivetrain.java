package org.usfirst.frc.team1736.robot;

public class Drivetrain {
	private static Drivetrain singularInstance = null;
	
	
	private Gearbox leftGearbox;
	private Gearbox rightGearbox;
	private double curFwdRevCmd;
	private double curRotCmd;
	private double rightMotorCommand;
	private double leftMotorCommand;
	public static synchronized Drivetrain getInstance() {
		if ( singularInstance == null)
			singularInstance = new Drivetrain();
		return singularInstance;
	}

	
	
	private Drivetrain() {
		
		leftGearbox = new Gearbox(0, 1, 2);
		rightGearbox = new Gearbox(3, 4, 5);
		

	}
	
	public double getLeftMotorCommand() {
		return (leftMotorCommand);
		
	}
	
	public double getRightMotorCoommand() {
		return (rightMotorCommand);
	}
	
		
	public void setForwardReverseCommand(double command) {
	
		curFwdRevCmd = command;
	}
	
	public void setRotateCommand(double command) {
		curRotCmd = command;
	}
	
	public void update() {
		
		
		leftMotorCommand = cap(curFwdRevCmd + curRotCmd);
		rightMotorCommand = cap(curFwdRevCmd - curRotCmd);
		
		
		
		
		
		leftGearbox.setMotorCommand(leftMotorCommand);
		
		rightGearbox.setMotorCommand(rightMotorCommand);
	}
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