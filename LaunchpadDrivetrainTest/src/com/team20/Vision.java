/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team20;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 * @author Driver
 */
public class Vision {

    boolean redBallDetected, blueBallDetected, horizontal, vertical;
    int redBallX, redBallY,
            blueBallX, blueBallY,
            horizontalX, horizontalY,
            verticalX, verticalY;
    int mode;
    NetworkTable visionTable = NetworkTable.getTable("Vision");

    public boolean getRedBallDetected() {

        redBallDetected = visionTable.getBoolean("redBallDetected");
        return redBallDetected;
    }

    public boolean getBlueBallDetected() {

        blueBallDetected = visionTable.getBoolean("blueBallDetected");
        return blueBallDetected;
    }

    public boolean getVerticalDetected() {
        vertical = visionTable.getBoolean("verticalDetected");
        return vertical;
    }

    public boolean getHorizontalDetected() {
        horizontal = visionTable.getBoolean("horizontalDetected");
        return horizontal;
    }

    public int getRedBallX() {
        redBallX = (int) visionTable.getNumber("redBallX");
        return redBallX;
    }

    public int getRedBallY() {
        redBallY = (int) visionTable.getNumber("redBallY");
        return redBallY;
    }

    public int getBlueBallX() {
        blueBallX = (int) visionTable.getNumber("blueBallX");
        return blueBallX;
    }

    public int getBlueBallY() {
        blueBallY = (int) visionTable.getNumber("blueBallY");
        return blueBallY;
    }

    public int getVerticalX() {
        verticalX = (int) visionTable.getNumber("verticalX");
        return verticalX;
    }

    public int getVerticalY() {
        verticalY = (int) visionTable.getNumber("verticalY");
        return verticalY;
    }

    public int getHorizontalX() {
        horizontalX = (int) visionTable.getNumber("horizontalX");
        return horizontalX;
    }

    public int getHorizontalY() {
        horizontalY = (int) visionTable.getNumber("horizontalY");
        return horizontalY;
    }
}
