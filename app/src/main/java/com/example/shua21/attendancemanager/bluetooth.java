package com.example.shua21.attendancemanager;

/**
 * Created by shua21 on 2015-10-04.
 */


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


/**
 * Created by shua21 on 2015-09-11.
 */
public class bluetooth {
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    public int mState = STATE_CONNECTING;
    AcceptThread at = null;
    public ConnectedThread ct = null;
    final Handler mh;
    bluetooth(Handler h) {
        at = new AcceptThread();
        at.start();
        mh = h;
    }

    class AcceptThread extends Thread {
        BluetoothServerSocket mmServerSocket;
        BluetoothAdapter mAdapter;



        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            mState = STATE_LISTEN;
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            try {
                tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                        NAME_INSECURE, MY_UUID_INSECURE);

            } catch (IOException e) {

            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            while(true){
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (bluetooth.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());

                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {

                                }
                                break;
                        }
                    }
                }
            }
        }
        }


    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
        at = null;
        ct = new ConnectedThread(socket);
        ct.start();
        mState = STATE_CONNECTED;
    }

    class ConnectedThread extends Thread {
        public final BluetoothSocket mmSocket;
        public final InputStream mmInStream;
        public final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        int a=0;
        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;


            // Keep listening to the InputStream while connected

                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String b = new String(buffer,0,bytes);
                    mh.obtainMessage(1, bytes, -1, buffer)
                            .sendToTarget();

                    int w = 1+1;
                    //Toast.makeText("a", buffer.toString());


                } catch (IOException e) {

                }


        }
        public void okmsg(String stuid)
        {

            write(stuid.getBytes());
            a=1;

        }
        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {

            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {

            }
        }


    }



}