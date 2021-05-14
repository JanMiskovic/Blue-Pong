package sk.ukf.bluepong.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

import sk.ukf.bluepong.Game;

public class ListeningThread extends Thread {

    private final BluetoothServerSocket bluetoothServerSocket;
    private ConnectedThread ct;
    private Handler handler;

    public ListeningThread(BluetoothAdapter ba, UUID uuid, ConnectedThread ct, Handler handler) {
        BluetoothServerSocket temp = null;
        this.ct = ct;
        this.handler = handler;

        try { temp = ba.listenUsingRfcommWithServiceRecord("BluePong", uuid); }
        catch (IOException e) { e.printStackTrace(); }
        bluetoothServerSocket = temp;
    }

    public void run() {
        BluetoothSocket bluetoothSocket;
        // This will block while listening until a BluetoothSocket is returned
        // or an exception occurs
        while (true) {
            try { bluetoothSocket = bluetoothServerSocket.accept(); }
            catch (IOException e) { break; }

            // If a connection is accepted
            if (bluetoothSocket != null) {
                // Manage the connection in a separate thread
                ct.setSocket(bluetoothSocket);
                // Send the obtained bytes to the UI Activity
                handler.obtainMessage(Game.CONNECTED, -1, -1, null)
                        .sendToTarget();
                ct.start();

                try { bluetoothServerSocket.close(); }
                catch (IOException e) { e.printStackTrace(); }
                break;
            }
        }
    }

    // Cancel the listening socket and terminate the thread
    public void cancel() {
        try { bluetoothServerSocket.close(); }
        catch (Exception e) { e.printStackTrace(); }
    }
}