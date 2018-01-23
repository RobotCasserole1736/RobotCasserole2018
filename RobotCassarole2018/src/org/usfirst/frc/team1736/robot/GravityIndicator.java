package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;

public class GravityIndicator{
	private static GravityIndicator acelCtrl = null;
	
	private BuiltInAccelerometer robotAccelerometer;
	
	public static synchronized GravityIndicator getInstance() {
		if(acelCtrl == null)
			acelCtrl = new GravityIndicator();
		return acelCtrl;
	}
	
	double finalOutput = 0;	
	private GravityIndicator() {
		robotAccelerometer = new BuiltInAccelerometer();
	}
	public void update(){
			double accelerometerX = robotAccelerometer.getX();
			double accelerometerY = robotAccelerometer.getY();
			double tanAngleX = accelerometerY/accelerometerX;
			double angleY = 0;
			angleY = Math.atan(tanAngleX);
			double rightRadian = Math.PI/2;
			double angleB = 0;
			if (angleY <= 0){
				angleB = rightRadian + angleY;
			}
			else if (angleY >= 0) {
					angleB = -1*(rightRadian - angleY);
			}
			finalOutput = Math.toDegrees(angleB);
						}	
	double getRobotAngle() {
		return finalOutput;
	}
	
}