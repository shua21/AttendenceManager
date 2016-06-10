package com.example.shua21.attendancemanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by shua21 on 2015-10-10.
 */
public class BluetoothOneshot extends Thread{
    BluetoothServerSocket mmServerSocket=null;
    BluetoothAdapter mAdapter=null;
    BluetoothSocket socket = null;
    InputStream mmInStream =null;
    OutputStream mmOutStream = null;
    Handler mh = null;
    boolean nowState=false;

    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    byte[] buffer = new byte[1024];
    int bytes;
    int st=0;
    BluetoothOneshot(Handler m){
        mh = m;
        try {
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                    NAME_INSECURE, MY_UUID_INSECURE);
        }catch(Exception e){}
        this.start();
    }

    public void run()
    {
        while(!this.isInterrupted()) {
             try {
                nowState = socket.isConnected();
             }catch(Exception e) {
                nowState = false;
             }
             try{
                 if(nowState==false) {


                     socket = mmServerSocket.accept(1000);
                     if(socket != null) {
                         mmInStream = socket.getInputStream();
                         mmOutStream = socket.getOutputStream();
                         mh.sendEmptyMessage(2);
                         bytes = mmInStream.read(buffer);
                         String b = new String(buffer, 0, bytes);
                         Message m = mh.obtainMessage(1, bytes, -1, buffer);
                         mh.sendMessage(m);
                         //mAdapter = BluetoothAdapter.getDefaultAdapter();
                         //mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                         //        NAME_INSECURE, MY_UUID_INSECURE);

                     }
                 }
             } catch (Exception e) {
                 try{socket.close();}catch (Exception ee){}
                 if(this.isInterrupted()) {
                     break;
                 }
             }
        }
        try {socket.close();}catch(Exception e){}
        try {mmServerSocket.close();}catch(Exception e){}

    }

    public void okmsg(byte[] buffer){
        try {

            mmOutStream.write(buffer);
            socket.close();
            nowState = false;


        } catch (IOException e) {
            mh.sendEmptyMessage(4);
            try{socket.close();}catch (Exception ee){}
        }

    }
    public void ThreadStop()
    {
        try{
            this.stop();
            //socket.close();
        }
        catch(Exception e){}
    }

}
