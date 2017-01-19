package org.usfirst.frc.team1294.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * A command that moves the robot from the left starting position to .5 meters away from the left lift
 * with the gear side of the robot squared up with the lift wall. Will time out after 5 seconds.
 */
public class MoveFromStartToLiftLeft extends CommandGroup {
    // TODO: calculate and set these constants
    public static final int HEADING_TO_TRAVEL = 0;
    public static final double DISTANCE_TO_TRAVEL = 0.5;
    public static final int HEADING_TO_FACE = 90;

    public MoveFromStartToLiftLeft() {
        addParallel(new DriveHeadingAndDistance(HEADING_TO_TRAVEL, DISTANCE_TO_TRAVEL));
        addParallel(new TurnToHeading(HEADING_TO_FACE));
    }
}
