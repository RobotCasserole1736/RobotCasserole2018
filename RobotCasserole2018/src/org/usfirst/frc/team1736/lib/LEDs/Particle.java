package org.usfirst.frc.team1736.lib.LEDs;

public class Particle {
//Number of the LED the center of the particle is occupying
	private int position;
//# of LEDs moved per cycle
	private int speed;
//# of LEDs taken up by the particle
	private int width;

 
//Age is # of cycles the particle has existed for.

	
	
	public Particle() {
		speed = 1;
		width = 4;
			
		
		
		
	}
	public void move() {
		position = position + speed;
		if (position > 24) {
			reset();
		}
	}
	
	
	public double colorAt(int idx) {
		int lower = position;
		int upper = position + width;
		
		if (idx < upper & idx > lower) {
			return .25;
		} else {
			return 0;
		}
	}
	
	public void reset() {
		position = 0;
	}
	
}

