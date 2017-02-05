package org.usfirst.frc.team1294.robot.subsystems;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team1294.robot.RobotMap;
import org.usfirst.frc.team1294.robot.commands.DriveGyroCorrect;
import org.usfirst.frc.team1294.robot.commands.MecanumDriveCommand;

/**
 * @author Austin Jenchi (timtim17)
 */
public class DriveSubsystem extends Subsystem {

  public final CANTalon leftFrontTalon;
  public final CANTalon leftRearTalon;
  public final CANTalon rightFrontTalon;
  public final CANTalon rightRearTalon;
  private final RobotDrive robotDrive;
  private static AHRS navX;
//  private final CANTalon extraTalon;

  public DriveSubsystem() {
    super("DriveSubsystem");

    leftFrontTalon = new CANTalon(RobotMap.DRIVEBASE_LEFT_FRONT_TALON);
    leftRearTalon = new CANTalon(RobotMap.DRIVEBASE_LEFT_REAR_TALON);
    rightFrontTalon = new CANTalon(RobotMap.DRIVEBASE_RIGHT_FRONT_TALON);
    rightRearTalon = new CANTalon(RobotMap.DRIVEBASE_RIGHT_REAR_TALON);
    robotDrive = new RobotDrive(leftFrontTalon, leftRearTalon, rightFrontTalon, rightRearTalon);
    robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
    navX = new AHRS(SPI.Port.kMXP);
    leftFrontTalon.setVoltageRampRate (RobotMap.RAMP_RATE);
    rightFrontTalon.setVoltageRampRate (RobotMap.RAMP_RATE);
    leftRearTalon.setVoltageRampRate (RobotMap.RAMP_RATE);
    rightRearTalon.setVoltageRampRate (RobotMap.RAMP_RATE);

//    extraTalon = new CANTalon(0);
    leftRearTalon.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
  }

  @Override
  protected void initDefaultCommand() {
    //setDefaultCommand(new MecanumDriveCommand());
    setDefaultCommand(new DriveGyroCorrect());
  }

  public void mecanumDrive(double x, double y, double rotate, double gyro) {
    robotDrive.mecanumDrive_Cartesian(x, y, rotate, gyro);
    //robotDrive.mecanumDrive_Cartesian();
  }

  public double getAngle() {
    double angle = Math.abs(navX.getAngle()) % 360;
//    System.out.println(angle);
    return angle;
  }


  public void resetGyro() {
    navX.reset();
  }

  public double getAnglePidGet() {
    return navX.pidGet();
  }

  public double getEncoder() {
//    return 0.;
    return leftRearTalon.getPosition();
  }

  public double getRate() {
    return navX.getRate();
  }
}
