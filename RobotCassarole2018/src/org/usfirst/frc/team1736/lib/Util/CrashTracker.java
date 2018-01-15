package org.usfirst.frc.team1736.lib.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

public class CrashTracker {
	private static final UUID RUN_INSTANCE_UUID = UUID.randomUUID();
	
	static boolean hasLogged = false;
	
	public static void logRobotConstruction() {
        logMarker("robot construction");
    }
	
	public static void logRobotInit() {
		logMarker("robot init");
	}
	
	public static void logDisabledInit() {
		logMarker("disabled init");
		hasLogged = false;
	}
	
	public static void logDisabledPeriodic() {
		if(hasLogged = false) {
			logMarker("disabled periodic");
			hasLogged = true;
		}
	}
	
	public static void logAutoInit() {
		logMarker("auto init");
		hasLogged = false;
	}
	
	public static void logAutoPeriodic() {
		if(hasLogged = false) {
			logMarker("auto periodic");
			hasLogged = true;
		}
	}
	
	public static void logTeleopInit() {
		logMarker("teleop init");
		hasLogged = false; 
	}
	
	public static void logTeleopPeriodic() {
		if(hasLogged = false) {
			logMarker("teleop periodic");
			hasLogged = true;
		}
	}
		
	 public static void logThrowableCrash(Throwable throwable) {
	        logMarker("Exception", throwable);
	}
	
	private static void logMarker(String mark) {
	        logMarker(mark, null);
	}

	private static void logMarker(String mark, Throwable nullableException) {

	        try (PrintWriter writer = new PrintWriter(new FileWriter("/home/lvuser/crash_tracking.txt", true))) {
	            writer.print(RUN_INSTANCE_UUID.toString());
	            writer.print(", ");
	            writer.print(mark);
	            writer.print(", ");
	            writer.print(new Date().toString());

	            if (nullableException != null) {
	                writer.print(", ");
	                nullableException.printStackTrace(writer);
	            }

	            writer.println();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
}
