package com.tistory.starcue.cuetalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        setDB();

//        setIsfirst();

    }

    private void setDB() {
        databaseHandler.setDB(Splash.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();

        new Handler().postDelayed(() -> {
            Cursor cursor = sqLiteDatabase.rawQuery("select * from id", null);
            int i = cursor.getCount();
            if (i == 0) {
                String test = Integer.toString(i);
                Log.d("splash<<<: ", test);
                Intent intent = new Intent(Splash.this, LoginActivity.class);
                startActivity(intent);
            } else {
                String test = Integer.toString(i);
                Log.d("splash<<<: ", test);
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
            }
            cursor.close();
        }, 1200);

    }


//    private void setIsfirst() {
//        if (!isfirst) {
//            new Handler().postDelayed(() -> {
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putBoolean("checkfirst", true);
//                editor.apply();
//                Log.d("Splash>>>", "not first");
//                Intent intent = new Intent(Splash.this, LoginActivity.class);
//                startActivity(intent);
//            }, 1000);
//        } else {
//            new Handler().postDelayed(() -> {
//                Log.d("Splash>>>", "first");
//                Intent intent = new Intent(Splash.this, MainActivity.class);
//                startActivity(intent);
//            }, 1000);
//        }
//    }
}
