package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.XboxController;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class DriverController {
	private static DriverController driveCtrl = null;

	private XboxController driveController;

	public static synchronized DriverController getInstance() {
		if (driveCtrl == null)
			driveCtrl = new DriverController();
		return driveCtrl;
	}

	private DriverController() {
		CrashTracker.logClassInitStart(this.getClass());
		driveController = new XboxController(0);
		CrashTracker.logClassInitEnd(this.getClass());
	}

	// assuming that positive is forward Cmd whereas negative is reverse
	public double getDriverForwardReverseCommand() {
		double driverLeftYValue = -1 * driveController.getY(Hand.kLeft);
		if (driveController.getTriggerAxis(Hand.kRight) > 0.5 || ElevatorCtrl.getInstance().getHeightAboveDTLimit()) {
			driverLeftYValue *= 0.7;
		}
		if (driverLeftYValue > -0.15 & driverLeftYValue < 0.15) {
			driverLeftYValue = 0;
		}
		return (Math.pow(driverLeftYValue, 3));
	}

	public double getDriverLeftRightCommand() {
		double driverRightXValue = -1 * driveController.getX(Hand.kRight);
		if (driveController.getTriggerAxis(Hand.kRight) > 0.5 || ElevatorCtrl.getInstance().getHeightAboveDTLimit()) {
			driverRightXValue *= 0.85;
		}
		if (driverRightXValue > -0.15 & driverRightXValue < 0.15) {
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

	public boolean getDriverThrowCmd() {
		return driveController.getBumper(Hand.kRight) && getDriverElbowRaiseCmd();
	}

	public boolean getDriverEjectCmd() {
		return driveController.getBumper(Hand.kLeft) && getDriverElbowRaiseCmd();
	}

}
