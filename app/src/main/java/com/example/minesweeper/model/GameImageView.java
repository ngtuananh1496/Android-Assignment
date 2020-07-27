package com.example.minesweeper.model;

import android.content.Context;
import android.util.AttributeSet;

public class GameImageView extends androidx.appcompat.widget.AppCompatImageView {

    private int coordinateX;
    private int coordinateY;
    private int value; //value = -1 is a bomb
    private boolean isShowed = false;
    private boolean isFlagged = false;

    public GameImageView(Context context) {
        super(context);
    }

    public GameImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(int coordinateX) {
        this.coordinateX = coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(int coordinateY) {
        this.coordinateY = coordinateY;
    }

    public int getValue() {
        return value;
    }

    public boolean isShowed() {
        return isShowed;
    }

    public void changeStatus() {
        this.isShowed = true;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }
}
