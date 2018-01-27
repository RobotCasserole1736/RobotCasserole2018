package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoSequencer;
import org.usfirst.frc.team1736.lib.PathPlanner.PathPlannerAutoEvent;
import org.usfirst.frc.team1736.lib.Util.CrashTracker;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;
import org.usfirst.frc.team1736.robot.auto.AutoEventCrossBaseLine;
import org.usfirst.frc.team1736.robot.auto.AutoEventScaleLeft;
import org.usfirst.frc.team1736.robot.auto.AutoEventScaleRight;
import org.usfirst.frc.team1736.robot.auto.AutoEventSwitchLeft;
import org.usfirst.frc.team1736.robot.auto.AutoEventSwitchLeft_Center;
import org.usfirst.frc.team1736.robot.auto.AutoEventSwitchRight;
import org.usfirst.frc.team1736.robot.auto.AutoEventSwitchRight_Center;


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
		
		//Anything modes
		if((action.compareTo(ACTION_MODES[0])==0) && (startPos.compareTo(START_POS_MODES[0])==0) && (FieldSetupString.getInstance().left_Scale_Owned)) {
			mode = 4; //On left and own left scale
		}else if((action.compareTo(ACTION_MODES[0])==0) && (startPos.compareTo(START_POS_MODES[0])==0) && (FieldSetupString.getInstance().left_Switch_Owned)) {
			mode = 1; //On left and own left switch
		}else if((action.compareTo(ACTION_MODES[0])==0) && (startPos.compareTo(START_POS_MODES[0])==0) && (!FieldSetupString.getInstance().left_Scale_Owned && !FieldSetupString.getInstance().left_Switch_Owned ) ) {
			mode = 6; //On left but own neither left scale nor left switch
		}else if((action.compareTo(ACTION_MODES[0])==0) && (startPos.compareTo(START_POS_MODES[1])==0) && (FieldSetupString.getInstance().left_Switch_Owned)) {
			mode = 0; //In center and own left switch
		}else if((action.compareTo(ACTION_MODES[0])==0) && (startPos.compareTo(START_POS_MODES[1])==0) && (FieldSetupString.getInstance().right_Switch_Owned)) {
			mode = 2; //In center and own right switch
		}else if((action.compareTo(ACTION_MODES[0])==0) && (startPos.compareTo(START_POS_MODES[2])==0) && (FieldSetupString.getInstance().right_Scale_Owned)) {
			mode = 5; //On right and own right scale
		}else if((action.compareTo(ACTION_MODES[0])==0) && (startPos.compareTo(START_POS_MODES[2])==0) && (FieldSetupString.getInstance().right_Switch_Owned)) {
			mode = 3; //On right and own right switch
		}else if((action.compareTo(ACTION_MODES[0])==0) && (startPos.compareTo(START_POS_MODES[2])==0) && (!FieldSetupString.getInstance().right_Scale_Owned && !FieldSetupString.getInstance().right_Switch_Owned ) ) {
			mode = 6; //On right but own neither right scale nor right switch
		
		//Switch Only Modes
		}else if((action.compareTo(ACTION_MODES[1])==0) && (startPos.compareTo(START_POS_MODES[0])==0) && (FieldSetupString.getInstance().left_Switch_Owned)) {
			mode = 1; //On left and own left
		}else if((action.compareTo(ACTION_MODES[1])==0) && (startPos.compareTo(START_POS_MODES[1])==0) && (FieldSetupString.getInstance().left_Switch_Owned)) {
			mode = 0; //In center and own left
		}else if((action.compareTo(ACTION_MODES[1])==0) && (startPos.compareTo(START_POS_MODES[2])==0) && (FieldSetupString.getInstance().right_Switch_Owned)) {
			mode = 3; //On right and own right
		}else if((action.compareTo(ACTION_MODES[1])==0) && (startPos.compareTo(START_POS_MODES[1])==0) && (FieldSetupString.getInstance().right_Switch_Owned)) {
			mode = 2; //In center and own right
			
		//Scale Only Modes
		}else if((action.compareTo(ACTION_MODES[2])==0) && (startPos.compareTo(START_POS_MODES[0])==0) && (FieldSetupString.getInstance().left_Scale_Owned)) {
			mode = 4; //On left and own left
		}else if((action.compareTo(ACTION_MODES[2])==0) && (startPos.compareTo(START_POS_MODES[2])==0) && (FieldSetupString.getInstance().right_Scale_Owned)) {
			mode = 5; //On right and own right
			
		//Drive Forward Mode
		}else if(action.compareTo(ACTION_MODES[3])==0) {	
			mode = 6;
			
		//Do Nothing Mode
		}else if(action.compareTo(ACTION_MODES[4])==0) {	
			mode = 9;
			
		//Test Modes
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
			
			case 0: //switch only if in center and own left
				AutoSequencer.addEvent(new AutoEventSwitchLeft());
				break;
				
			case 1: //switch only if on left and own left
				AutoSequencer.addEvent(new AutoEventSwitchLeft());
				break;
				
			case 2: //switch only if in center and own right
				AutoSequencer.addEvent(new AutoEventSwitchRight_Center());
				break;
				
			case 3: //switch only if on right and own right
				AutoSequencer.addEvent(new AutoEventSwitchRight());
				break;
				
			case 4: // scale only left
				AutoSequencer.addEvent(new AutoEventScaleLeft());
				break;
				
			case 5: // scale only right
				AutoSequencer.addEvent(new AutoEventScaleRight());
				break;
			
			case 6: //drive forward
				AutoSequencer.addEvent(new AutoEventCrossBaseLine());
				break;
					
			case 7: //Test Mode 1
				AutoSequencer.addEvent(new AutoEventCrossBaseLine());//Event in parenthesis
				break;

			case 8: //Test Mode 2
				if (FieldSetupString.getInstance().leftSwitchOwned()){
					AutoSequencer.addEvent(new AutoEventScaleLeft());//Event in parenthesis
				}
				else if(FieldSetupString.getInstance().rightSwitchOwned()){
					AutoSequencer.addEvent(new AutoEventScaleRight());//Event in parenthesis
				}
				break;
			
			case 9: //Do nothing
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
