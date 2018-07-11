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
		 	H_Value = 0;
		 	S_Value = 1;
		 	L_Value = .5;
		} else if (Level == 2) {
			H_Value = 0;
			S_Value = 1;
			L_Value = .5;
		} else if (Level == 3) {
			H_Value = 0;
			S_Value = 1;
			L_Value = .5;
		} else if (Level == 4) {
			H_Value = 0;
			S_Value = 1;
			L_Value = .5;
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
	
	public void setL_Value(double in) {
		L_Value = in;
	}

		
	public void addToMe(Color in) {
		double newH = in.getH_Value();
		double heldH = H_Value;
		double avrgpt1 = heldH + newH;
		H_Value = (avrgpt1/2);
		S_Value = 1;
		L_Value += in.L_Value;
		 if (L_Value > .52) {
			 L_Value = .52;
			 H_Value = H_Value + .4; //original: .072 
			 //System.out.println(H_Value);
		 }
		
	}
	
		

	
	
		
		
	}
	

