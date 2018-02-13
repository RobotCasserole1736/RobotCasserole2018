
![logo](http://robotcasserole.org/wp-content/uploads/2017/01/banner_2017_text.png)

# RobotCasserole2018
Robot Casserole robot source code for the 2018 FRC game, First PowerUp.

## Contents
1. Driver view
2. Main Source Code
3. Log File Snagger & Log Viewer
4. Autonomous Routines

## Driver View Website
The Driver View web site is used to help test and tweak our code, and used to help drive team during match. The driver view is a javascript/HTML based viewer of data logs captured from the robot during operation. These data logs are then used to tweak code before, during, and after competition. 

Me selecting auto mode in driver view

![Driver View GIF](https://github.com/RobotCasserole1736/RobotCasserole2018/blob/master/GIFs/Driver%20View%20GIf.gif)

Me checking that my request was noticed in state data

![State Data GIF](https://github.com/RobotCasserole1736/RobotCasserole2018/blob/Jack_Making_GIFs_for_ReadMe/GIFs/state%20data%20GIF.gif)

Me changing a value in calibrations

![Calibraton GIF](https://github.com/RobotCasserole1736/RobotCasserole2018/blob/Jack_Making_GIFs_for_ReadMe/GIFs/calibration%20GIF.gif)

Me testing code in plot views

![Plot Views GIF](https://github.com/RobotCasserole1736/RobotCasserole2018/blob/Jack_Making_GIFs_for_ReadMe/GIFs/plot%20view%20GIF.gif)

## Main Source Code
The source code this year, included code for: raising and lowerign an elevator, intaking, ejecting, and throwing a power cube, and programming a west coast drive-train to move. If you care to look at it look [here](https://github.com/RobotCasserole1736/RobotCasserole2018/tree/master/RobotCasserole2018/src/org/usfirst/frc/team1736/robot) 

## Log File Snagger & Log Viewer
The log file snagger is a python script used to  communticate with the roborio and grab all csv logs in a certain directory and put them in a log viewer where we can view them when the robot does somthing wacky.

## Autonomous Routines
The autonomous routines we had this year were: crossing the basseline, going and placing a cube on the switch, and driving and placing a cube on the scale. To help us have smooth auto routines, we use a falcon path planner, this uses a set of waypoints and creates a smooth path for the robot to follow. 
