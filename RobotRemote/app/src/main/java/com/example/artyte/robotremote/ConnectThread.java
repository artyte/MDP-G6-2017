package com.example.artyte.robotremote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;
import static android.content.ContentValues.TAG;


public class ConnectThread extends Thread{
    private BluetoothSocket mmSocket = null;
    private BluetoothDevice mmDevice;


    public ConnectThread(BluetoothDevice device, UUID MY_UUID) {
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    @Override
    public void run() {
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            Log.i("Bluetooth", "connecting");
            mmSocket.connect();
            Log.i("Bluetooth", "connected");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
