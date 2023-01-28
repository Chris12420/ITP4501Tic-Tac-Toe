package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import java.util.Arrays;

public class YourRecords extends Activity implements AdapterView.OnItemClickListener {

    private ListView list;
    private String[] items;
    private Button btnShowChart;
    private static final int REQUEST_CODE = 1234;


    // "Create data base" variable
    SQLiteDatabase db;
    Cursor cursor = null;
    String dataStrHeader = String.format("%4s %-12s %-9s %3s\n", "Mid", "Name", "Password", "Age");
    String dataStr;
    String[] columns = {"gameID", "playDate", "playTime", "duration", "winningStatus"};
    long numRows;
    long numRow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_records);

        list = findViewById(R.id.list);
        btnShowChart = findViewById(R.id.btnShowChart);

        try {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.tic_tac_toe/YourRecordsDB", null, SQLiteDatabase.OPEN_READWRITE);


            numRows = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM GamesLog", null);
            int numRows2 = (int)numRows;
            items = new String[numRows2];
            System.out.println(numRows2);

            cursor = db.query("GamesLog", columns, null, null, null, null, null);
            dataStr = dataStrHeader;
            int i = 0;
            while (cursor.moveToNext()){
                int gameID = cursor.getInt(cursor.getColumnIndex("gameID"));
                String playDate = cursor.getString(cursor.getColumnIndex("playDate"));
                String playTime = cursor.getString(cursor.getColumnIndex("playTime"));
                int duration = cursor.getInt(cursor.getColumnIndex("duration"));
                String winningStatus = cursor.getString(cursor.getColumnIndex("winningStatus"));
                //dataStr += String.format("%4d %-12s %-9s %3d\n",mid,name,password,age);
                dataStr = String.format("%s, %s, %s, %s sec", playDate,playTime,winningStatus,duration);
                items[i] = dataStr;
                i++;
            }
           numRow = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM GamesLog", null);
            db.close();
        }catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    public void btnClickShowChart(View view){
        Intent i = new Intent(this, PieChart.class);
        startActivityForResult(i, REQUEST_CODE);
    }
}



