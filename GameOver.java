package com.example.lukas.arkanoid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GameOver extends Activity {
    Button Mainmenu;
    Button Save_score;
    EditText Name;
    String score;
    TextView scor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Mainmenu = findViewById(R.id.button3);
        Save_score = findViewById(R.id.save_score);
        Name = findViewById(R.id.editText);
        scor = findViewById(R.id.score_view);

        Intent intent;
        intent = getIntent();

        score = intent.getStringExtra("highscore");

        scor.setText("Your score: " + score);




    }

    public void MainMenu(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void SaveScore(View view) {
        Intent intent = new Intent(this, Score.class);
        String nam;
        nam = Name.getText().toString();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        String sc=pref.getString("score", "0");
        int sco = Integer.parseInt(sc);
        int highsco = Integer.parseInt(score);
        if(highsco > sco){
            editor.putString("username", nam);
            editor.putString("score", score);
            editor.apply();
        }
        startActivity(intent);
    }
}
