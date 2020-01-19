/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.IntakeSub;
import frc.robot.subsystems.ShooterSub;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  public static ExampleSubsystem m_subsystem = new ExampleSubsystem();

  public static ShooterSub shooterSub = new ShooterSub();

  public static IntakeSub intakeSub = new IntakeSub();

  private boolean m_LimelightHasValidTarget = false;


  public static OI m_oi;

  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  WPI_TalonFX leftMotor = new  WPI_TalonFX(2);
  WPI_TalonFX rightMotor =  new  WPI_TalonFX(3);
  WPI_TalonFX leftMotor2 = new  WPI_TalonFX(1);
  WPI_TalonFX rightMotor2 = new  WPI_TalonFX(4);

  
  DifferentialDrive roboDrive = new DifferentialDrive(leftMotor, rightMotor);
  static Joystick joystick = new Joystick(0);

  final int kTimeoutMs = 30;
  final boolean kDiscontinuityPresent = true;
  final int kBookEnd_0 = 910;
  final int kBookEnd_1 = 1137;
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {

    m_oi = new OI();
    m_chooser.setDefaultOption("Default Auto", new ExampleCommand());
    // chooser.addOption("My Auto", new MyAutoCommand());
    SmartDashboard.putData("Auto mode", m_chooser);
    
  }

  private void initQuadrature() {
    if (kDiscontinuityPresent)
    {
      int newCenter;
      newCenter = (kBookEnd_0 + kBookEnd_1) /2 ;
      newCenter &= 0xFFF;
    }
  }

  String ToDeg(int units)
  {
    double deg = units * 360 / 4096.0;
    deg *= 10;
    deg = (int) deg;
    deg /= 10;

    return "" + deg;
    }
  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This function is called once each time the robot enters Disabled mode. You
   * can use it to reset any subsystem information you want to clear when the
   * robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString code to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons to
   * the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();

    /*
     * String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
     * switch(autoSelected) { case "My Auto": autonomousCommand = new
     * MyAutoCommand(); break; case "Default Auto": default: autonomousCommand = new
     * ExampleCommand(); break; }
     */

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    rightMotor.set(ControlMode.Velocity, 1 );
    leftMotor.set(ControlMode.Velocity, 1);
    rightMotor2.set(ControlMode.Follower, rightMotor2.getDeviceID());
    leftMotor2.set(ControlMode.Follower, leftMotor2.getDeviceID());

    leftMotor.setInverted(false);
    rightMotor.setInverted(false);
    leftMotor2.setInverted(true);
    rightMotor2.setInverted(true);
   // roboDrive.arcadeDrive(-joystick.getY(), -joystick.getRawAxis(4));
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    double forward = -1* joystick.getY();
    double turn = +1 * joystick.getX();

    roboDrive.tankDrive(forward, turn);
    
    Scheduler.getInstance().run();

    Update_Limelight_Tracking();

    if (m_LimelightHasValidTarget)
    {
      intakeSub.intakeOn();
    } else {
      intakeSub.intakeOff();
    }
  }

  

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void Update_Limelight_Tracking() {
    final double STEER_K = 0.03;
    final double DRIVE_K = 0.26;
    final double DESIRED_TARGET_AREA = 13.0;
    final double MAX_DRIVE = 0.7;

    double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
    double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
    double ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
    double ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);

    if (tv < 1.0)
    {
      m_LimelightHasValidTarget = false;
      System.out.println("No target");
      return;
    }

    m_LimelightHasValidTarget = true;
    System.out.println("Valid Target");
  }
}
