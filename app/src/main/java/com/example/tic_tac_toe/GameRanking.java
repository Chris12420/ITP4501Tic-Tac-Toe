package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameRanking extends AppCompatActivity {
    TextView textViewUrl;
    TextView textViewResult;
    Button buttonGo;
    ListView list;
    String[] listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ranking);
        //store references of UI components
        //textViewUrl = findViewById(R.id.textViewUrl);
        textViewResult = findViewById(R.id.textViewResult);
        list = findViewById(R.id.listViewCode);

        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        InputStream inputStream = null;
        String result = "";


        try {
            // execute http request
            URL url = new URL(getResources().getString(R.string.ranking_code_url));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //Make GET request
            con.setRequestMethod("GET"); //May omit this line since "GET" is the default.
            con.connect();

            //GET response string from inputstream of the connection
            inputStream = con.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            System.out.println(line);
            inputStream.close();

            //parse json to get json codes
            JSONArray json = new JSONArray(result);
            listItems = new String[json.length()];


            // Given Rank array arr[]
            int[] arr = new int[json.length()];

            for (int i = 0; i < json.length(); i++) {
                //System.out.println(Integer.parseInt(json.getJSONObject(i).getString(("Duration"))));
                arr[i] = Integer.parseInt(json.getJSONObject(i).getString(("Duration")));
            }

            System.out.println(Arrays.toString(arr));

            // Function Call
            changeArr(arr);

//            // Print the array elements
//            System.out.println(Arrays.toString(arr));


            //json[104, 25, 38, 117, 23, 49, 18, 68, 93, 78]
            //arr[9, 3, 4, 10, 2, 5, 1, 6, 8, 7]
            for (int i = 0; i < json.length(); i++) {
                System.out.println(json.length());

                listItems[arr[i] - 1] = "Rank " + arr[i] + ", ";
                listItems[arr[i] - 1] += json.getJSONObject(i).getString(("Name")) + ", ";
                listItems[arr[i] - 1] += json.getJSONObject(i).getString(("Duration")) + " sec";
            }
            list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems));
            //list.setOnItemClickListener(this);
        } catch (Exception e) {
            textViewResult.setText(e.getMessage());
        }
    }

    public static void changeArr(int[] input) {
        // Copy input array into newArray
        int newArray[]
                = Arrays
                .copyOfRange(input,
                        0,
                        input.length);

        // Sort newArray[] in ascending order
        Arrays.sort(newArray);
        int i;

        // Map to store the rank of
        // the array element
        Map<Integer, Integer> ranks
                = new HashMap<>();

        int rank = 1;

        for (int index = 0;
             index < newArray.length;
             index++) {

            int element = newArray[index];

            // Update rank of element
            if (ranks.get(element) == null) {

                ranks.put(element, rank);
                rank++;
            }
        }

        // Assign ranks to elements
        for (int index = 0;
             index < input.length;
             index++) {

            int element = input[index];
            input[index]
                    = ranks.get(input[index]);

        }
    }
}