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
	// assuming that positive is forward Cmd whereas negative is reverse
	public double getDriverForwardReverseCommand() {
		double driverLeftYValue = -1 * driveController.getY(Hand.kLeft);
		if (driverLeftYValue > -0.15 & driverLeftYValue < 0.15) {
			driverLeftYValue = 0;
		}
		return (driverLeftYValue*driverLeftYValue*driverLeftYValue);
	}
	
	public double getDriverLeftRightCommand() {
		double driverRightXValue = driveController.getX(Hand.kRight);
		if (driverRightXValue > -0.15 & driverRightXValue <0.15) {
		    driverRightXValue = 0;
		}
		return (driverRightXValue*driverRightXValue*driverRightXValue);
		
	}

	public boolean getDriverElbowRaiseCmd() {
		return driveController.getYButton();
	}
	
	public boolean getDriverElbowLowerCmd() {
		return driveController.getAButton();
	}

	
}
