/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team20;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author Driver
 */
public class Catapult {

    Talon talon1 = new Talon(7);
    Talon talon2 = new Talon(8);
    DoubleSolenoid motors = new DoubleSolenoid(1, 1, 2);
    DoubleSolenoid ratchet = new DoubleSolenoid(1, 3, 4);
    DigitalInput limitSwitch = new DigitalInput(2);
    public void drive(double speed) {
        talon1.set(speed);
        talon2.set(speed);
    }

    public void engageRatchet() {
        ratchet.set(DoubleSolenoid.Value.kForward);
    }

    public void disengageRatchet() {
        ratchet.set(DoubleSolenoid.Value.kReverse);
    }

    public void engageMotors() {
        motors.set(DoubleSolenoid.Value.kForward);
    }

    public void disengageMotors() {
        motors.set(DoubleSolenoid.Value.kReverse);
    }

    public void sweetSpotShotRetract() {
        engageMotors();
        engageRatchet();
        if (!isCatapultDown()) {
            drive(1);
        } else {
            drive(0);
            disengageMotors();
        }
    }

    public void smallSpotShotRetract() {
        engageMotors();
        engageRatchet();
        if (!isCatapultDown()) {
            drive(1);
        } else {
            engageMotors();
            drive(0);
        }
    }

    public boolean isCatapultDown() {
        System.out.println(limitSwitch.get());
        return !limitSwitch.get();
    }

    public boolean areMotorsEngaged() {
        return motors.get() == DoubleSolenoid.Value.kForward;
    }

    public boolean isRatchetEngaged() {
        return ratchet.get() == DoubleSolenoid.Value.kReverse;
    }
}
