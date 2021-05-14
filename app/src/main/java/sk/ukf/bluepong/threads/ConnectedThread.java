package sk.ukf.bluepong.threads;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sk.ukf.bluepong.Game;

public class ConnectedThread extends Thread {

    private BluetoothSocket socket;
    private InputStream in;
    private OutputStream out;
    private final Handler handler;

    public ConnectedThread(Handler handler) {
        this.handler = handler;
    }

    public void setSocket(BluetoothSocket s) {
        socket = s;
        InputStream tempIn = null;
        OutputStream tempOut = null;
        // Get the BluetoothSocket input and output streams
        try {
            tempIn = socket.getInputStream();
            tempOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("", "Temp sockets not created", e);
        }
        in = tempIn;
        out = tempOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        // Keep listening to the InputStream while connected
        while (true) {
            try {
                bytes = in.read(buffer);
                // Send the obtained bytes to the UI Activity
                handler.obtainMessage(Game.MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e("", "disconnected", e);
                break;
            }
        }
    }
    /**
     * Write to the connected OutStream.
     * @param buffer  The bytes to write
     */
    public void write(byte[] buffer) {
        try {
            out.write(buffer);
            // Share the sent message back to the UI Activity
            handler.obtainMessage(Game.MESSAGE_WRITE, -1, -1, buffer)
                    .sendToTarget();
        } catch (IOException e) {
            Log.e("", e.getMessage());
        }
    }
    public void cancel() {
        try { socket.close(); }
        catch (Exception e) { Log.e("connectedThread close", e.getMessage()); }
    }
}
