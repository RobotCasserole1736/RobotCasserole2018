package org.usfirst.frc.team1736.lib.LEDs;

import edu.wpi.first.wpilibj.Timer;

public class Particle {
	private double position;
	private double speed;
	private int width;
	private int level;
	private int callNum;

	public Particle() {
		double time = callNum;
		position = (0 + (time * speed));
		for(position = (0 + (time * speed)); position < 25; callNum++ , position++ ) {}
		
	}
}

