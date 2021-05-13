package sk.ukf.bluepong;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

class ConnectingThread extends Thread {

    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice opponent;
    private BluetoothAdapter ba;
    private ConnectedThread ct;
    private Handler handler;

    public ConnectingThread(BluetoothAdapter ba, BluetoothDevice opponent, UUID uuid, ConnectedThread ct, Handler handler) {
        BluetoothSocket temp = null;
        this.opponent = opponent;
        this.ba = ba;
        this.ct = ct;
        this.handler = handler;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try { temp = opponent.createRfcommSocketToServiceRecord(uuid); }
        catch (IOException e) { e.printStackTrace(); }
        bluetoothSocket = temp;
    }

    public void run() {
        // Cancel any discovery as it will slow down the connection
        ba.cancelDiscovery();

        // This will block until it succeeds in connecting to the device
        // through the bluetoothSocket or throws an exception
        try { bluetoothSocket.connect(); }
        catch (IOException connectException) {
            connectException.printStackTrace();
            try { bluetoothSocket.close(); }
            catch (IOException closeException) { closeException.printStackTrace(); }
        }

        // Code to manage the connection in a separate thread
        /*
            manageBluetoothConnection(bluetoothSocket);
        */
        ct.setSocket(bluetoothSocket);
        try {
            // Send the obtained bytes to the UI Activity
            handler.obtainMessage(GameActivity.ACCEPTED, -1, -1, null)
                    .sendToTarget();
        } catch (Exception e) { Log.e("", "error", e); }
        ct.start();
    }

    // Cancel an open connection and terminate the thread
    public void cancel() {
        try { bluetoothSocket.close(); }
        catch (IOException e) { e.printStackTrace(); }
    }
}