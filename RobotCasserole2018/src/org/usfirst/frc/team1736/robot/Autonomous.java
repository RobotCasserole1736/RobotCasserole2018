package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.lib.AutoSequencer.AutoSequencer;
import org.usfirst.frc.team1736.lib.Util.CrashTracker;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;
import org.usfirst.frc.team1736.robot.auto.AutoEventBackUp;
import org.usfirst.frc.team1736.robot.auto.AutoEventBackUpFromPyramid;
import org.usfirst.frc.team1736.robot.auto.AutoEventBackupFromSwitch;
import org.usfirst.frc.team1736.robot.auto.AutoEventCrossBaseLine;
import org.usfirst.frc.team1736.robot.auto.AutoEventDrive50Inches;
import org.usfirst.frc.team1736.robot.auto.AutoEventDriveToCubePyramid;
import org.usfirst.frc.team1736.robot.auto.AutoEventEjectCube;
import org.usfirst.frc.team1736.robot.auto.AutoEventIntakeCube;
import org.usfirst.frc.team1736.robot.auto.AutoEventLeftScaleToLeftSwitch;
import org.usfirst.frc.team1736.robot.auto.AutoEventLowerElbow;
import org.usfirst.frc.team1736.robot.auto.AutoEventMoveElevator;
import org.usfirst.frc.team1736.robot.auto.AutoEventRightScaleToRightSwitch;
import org.usfirst.frc.team1736.robot.auto.AutoEventScaleLeft;
import org.usfirst.frc.team1736.robot.auto.AutoEventScaleRight;
import org.usfirst.frc.team1736.robot.auto.AutoEventSixInchForward;
import org.usfirst.frc.team1736.robot.auto.AutoEventSwitchLeft;
import org.usfirst.frc.team1736.robot.auto.AutoEventSwitchLeft_Center;
import org.usfirst.frc.team1736.robot.auto.AutoEventSwitchRight;
import org.usfirst.frc.team1736.robot.auto.AutoEventSwitchRight_Center;
import org.usfirst.frc.team1736.robot.auto.AutoEventTest1;
import org.usfirst.frc.team1736.robot.auto.AutoEventTest1Reversed;
import org.usfirst.frc.team1736.robot.auto.AutoEventTest2;
import org.usfirst.frc.team1736.robot.auto.AutoEventThrowCube;
import org.usfirst.frc.team1736.robot.auto.AutoEventTurn180Degrees;
import org.usfirst.frc.team1736.robot.auto.AutoEventTurn45DegreesLeft;
import org.usfirst.frc.team1736.robot.auto.AutoEventTurn45DegreesRight;
import org.usfirst.frc.team1736.robot.auto.AutoEventTurn90DegreesLeft;
import org.usfirst.frc.team1736.robot.auto.AutoEventTurn90DegreesRight;
import org.usfirst.frc.team1736.robot.auto.AutoEventWait;

import edu.wpi.first.wpilibj.DriverStation;


public class Autonomous {

