package sk.ukf.bluepong.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import sk.ukf.bluepong.Game;
import sk.ukf.bluepong.R;

public class GameActivity extends AppCompatActivity {

    private TextView gameText;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameText = findViewById(R.id.gameText);
        game = findViewById(R.id.gameView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        game.killThreads();
    }

    public void waitingForConnection() {
        gameText.setText(R.string.waiting_for_opponent);
    }

    public void tryingToConnect() {
        gameText.setText(R.string.trying_to_connect);
    }

    public void opponentConnected() {
        //gameText.setText(R.string.opponent_connected);
        gameText.setVisibility(View.GONE);
    }

    public void connectionSuccessful() {
        //gameText.setText(R.string.connection_successful);
        gameText.setVisibility(View.GONE);
    }

}