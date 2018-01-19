package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.DriverStation;

public class Field_setup_string {
		
		boolean left_Switch_Owned;
		boolean right_Switch_Owned;
		boolean left_Scale_Owned;
		boolean right_Scale_Owned;
		
		public void update() {
			String gameData;
			gameData = DriverStation.getInstance().getGameSpecificMessage();
			if(gameData.charAt(0) == 'L')
			{
				left_Switch_Owned = true;
			} else {
				right_Switch_Owned = true;
			}
			if(gameData.charAt(1) == 'L')
			{
				left_Scale_Owned = true;
			} else {
				right_Scale_Owned = true;
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
