package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoSequencer;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;

public class Autonomous {

	String autoModeName = "Not Initalized";
	int mode;
	public Autonomous() {
		String autoMode = CasseroleDriverView.getAutoSelectorVal("Start Position");
		mode = 100;
	}
		
	public void updateAutoSelection() {
		if((int) Math.round(autoMode.get()) != mode) {
			mode = (int) Math.round(autoMode.get());
			switch(mode) {

			case 1: // Test Case
				autoModeName = "Test Name";
				break;
				
			default: // Do nothing
				autoModeName = "Do Nothing";
				break;
			}
				System.out.println("[Auto] New mode selected: " + autoModeName);
		}
	}
	public void executeAutonomus() {
		System.out.println("[Auto] Initalizing " + autoModeName + " auton routine.");

		AutoSequencer.clearAllEvents();
		
		switch(mode) {
		case 1: // drive forward across base line
			AutoEventTestName driveForward = new AutoEventTestName();
			AutoSequencer.addEvent();//Event in parenthesis
			break;
			
			default: // Do nothing
				break;
		}

		AutoSequencer.start();
	}
	public void update() {
		AutoSequencer.update();
	}
	public void stop() {
		AutoSequencer.stop();
	}
}
