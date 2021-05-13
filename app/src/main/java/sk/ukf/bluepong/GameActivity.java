package sk.ukf.bluepong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    public static final int CONNECTED = 0;
    public static final int ACCEPTED = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;

    private Game game;
    private static boolean isHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        isHost = intent.getBooleanExtra("isHost", true);

        game = findViewById(R.id.gameView);
        if (!isHost) game.setRotation(180);
    }

    public static boolean getIsHost() { return isHost; }
}