	public static final String[] ACTION_MODES =  new String[]{"Anything", 
			                                                  "Switch Only", 
			                                                  "Scale Only", 
			                                                  "Drive Fwd Only", 
			                                                  "Do Nothing", 
			                                                  "TEST MODE 1", 
			                                                  "TEST MODE 2",
			                                                  "Two Cube"};
	
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
	double prevDelayTime_s = 0;
	
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
		prevDelayTime_s = delayTime_s;
		
		
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
					mode = AutoModes.CROSS_BASELINE_FROM_CENTER; //In center but no switch ownership detected.
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
			//Two cube modes
		} else if (action.compareTo(ACTION_MODES[7])==0) {
			
			if(startPos.compareTo(START_POS_MODES[0])==0) { //Starting from Left
				
				if (FieldSetupString.getInstance().left_Switch_Owned && FieldSetupString.getInstance().left_Scale_Owned) {
					mode = AutoModes.TWO_CUBE_LEFT;
					
				} else if(FieldSetupString.getInstance().left_Switch_Owned) {
					mode = AutoModes.LEFT_SWITCH_FROM_LEFT;
					
				} else if(FieldSetupString.getInstance().left_Scale_Owned) {
					mode = AutoModes.LEFT_SCALE_FROM_LEFT;
				
				} else {
					mode = AutoModes.CROSS_BASELINE; 
				}
			} else if(startPos.compareTo(START_POS_MODES[1])==0) { //Starting Center 
				
				if(FieldSetupString.getInstance().left_Switch_Owned) {
					mode = AutoModes.TWO_CUBE_LEFT_FROM_CENTER;
					
				}else if(FieldSetupString.getInstance().right_Switch_Owned) {
					mode = AutoModes.TWO_CUBE_RIGHT_FROM_CENTER;
					
				}else{
					mode = AutoModes.CROSS_BASELINE;
				}
				
			} else if(startPos.compareTo(START_POS_MODES[2])==0) { //Starting from Right
				
				if (FieldSetupString.getInstance().right_Switch_Owned && FieldSetupString.getInstance().right_Scale_Owned) {
					mode = AutoModes.TWO_CUBE_RIGHT;
					
				} else if(FieldSetupString.getInstance().right_Switch_Owned) {
					mode = AutoModes.RIGHT_SWITCH_FROM_RIGHT;
					
				} else if(FieldSetupString.getInstance().right_Scale_Owned) {
					mode = AutoModes.RIGHT_SCALE_FROM_RIGHT;
					
				} else {
					mode = AutoModes.CROSS_BASELINE; 
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
			if(startPos.compareTo(START_POS_MODES[1])==0) {
				mode = AutoModes.CROSS_BASELINE_FROM_CENTER; //center needs a special path
			} else {
				mode = AutoModes.CROSS_BASELINE;
			}
			
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

	public void calculatePaths() {
		
		AutoEvent parent;
		
		if((mode != prevMode) || (delayTime_s != prevDelayTime_s)) {
			
			//Indicate we have a new auto mode
			String msg = "[Auto] New mode selected: " + autoModeName;
			CrashTracker.logGenericMessage(msg);
			System.out.println(msg);
			
			AutoSequencer.clearAllEvents();
			
			//Add wait event if needed
			if(delayTime_s != 0.0) {
				AutoSequencer.addEvent(new AutoEventWait(delayTime_s));
			}
			
			//Add other action events
			switch(mode) {
			
			case LEFT_SWITCH_FROM_CENTER: //switch only if in center and own left
				parent = new AutoEventSwitchLeft_Center();
				parent.addChildEvent(new AutoEventLowerElbow());
				parent.addChildEvent(new AutoEventMoveElevator(0.25, ElevatorIndex.SWITCH));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				AutoSequencer.addEvent(new AutoEventBackUp());
				break;
				
			case LEFT_SWITCH_FROM_LEFT: //switch only if on left and own left
				parent =new AutoEventSwitchLeft();
				parent.addChildEvent(new AutoEventLowerElbow());
				parent.addChildEvent(new AutoEventMoveElevator(0.25, ElevatorIndex.SWITCH));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				AutoSequencer.addEvent(new AutoEventBackUp());
				break;
				
			case RIGHT_SWITCH_FROM_CENTER: //switch only if in center and own right
				parent = new AutoEventSwitchRight_Center();
				parent.addChildEvent(new AutoEventLowerElbow());
				parent.addChildEvent(new AutoEventMoveElevator(0.25, ElevatorIndex.SWITCH));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				AutoSequencer.addEvent(new AutoEventBackUp());
				break;
				
			case RIGHT_SWITCH_FROM_RIGHT: //switch only if on right and own right
				parent = new AutoEventSwitchRight();
				parent.addChildEvent(new AutoEventLowerElbow());
				parent.addChildEvent(new AutoEventMoveElevator(0.25, ElevatorIndex.SWITCH));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				AutoSequencer.addEvent(new AutoEventBackUp());
				break;
				
			case LEFT_SCALE_FROM_LEFT: // scale only left
				parent = new AutoEventScaleLeft();
				parent.addChildEvent(new AutoEventLowerElbow());
				parent.addChildEvent(new AutoEventMoveElevator(4.0, ElevatorIndex.SCALE_UP));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				break;
				
			case RIGHT_SCALE_FROM_RIGHT: // scale only right
				parent = new AutoEventScaleRight();
				parent.addChildEvent(new AutoEventLowerElbow());
				parent.addChildEvent(new AutoEventMoveElevator(4.0, ElevatorIndex.SCALE_UP));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				break;
			
			case CROSS_BASELINE: //drive forward
				AutoSequencer.addEvent(new AutoEventCrossBaseLine());
				break;
				
			case CROSS_BASELINE_FROM_CENTER: //drive forward and to the right
				AutoSequencer.addEvent(new AutoEventSwitchRight_Center());
				break;
					
			case TEST_MODE_1: //Test Mode 1
				
				//Reversal test
				//AutoSequencer.addEvent(new AutoEventTest1());
				//AutoSequencer.addEvent(new AutoEventWait(1.5));
				//AutoSequencer.addEvent(new AutoEventTest1Reversed());
				
				//Launch test
				AutoSequencer.addEvent(new AutoEventLowerElbow());
				AutoSequencer.addEvent(new AutoEventWait(0.25));
				/*parent = new AutoEventRaiseElbow();
				parent.addChildEvent(new AutoEventThrowCube(0.85));
				AutoSequencer.addEvent(parent);*/
				break;

			case TEST_MODE_2: //Test Mode 2
				//Sine-wave drivetrain test
				//AutoSequencer.addEvent(new AutoEventTest2());
				AutoSequencer.addEvent(new AutoEventTurn180Degrees());
				
				
				break;
				
			case TWO_CUBE_LEFT:
				parent = new AutoEventScaleLeft();
				parent.addChildEvent(new AutoEventLowerElbow());
				parent.addChildEvent(new AutoEventMoveElevator(4.0, ElevatorIndex.SCALE_BALANCED));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				parent = new AutoEventBackUp();
				parent.addChildEvent(new AutoEventMoveElevator(1.0, ElevatorIndex.BOTTOM));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventTurn180Degrees());
				parent = new AutoEventLeftScaleToLeftSwitch();
				parent.addChildEvent(new AutoEventIntakeCube(AutoEventLeftScaleToLeftSwitch.time));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventMoveElevator(0.0, ElevatorIndex.SWITCH));
				AutoSequencer.addEvent(new AutoEventSixInchForward());
				AutoSequencer.addEvent(new AutoEventEjectCube());
				break;
			
			case TWO_CUBE_RIGHT:
				parent = new AutoEventScaleRight();
				parent.addChildEvent(new AutoEventLowerElbow());
				parent.addChildEvent(new AutoEventMoveElevator(3.0, ElevatorIndex.SCALE_BALANCED));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				parent = new AutoEventBackUp();
				parent.addChildEvent(new AutoEventMoveElevator(1.0, ElevatorIndex.BOTTOM));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventTurn180Degrees());
				parent = new AutoEventRightScaleToRightSwitch();
				parent.addChildEvent(new AutoEventIntakeCube(AutoEventRightScaleToRightSwitch.time));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventMoveElevator(0.0, ElevatorIndex.SWITCH));
				AutoSequencer.addEvent(new AutoEventSixInchForward());
				AutoSequencer.addEvent(new AutoEventEjectCube());
				break;	
			case TWO_CUBE_LEFT_FROM_CENTER:	
				parent = new AutoEventSwitchLeft_Center();
				parent.addChildEvent(new AutoEventLowerElbow());
				parent.addChildEvent(new AutoEventMoveElevator(0.25, ElevatorIndex.SWITCH));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				
				
				//Reapeating events
				parent = new AutoEventBackupFromSwitch();
				parent.addChildEvent(new AutoEventMoveElevator(1.0, ElevatorIndex.BOTTOM));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventTurn45DegreesRight());
				parent = new AutoEventDriveToCubePyramid();
				parent.addChildEvent(new AutoEventIntakeCube(AutoEventDriveToCubePyramid.time));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventBackUpFromPyramid());
				parent = new AutoEventTurn45DegreesLeft();
				parent.addChildEvent(new AutoEventMoveElevator(0.0, ElevatorIndex.EXCHANGE));
				AutoSequencer.addEvent(parent);
				parent = new AutoEventDrive50Inches();
				parent.addChildEvent(new AutoEventMoveElevator(0.0, ElevatorIndex.SWITCH));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				
				parent = new AutoEventBackupFromSwitch();
				parent.addChildEvent(new AutoEventMoveElevator(1.0, ElevatorIndex.BOTTOM));
				AutoSequencer.addEvent(parent);
				break;
				
			case TWO_CUBE_RIGHT_FROM_CENTER:	
				parent = new AutoEventSwitchRight_Center();
				parent.addChildEvent(new AutoEventLowerElbow());
				parent.addChildEvent(new AutoEventMoveElevator(0.25, ElevatorIndex.SWITCH));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				
				
				//Reapeating events
				parent = new AutoEventBackupFromSwitch();
				parent.addChildEvent(new AutoEventMoveElevator(1.0, ElevatorIndex.BOTTOM));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventTurn45DegreesLeft());
				parent = new AutoEventDriveToCubePyramid();
				parent.addChildEvent(new AutoEventIntakeCube(AutoEventDriveToCubePyramid.time));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventBackUpFromPyramid());
				parent = new AutoEventTurn45DegreesRight();
				parent.addChildEvent(new AutoEventMoveElevator(0.0, ElevatorIndex.EXCHANGE));
				AutoSequencer.addEvent(parent);
				parent = new AutoEventDrive50Inches();
				parent.addChildEvent(new AutoEventMoveElevator(0.0, ElevatorIndex.SWITCH));
				AutoSequencer.addEvent(parent);
				AutoSequencer.addEvent(new AutoEventEjectCube());
				
				parent = new AutoEventBackupFromSwitch();
				parent.addChildEvent(new AutoEventMoveElevator(1.0, ElevatorIndex.BOTTOM));
				AutoSequencer.addEvent(parent);
				break;
			case DO_NOTHING: //Do nothing
				break;
				
			default: // Do nothing
				DriverStation.reportError("Unimplemented autonomous mode requested! Tell software team they got auto mode " + mode.toString() , false);
				break;
			}
			
			msg = "[Auto] Path calculation completed.";
			System.out.println(msg);
			CrashTracker.logGenericMessage(msg);
		}
		

	}


	public void start() {
		CrashTracker.logGenericMessage("[Auto] Final Auto Mode Selection: " + autoModeName);
		CrashTracker.logGenericMessage("[Auto] Running routine " + mode.toString());
		AutoSequencer.start();
	}
	
	public void update() {
		AutoSequencer.update();
	}
	
	public void stop() {
		AutoSequencer.stop();
	}
}
