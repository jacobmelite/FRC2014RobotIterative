/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team20;

import com.sun.squawk.platform.windows.natives.Socket;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

/**
 *
 * @author Driver
 */
public class Vision {

    private boolean redBallDetected = false, blueBallDetected = false, horizontal = false, vertical = false;
    private boolean redBallInfoUpdated = false, blueBallInfoUpdated = false, horizontalInfoUpdated = false, verticalInfoUpdated = false;
    private int redBallX = 0, redBallY = 0,
            blueBallX = 0, blueBallY = 0,
            horizontalX = 0, horizontalY = 0,
            verticalX = 0, verticalY = 0;
    private int mode = 0;
    public final int CAMERA_PROCESSING_TIME = 500, THREAD_TIMEOUT_TIME = 15000, DATA_CYCLE_TIME = CAMERA_PROCESSING_TIME + 200;
    //NetworkTable visionTable = NetworkTable.getTable("Vision");
    private SocketConnection sc;
    private OutputStream os;
    private InputStream is;
    private byte[] buffer = new byte[5];

    public Vision() {

        new Thread() {
            public void run() {
                //open connection
                try {
                    sc = (SocketConnection) Connector.open("socket://10.0.20.15:9090");
                    sc.setSocketOption(SocketConnection.LINGER, 5);
                    os = sc.openOutputStream();
                    is = sc.openInputStream();
                } catch (Exception e) {
                    System.out.println("Exception 1 " + e.toString());
                }
                long startTime = System.currentTimeMillis();
                long incrementalTime = System.currentTimeMillis();

                while (true) {
                    //falsify all infoUpdated so the class knows that its info is out of date
                    if (incrementalTime - System.currentTimeMillis() > DATA_CYCLE_TIME + 50) {
                        redBallInfoUpdated = false;
                        blueBallInfoUpdated = false;
                        horizontalInfoUpdated = false;
                        verticalInfoUpdated = false;
                        incrementalTime = System.currentTimeMillis();
                    }
                    //get data from the beaglebone
                    getData(mode);
                    processBuffer();

                    //time limit on connection
                    if (startTime - System.currentTimeMillis() > THREAD_TIMEOUT_TIME) {
                        try {
                            sc.close();
                            os.close();
                            is.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    }
                }
            }
        };
    }

    private boolean isByteTrue(byte b) {
        return b == 1;
    }

    private short addBytes(byte b, byte c) {
        short ret = (short) (b << 8);
        ret |= c;
        return (short) (256 + ret);
    }

    public int getMode() {
        return mode;
    }

    private void getData(int b) {

        try {

            //is.skip(is.available());
            os.write(b);//0 horiz 1 vert 2 red 3 blue
            os.flush();
            Thread.sleep(CAMERA_PROCESSING_TIME);//wait for processing
            os.write(5);//ask for image processing data
            os.flush();

            int n;
            do {
            } while ((n = is.available()) == 0);
            if (n == 6) {
                if (is.read() == mode) {
                    is.read(buffer);
                }
            } else if (n > 6) {
                is.skip(is.available());
            }
        } catch (Exception e) {
            System.out.println("I just blew up");
        }
    }

    private void processBuffer() {
        if (buffer == null || buffer.length < 5) {
            return;
        }
        switch (mode) {
            case 0:
                horizontal = isByteTrue(buffer[0]);
                horizontalX = addBytes(buffer[1], buffer[2]);
                horizontalY = addBytes(buffer[3], buffer[4]);
                horizontalInfoUpdated = true;
                break;
            case 1:
                vertical = isByteTrue(buffer[0]);
                verticalX = addBytes(buffer[1], buffer[2]);
                verticalY = addBytes(buffer[3], buffer[4]);
                verticalInfoUpdated = true;
                break;
            case 2:
                redBallDetected = isByteTrue(buffer[0]);
                redBallX = addBytes(buffer[1], buffer[2]);
                redBallY = addBytes(buffer[3], buffer[4]);
                redBallInfoUpdated = true;
                break;
            case 3:
                blueBallDetected = isByteTrue(buffer[0]);
                blueBallX = addBytes(buffer[1], buffer[2]);
                blueBallY = addBytes(buffer[3], buffer[4]);
                blueBallInfoUpdated = true;
                break;
        }
    }

    public void lookForHorizontalInfo() {
        mode = 0;

    }

    public void lookForVerticalInfo() {
        mode = 1;
    }

    public void lookForRedBallInfo() {
        mode = 2;
    }

    public void lookForBlueBallInfo() {
        mode = 3;
    }

    public boolean isHorizontalInfoUpdated() {
        return horizontalInfoUpdated;
    }

    public boolean isVerticalInfoUpdated() {
        return verticalInfoUpdated;
    }

    public boolean isRedBallInfoUpdated() {
        return redBallInfoUpdated;
    }

    public boolean isBlueBallInfoUpdated() {
        return blueBallInfoUpdated;
    }

    public boolean getRedBallDetected() {
        return redBallDetected;

    }

    public boolean getBlueBallDetected() {
        return blueBallDetected;
    }

    public boolean getVerticalDetected() {
        return vertical;
    }

    public boolean getHorizontalDetected() {
        return horizontal;
    }

    public int getRedBallX() {
        return redBallX;
    }

    public int getRedBallY() {
        return redBallY;
    }

    public int getBlueBallX() {
        return blueBallX;
    }

    public int getBlueBallY() {
        return blueBallY;
    }

    public int getVerticalX() {
        return verticalX;
    }

    public int getVerticalY() {
        return verticalY;
    }

    public int getHorizontalX() {
        return horizontalX;
    }

    public int getHorizontalY() {
        return horizontalY;
    }
}
