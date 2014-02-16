/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team20;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author Driver
 */
public class Drivetrain {
    
    Talon talon1 = new Talon(1);
    Talon talon2 = new Talon(2);
    Talon talon3 = new Talon(3);
    Talon talon4 = new Talon(4);
    Talon talon5 = new Talon(5);
    Talon talon6 = new Talon(6);
    
    DoubleSolenoid shifter = new DoubleSolenoid(1, 5, 6);

    public void arcadeDrive(double moveValue, double rotateValue){
        double leftMotorSpeed = 0, rightMotorSpeed = 0;
        
        rotateValue = -rotateValue;
        moveValue = limit(moveValue);
        rotateValue = limit(rotateValue);
        
        if (moveValue > 0.0) {
            if (rotateValue > 0.0) {
                leftMotorSpeed = moveValue - rotateValue;
                rightMotorSpeed = Math.max(moveValue, rotateValue);
            } else {
                leftMotorSpeed = Math.max(moveValue, -rotateValue);
                rightMotorSpeed = moveValue + rotateValue;
            }
        } else {
            if (rotateValue > 0.0) {
                leftMotorSpeed = -Math.max(-moveValue, rotateValue);
                rightMotorSpeed = moveValue + rotateValue;
            } else {
                leftMotorSpeed = moveValue - rotateValue;
                rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
            }
        }
        
        //Setting talon values
        talon1.set(-rightMotorSpeed);
        talon2.set(rightMotorSpeed);
        talon3.set(rightMotorSpeed);
        talon4.set(leftMotorSpeed);
        talon5.set(-leftMotorSpeed);
        talon6.set(-leftMotorSpeed);
    }
    
    protected static double limit(double num) {
        if (num > 1.0) {
            return 1.0;
        }
        if (num < -1.0) {
            return -1.0;
        }
        return num;
    }
    
    public void highGear(){
        shifter.set(DoubleSolenoid.Value.kReverse);
    }

    public void lowGear(){
        shifter.set(DoubleSolenoid.Value.kForward);
    }
}
