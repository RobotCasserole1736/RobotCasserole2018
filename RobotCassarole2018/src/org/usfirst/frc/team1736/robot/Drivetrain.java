package org.usfirst.frc.team1736.robot;

public class Drivetrain {
	private static Drivetrain singularInstance = null;
	private TalonSRX motorleft1;
	private TalonSRX motorleft2;
	private TalonSRX motorleft3;
	private TalonSRX motorright1;
	private TalonSRX motorright2;
	private TalonSRX motorright3;
	
	public static synchronized Drivetrain getInstance() {
		if ( singularInstance == null)
			singularInstance = new Drivetrain();
		return singularInstance;
	}
	
	
	private Drivetrain() {

	}
}
