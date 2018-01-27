package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoSequencer;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;
import org.usfirst.frc.team1736.lib.Util.CrashTracker;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;
import org.usfirst.frc.team1736.robot.auto.AutoEventCrossBaseLine;
import org.usfirst.frc.team1736.robot.auto.AutoEventScaleLeft;
import org.usfirst.frc.team1736.robot.auto.AutoEventScaleRight;


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
		} else if(action.compareTo(ACTION_MODES[6])==0) { //Testmode 2
				mode = 6;
		} else {
			String msg = "[Auto] ERR: Unimplemented mode selected! " + autoModeName;
			CrashTracker.logGenericMessage(msg);
			System.out.print(msg);
		}

		
	}

	public void executeAutonomus() {
		String msg = "[Auto] Initalizing " + autoModeName + " auton routine.";
		CrashTracker.logGenericMessage(msg);
		System.out.print(msg);

		AutoSequencer.clearAllEvents();
		
		switch(mode) {
			case 1:
			break;
			
			case 2:
				break;
			
			case 3:
				break;
				
			case 4:
				break;
				
			case 5: //Test Mode 1
				AutoSequencer.addEvent(new AutoEventCrossBaseLine());//Event in parenthesis
				break;
				
			case 6: //Test Mode 2
				if (FieldSetupString.getInstance().leftSwitchOwned()){
				AutoSequencer.addEvent(new AutoEventScaleLeft());//Event in parenthesis
				}
				else if(FieldSetupString.getInstance().rightSwitchOwned()){
				AutoSequencer.addEvent(new AutoEventScaleRight());//Event in parenthesis
				}
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
