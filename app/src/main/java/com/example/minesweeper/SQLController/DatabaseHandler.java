package com.example.minesweeper.SQLController;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.minesweeper.model.HighScore;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "HighScore.db";
    private static final String TABLE_NAME = "HighScore";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_GAME_MODE = "game_mode"; // 1-easy, 2-medium, 3-expert
    private static final String COLUMN_HIGH_SCORE = "high_score";
    private ContentResolver resolver;


    public DatabaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, VERSION);
        resolver = context.getContentResolver();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_PRODUCT = "CREATE TABLE " + TABLE_NAME + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_GAME_MODE + " INTEGER, "
                + COLUMN_HIGH_SCORE + " INTEGER) ";
        sqLiteDatabase.execSQL(CREATE_TABLE_PRODUCT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long insertHighScore(HighScore highScore) {
        int highScoreSize = getCountHighScore(highScore.getGameMode());
        if (highScoreSize == 10) {
            return updateHighScore(highScore);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_GAME_MODE, highScore.getGameMode());
            contentValues.put(COLUMN_HIGH_SCORE, highScore.getTime());
            SQLiteDatabase db = getWritableDatabase();
            return db.insert(TABLE_NAME, null, contentValues);
        }

    }

    public List<HighScore> findHighScores(int gameMode) {
        List<HighScore> highScores = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_GAME_MODE + " = "
                + gameMode + " ORDER BY " + COLUMN_HIGH_SCORE + " ASC ";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            HighScore highScore = new HighScore();
            highScore.setId(cursor.getInt(0));
            highScore.setGameMode(cursor.getInt(1));
            highScore.setTime(cursor.getInt(2));
            highScores.add(highScore);
        }
        return highScores;
    }

    public HighScore findHighScore(int gameMode) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_GAME_MODE + " = "
                + gameMode + " ORDER BY " + COLUMN_HIGH_SCORE + " DESC ";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            HighScore highScore = new HighScore();
            highScore.setId(cursor.getInt(0));
            highScore.setGameMode(cursor.getInt(1));
            highScore.setTime(cursor.getInt(2));
            return highScore;
        }
        return null;
    }

    public int getCountHighScore(int gameMode) {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_GAME_MODE + " = " + gameMode;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) return cursor.getInt(0);
        return -1;
    }

    private int updateHighScore(HighScore highScore) {
        HighScore findHighScore = findHighScore(highScore.getGameMode());
        if (highScore != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_HIGH_SCORE, highScore.getTime());
            SQLiteDatabase db = getWritableDatabase();
            return db.update(TABLE_NAME, contentValues, COLUMN_GAME_MODE + " = "
                    + findHighScore.getGameMode() + " AND " + COLUMN_ID + " = " + findHighScore.getId(), null);
        }
        return -1;
    }

}
