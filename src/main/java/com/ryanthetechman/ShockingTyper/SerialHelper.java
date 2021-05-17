package com.ryanthetechman.ShockingTyper;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialHelper {
    SerialPort comPort;
    OutputStream comOut;
    InputStream comIn;
    boolean debugMode = false;

    public SerialHelper(){}

    public SerialHelper(boolean debugMode) {this.debugMode = debugMode;}

    boolean write(int i){return write(i, false);}
    boolean write(int i, boolean throwError){
        try {
            this.comOut.write(i);
            return true;
        }
        catch (IOException e){
            if (throwError) e.printStackTrace();
            return false;
        }
    }

    boolean setPort(int comPort) {return setPort(comPort, false);}
    boolean setPort(int comPort, boolean throwError){
        try {
            this.comPort = SerialPort.getCommPort("COM" + comPort);
        }
        catch (SerialPortInvalidPortException e){
            if (throwError) e.printStackTrace();
            return false;
        }
        this.comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        this.comPort.setBaudRate(9600);
        return true;
    }

    void connect(){
        comPort.openPort();
        comOut = comPort.getOutputStream();
        comIn = comPort.getInputStream();
    }

    void disconnect(){
        try {comOut.close();} catch (IOException ignore) {}
        try {comIn.close();} catch (IOException ignore) {}
        comPort.closePort();
    }

    boolean isOpen(){
        return comPort.isOpen();
    }

}
