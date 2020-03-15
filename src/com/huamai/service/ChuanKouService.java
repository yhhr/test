package com.huamai.service;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChuanKouService {


    public void runTask(){
        SerialPort serialPort = getSerialPort(getConnectionPortId("COM1"));
        try {
            serialPort.setSerialPortParams(9600,8,1,0);
            ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
            scheduledThreadPool.scheduleWithFixedDelay(new ChuanKouThread(serialPort), 1, 120, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 通过串口主键打开串口(COM1)
     * @param portId
     * @return SerialPort 串口对象
     */
    private SerialPort getSerialPort(CommPortIdentifier portId){
        if(portId == null){
            return null;
        }
        try {
            //延时200毫秒打开串口  服务器电脑：Serial_Communication 本地电脑：  USB Serial Port
            SerialPort serialPort = (SerialPort)portId.open("Serial_Communication", 200);
            return serialPort;
        } catch (PortInUseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取串口主键(COM1主键)
     * @return CommPortIdentifier
     */
    @SuppressWarnings("unchecked")
    private CommPortIdentifier getConnectionPortId(String port){
        //获取所有串口集合
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier returnPort = null;
        while (portList.hasMoreElements())
        {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            /* 如果端口类型是串口 */
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                //如果端口名称为：COM1
                if(portId.getName().equals(port)){
                    returnPort = portId;
                    break;
                }
            }
        }
        return returnPort;
    }
}
