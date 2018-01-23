package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.XboxController;
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
		driveController = new XboxController(0);
	}

	public double getDriverForwardReverseCommand() {
		double driverLeftYValue = driveController.getY(Hand.kLeft);
		return driverLeftYValue;
	}
	public double getDriverLeftRightCommand() {
		double driverRightXValue = driveController.getX(Hand.kRight);
		return driverRightXValue;
	}
	
	public boolean getDriverElbowRaiseCmd() {
		return driveController.getYButton();
	}
	
	public boolean getDriverElbowLowerCmd() {
		return driveController.getAButton();
	}
	
}
