package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ClickPlayHandler(View view){
        Intent i = new Intent(this, Play.class);
        startActivityForResult(i, REQUEST_CODE);
    }
    public void ClickRankHandler(View view){
        Intent i = new Intent(this, GameRanking.class);
        startActivityForResult(i, REQUEST_CODE);
    }
    public void ClickYourRecords(View view){
        Intent i = new Intent(this, YourRecords.class);
        startActivityForResult(i, REQUEST_CODE);
    }
    public void ClickClose(View view){
        this.finishAffinity();
    }



}