package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.WebServer.CasseroleRobotPoseView;

public class RobotPose {

	public double leftVelosity_RPM;
	public double rightVelosity_RPM;
	public final double wheelRadius_Ft = 0.24;
	public final double robotRadius_Ft  = 0.9;
	public double poseX = 0;
	public double poseY = 0;
	public double poseThadus = 90;
	
	public void setLeftMotorSpeed(double speed) {
		leftVelosity_RPM = speed;
	}

	public void setRightMotorSpeed(double speed){
		rightVelosity_RPM = speed;
    }
	
	public void update() {
		double leftVelosity_FPS = leftVelosity_RPM * (2*3.14*wheelRadius_Ft / 60);
		double rightVelosity_FPS = rightVelosity_RPM * (2*3.14*wheelRadius_Ft / 60);
		double robotAngle_DPS = ((rightVelosity_FPS-leftVelosity_FPS)/(2*robotRadius_Ft) * 180/3.14);
		double X_dot = (rightVelosity_FPS+leftVelosity_FPS)/2; 
		
		poseX += 0.02 * (X_dot*Math.cos(poseThadus*(3.14/180)));
		poseY += 0.02 * (X_dot*Math.sin(poseThadus*(3.14/180)));
		poseThadus += 0.02 * robotAngle_DPS;
		CasseroleRobotPoseView.setRobotPose(poseX, poseY, poseThadus - 90);
		System.out.println("x");
		System.out.println(poseX);
		System.out.println("y");
		System.out.println(poseY);
		System.out.println("Thadus");
		System.out.println(poseThadus);
		}
	
}
