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
import org.usfirst.frc.team1736.robot.auto.AutoEventTest1;
import org.usfirst.frc.team1736.robot.auto.AutoEventTest1Reversed;
import org.usfirst.frc.team1736.robot.auto.AutoEventTest2;
import org.usfirst.frc.team1736.robot.auto.AutoEventWait;

import edu.wpi.first.wpilibj.DriverStation;


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
	
	public static final String[] DELAY_OPTIONS = new String[]{"0s", 
                                                              "3s", 
                                                              "6s",
                                                              "9s",
                                                              "12s"};
	
	String autoModeName = "Not Initalized";
	
	double delayTime_s = 0;
	
	AutoModes mode = AutoModes.UNKNOWN;
	AutoModes prevMode = AutoModes.UNKNOWN;

	public Autonomous() {
		CrashTracker.logClassInitStart(this.getClass());
		
		CrashTracker.logClassInitEnd(this.getClass());
	}
		
	public void updateAutoSelection() {
		String startPos     = CasseroleDriverView.getAutoSelectorVal("Start Position");
		String action       = CasseroleDriverView.getAutoSelectorVal("Action");
		String delayTimeStr = CasseroleDriverView.getAutoSelectorVal("Delay");
		autoModeName = startPos + " " + action + " delay by " + delayTimeStr;
		prevMode = mode;
		//CrashTracker.logGenericMessage("[Auto] New mode selected: " + autoModeName);
		
		//Map delay times
		if(delayTimeStr.compareTo(DELAY_OPTIONS[0]) == 0) { //No Delay
			delayTime_s = 0.0;
		} else if (delayTimeStr.compareTo(DELAY_OPTIONS[1]) == 0) { //First delay time
			delayTime_s = 3.0;
		} else if (delayTimeStr.compareTo(DELAY_OPTIONS[2]) == 0) { //Second delay time
			delayTime_s = 6.0;
		} else if (delayTimeStr.compareTo(DELAY_OPTIONS[3]) == 0) { //Third delay time
			delayTime_s = 9.0;
		} else if (delayTimeStr.compareTo(DELAY_OPTIONS[4]) == 0) { //Fourth delay time
			delayTime_s = 12.0;
		}
		
		//Anything modes
		if(action.compareTo(ACTION_MODES[0])==0) { //Attempt anything mode
			
			if(startPos.compareTo(START_POS_MODES[0])==0) { //Starting from Left
				
				if(FieldSetupString.getInstance().left_Scale_Owned) {
					mode = AutoModes.LEFT_SCALE_FROM_LEFT;
				} else if (FieldSetupString.getInstance().left_Switch_Owned) {
					mode = AutoModes.LEFT_SWITCH_FROM_LEFT;
				} else {
					mode = AutoModes.CROSS_BASELINE; //On left but own neither left scale nor left switch
				}
				
			} else if(startPos.compareTo(START_POS_MODES[1])==0) { //Starting Center 
				
				if(FieldSetupString.getInstance().left_Switch_Owned) {
					mode = AutoModes.LEFT_SWITCH_FROM_CENTER;
				} else if (FieldSetupString.getInstance().right_Switch_Owned) {
					mode = AutoModes.RIGHT_SWITCH_FROM_CENTER;
				} else {
					mode = AutoModes.CROSS_BASELINE; //In center but no switch ownership detected.
				}
				
			} else if(startPos.compareTo(START_POS_MODES[2])==0) { //Starting from Right
				
				if(FieldSetupString.getInstance().right_Scale_Owned) {
					mode = AutoModes.RIGHT_SCALE_FROM_RIGHT;
				} else if (FieldSetupString.getInstance().right_Switch_Owned) {
					mode = AutoModes.RIGHT_SWITCH_FROM_RIGHT;
				} else {
					mode = AutoModes.CROSS_BASELINE; //On right but own neither right scale nor right switch
				}
			
			}
				
			
		//Switch Only Modes
		} else if (action.compareTo(ACTION_MODES[1])==0) {

			if(startPos.compareTo(START_POS_MODES[0])==0) { //Starting from Left
				
				if (FieldSetupString.getInstance().left_Switch_Owned) {
					mode = AutoModes.LEFT_SWITCH_FROM_LEFT;
				} else {
					mode = AutoModes.CROSS_BASELINE; //On left but do not own switch
				}
				
			} else if(startPos.compareTo(START_POS_MODES[1])==0) { //Starting Center 
				
				if(FieldSetupString.getInstance().left_Switch_Owned) {
					mode = AutoModes.LEFT_SWITCH_FROM_CENTER;
				} else if (FieldSetupString.getInstance().right_Switch_Owned) {
					mode = AutoModes.RIGHT_SWITCH_FROM_CENTER;
				} else {
					mode = AutoModes.CROSS_BASELINE; //In center but no switch ownership detected.
				}
				
			} else if(startPos.compareTo(START_POS_MODES[2])==0) { //Starting from Right
				
				if (FieldSetupString.getInstance().right_Switch_Owned) {
					mode = AutoModes.RIGHT_SWITCH_FROM_RIGHT;
				} else {
					mode = AutoModes.CROSS_BASELINE; //On right but do not own switch
				}
			
			}
			
		//Scale Only Modes
		} else if (action.compareTo(ACTION_MODES[2])==0) {

			if(startPos.compareTo(START_POS_MODES[0])==0) { //Starting from Left
				
				if(FieldSetupString.getInstance().left_Scale_Owned) {
					mode = AutoModes.LEFT_SCALE_FROM_LEFT;
				} else {
					mode = AutoModes.CROSS_BASELINE; //On left but do not own scale
				}
				
			} else if(startPos.compareTo(START_POS_MODES[1])==0) { //Starting Center 
				
				mode = AutoModes.CROSS_BASELINE; //Cannot reach scale from center
				
			} else if(startPos.compareTo(START_POS_MODES[2])==0) { //Starting from Right
				
				if(FieldSetupString.getInstance().right_Scale_Owned) {
					mode = AutoModes.RIGHT_SCALE_FROM_RIGHT;
				} else {
					mode = AutoModes.CROSS_BASELINE; //On right but do not own scale
				}
			
			}

		//Drive Forward Only Modes
		}else if(action.compareTo(ACTION_MODES[3])==0) { 
			mode = AutoModes.CROSS_BASELINE;
			
		//Do Nothing Mode
		}else if(action.compareTo(ACTION_MODES[4])==0) { 
			mode = AutoModes.DO_NOTHING;
			
		//Test Modes
		} else if(action.compareTo(ACTION_MODES[5])==0) { //Testmode 1
			mode = AutoModes.TEST_MODE_1;
			
		} else if(action.compareTo(ACTION_MODES[6])==0) { //Testmode 2
			mode = AutoModes.TEST_MODE_2;
			
		} else {
			mode = AutoModes.UNKNOWN;
			autoModeName += " THIS IS UNIMPLEMENTED!";
		}		
		
	}

	public void executeAutonomus() {
		
		if(!(mode == prevMode)) {
			
			String msg = "[Auto] Initalizing " + autoModeName + " auton routine.";
			CrashTracker.logGenericMessage(msg);
			System.out.print(msg);
	
			AutoSequencer.clearAllEvents();
			
			//Set up initial delay
			if(delayTime_s != 0.0) {
				AutoSequencer.addEvent(new AutoEventWait(delayTime_s));
			}
			
			//Set up each mode
			switch(mode) {
				
				case LEFT_SWITCH_FROM_CENTER: //switch only if in center and own left
					AutoSequencer.addEvent(new AutoEventSwitchLeft_Center());
					break;
					
				case LEFT_SWITCH_FROM_LEFT: //switch only if on left and own left
					AutoSequencer.addEvent(new AutoEventSwitchLeft());
					break;
					
				case RIGHT_SWITCH_FROM_CENTER: //switch only if in center and own right
					AutoSequencer.addEvent(new AutoEventSwitchRight_Center());
					break;
					
				case RIGHT_SWITCH_FROM_RIGHT: //switch only if on right and own right
					AutoSequencer.addEvent(new AutoEventSwitchRight());
					break;
					
				case LEFT_SCALE_FROM_LEFT: // scale only left
					AutoSequencer.addEvent(new AutoEventScaleLeft());
					break;
					
				case RIGHT_SCALE_FROM_RIGHT: // scale only right
					AutoSequencer.addEvent(new AutoEventScaleRight());
					break;
				
				case CROSS_BASELINE: //drive forward
					AutoSequencer.addEvent(new AutoEventCrossBaseLine());
					break;
						
				case TEST_MODE_1: //Test Mode 1
					AutoSequencer.addEvent(new AutoEventTest1());
					AutoSequencer.addEvent(new AutoEventWait(1.5));
					AutoSequencer.addEvent(new AutoEventTest1Reversed());
					break;
	
				case TEST_MODE_2: //Test Mode 2
					AutoSequencer.addEvent(new AutoEventTest2());
					break;
				
				case DO_NOTHING: //Do nothing
					break;
					
				default: // Do nothing
					DriverStation.reportError("Unimplemented autonomous mode requested! Tell software team they got auto mode " + mode.toString() , false);
					break;
			}
		}else {}
	}
	public void start() {
		AutoSequencer.start();
	}
	
	public void update() {
		AutoSequencer.update();
	}
	
	public void stop() {
		AutoSequencer.stop();
	}
}
