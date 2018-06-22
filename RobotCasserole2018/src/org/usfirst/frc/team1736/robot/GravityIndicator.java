package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;

public class GravityIndicator {
	private static GravityIndicator acelCtrl = null;

	private BuiltInAccelerometer robotAccelerometer;

	public static synchronized GravityIndicator getInstance() {
		if (acelCtrl == null)
			acelCtrl = new GravityIndicator();
		return acelCtrl;
	}

	double finalOutput = 0;

	private GravityIndicator() {
		CrashTracker.logClassInitStart(this.getClass());
		robotAccelerometer = new BuiltInAccelerometer();
		CrashTracker.logClassInitEnd(this.getClass());
	}

	public void update() {

		// Read input values from accel
		double accelerometerX = -1 * robotAccelerometer.getX();
		double accelerometerZ = robotAccelerometer.getZ();

		// Do some trig
		double tanAngleX = accelerometerZ / accelerometerX;
		double angleY = Math.atan(tanAngleX);

		double angleB = angleY;
		finalOutput = Math.toDegrees(angleB);
	}

	double getRobotAngle() {
		return finalOutput;
	}

}