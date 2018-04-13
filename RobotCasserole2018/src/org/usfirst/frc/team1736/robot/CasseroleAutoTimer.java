package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Timer;

public class CasseroleAutoTimer {
	private double currentTime = 0.0; 
	private double startTime = 0.0;
	private double elapsedTime = 0.0;
	
	public double timeStart(){
		startTime = Timer.getFPGATimestamp();	
		return startTime;
	}	
	
	public double elapsedTime() {
		currentTime = Timer.getFPGATimestamp();
		elapsedTime = currentTime - startTime;
		return elapsedTime;
	}

	
}
