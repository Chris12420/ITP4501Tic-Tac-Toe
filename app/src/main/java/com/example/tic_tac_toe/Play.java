package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

//public class Play extends AppCompatActivity implements View.OnClickListener {
public class Play extends AppCompatActivity implements View.OnClickListener {

    // "Play" variable
    int[] btnPlay = new int[]{R.id.btnPlay0, R.id.btnPlay1, R.id.btnPlay2, R.id.btnPlay3, R.id.btnPlay4, R.id.btnPlay5, R.id.btnPlay6, R.id.btnPlay7, R.id.btnPlay8};
    // set up onclick listener
    private Button[] buttons = new Button[9];
    private static Button[] btnPlayID = new Button[9];

    // check the button O or X
    // gameState{1} == the button is checked
    // gameState{2} == the button is not checked
    private static int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    private TextView timeRecord;
    private static long startTime = 0;

    // "Create data base" variable
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        timeRecord = (TextView) findViewById(R.id.timeRecord);

        // start recording time
        startTime = System.currentTimeMillis();

        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);
        btnContinue.setVisibility(View.GONE);

        for (int i = 0; i <= 8; i++) {
            btnPlayID[i] = findViewById(btnPlay[i]);
        }


        for (int i = 0; i < buttons.length; i++) {
            String buttonID = "btnPlay" + i;
            int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = (Button) findViewById(resourceID);
            buttons[i].setOnClickListener(this);
//        }
        }
    }

    @Override
    public void onClick(View v) {

        // get the onclick button id
        int id = v.getId();

        // "continue" button clicked
        if (id == R.id.btnContinue) {
            gameReStart();
            //make the cell can click again after finish the game
            for (int x = 0; x <= 8; x++) {
                btnPlayID[x].setEnabled(true);
            }
        }


        for (int i = 0; i <= 8; i++) {
            //check if it is the last cell remain
            boolean isLastCell = lastCellCheck(i, id);
            if (isLastCell) {
                btnPlayID[i].setText("O");
                break;
            }

            // select cell
            if (id == btnPlay[i]) {
                Log.i("test", "button is ok");
                if (btnPlayID[i].getText().equals("")) {
                    btnPlayID[i].setText("O");
                    gameState[i] = 1;   // gameState{1} == the button is checked

                    // when we win, don't add cell anymore
                    int result = 0;
                    int resultOutput = checkIfWin(result);
                    // prompt statement when win event occur
                    if (!(resultOutput == 1)) {
                        randomCpuPick();    // CPU go to pick random box
                    }
                }
            }

            // Check if we are the winner
            int result = 0;
            int resultOutput = checkIfWin(result);
            // prompt statement when win event occur
            // ------------Player win-----------
            if (resultOutput == 1) {
                Button btnContinue = findViewById(R.id.btnContinue);
                btnContinue.setVisibility(View.VISIBLE);
                //gameReStart();
                //make the button not clickable after finish the game
                for (int x = 0; x <= 8; x++) {
                    btnPlayID[x].setEnabled(false);
                }
                // show time recorder text
                long finishTime = System.currentTimeMillis();
                int elapsedTime = (int) (finishTime - startTime) / 1000;
                TextView tv = findViewById(R.id.timeRecord);
                //tv.setText("You Win! Duration " + elapsedTime + " sec!");
                tv.setText("You Win! Duration " + elapsedTime + " sec!");
                // play music
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bgm_wining_sound_effect);
                mp.start();
                // show win effect
                manageEffect();
                // show the textview
                timeRecord.setVisibility(View.VISIBLE);
                // reset time recorder
                //startTime = 0;
                startTime = System.currentTimeMillis();

                // add record to database after winning
                insertDBRecord(resultOutput, elapsedTime);
                break;
            }
            // ---------------CPU win----------------
            else if (resultOutput == 2) {
                Button btnContinue = findViewById(R.id.btnContinue);
                btnContinue.setVisibility(View.VISIBLE);

                //make the button not clickable after finish the game
                for (int x = 0; x <= 8; x++) {
                    btnPlayID[x].setEnabled(false);
                }
                // show time recorder text
                long finishTime = System.currentTimeMillis();
                int elapsedTime = (int) (finishTime - startTime) / 1000;
                TextView tv = findViewById(R.id.timeRecord);
                tv.setText("You lose! Duration: " + elapsedTime + " sec!");
                // play music
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bgm_lose_effect);
                mp.start();
                // show the textview
                timeRecord.setVisibility(View.VISIBLE);

                // reset time recorder
                startTime = System.currentTimeMillis();
                insertDBRecord(resultOutput, elapsedTime);
                break;
            }
        }
        // Check if there is no winner
        noWinner();
    }




    public void insertDBRecord(int PlayerWin, int elapsedTime) {
        try {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.tic_tac_toe/YourRecordsDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

            String sql = "CREATE TABLE " + "GamesLog" + " ("
                    + "gameID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "playDate" + " TEXT,"
                    + "playTime" + " TEXT,"
                    + "duration" + " INT,"
                    + "winningStatus" + " TEXT)";

            db.execSQL(sql);
        }
    catch(SQLiteException e) {
    }
        finally {
            // get current date
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            // get current time
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault());
            String currentDateandTime = currentTime.format(new Date());

            // win or lose
            String playerwinString = null;
            // 1 == Player win
            if (PlayerWin == 1) {
                playerwinString = "Win";
            }
            // 2 == CPU win
            else if (PlayerWin == 2) {
                playerwinString = "Lose";
            }
            // else == Draw
            else if (PlayerWin == 0) {
                playerwinString = "Draw";
            }


            //insert play Record to table "GamesLog" from database "YourRecordsDB"
            db = SQLiteDatabase.openDatabase("/data/data/com.example.tic_tac_toe/YourRecordsDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            ContentValues insertValues = new ContentValues();
            insertValues.put("playDate", date);
            insertValues.put("playTime", currentDateandTime);
            insertValues.put("duration", elapsedTime);
            insertValues.put("winningStatus", playerwinString);
            db.insert("GamesLog", null, insertValues);
            db.close();
        }
}





    public void noWinner() {
        int count = 0;
        boolean isLastCell = false;
        for (int j = 0; j <= 8; j++) {
            if (gameState[j] == 1) {
                count++;
                if (count == 9) {
                    Button btnContinue = findViewById(R.id.btnContinue);
                    btnContinue.setVisibility(View.VISIBLE);

                    //make the button not clickable after finish the game
                    for (int x = 0; x <= 8; x++){
                        btnPlayID[x].setEnabled(false);
                    }
                    // show "Draw" view
                    // show time recorder text
                    long finishTime = System.currentTimeMillis();
                    int elapsedTime = (int)(finishTime - startTime)/1000;
                    TextView tv = findViewById(R.id.timeRecord);
                    tv.setText("Draw! Duration " + elapsedTime + " sec!" );
                    // show the textview
                    timeRecord.setVisibility(View.VISIBLE);

                    //
                    int resultOutput = 0;
                    insertDBRecord(resultOutput, elapsedTime);

                    // Double confrime last button is correct, and no show error when last check button is win.
                    int result = 0;
                    resultOutput = checkIfWin(result);
                    // prompt statement when win event occur
                    if(resultOutput == 1){
                        //make the button not clickable after finish the game
                        for (int x = 0; x <= 8; x++){
                            btnPlayID[x].setEnabled(false);
                        }
                        // show time recorder text
                        finishTime = System.currentTimeMillis();
                        elapsedTime = (int)(finishTime - startTime)/1000;
                        //tv.setText("You Win! Duration " + elapsedTime + " sec!");
                        tv.setText("You Win! Duration " + elapsedTime + " sec!");
                        // play music
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bgm_wining_sound_effect);
                        mp.start();
                        // show win effect
                        manageEffect();
                        // show the textview
                        timeRecord.setVisibility(View.VISIBLE);

                        // reset time recorder
                        //startTime = 0;
                        startTime = System.currentTimeMillis();
                        break;
                    }

                    else if (resultOutput == 2){
                        //make the button not clickable after finish the game
                        for (int x = 0; x <= 8; x++){
                            btnPlayID[x].setEnabled(false);
                        }
                        // show time recorder text
                        finishTime = System.currentTimeMillis();
                        elapsedTime = (int)(finishTime - startTime)/1000;
                        tv.setText("You lose! Duration: " + elapsedTime + " sec!");
                        // show the textview
                        timeRecord.setVisibility(View.VISIBLE);
                        // reset time recorder
                        //startTime = 0;
                        startTime = System.currentTimeMillis();
                        break;
                    }
                    break;
                }
            }
        }
    }

    // winning textview shinning effect
    private void manageEffect() {
        timeRecord.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofInt(timeRecord, "backgroundColor", Color.WHITE, Color.BLUE,
                Color.WHITE);
        anim.setDuration(3000);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(Animation.ABSOLUTE);
        anim.start();
    }


    public void gameReStart() {
        for (int i = 0;i<=8;i++){
            btnPlayID[i].setText("");
            gameState[i] = 2;
        }
        //timeRecord.setVisibility(View.GONE);
        // show the textview
        timeRecord.setVisibility(View.INVISIBLE);

        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setVisibility(View.GONE);
    }

    public boolean lastCellCheck(int i,int id) {
        int count = 0;
        boolean isLastCell = false;
        for (int j = 0; j <= 8; j++) {
            if (gameState[j] == 1) {
                count++;
                if (count == 8) {
                    if (id == btnPlay[i]){
                        if (btnPlayID[i].getText().equals("")) {
                            //btnPlayID[i].setText("X");
                            gameState[i] = 1;
                            isLastCell = true;
                            break;
                        }
                }
                }
            }
        }
        return isLastCell;
    }


    public static void randomCpuPick(){
        int min = 0;
        int max = 8;

        // if gameState == {2}, then CPU can pick it.
        while (true){
            int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
            if(gameState[random_int] == 2){
                btnPlayID[random_int].setText("X");
                gameState[random_int] = 1;
                break;
            }
        }
    }

    public static int checkIfWin(int result) {
        if (btnPlayID[0].getText().equals("O") && btnPlayID[1].getText().equals("O") && btnPlayID[2].getText().equals("O") ||
                btnPlayID[3].getText().equals("O") && btnPlayID[4].getText().equals("O") && btnPlayID[5].getText().equals("O") ||
                btnPlayID[6].getText().equals("O") && btnPlayID[7].getText().equals("O") && btnPlayID[8].getText().equals("O") ||
                btnPlayID[0].getText().equals("O") && btnPlayID[3].getText().equals("O") && btnPlayID[6].getText().equals("O") ||
                btnPlayID[1].getText().equals("O") && btnPlayID[4].getText().equals("O") && btnPlayID[7].getText().equals("O") ||
                btnPlayID[2].getText().equals("O") && btnPlayID[5].getText().equals("O") && btnPlayID[8].getText().equals("O") ||
                btnPlayID[0].getText().equals("O") && btnPlayID[4].getText().equals("O") && btnPlayID[8].getText().equals("O") ||
                btnPlayID[2].getText().equals("O") && btnPlayID[4].getText().equals("O") && btnPlayID[6].getText().equals("O")){
            result = 1;
        } else if (btnPlayID[0].getText().equals("X") && btnPlayID[1].getText().equals("X") && btnPlayID[2].getText().equals("X") ||
                btnPlayID[3].getText().equals("X") && btnPlayID[4].getText().equals("X") && btnPlayID[5].getText().equals("X") ||
                btnPlayID[6].getText().equals("X") && btnPlayID[7].getText().equals("X") && btnPlayID[8].getText().equals("X") ||
                btnPlayID[0].getText().equals("X") && btnPlayID[3].getText().equals("X") && btnPlayID[6].getText().equals("X") ||
                btnPlayID[1].getText().equals("X") && btnPlayID[4].getText().equals("X") && btnPlayID[7].getText().equals("X") ||
                btnPlayID[2].getText().equals("X") && btnPlayID[5].getText().equals("X") && btnPlayID[8].getText().equals("X") ||
                btnPlayID[0].getText().equals("X") && btnPlayID[4].getText().equals("X") && btnPlayID[8].getText().equals("X") ||
                btnPlayID[2].getText().equals("X") && btnPlayID[4].getText().equals("X") && btnPlayID[6].getText().equals("X"))  {
            result = 2;
        }
            return result;
    }

}
//e.printStackTrace();
//Log.i("int", "button is ok");
//Toast.makeText(this,String.valueOf(startTime),Toast.LENGTH_SHORT).show();
//Toast.makeText(this, "e.getMessage()", Toast.LENGTH_LONG).show();