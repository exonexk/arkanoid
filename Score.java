package com.example.lukas.arkanoid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

public class Score extends Activity {

    TextView highscore;
    TextView PlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        highscore = (TextView)findViewById(R.id.highscore);
        PlayerName = (TextView)findViewById(R.id.textView6);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String score=pref.getString("score", "0");
        String name=pref.getString("username","Name");

        highscore.setText("Score: " + score);
        PlayerName.setText(name);
    }

    public void MainMenu(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
