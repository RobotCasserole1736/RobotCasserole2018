package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoSequencer;
import org.usfirst.frc.team1736.lib.Util.CrashTracker;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;

public class Autonomous {

	public static final String[] ACTION_MODES =  new String[]{"Anything", 
			                                                  "Switch Only", 
			                                                  "Scale Only", 
			                                                  "Drive Fwd Only", 
			                                                  "Do Nothing", 
			                                                  "TEST MODE 1", 
			                                                  "TEST MODE 2"};
	
	public static final String[] START_POS_MODES = new String[]{"Left", 
			                                                    "Center", 
			                                                    "Right"};
	
	
	String autoModeName = "Not Initalized";
	
	int mode = -1;

	public Autonomous() {

	}
		
	public void updateAutoSelection() {
		String startPos = CasseroleDriverView.getAutoSelectorVal("Start Position");
		String action = CasseroleDriverView.getAutoSelectorVal("Action");
		autoModeName = startPos + " " + action;
		CrashTracker.logGenericMessage("[Auto] New mode selected: " + autoModeName);
		
		if(action.compareTo(ACTION_MODES[0])==0) { //Anything
			mode = 0;
		} else if(action.compareTo(ACTION_MODES[5])==0) { //Testmode 1
			mode = 5;
		} else {
			CrashTracker.logGenericMessage("[Auto] ERR: Unimplemented mode selected! " + autoModeName);
		}

		
	}

	public void executeAutonomus() {
		CrashTracker.logGenericMessage("[Auto] Initalizing " + autoModeName + " auton routine.");

		AutoSequencer.clearAllEvents();
		
		switch(mode) {
		case 1: //Test Mode 1
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
