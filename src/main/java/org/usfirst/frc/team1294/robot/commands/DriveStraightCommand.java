package org.usfirst.frc.team1294.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriveStraightCommand extends CommandGroup {
  private final DriveStraightTurnCommand driveStraightTurnCommand;
  private final DriveStraightStrafeCommand driveStraightStrafeCommand;
  private final DriveStraightApproachCommand driveStraightApproachCommand;
  private final DriveStraightDriveCommand driveStraightDriveCommand;
  private double xRate;
  private double yRate;
  private double zRate;

  public DriveStraightCommand() {
    driveStraightTurnCommand = new DriveStraightTurnCommand();
    driveStraightStrafeCommand = new DriveStraightStrafeCommand();
    driveStraightApproachCommand = new DriveStraightApproachCommand();
    driveStraightDriveCommand = new DriveStraightDriveCommand();

    addParallel(driveStraightApproachCommand);
    addParallel(driveStraightTurnCommand);
    addParallel(driveStraightStrafeCommand);
    addParallel(driveStraightDriveCommand);

    setTimeout(15);
  }

  public double getxRate() {
    return xRate;
  }

  public void setxRate(double xRate) {
    this.xRate = xRate;
  }

  public double getyRate() {
    return yRate;
  }

  public void setyRate(double yRate) {
    this.yRate = yRate;
  }

  public double getzRate() {
    return zRate;
  }

  public void setzRate(double zRate) {
    this.zRate = zRate;
  }

  @Override
  protected boolean isFinished() {
    return isTimedOut() ||
            (driveStraightApproachCommand.onTarget()
                    && driveStraightStrafeCommand.onTarget()
                    && driveStraightTurnCommand.onTarget());

  }
}
