package org.usfirst.frc.team1736.robot;

public class Drivetrain {
	private static Drivetrain singularInstance = null;
	private Gearbox leftGearbox;
	private Gearbox rightGearbox;
	private double curFwdRevCmd;
	private double curRotCmd;
	public static synchronized Drivetrain getInstance() {
		if ( singularInstance == null)
			singularInstance = new Drivetrain();
		return singularInstance;
	}

	
	
	private Drivetrain() {
		
		leftGearbox = new Gearbox();
		rightGearbox = new Gearbox();
		

	}
	
	public void setForwardReverseCommand(double command) {
	
		curFwdRevCmd = command;
	}
	
	public void setRotateCommand(double command) {
		curRotCmd = command;
	}
	
	public void update() {
		
		
		double left = cap(curFwdRevCmd + curRotCmd);
		
		
		
		
		
		
		leftGearbox.setMotorCommand(0);
		
		rightGearbox.setMotorCommand(0);
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