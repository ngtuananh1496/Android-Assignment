package com.example.minesweeper.model;

public class Bomb {
    private int x;
    private int y;

    public Bomb() {
    }

    public boolean compare(Bomb bomb){
        if(this.x == bomb.getX() && this.y == bomb.getY())
            return true;
        return false;
    }

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
