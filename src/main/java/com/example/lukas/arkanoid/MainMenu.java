package com.example.lukas.arkanoid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button newGame;
        Button Exit;

        newGame = findViewById(R.id.button);
        Exit = findViewById(R.id.button2);



    }

    // tlačítko pro vypnutí hry
    public void ExitGame(View view) {
        finish();
        System.exit(0);
    }

    // tlačítko pro zapnutí hry
    public void NewGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
