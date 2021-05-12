package sk.ukf.bluepong;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class PairActivity extends AppCompatActivity {

    private BluetoothAdapter ba;
    private ArrayList<BluetoothDevice> devices;
    private ArrayList<String> deviceNames;
    private ListView devicesListView;
    private ArrayAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);

        ba = BluetoothAdapter.getDefaultAdapter();
        devices = new ArrayList<>();
        deviceNames = new ArrayList<>();
        devicesListView = findViewById(R.id.devicesListView);
        aa = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNames);

        devicesListView.setAdapter(aa);
        addListeners();
    }

    public void scanForNearbyDevices(View v) {
        ba.cancelDiscovery();
        devices.clear();
        deviceNames.clear();
        aa.notifyDataSetChanged();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        registerReceiver(scanReciever, filter);
        ba.startDiscovery();
    }

    private void pair(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond");
            method.invoke(device, (Object[]) null);
            Toast.makeText(this, "Pairing with " + device.getName() + " ...", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(this, "Pairing failed.", Toast.LENGTH_LONG).show();
        }
    }

    private void addListeners() {
        devicesListView.setOnItemClickListener((parent, view, position, id) -> {
            ba.cancelDiscovery();
            pair(devices.get(position));
        });
    }

    private final BroadcastReceiver scanReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Toast.makeText(context, "Scanning for nearby devices...", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(context, "Scanning finished.", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getName() != null && !devices.contains(device)) {
                        devices.add(device);
                        deviceNames.add(device.getName());
                        aa.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    public void onDestroy() {
        try {
            unregisterReceiver(scanReciever);
        } catch (Exception ignored) { }
        super.onDestroy();
    }
}