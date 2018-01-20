package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Spark;

public class ElevatorOpenLoop {
	private static ElevatorOpenLoop  singularInstance = null;
	
	private boolean contMode;
	private double contModeCmd;
	private Spark motor1;
	private Spark motor2;
	
	public static synchronized ElevatorOpenLoop getInstance() {
		if ( singularInstance == null)
			singularInstance = new ElevatorOpenLoop();
		return singularInstance;
	}
	
	private ElevatorOpenLoop() {
		motor1 = new Spark(0);
		motor2 = new Spark(1);
	}
	
	
	public void update() {
		if (contMode == true) {
		motor1.set(contModeCmd);
		motor2.set(contModeCmd);
		}
	}
	
	
	public void setContMode (boolean modecommand) {
		contMode = modecommand;
	}
	
	public void setContModeCmd (double cmd) {
		contModeCmd = cmd;
	}
	public double getmotor1;
}
