package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radioGroup = findViewById(R.id.radioGroup);

    }

    public void StartGame(View view) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != 0) {
            RadioButton radioButton = findViewById(selectedId);
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("gameMode", radioButton.getText().toString().trim());
            startActivity(intent);
        }
    }

    public void viewHighScore(View view) {
        Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
        startActivity(intent);
    }
}