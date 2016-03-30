package charles.mobiledevfinalproject;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// this is the main activity
// responsible for managing GameView object and know when the
// player has won or lost the game and respond appropriately
// it should also manage high scores

//TODO everything

public class MainActivity extends AppCompatActivity {

    GameView v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        v = new GameView(getApplicationContext(), null);

    }
}
