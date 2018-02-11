package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Util.CrashTracker;

import edu.wpi.first.wpilibj.DriverStation;

public class FieldSetupString {
	private static FieldSetupString singularInstance = null;
		boolean left_Switch_Owned;
		boolean right_Switch_Owned;
		boolean left_Scale_Owned;
		boolean right_Scale_Owned;
		String prevGameData = "";
		boolean validString;
		
		public static synchronized FieldSetupString getInstance() {
			if ( singularInstance == null)
				singularInstance = new FieldSetupString();
			return singularInstance;
		}
		
		private FieldSetupString() {
			CrashTracker.logClassInitStart(this.getClass());
			//Nothing to do?
			
			CrashTracker.logClassInitEnd(this.getClass());
		}
		
		
		public void update() {
			String gameData = "";
			try {
				prevGameData = gameData;
				gameData = DriverStation.getInstance().getGameSpecificMessage();
				
				if(gameData.length() >= 2) {	
					
					if(gameData.charAt(0) == 'L') {
						left_Switch_Owned = true;
						right_Switch_Owned = false;
					} else if(gameData.charAt(0) == 'R') {
						left_Switch_Owned = false;
						right_Switch_Owned = true;
					} else {
						right_Switch_Owned = false;
						left_Switch_Owned = false;
					}
					
					if(gameData.charAt(1) == 'L')
					{
						left_Scale_Owned = true;
						right_Scale_Owned = false;
					} else if(gameData.charAt(1) == 'R') {
						right_Scale_Owned = true;
						left_Scale_Owned = false;
					}else {
						right_Scale_Owned = false;
						left_Scale_Owned = false;
					}	
					
				} else {
					left_Switch_Owned = false;
					right_Switch_Owned = false;
					left_Scale_Owned = false;
					right_Scale_Owned = false;
				}
			
			
				if(gameData.compareTo(prevGameData) != 0 && (left_Switch_Owned == true || right_Switch_Owned == true) && (left_Scale_Owned == true || right_Scale_Owned == true)) {
					//New string, and some ownership was found. Good game data
					CrashTracker.logGenericMessage("got new game data:" + gameData);
				}
				else if(gameData.compareTo(prevGameData) != 0 && (left_Switch_Owned == false && right_Switch_Owned == false) && (left_Scale_Owned == false && right_Scale_Owned == false)) {
					//New string, but no ownership. Must have been a bogus string
					CrashTracker.logGenericMessage("got unexpected game data:" + gameData);
				}
				else {
					//When no new strings are received nothing is returned
				}
				

			} catch (Throwable t) {
				String msg = "Error parsing string data\n" + t.getMessage() + "\n" + t.getStackTrace();
				CrashTracker.logGenericMessage(msg);
				DriverStation.reportError(msg, false);
				right_Switch_Owned = false;
				left_Switch_Owned = false;
				left_Scale_Owned = false;
				right_Scale_Owned = false;
				
			}
		}
		
		
		public boolean leftSwitchOwned() {
			boolean leftSwitchPath = left_Switch_Owned;
			return leftSwitchPath;
			
		}
		public boolean rightSwitchOwned() {
			boolean rightSwitchPath = right_Switch_Owned;
			return rightSwitchPath;
			
		}
		public boolean leftScaleOwned() {
			boolean leftScalePath = left_Scale_Owned;
			return leftScalePath;
			
		}
		public boolean rightScaleOwned() {
			boolean rightScalePath = right_Scale_Owned;
			return rightScalePath;
			
		}
		
	}
