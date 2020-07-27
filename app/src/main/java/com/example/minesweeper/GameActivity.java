package com.example.minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.example.minesweeper.SQLController.DatabaseHandler;
import com.example.minesweeper.model.Bomb;
import com.example.minesweeper.model.GameImageView;
import com.example.minesweeper.model.GameMode;
import com.example.minesweeper.model.HighScore;
import com.example.minesweeper.model.ListGameImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

public class GameActivity extends AppCompatActivity {

    private final int easyBombNumber = 12;
    private final int mediumBombNumber = 50;
    private final int expertBombNumber = 96;
    private int[] gameSize; //gameSize[0]: x, gameSize[1]: y, gameSize[2]: number of bombs
    private String gameMode = "";
    private ImageButton imageButton;
    private boolean buttonFlag = false;
    private LinearLayout gameLayout;
    private List<Bomb> bombs;
    private ListGameImageView listGameImageView;
    private boolean isGameOver = false;
    private TextView numberRemainingTxt;
    private TextView timerTxt;
    private boolean timerRun = true;
    private int timer = 0;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        databaseHandler = new DatabaseHandler(this, null, null, 1);
        imageButton = findViewById(R.id.imgBtnStatus);
        gameLayout = findViewById(R.id.gameLayout);
        numberRemainingTxt = findViewById(R.id.numberRemainTxt);
        timerTxt = findViewById(R.id.timerTxt);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gameMode = bundle.getString("gameMode");
            switch (gameMode.trim()) {
                case GameMode.EASY:
                    gameSize = new int[]{10, 10, easyBombNumber};
                    break;
                case GameMode.MEDIUM:
                    gameSize = new int[]{14, 20, mediumBombNumber};
                    break;
                case GameMode.EXPERT:
                    gameSize = new int[]{16, 30, expertBombNumber};
                    break;
            }
        }
        numberRemainingTxt.setText(gameSize[2] + "");

        addBomb(gameSize);
        listGameImageView = new ListGameImageView(bombs);
        createGame();
        createTimer();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            int data = bundle.getInt("timer");
            timerTxt.setText(String.valueOf(data));
        }
    };

    public void statusChange(View view) {
        if (!isGameOver) {
            if (!buttonFlag) {
                buttonFlag = true;
                imageButton.setImageResource(R.drawable.flagged);
            } else {
                buttonFlag = false;
                imageButton.setImageResource(R.drawable.bomb);
            }
        }
    }

    public void newGame(View view) {
        buttonFlag = true;
        timerRun = true;
        timer = 0;
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        listGameImageView.clear();
        bombs.clear();
        startActivity(intent);
    }

