package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.minesweeper.SQLController.DatabaseHandler;
import com.example.minesweeper.model.HighScore;

import java.util.ArrayList;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {

    private Button btnEasy;
    private Button btnMedium;
    private Button btnExpert;
    private TableLayout highScoreLayout;
    private DatabaseHandler databaseHandler;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        btnEasy = findViewById(R.id.btnEasy);
        btnMedium = findViewById(R.id.btnMedium);
        btnExpert = findViewById(R.id.btnExpert);
        highScoreLayout = findViewById(R.id.highScoreLayout);
        txtTitle = findViewById(R.id.txtTitle);
    }

    public void onClickEasy(View view) {
        highScoreLayout.removeAllViews();
        txtTitle.setText("EASY");
        getHighScore(1);
    }

    public void onClickMedium(View view) {
        highScoreLayout.removeAllViews();
        txtTitle.setText("MEDIUM");
        getHighScore(2);
    }

    public void onClickExpert(View view) {
        highScoreLayout.removeAllViews();
        txtTitle.setText("EXPERT");
        getHighScore(3);
    }

    public void onClickBackMenu(View view) {
        txtTitle.setText("");
        highScoreLayout.removeAllViews();
        Intent intent = new Intent(HighScoreActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void getHighScore(int gameMode) {
        databaseHandler = new DatabaseHandler(this, null, null, 1);
        List<HighScore> highScores = new ArrayList<>();
        highScores = databaseHandler.findHighScores(gameMode);
        if (highScores.size() > 0) {
            TableRow tableRow = new TableRow(this);
            tableRow.setGravity(Gravity.CENTER);
            TextView txtId = new TextView(this);
            txtId.setText("No.");
            txtId.setTextSize(18);
            txtId.setPadding(20,8,20,8);
            tableRow.addView(txtId);
            TextView txtTime = new TextView(this);
            txtTime.setText("Time");
            txtTime.setTextSize(18);
            txtTime.setPadding(20,8,20,8);
            tableRow.addView(txtTime);
            highScoreLayout.addView(tableRow);
            for (int i = 0; i < highScores.size(); i++) {
                HighScore hs = highScores.get(i);
                TableRow tableRow0 = new TableRow(this);
                tableRow0.setGravity(Gravity.CENTER);
                TextView textView0 = new TextView(this);
                textView0.setText((i + 1) + "");
                textView0.setTextSize(18);
                textView0.setPadding(20,8,20,8);
                tableRow0.addView(textView0);
                TextView textView1 = new TextView(this);
                textView1.setText("" + hs.getTime());
                textView1.setTextSize(18);
                textView1.setPadding(20,8,20,8);
                tableRow0.addView(textView1);
                highScoreLayout.addView(tableRow0);
            }
        }
    }
}