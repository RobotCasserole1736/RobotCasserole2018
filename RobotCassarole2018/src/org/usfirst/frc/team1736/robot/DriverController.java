package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.XboxController;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class DriverController {
	private static DriverController driveCtrl = null;

	private XboxController driveController;
	
	public static synchronized DriverController getInstance() {
		if(driveCtrl == null)
			driveCtrl = new DriverController();
		return driveCtrl;
	}
	private DriverController() {
		CrashTracker.logGenericMessage("start of"+(this.getClass().getSimpleName()));
		driveController = new XboxController(0);
		CrashTracker.logGenericMessage("End of"+(this.getClass().getSimpleName()));
	}
	// assuming that positive is forward Cmd whereas negative is reverse
	public double getDriverForwardReverseCommand() {
		double driverLeftYValue = -1 * driveController.getY(Hand.kLeft);
		if (driverLeftYValue > -0.15 & driverLeftYValue < 0.15) {
			driverLeftYValue = 0;
		}
		return (Math.pow(driverLeftYValue,3));
	}
	
	public double getDriverLeftRightCommand() {
		double driverRightXValue = -1 * driveController.getX(Hand.kRight);
		if (driverRightXValue > -0.15 & driverRightXValue <0.15) {
		    driverRightXValue = 0;
		}
		return (Math.pow(driverRightXValue, 3));
		
	}

	public boolean getDriverElbowRaiseCmd() {
		return driveController.getYButton();
	}
	
	public boolean getDriverElbowLowerCmd() {
		return driveController.getAButton();
	}

	
}