//    public void onClickPinchZoom(View view){
//        gameLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Animation zoomOut = AnimationUtils.loadAnimation(GameActivity.this, R.anim.zoom_out);
//                gameLayout.startAnimation(zoomOut);
//            }
//        });
//    }

    private void createGame() {
        for (int i = 0; i < gameSize[1]; i++) {
            LinearLayout linearLayout = new LinearLayout(GameActivity.this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 0);
            for (int j = 0; j < gameSize[0]; j++) {
                GameImageView gameImageView = new GameImageView(this);
                gameImageView.setCoordinateX(j);
                gameImageView.setCoordinateY(i);
                gameImageView.setLayoutParams(layoutParams);
                gameImageView.setImageResource(R.drawable.facing_down);
                gameImageView.setBackgroundColor(000000);
                gameImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(GameActivity.this,
//                                "x = "+ gameImageView.getCoordinateX() + " y = " + gameImageView.getCoordinateY() + " value = " + gameImageView.getValue(),
//                                Toast.LENGTH_LONG).show();
                        imageViewOnClick(gameImageView);
                    }
                });
                int value = listGameImageView.addGameButton(gameImageView);
                gameImageView.setValue(value);

                /* show all value of gameImageView use for test only  */
//                switch (gameImageView.getValue()){
//                    case 0:
//                        gameImageView.setImageResource(R.drawable.num0);
//                        break;
//                    case 1:
//                        gameImageView.setImageResource(R.drawable.num1);
//                        break;
//                    case 2:
//                        gameImageView.setImageResource(R.drawable.num2);
//                        break;
//                    case 3:
//                        gameImageView.setImageResource(R.drawable.num3);
//                        break;
//                    case 4:
//                        gameImageView.setImageResource(R.drawable.num4);
//                        break;
//                    case 5:
//                        gameImageView.setImageResource(R.drawable.num5);
//                        break;
//                    case 6:
//                        gameImageView.setImageResource(R.drawable.num6);
//                        break;
//                    case 7:
//                        gameImageView.setImageResource(R.drawable.num7);
//                        break;
//                    case 8:
//                        gameImageView.setImageResource(R.drawable.num8);
//                        break;
//                    case -1:
//                        gameImageView.setImageResource(R.drawable.boom);
//                        break;
//                }
                linearLayout.addView(gameImageView);
            }
            gameLayout.addView(linearLayout);
        }
    }

    private void imageViewOnClick(GameImageView gameImageView) {
        if (!gameImageView.isShowed() && !isGameOver) {
            if (buttonFlag) {
                int numberFlagRemaining = Integer.parseInt(numberRemainingTxt.getText().toString().trim());
                if (!gameImageView.isFlagged()) {
                    numberFlagRemaining--;
                    gameImageView.setImageResource(R.drawable.flagged);
                    gameImageView.setFlagged(true);
                } else {
                    numberFlagRemaining++;
                    gameImageView.setImageResource(R.drawable.facing_down);
                    gameImageView.setFlagged(false);
                }
                numberRemainingTxt.setText(numberFlagRemaining + "");
            } else if (!gameImageView.isFlagged()) {
                if (gameImageView.getValue() != -1) {
                    showValueImageView(gameImageView);
                } else {
                    gameImageView.setImageResource(R.drawable.boom);
                    isGameOver = true;
                    timerRun = false;
                    makeAlertDialog("Unfortunate!!!", "You loose");
                }
            }
        } else if (gameImageView.isShowed() && !isGameOver) {
            if (checkFlagAroundGameImageView(gameImageView))
                quickOpen(gameImageView);
        }
        if (checkWin()) {
            timerRun = false;
            long result = saveHighScoreToDb();
            makeAlertDialog("Congratulation!!!", "You win");
        }
    }

    private void openImageViewWithZeroValue(GameImageView gameImageView) {
        gameImageView.setImageResource(R.drawable.num0);
        int x = gameImageView.getCoordinateX();
        int y = gameImageView.getCoordinateY();
        gameImageView.changeStatus();

        GameImageView gameImageView1 = listGameImageView.searchGameButton(x, (y - 1));
        if (gameImageView1 != null)
            if (!gameImageView1.isShowed() && !gameImageView1.isFlagged()) {
                if (gameImageView1.getValue() != -1) {
                    showValueImageView(gameImageView1);
                }
            }

        GameImageView gameImageView2 = listGameImageView.searchGameButton((x + 1), (y - 1));
        if (gameImageView2 != null)
            if (!gameImageView2.isShowed() && !gameImageView2.isFlagged()) {
                if (gameImageView1.getValue() != -1) {
                    showValueImageView(gameImageView2);
                }
            }

        GameImageView gameImageView3 = listGameImageView.searchGameButton((x + 1), y);
        if (gameImageView3 != null)
            if (!gameImageView3.isShowed() && !gameImageView3.isFlagged()) {
                if (gameImageView3.getValue() != -1) {
                    showValueImageView(gameImageView3);
                }
            }

        GameImageView gameImageView4 = listGameImageView.searchGameButton((x + 1), (y + 1));
        if (gameImageView4 != null)
            if (!gameImageView4.isShowed() && !gameImageView4.isFlagged()) {
                if (gameImageView4.getValue() != -1) {
                    showValueImageView(gameImageView4);
                }
            }

        GameImageView gameImageView5 = listGameImageView.searchGameButton(x, (y + 1));
        if (gameImageView5 != null)
            if (!gameImageView5.isShowed() && !gameImageView5.isFlagged()) {
                if (gameImageView5.getValue() != -1) {
                    showValueImageView(gameImageView5);
                }
            }

        GameImageView gameImageView6 = listGameImageView.searchGameButton((x - 1), (y + 1));
        if (gameImageView6 != null)
            if (!gameImageView6.isShowed() && !gameImageView6.isFlagged()) {
                if (gameImageView6.getValue() != -1) {
                    showValueImageView(gameImageView6);
                }
            }

        GameImageView gameImageView7 = listGameImageView.searchGameButton((x - 1), y);
        if (gameImageView7 != null)
            if (!gameImageView7.isShowed() && !gameImageView7.isFlagged()) {
                if (gameImageView7.getValue() != -1) {
                    showValueImageView(gameImageView7);
                }
            }

        GameImageView gameImageView8 = listGameImageView.searchGameButton((x - 1), (y - 1));
        if (gameImageView8 != null)
            if (!gameImageView8.isShowed() && !gameImageView8.isFlagged()) {
                if (gameImageView8.getValue() != -1) {
                    showValueImageView(gameImageView8);
                }
            }
    }

    private boolean checkWin() {
        for (GameImageView gameImageView : listGameImageView.getGameImageViewsNotBomb()) {
            if (!gameImageView.isShowed()) return false;
        }
        return true;
    }

    private void showValueImageView(GameImageView gameImageView) {
        switch (gameImageView.getValue()) {
            case 0:
                openImageViewWithZeroValue(gameImageView);
                break;
            case 1:
                gameImageView.setImageResource(R.drawable.num1);
                break;
            case 2:
                gameImageView.setImageResource(R.drawable.num2);
                break;
            case 3:
                gameImageView.setImageResource(R.drawable.num3);
                break;
            case 4:
                gameImageView.setImageResource(R.drawable.num4);
                break;
            case 5:
                gameImageView.setImageResource(R.drawable.num5);
                break;
            case 6:
                gameImageView.setImageResource(R.drawable.num6);
                break;
            case 7:
                gameImageView.setImageResource(R.drawable.num7);
                break;
            case 8:
                gameImageView.setImageResource(R.drawable.num8);
                break;
        }
        gameImageView.changeStatus();
    }

    private void addBomb(int[] gameSize) {
        bombs = new ArrayList<>();
        for (int i = 0; i < gameSize[2]; i++) {
            Random random = new Random();
            int x = random.nextInt(gameSize[0]);
            int y = random.nextInt(gameSize[1]);
            Bomb bomb = new Bomb(x, y);
            boolean checkBomb = true;
            for (Bomb b : bombs) {
                if (b.compare(bomb)) {
                    i--;
                    checkBomb = false;
                    break;
                }
            }
            if (checkBomb) bombs.add(bomb);
        }
    }

    private void quickOpen(GameImageView gameImageView) {
        int x = gameImageView.getCoordinateX();
        int y = gameImageView.getCoordinateY();

        GameImageView gameImageView1 = listGameImageView.searchGameButton(x, (y - 1));
        if (gameImageView1 != null && !gameImageView1.isShowed() && !gameImageView1.isFlagged()) {
            if (gameImageView1.getValue() == -1) {
                gameImageView1.setImageResource(R.drawable.boom);
                isGameOver = true;
                makeAlertDialog("Unfortunate!!!", "You loose");
            } else showValueImageView(gameImageView1);
        }

        GameImageView gameImageView2 = listGameImageView.searchGameButton((x + 1), (y - 1));
        if (gameImageView2 != null && !gameImageView2.isShowed() && !gameImageView2.isFlagged()) {
            if (gameImageView2.getValue() == -1) {
                gameImageView2.setImageResource(R.drawable.boom);
                isGameOver = true;
                makeAlertDialog("Unfortunate!!!", "You loose");
            } else showValueImageView(gameImageView2);
        }

        GameImageView gameImageView3 = listGameImageView.searchGameButton((x + 1), y);
        if (gameImageView3 != null && !gameImageView3.isShowed() && !gameImageView3.isFlagged()) {
            if (gameImageView3.getValue() == -1) {
                gameImageView3.setImageResource(R.drawable.boom);
                isGameOver = true;
                makeAlertDialog("Unfortunate!!!", "You loose");
            } else showValueImageView(gameImageView3);
        }

        GameImageView gameImageView4 = listGameImageView.searchGameButton((x + 1), (y + 1));
        if (gameImageView4 != null && !gameImageView4.isShowed() && !gameImageView4.isFlagged()) {
            if (gameImageView4.getValue() == -1) {
                gameImageView4.setImageResource(R.drawable.boom);
                isGameOver = true;
                makeAlertDialog("Unfortunate!!!", "You loose");
            } else showValueImageView(gameImageView4);
        }

        GameImageView gameImageView5 = listGameImageView.searchGameButton(x, (y + 1));
        if (gameImageView5 != null && !gameImageView5.isShowed() && !gameImageView5.isFlagged()) {
            if (gameImageView5.getValue() == -1) {
                gameImageView5.setImageResource(R.drawable.boom);
                isGameOver = true;
                makeAlertDialog("Unfortunate!!!", "You loose");
            } else showValueImageView(gameImageView5);
        }

        GameImageView gameImageView6 = listGameImageView.searchGameButton((x - 1), (y + 1));
        if (gameImageView6 != null && !gameImageView6.isShowed() && !gameImageView6.isFlagged()) {
            if (gameImageView6.getValue() == -1) {
                gameImageView6.setImageResource(R.drawable.boom);
                isGameOver = true;
                makeAlertDialog("Unfortunate!!!", "You loose");
            } else showValueImageView(gameImageView6);
        }

        GameImageView gameImageView7 = listGameImageView.searchGameButton((x - 1), y);
        if (gameImageView7 != null && !gameImageView7.isShowed() && !gameImageView7.isFlagged()) {
            if (gameImageView7.getValue() == -1) {
                gameImageView7.setImageResource(R.drawable.boom);
                isGameOver = true;
                makeAlertDialog("Unfortunate!!!", "You loose");
            } else showValueImageView(gameImageView7);
        }

        GameImageView gameImageView8 = listGameImageView.searchGameButton((x - 1), (y - 1));
        if (gameImageView8 != null && !gameImageView8.isShowed() && !gameImageView8.isFlagged()) {
            if (gameImageView8.getValue() == -1) {
                gameImageView8.setImageResource(R.drawable.boom);
                isGameOver = true;
                makeAlertDialog("Unfortunate!!!", "You loose");
            } else showValueImageView(gameImageView8);
        }
    }

    private boolean checkFlagAroundGameImageView(GameImageView gameImageView) {
        int count = 0;
        int x = gameImageView.getCoordinateX();
        int y = gameImageView.getCoordinateY();

        GameImageView gameImageView1 = listGameImageView.searchGameButton(x, (y - 1));
        if (gameImageView1 != null && !gameImageView1.isShowed() && gameImageView1.isFlagged()) {
            count++;
        }

        GameImageView gameImageView2 = listGameImageView.searchGameButton((x + 1), (y - 1));
        if (gameImageView2 != null && !gameImageView2.isShowed() && gameImageView2.isFlagged()) {
            count++;
        }

        GameImageView gameImageView3 = listGameImageView.searchGameButton((x + 1), y);
        if (gameImageView3 != null && !gameImageView3.isShowed() && gameImageView3.isFlagged()) {
            count++;
        }

        GameImageView gameImageView4 = listGameImageView.searchGameButton((x + 1), (y + 1));
        if (gameImageView4 != null && !gameImageView4.isShowed() && gameImageView4.isFlagged()) {
            count++;
        }

        GameImageView gameImageView5 = listGameImageView.searchGameButton(x, (y + 1));
        if (gameImageView5 != null && !gameImageView5.isShowed() && gameImageView5.isFlagged()) {
            count++;
        }

        GameImageView gameImageView6 = listGameImageView.searchGameButton((x - 1), (y + 1));
        if (gameImageView6 != null && !gameImageView6.isShowed() && gameImageView6.isFlagged()) {
            count++;
        }

        GameImageView gameImageView7 = listGameImageView.searchGameButton((x - 1), y);
        if (gameImageView7 != null && !gameImageView7.isShowed() && gameImageView7.isFlagged()) {
            count++;
        }

        GameImageView gameImageView8 = listGameImageView.searchGameButton((x - 1), (y - 1));
        if (gameImageView8 != null && !gameImageView8.isShowed() && gameImageView8.isFlagged()) {
            count++;
        }

        if (gameImageView.getValue() == count) return true;
        else return false;
    }

    private void makeAlertDialog(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setMessage(content);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                buttonFlag = true;
                timer = 0;
                timerRun = false;
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                listGameImageView.clear();
                bombs.clear();
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void createTimer() {
        Runnable runnable = () -> {
            while (timerRun) {
                synchronized (this) {
                    try {
                        wait(1000);
                        timer++;
                        if (timerRun) {
                            Message message = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putInt("timer", timer);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    private long saveHighScoreToDb() {
        HighScore highScore = new HighScore();
        int countHighScore = 0;
        switch (gameMode.trim()) {
            case GameMode.EASY:
                highScore.setGameMode(1);
                countHighScore = databaseHandler.getCountHighScore(1);
                break;
            case GameMode.MEDIUM:
                highScore.setGameMode(2);
                countHighScore = databaseHandler.getCountHighScore(2);
                break;
            case GameMode.EXPERT:
                highScore.setGameMode(3);
                countHighScore = databaseHandler.getCountHighScore(3);
                break;
        }
        highScore.setTime(timer);
        highScore.setId((countHighScore + 1));
        return databaseHandler.insertHighScore(highScore);
    }
}