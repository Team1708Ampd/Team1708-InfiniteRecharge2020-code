/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Add your docs here.
 */
public class IntakeSub extends Subsystem {
  Spark intakeMotor = new Spark(0);
  VictorSPX elevatorMotor = new VictorSPX(1);

  public void intakeOn() {
    intakeMotor.set(1);
    System.out.println("Intake on");
  }

  public void outtake() {
    intakeMotor.set(-1);
  }
  public void intakeOff() {
    intakeMotor.set(0);
  }

  public void elevatorUp() {
    elevatorMotor.set(ControlMode.PercentOutput, 1);
  }

  public void elevatorDown() {
    elevatorMotor.set(ControlMode.PercentOutput, -1);
  }
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
