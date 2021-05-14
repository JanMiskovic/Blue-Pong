package sk.ukf.bluepong.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

import sk.ukf.bluepong.R;

public class ShowPairedActivity extends AppCompatActivity {

    private BluetoothAdapter ba;
    private Set<BluetoothDevice> devices;
    private ArrayList<String> deviceNames;
    private ListView pairedDevicesistView;
    private ArrayAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_paired);

        ba = BluetoothAdapter.getDefaultAdapter();
        devices = ba.getBondedDevices();
        deviceNames = new ArrayList<>();
        pairedDevicesistView = findViewById(R.id.pairedDevicesListView);
        aa = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNames);

        pairedDevicesistView.setAdapter(aa);

        showPaired();
    }

    private void showPaired() {
        for (BluetoothDevice device : devices) {
            deviceNames.add(String.format("%s\n(MAC %s)", device.getName(), device.getAddress()));
        }
        aa.notifyDataSetChanged();
    }
}