/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package com.team20;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Launchpad extends IterativeRobot {

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    Talon talon1, talon2, talon3, talon4, talon5, talon6;
    LogitechGamepadController driverGamepad;
    JoystickButton yButton, xButton, bButton, aButton, rightTrigger, leftTrigger;
    LogitechDualActionController operatorController;
    JoystickButton button1, button2, button3, button4, button5, button6, button7, button8, button9, button10;
    Drivetrain drive;
    Collector collector;
    Compressor compressor;
    Relay relay;
    Catapult catapult;
    CatcherPanel rightPanel, leftPanel, backPanel;
    boolean shootAndBringBack = false, spitBallOut = false, bringBack = false, catcherOverride = false;
    long shootAndBringBackStartTime, spitBallOutStartTime, bringBackStartTime;
    DriverStationLCD dsLCD;
    public Vision vision;

    //LEDs leds;
    public void robotInit() {
        compressor = new Compressor(1, 1);
        compressor.start();

        drive = new Drivetrain();
        collector = new Collector();
        catapult = new Catapult();

        //leds = new LEDs(0,7,6,5);
        rightPanel = new CatcherPanel(new DoubleSolenoid(2, 1, 2));
        backPanel = new CatcherPanel(new DoubleSolenoid(2, 3, 4));
        leftPanel = new CatcherPanel(new DoubleSolenoid(2, 5, 6));

        driverGamepad = new LogitechGamepadController(1);

        yButton = driverGamepad.getYButton();
        xButton = driverGamepad.getXButton();
        bButton = driverGamepad.getBButton();
        aButton = driverGamepad.getAButton();
        rightTrigger = driverGamepad.getRightBumper();
        leftTrigger = driverGamepad.getLeftBumper();

        operatorController = new LogitechDualActionController(2);
        button1 = operatorController.getButton(1);
        button2 = operatorController.getButton(2);
        button3 = operatorController.getButton(3);
        button4 = operatorController.getButton(4);
        button5 = operatorController.getButton(5);
        button6 = operatorController.getButton(6);
        button7 = operatorController.getButton(7);
        button8 = operatorController.getButton(8);
        button9 = operatorController.getButton(9);
        button10 = operatorController.getButton(10);
        dsLCD = DriverStationLCD.getInstance();
    }

    public void autonomousInit() {
        //start vision thread and connect with beaglebone
       /* vision = new Vision();*/

    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        //leds.setMode(1);
      /*  if (!vision.isRedBallInfoUpdated()) {
         vision.lookForRedBallInfo();
         }
         if (vision.isRedBallInfoUpdated()) {
         //execute auto code here
            
         }*/
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        compressor.start();
        drive.arcadeDrive(driverGamepad.getLeftY(), driverGamepad.getAnalogTriggers());
        collector.update();
        //catapult.drive(operatorController.getLeftY());

        //driver station
        if (catapult.isCatapultDown()) {
            dsLCD.println(DriverStationLCD.Line.kUser1, 1, "Catapult is: DOWN");
        } else if (!catapult.isCatapultDown()) {
            dsLCD.println(DriverStationLCD.Line.kUser1, 1, "Catapult is: UP  ");
        }
        dsLCD.updateLCD();

        //driver trigger buttons
        if (rightTrigger.get()) {
            drive.lowGear();
        } else if (leftTrigger.get()) {
            drive.highGear();
        }

        //operator left shoulder buttons
        if (button5.get()) {
            //disengage motors, delay, disengage ratchet, delay, smallspotshotretract
            //shoots and retracts
            if (!collector.isWilted() && !backPanel.isWilted()) {
                catapult.disengageMotors();
                shootAndBringBack = true;
                shootAndBringBackStartTime = System.currentTimeMillis();
            }
        } else if (button7.get()) {
            //disengage ratchet, delay, reengage ratchet
            //spits the ball out
            catapult.disengageRatchet();
            spitBallOutStartTime = System.currentTimeMillis();
            spitBallOut = true;
            bringBack = false;
            shootAndBringBack = false;
        }

        //operator right shoulder buttons
        if (button6.get()) {
            collector.wilt();
        } else if (button8.get()) {
            collector.bloom();
        }

        //operator 1/2/3/4 buttons
        if (button4.get()) {
            collector.drive();
        } else if (button2.get()) {
            collector.backdrive();
        } else if (button3.get() || button1.get()) {
            collector.stop();
        }
        if (button1.get()) {
            catcherOverride = !catcherOverride;
        }
        if (button9.get()) {
            // catapult.disengageMotors();
            catapult.drive(0);
            shootAndBringBack = false;
            bringBack = false;
            spitBallOut = false;
        } else if (button10.get()) {
            catapult.smallSpotShotRetract();
        }
        if (!button10.get() && !spitBallOut/* && !shootAndBringBack*/ && !bringBack) {
            catapult.drive(0);
        }

        //operator dpad
        if (!catcherOverride) {//override is off
            if (operatorController.dPadUp()) {
                //flips all catchers to bloomed, unless theyre all bloomed, then wilt them
                if (leftPanel.isBloomed() && backPanel.isBloomed() && rightPanel.isBloomed()) {
                    leftPanel.wilt();
                    backPanel.wilt();
                    rightPanel.wilt();
                } else {
                    leftPanel.bloom();
                    backPanel.bloom();
                    rightPanel.bloom();
                }

            } //toggle the values of all the catchers between wilted and bloomed
            else if (operatorController.dPadDown()) {
                backPanel.toggleWiltedOrBloomed();
            } else if (operatorController.dPadLeft()) {
                leftPanel.toggleWiltedOrBloomed();
            } else if (operatorController.dPadRight()) {
                rightPanel.toggleWiltedOrBloomed();
            }
        } else {//override is on
            if (operatorController.dPadUp()) {
                leftPanel.bloom();
                backPanel.bloom();
                rightPanel.bloom();

            } //toggle the values of all the catchers between wilted and bloomed
            else if (operatorController.dPadDown()) {
                backPanel.wilt();
            } else if (operatorController.dPadLeft()) {
                leftPanel.wilt();
            } else if (operatorController.dPadRight()) {
                rightPanel.wilt();
            }
        }

        //statements for delayed actions
        if (shootAndBringBack) {
            if (System.currentTimeMillis() - shootAndBringBackStartTime >= 300) {
                catapult.disengageRatchet();
                shootAndBringBack = false;
                bringBack = true;
                bringBackStartTime = System.currentTimeMillis();
            }
        }
        if (spitBallOut) {
            if (System.currentTimeMillis() - spitBallOutStartTime >= 2000) {
                System.out.println(System.currentTimeMillis() - spitBallOutStartTime);
                catapult.engageRatchet();
                spitBallOut = false;
            }
        }
        if (bringBack) {
            if (System.currentTimeMillis() - bringBackStartTime >= 500&&System.currentTimeMillis() - bringBackStartTime <=2000) {
                catapult.smallSpotShotRetract();
                bringBack = false;
            }
        }

        //led code
     /*   if (shootAndBringBack) {//shooting
         leds.setMode(5);
         }
         else if(bringBack){//pulling back
         leds.setMode(6);
         }
         else if (drive.isInHighGear()) {
         leds.setMode(3);
         } else {
         leds.setMode(4);
         }*/
    }
}
