package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class PieChart extends Activity {
    // "Create data base" variable
    SQLiteDatabase db;

    float numRowsWin;
    float numRowsLose;
    float numRowsDraw;

    float WinRate;
    float LoseRate;
    float DrawRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Panel(this));



        try {

            db = SQLiteDatabase.openDatabase("/data/data/com.example.tic_tac_toe/YourRecordsDB", null, SQLiteDatabase.OPEN_READWRITE);

            numRowsWin = (float) DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM GamesLog WHERE winningStatus = 'Win'", null);
            System.out.println(numRowsWin);

            numRowsLose = (float) DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM GamesLog WHERE winningStatus = 'Lose'", null);
            System.out.println(numRowsLose);

            numRowsDraw = (float) DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM GamesLog WHERE winningStatus = 'Draw'", null);
            System.out.println(numRowsDraw);

            // get the Win Lose Draw rate.
            WinRate = ((numRowsWin) / (numRowsWin + numRowsLose + numRowsDraw))*100;
            System.out.println(WinRate);
            LoseRate = ((numRowsLose) / (numRowsWin + numRowsLose + numRowsDraw))*100;
            System.out.println(LoseRate);
            DrawRate = ((numRowsDraw) / (numRowsWin + numRowsLose + numRowsDraw))*100;
            System.out.println(DrawRate);

            db.close();
        }catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        System.out.println(WinRate);

    }

    class Panel extends View {
        public Panel (Context context){
            super(context);
        }
        String title = "Win Status";
        String items[] = {"Win", "Lose", "Draw"};
        //float data[] = {(float) WinRate, (float)LoseRate, (float)DrawRate};
//        int rColor[] = {0xffff0000, 0xffffff00, 0xff32cd32};
        int rColor[] = {0xff1E5DA5, 0xffC898FF, 0xff331A9B};
        //{0xffff0000, 0xffffff00, 0xff32cd32, 0xff880055};
        float cDegree = 0;

        public void onDraw(Canvas c){

            float data[] = {(float) WinRate, (float)LoseRate, (float)DrawRate};
            super.onDraw(c);
            Paint paint = new Paint();

            paint.setColor(Color.WHITE);
            c.drawPaint(paint);

            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            for (int i = 0;i<data.length;i++){
                float drawDegree = data[i] * 360/100;

                paint.setColor(rColor[i]);
                // 600 left right
                // 600 top bottom
                RectF rec = new RectF(100,700,900,1500);
                //RectF rec = new RectF(75,150,800,900);
                c.drawArc(rec,cDegree,drawDegree,true,paint);
                cDegree += drawDegree;
            }

            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(150);
            c.drawText(title,180,160,paint);

            // height (bar)
            int vSpace = getHeight()-1200;
            paint.setTextSize(60);
            for (int i = items.length-1;i>=0;i--){
                paint.setColor(rColor[i]);
                // color (bar)
                c.drawRect(getWidth()-260,vSpace,getWidth()-220,vSpace+40,paint);


                paint.setColor(Color.BLACK);
                // Text (bar)
                c.drawText(items[i],getWidth()-200,vSpace+30,paint);
                // padding between text
                vSpace-=100;
            }
        }
    }
}
