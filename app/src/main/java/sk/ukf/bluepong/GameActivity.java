package sk.ukf.bluepong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    private Game game;
    private static boolean isHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        isHost = intent.getBooleanExtra("isHost", true);

        game = findViewById(R.id.gameView);
    }

    public static boolean getIsHost() { return isHost; }
}