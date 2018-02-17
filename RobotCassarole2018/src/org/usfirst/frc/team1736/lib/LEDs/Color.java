package org.usfirst.frc.team1736.lib.LEDs;

import java.util.Set;

public class Color {
	private double H_Value;
	private double S_Value;
	private double L_Value;
	
	
	public Color() {
		H_Value = 0;
		S_Value = 1;
		L_Value = .5;
	}
	
	
	
	public void setLevel(int Level) {
		if (Level == 0)/*mooo*/{
			H_Value = 0;
			S_Value = 0;
			L_Value = 0;
		} else if (Level == 1) {
		 	H_Value = .15;
		} else if (Level == 2) {
			H_Value = .25;
		} else if (Level == 3) {
			H_Value = .35;
		} else if (Level == 4) {
			H_Value = .50;
		}
	}
	
	public double getH_Value() {
		return H_Value;
	}
	
	public double getS_Value() {
		return S_Value;
	}
	
	public double getL_Value() {
		return L_Value;
	}
	
	public void setH_Value(int val) {
		H_Value = val;
	}

		
	public void addToMe(Color in) {
		double prevH = 0;
		double newH = 0;
		double temp = prevH + newH;
		H_Value = (temp/2);
		
		
		
		
	}
	
		

	
	
		
		
	}
	

