package org.usfirst.frc.team1736.robot;

public class Drivetrain {
	private static Drivetrain singularInstance = null;
	
	
	public static synchronized Drivetrain getInstance() {
		if ( singularInstance == null)
			singularInstance = new Drivetrain();
		return singularInstance;
	}

	
	
	private Drivetrain() {
		

	}
}

