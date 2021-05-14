package sk.ukf.bluepong.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

import sk.ukf.bluepong.R;

public class JoinActivity extends AppCompatActivity {

    private BluetoothAdapter ba;
    private ArrayList<BluetoothDevice> devices;
    private ArrayList<String> deviceNames;
    private ListView joinableDevicesListView;
    private ArrayAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        ba = BluetoothAdapter.getDefaultAdapter();
        devices = new ArrayList<>(ba.getBondedDevices());
        deviceNames = new ArrayList<>();
        joinableDevicesListView = findViewById(R.id.joinableDevicesListView);
        aa = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNames);

        joinableDevicesListView.setAdapter(aa);

        addListeners();
        showJoinable();
    }

    private void showJoinable() {
        for (BluetoothDevice device : devices) {
            deviceNames.add(device.getName());
        }
        aa.notifyDataSetChanged();
    }

    private void addListeners() {
        joinableDevicesListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(JoinActivity.this, GameActivity.class);
            intent.putExtra("isHost", false);
            intent.putExtra("opponentMAC", devices.get(position).getAddress());
            startActivity(intent);
            finish();
        });
    }
}