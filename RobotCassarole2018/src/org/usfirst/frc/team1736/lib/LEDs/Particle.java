package org.usfirst.frc.team1736.lib.LEDs;

public class Particle {
//Number of the LED the center of the particle is occupying
	private int position;
//# of LEDs moved per cycle
	private int speed;
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
	
	
	public Particle() {
		double time = (callNum * 50);
		int ledNum = position;
		On_Color = new Color();
		On_Color.setLevel(1);
		Off_Color = new Color();
		Off_Color.setLevel(0);
		speed = 1;
		width = 4;
	}	
	
	public void move() {
		position = position + speed;
		if (position > 24) {
			reset();
		}
	}

	
	public Color ColorAt(int idx) {
		int lower = position;
		int upper = position + width;
		
		if (idx < upper & idx > lower) {
			return On_Color;
		} else {
			return Off_Color;
		}
	}
	
	public void reset() {
		position = 0;
	}
	
}

