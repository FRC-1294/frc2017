package org.usfirst.frc.team1294.robot.commands;

import org.usfirst.frc.team1294.robot.Robot;

import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Austin Jenchi (timtim17)
 */
public class DriveStraightTurnCommand extends PIDCommand {
  private static final double p = 1.;
  private static final double i = 0.;
  private static final double d = 0.;
  private static final double TOLERANCE = 2.;
  private boolean hasRunReturnPidInputAtLeastOnce;

  public DriveStraightTurnCommand() {
    super("DriveStraightTurnCommand", p, i, d);
    getPIDController().setAbsoluteTolerance(TOLERANCE);
    getPIDController().setInputRange(0, 360);
    getPIDController().setOutputRange(-1, 1);
    SmartDashboard.putData("DriveStraightTurnCommandPID", getPIDController());
  }

  @Override
  protected void initialize() {
    getPIDController().setSetpoint(Robot.driveSubsystem.getAngle());
  }

  @Override
  protected double returnPIDInput() {
    if (!hasRunReturnPidInputAtLeastOnce) hasRunReturnPidInputAtLeastOnce = true;
    return Robot.driveSubsystem.getAngle();
  }

  // assuming angle is the error and target angle is 0

  protected void usePIDOutput(double output) {
	  if (getGroup() instanceof DriveStraightCommand) {
	      ((DriveStraightCommand) getGroup()).setzRate(output);
	    }
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  public boolean onTarget() {
    return hasRunReturnPidInputAtLeastOnce && getPIDController().onTarget();
  }
}
