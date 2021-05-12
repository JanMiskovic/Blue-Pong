package sk.ukf.bluepong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter ba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ba = BluetoothAdapter.getDefaultAdapter();
        updateButtons();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        if (prefs.getBoolean("firstStart", true)) {
            showFirstTimeTutorial();
        }
    }

    public void enableServices(View v) {
        if (ba == null) {
            Toast.makeText(this, "Your device doesn't have bluetooth." +
                    "Please try launching the game on a different device.", Toast.LENGTH_LONG).show();
            return;
        }

        if (ba.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
            enableBluetoothAndDiscoverable();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (lm == null) {
            Toast.makeText(this, "Your device doesn't have GPS." +
                    "Please try launching the game on a different device.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLocation();
        }

        updateButtons();
    }

    private void enableBluetoothAndDiscoverable() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(intent, 1);
    }

    private void enableLocation() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent,2);
    }

    // Disables or enables the 4 main menu buttons, depending on the status of Bluetooth, Discoverable and GPS access
    private void updateButtons() {
        boolean shouldBeEnabled = (ba.isEnabled() && ba.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        findViewById(R.id.pairButton).setEnabled(shouldBeEnabled);
        findViewById(R.id.showPairedButton).setEnabled(shouldBeEnabled);
        findViewById(R.id.createButton).setEnabled(shouldBeEnabled);
        findViewById(R.id.joinButton).setEnabled(shouldBeEnabled);
    }

    private void showFirstTimeTutorial() {
        startTutorialActivity(null);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    public void startTutorialActivity(View view) {
        Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
        startActivity(intent);
    }

    public void startPairActivity(View view) {
        Intent intent = new Intent(MainActivity.this, PairActivity.class);
        startActivity(intent);
    }

    public void startShowPairedActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ShowPairedActivity.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // If returning from turning on bluetooth or discovery mode, check if buttons should change
            case 1: case 2:
                updateButtons();
                break;

            default: updateButtons();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                updateButtons();
                break;

            default: updateButtons();
        }
    }
}
