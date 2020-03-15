package com.huamai.service;

import gnu.io.SerialPort;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class ChuanKouThread implements Runnable {
    private static String[] num = { "0", "1", "2", "3", "4", "5", "6", "7", "8","9", "A", "B", "C", "D", "E", "F" };
    SerialPort serialPort;



    @Override
    public void run() {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(serialPort.getInputStream());//得到串口输入流
            out = new BufferedOutputStream(serialPort.getOutputStream());//得到串口输出流
            int gateWay = 5;
            for(int i=1;i<=gateWay;i++){
                //一台交换机有3个网关，每个网关有8个网口，对应8台透析机
                for(byte j=65;j<68;j++) {//下行数据中第6字节：网关分组编号：0X41、0X42、0X43 对应byte是65、66、67
                    readData(in,out,i,j);//读取当前网关三个分组信息.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(in != null){
                    in.close();
                }
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    public ChuanKouThread(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    private void readData(BufferedInputStream in,BufferedOutputStream out,int gateWay,int group) throws InterruptedException, IOException {
        byte[] b= {-1,-86,-1,2,1,65,-125,124,0,0};//定义发送请求
        b[4]=(byte)gateWay;
        b[5]=(byte)group;
        b[9]=(byte) ((-1*(b[4]+b[5]+b[6]+b[7]+b[8])));
        out.write(b);//发送请求
        try {
            out.flush();
        } catch (Exception e) {

        }
        Thread.sleep(1000);
        byte[] data=new byte[1240];//定义数组,能够接收至少二组数据的大小.
        int dataLength=in.read(data);//读取数据.
        if(dataLength>0) {//如果读取到数据则放到数据处理类中处理.
            processData(data,dataLength,gateWay,group);
        }else{
            System.out.println("没有接收到交换机数据,交换机:"+gateWay+"  分组:"+group);
        }
    }

    private void processData(byte[] data,int dataLength,int gateWay,int group){
        boolean checkhard = true;//判断数据接收是否与发送的数据匹配
        byte[] temp=new byte[570];//接收数据总长度
        byte[] h=new byte[5];//判断标志
        int count=0;
        int x = 0;
        for(int j=0;j<dataLength;j++){
            if(checkhard){
                if(x<5){
                    h[x++]=data[j];
                }else{
                    if(h[0]==-1&&h[1]==-86&&h[2]==-1&&h[3]==2){//判断初始值
                        checkhard=false;
                        x=0;
                        Save(h[0],temp,count++,checkhard,gateWay,group);
                        Save(h[1],temp,count++,checkhard,gateWay,group);
                        Save(h[2],temp,count++,checkhard,gateWay,group);
                        Save(h[3],temp,count++,checkhard,gateWay,group);
                        Save(h[4],temp,count++,checkhard,gateWay,group);
                        Save(data[j],temp,count++,checkhard,gateWay,group);
                        h=new byte[5];
                    }else{
                        h[0]=h[1];
                        h[1]=h[2];
                        h[2]=h[3];
                        h[3]=h[4];
                        h[4]=data[j];
                    }
                }
            }else{
                Save(data[j],temp,count++,checkhard,gateWay,group);
            }
        }
    }
    private void Save(byte b,byte[] temp,int count,boolean checkhard,int gateWay,int group){
        if(count < temp.length){
            temp[count]=b;
            if(count==(temp.length-1)){
                checkhard=true;
                check(temp,gateWay,group);//如果数据满了则检测数据
                count=0;
            }
        }
    }
    private void check(byte[] temp,int gateWay,int group) {
        byte b=temp[4];
        if(temp[4]!=gateWay||temp[5]!=group) {//检测网关及分组信息
            System.err.println(temp[4]+"***********************网关或分组数据错误****************************"+temp[5]);
        }else {
            System.out.println("***********************网关或分组数据正确****************************");
        }
        for(int i=5;i<570;i++) {
            b^=temp[i];
        }
        if(b!=temp[569]) {//检测数据包信息
            System.err.println("***********************数据包的校验字节不正确2************************");
        }else {
            System.out.println("***********************数据包的校验字节正确************************");
        }
        for(int i=0;i<8;i++) {
            check(i,temp);//检测分组信息
        }
    }
    private void check(int i,byte[] temp) {
        checkGroup(8+(70*i),temp);
    }
    private void checkGroup(int index,byte[] temp) {
        byte b=temp[index+1];
        for(int i=index+2;i<(index+67);i++) {
            b^=temp[i];
        }

//		if(b!=temp[index+68]||temp[index]!=40||temp[index+69]!=41) {//检测分组信息
//			return;
//		}
        if(temp.length!=570){
            return;
        }
        byte[] bs=new byte[70];
        for(int i=0;i<bs.length;i++) {
            bs[i]=temp[index+i];
        }
        //更新数据到内存中.
        String gateWay = getString(bs[1]);//网关地址
        String group = getString(bs[2]);//网关内分组编号
        String portId = getString(bs[3]);//端口号

        String address = gateWay+" "+group+" "+portId;

        StringBuffer sb = new StringBuffer();
        sb.append(getString(bs[0]));
        for (int i = 1; i < bs.length; i++) {
            sb.append(" ").append(getString(bs[i]));
        }
        String data = sb.toString();
        String uuid= UUID.randomUUID().toString();
//        logger.debug("唯一编号="+uuid+"#机器地址:"+address+"#数据:"+data);
        sb.setLength(0);

        sb.append("update MachineData set reak_mark='").append(uuid).append("',data='").append(data).append("',UpdateTime=getDate() where code='").append(address).append("'");
        try {
            int r = 5;
            if(r==0){
                sb.setLength(0);
                sb.append("insert into MachineData(data,code,updatetime,reak_mark) values ('").append(data).append("','").append(address).append("',getDate(),'").append(uuid).append("')");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static String getString(byte b) {
        int s=b&255;
        return num[s >> 4] + num[s & 15];
    }
}
