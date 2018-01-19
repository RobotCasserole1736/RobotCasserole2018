package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;

public class Accelerometer {
	private Accelerometer() {
		robotAccelerometer = new BuiltInAccelerometer();
	}
	public void update(){
			double accelerometerX = robotAccelerometer.getX();
			double accelerometerY = robotAccelerometer.getY();
			
						}	
}