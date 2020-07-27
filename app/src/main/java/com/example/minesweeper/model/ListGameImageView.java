package com.example.minesweeper.model;

import java.util.ArrayList;
import java.util.List;

public class ListGameImageView {
    List<GameImageView> gameImageViews;
    List<Bomb> bombs;
    List<GameImageView> gameImageViewsNotBomb;

    public List<GameImageView> getGameImageViewsNotBomb() {
        return gameImageViewsNotBomb;
    }

    public ListGameImageView(List<Bomb> bombs) {
        this.gameImageViewsNotBomb = new ArrayList<>();
        this.gameImageViews = new ArrayList<>();
        this.bombs = bombs;
    }

    public int addGameButton(GameImageView gameImageView) {
        gameImageViews.add(addValueForButton(gameImageView));
        return gameImageView.getValue();
    }

    public GameImageView searchGameButton(int x, int y) {
        for (GameImageView gameImageView : gameImageViews) {
            if (gameImageView.getCoordinateX() == x && gameImageView.getCoordinateY() == y)
                return gameImageView;
        }
        return null;
    }

    private GameImageView addValueForButton(GameImageView gameImageView) {
        int value = 0;
        for (Bomb b : bombs) {
            if (b.getX() == gameImageView.getCoordinateX() && b.getY() == gameImageView.getCoordinateY()) {
                gameImageView.setValue(-1);
                return gameImageView;
            }
            else if (gameImageView.getCoordinateX() == b.getX() && gameImageView.getCoordinateY() == (b.getY() + 1))
                value++;
            else if (gameImageView.getCoordinateX() == (b.getX() - 1) && gameImageView.getCoordinateY() == (b.getY() + 1))
                value++;
            else if (gameImageView.getCoordinateX() == (b.getX() - 1) && gameImageView.getCoordinateY() == b.getY())
                value++;
            else if (gameImageView.getCoordinateX() == (b.getX() - 1) && gameImageView.getCoordinateY() == (b.getY() - 1))
                value++;
            else if (gameImageView.getCoordinateX() == b.getX() && gameImageView.getCoordinateY() == (b.getY() - 1))
                value++;
            else if (gameImageView.getCoordinateX() == (b.getX() + 1) && gameImageView.getCoordinateY() == (b.getY() - 1))
                value++;
            else if (gameImageView.getCoordinateX() == (b.getX() + 1) && gameImageView.getCoordinateY() == b.getY())
                value++;
            else if (gameImageView.getCoordinateX() == (b.getX() + 1) && gameImageView.getCoordinateY() == (b.getY() + 1))
                value++;
        }
        gameImageView.setValue(value);
        gameImageViewsNotBomb.add(gameImageView);
        return gameImageView;
    }

    public void clear() {
        gameImageViews.clear();
        bombs.clear();
        gameImageViewsNotBomb.clear();
    }
}
