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
		
		public static synchronized FieldSetupString getInstance() {
			if ( singularInstance == null)
				singularInstance = new FieldSetupString();
			return singularInstance;
		}
		
		private FieldSetupString() {
			CrashTracker.logGenericMessage("Start of FieldSetupString init");
			CrashTracker.logGenericMessage("end of FieldSetupString init");
		}
		
		
		public void update() {
			String gameData;
			try {
				gameData = DriverStation.getInstance().getGameSpecificMessage();
				if(gameData.charAt(0) == 'L')
				{
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
				} else if(gameData.charAt(1) == 'R'){
					right_Scale_Owned = true;
					left_Scale_Owned = false;
				}else {
					right_Scale_Owned = false;
					left_Scale_Owned = false;
				}
				
				if(gameData.compareTo(prevGameData) != 0 ) {
					CrashTracker.logGenericMessage("got new game data:" + gameData);
				}
				
				prevGameData = gameData;
				
			} catch (Throwable t){
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
