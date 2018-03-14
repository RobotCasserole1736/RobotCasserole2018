package org.usfirst.frc.team1736.lib.LEDs;

public class Particle {
//Number of the LED the center of the particle is occupying
	private double position;
//# of LEDs moved per cycle
	private double speed;
//# of LEDs taken up by the particle
	private int width;
//Sort of a multiplier,1 but also not
	private int level;
//# of times called
	private int callNum;
//Color 
	private Color On_Color; 
	private Color Off_Color;
//Age is # of cycles the particle has existed for.
	private double age;
	
	
	public Particle(int levelin) {
		double time = (callNum * 50);
		double ledNum = position;
		On_Color = new Color();
		On_Color.setLevel(levelin);
		Off_Color = new Color();
		Off_Color.setLevel(0);
		speed = 1;
		width = 4;
	}	
	
	public void move() {
		double Lval = 0;
		double Nval = 0;
		Lval = 0;
		position = position + speed;
		System.out.println(position);
		if (level == 1 && position > 8) {
			reset();
		}
		
		if (position > 18 | position < 24) {
				Lval = On_Color.getL_Value();
				Nval = Lval - Lval / 15;
				On_Color.setL_Value(Nval);
		}
		
		if (position > 24 | position < 0) {
			reset();
		}
	}

	
	

	
	public Color ColorAt(int idx) {
		double lower = position;
		double upper = position + width;
		
		if (idx < upper & idx > lower) {
			return On_Color;
		} else {
			return Off_Color;
		}
	}
	
	public void reset() {
		position = 0;
		speed += Math.random() - .5;
		On_Color.setL_Value(.5);
		if (speed > 1.75) {
			speed = 1.75;
		} else if (speed < .25) {
			speed = .25;
		}
	}
	
}

