package org.usfirst.frc.team1736.lib.PathPlanner;

/*
 *******************************************************************************************
 * Copyright (C) 2017 FRC Team 1736 Robot Casserole - www.robotcasserole.org
 *******************************************************************************************
 *
 * This software is released under the MIT Licence - see the license.txt
 *  file in the root of this repo.
 *
 * Non-legally-binding statement from Team 1736:
 *  Thank you for taking the time to read through our software! We hope you
 *   find it educational and informative! 
 *  Please feel free to snag our software for your own use in whatever project
 *   you have going on right now! We'd love to be able to help out! Shoot us 
 *   any questions you may have, all our contact info should be on our website
 *   (listed above).
 *  If you happen to end up using our software to make money, that is wonderful!
 *   Robot Casserole is always looking for more sponsors, so we'd be very appreciative
 *   if you would consider donating to our club to help further STEM education.
 */

import org.usfirst.frc.team1736.lib.AutoSequencer.AutoEvent;
import org.usfirst.frc.team1736.robot.Drivetrain;

/**
 * Interface into the Casserole autonomous sequencer for a path-planned traversal. Simply wraps
 * path-planner functionality into the AutoEvent abstract class.
 */

public class PathPlannerAutoEvent extends AutoEvent {

    /* Path planner wrapped by this auto event */
    public FalconPathPlanner path;
    private double[][] waypoints;
    private double time_duration_s; 
    boolean pathCalculated;

    private int timestep;
    private double taskRate = 0.02;
    private final double DT_TRACK_WIDTH_FT = 25 / 12; //Width in Feet

    /**
     * Constructor. Set up the parameters of the planner here.
     * 
     * @param waypoints_in Set of x/y points which define the path the robot should take.
     * @param timeAllowed_in Number of seconds the path traversal should take. Must be long enough
     *        to allow the path planner to output realistic speeds.         
     */
    public PathPlannerAutoEvent(double[][] waypoints_in, double timeAllowed_in) {        
    	super();
        waypoints = waypoints_in;
        time_duration_s = timeAllowed_in;
        
        path = new FalconPathPlanner(waypoints);
        pathCalculated = false;
    }


    /**
     * On the first loop, calculates velocities needed to take the path specified. Later loops will
     * assign these velocities to the drivetrain at the proper time.
     */
    public void userUpdate() {
        if (pathCalculated == false) {
            path.calculate(time_duration_s, taskRate, DT_TRACK_WIDTH_FT);
            timestep = 0;
            pathCalculated = true;
        }
        
        Drivetrain.getInstance().setLeftWheelSpeed(FT_PER_SEC_TO_RPM(path.smoothLeftVelocity[timestep][1]));
        Drivetrain.getInstance().setRightWheelSpeed(FT_PER_SEC_TO_RPM(path.smoothRightVelocity[timestep][1]));
        Drivetrain.getInstance().setDesiredHeading(path.heading[timestep][1]);

        timestep++;
    }


    /**
     * Force both sides of the drivetrain to zero
     */
    public void userForceStop() {
    	Drivetrain.getInstance().setForwardReverseCommand(0);
    	Drivetrain.getInstance().setRotateCommand(0);
    }


    /**
     * Always returns true, since the routine should run as soon as it comes up in the list.
     */
    public boolean isTriggered() {
        return true; // we're always ready to go
    }


    /**
     * Returns true once we've run the whole path
     */
    public boolean isDone() {
        return timestep >= path.numFinalPoints;
    }


	@Override
	public void userStart() {
		path.calculate(time_duration_s, taskRate,DT_TRACK_WIDTH_FT);
        timestep = 0;
        pathCalculated = true;
        
	}
	
	private double FT_PER_SEC_TO_RPM(double ftps_in) {
		return ftps_in / (2*Math.PI*Drivetrain.WHEEL_ROLLING_RADIUS_FT) * 60;
	}


}