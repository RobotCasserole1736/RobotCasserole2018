package org.usfirst.frc.team1736.lib.LEDs;

import java.util.Set;

public class Color {
//Hue can range from 0-60
	private int H_Value;
//Saturation will stay at 50%
	private int S_Value;
//Luminousness will remain at 100%, brightness will be controlled via the power to the LEDs
	private int L_Value;
	
	
	public Color() {
		H_Value = 0;
		S_Value = 100;
		L_Value = 100;
	}
	
	public void setLevel(int Level) {
		if (Level == 0)/*mooo*/{
			
		} else if (Level == 1) {
		 	
		} else if (Level == 2) {
			
		} else if (Level == 3) {
			
		} else if (Level == 4) {
			
		}
	}
	
	public int getH_Value() {
		return H_Value;
	}
	
	public void setH_Value(int val) {
		H_Value = val;
	}
	
		
/*	private Color() {
		
		
			int levelvar = level * 10;				
			 if (levelvar == 0) {
				 H_Value = 10;
			 } else {
				 H_Value = levelvar + 20;
			 }
			return HValue;
		}
*/		
				
		
		
		
		
	}
	

