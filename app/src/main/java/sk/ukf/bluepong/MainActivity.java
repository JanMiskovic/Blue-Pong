package sk.ukf.bluepong;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter ba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        if (prefs.getBoolean("firstStart", true)) {
            showFirstTimeTutorial();
        }

    }

    private void enableServices() {
        ba = BluetoothAdapter.getDefaultAdapter();

        if (ba == null) {
            Toast.makeText(this, "Your device doesn't have bluetooth." +
                    "Please try launching the game on a different device.", Toast.LENGTH_LONG).show();
            return;
        }

        //if (ba.isEnabled())
        //}TODO
    }

    private void disableButtons() {

    }

    private void showFirstTimeTutorial() {
        startTutorial(null);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    public void startTutorial(View view) {
        Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
        startActivity(intent);
    }
}