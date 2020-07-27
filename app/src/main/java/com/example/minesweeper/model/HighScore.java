package com.example.minesweeper.model;

public class HighScore {

    private int id;
    private int gameMode; // 1-easy, 2-medium, 3-expert
    private int time;

    public HighScore() {
    }

    public HighScore(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
