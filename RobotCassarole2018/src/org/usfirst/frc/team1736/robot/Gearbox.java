package org.usfirst.frc.team1736.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Gearbox {
	private TalonSRX motor1;
	private TalonSRX motor2;
	private TalonSRX motor3;
	public Gearbox(int canid1, int canid2, int canid3) {
		
		motor1 = new TalonSRX(canid1);
		motor2 = new TalonSRX(canid2);
		motor3 = new TalonSRX(canid3);
	}
	
	
	
	public void setMotorCommand(double command) {
		
		motor1.set(ControlMode.PercentOutput,command);
		motor2.follow(motor1);
		motor3.follow(motor1);
	}
	
	public double getSpeedRPM() {
		return 0;
	}
}



