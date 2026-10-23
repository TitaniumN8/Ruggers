package com.ruggers.front_page;

import static com.ruggers.R.id.GameList;
import static com.ruggers.R.id.Title;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.ruggers.R;
import com.ruggers.login.Login;
import com.ruggers.wallet.walletHome;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FrontPage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button refreshButton;
    private Button calendarButton;
    private Button walletButton;
    private Button signOutButton;
    private Context cont;
    private ArrayList<Fixture> fixtureArrayList;

    private String date;
    public static final String API_KEY = com.ruggers.BuildConfig.RAPIDAPI_KEY;
    public static final String API_HOST ="api-rugby.p.rapidapi.com";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cont = FrontPage.this;
        setContentView(R.layout.activity_front_page);
        //getAPI();
        fixtureArrayList = new ArrayList<>();
        //Refresh button
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Instantiate the RequestQueue.
                //getAPI();
                getFixtures();

            }
        });

        //Sign out button
        signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(cont, Login.class);
                cont.startActivity(intent);
                finish();
            }
        });

        //Calendar button
        calendarButton = findViewById(R.id.DateButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(cal.getTime());
        //Toast.makeText(FrontPage.this, date, Toast.LENGTH_SHORT).show();
        //date = "2023-02-04";

        //Wallet Button
        walletButton = findViewById(R.id.walletButton);
        walletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(cont, walletHome.class);
                cont.startActivity(intent);
            }
        });

        getFixtures();


    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c =Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,day);
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(c.getTime());
        //String outString = c.get(Calendar.MONTH) + "/" +c.get(Calendar.DAY_OF_MONTH);
        //calendarButton.setText(outString);
        date = dateString;
        Toast.makeText(FrontPage.this, date, Toast.LENGTH_SHORT).show();
    }

    protected void updateUI()
    {




        ListAdapter LA = new ListAdapter((Activity)cont, fixtureArrayList);
        try{
            ListView listView = findViewById(GameList);
            listView.setAdapter(LA);

        }catch (Exception ignored){}


    }

    public void getFixtures()
    {
        ArrayList<Fixture> upcoming = new ArrayList<Fixture>();
        //Get upcoming date as (YYYY-MM-DD) string
        //date = "2023-01-28";
        //Add it to api string
        //Hardcoded for now

        //Call API
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api-rugby.p.rapidapi.com/games?date="+date)  //+"&league=51&season=2023")
                .get()
                .addHeader("X-RapidAPI-Key", API_KEY)
                .addHeader("X-RapidAPI-Host", "api-rugby.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String teams = "";
                if (response.isSuccessful())
                {


                    String myResponse =  response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(myResponse);

                        //JSONArray jsonArray = jsonObject.getJSONArray("teams");
                        teams = jsonObject.toString();
                        Log.e("OUTPUT ------------------   -------------",teams);
                        String[] sp = teams.split("\"response\":");
                        teams = sp[1];
                        JSONArray jsonArray = new JSONArray(teams);
                        for(int i = 0; i<jsonArray.length();i++){

                           // Log.e("OUTPUT 1------------------   -------------",jsonArray.getJSONObject(i).getString("teams"));
                            Log.e("OUTPUT  Game ID?------------------   -------------",jsonArray.getJSONObject(i).getString("id"));
                            int ID = Integer.parseInt(jsonArray.getJSONObject(i).getString("id"));
                            String time = (jsonArray.getJSONObject(i).getString("date"));
                            JSONObject tems = new JSONObject(jsonArray.getJSONObject(i).getString("teams"));
                            //Log.e("OUTPUT 2------------------   -------------",tems.getString("home"));
                            JSONObject home = new JSONObject(tems.getString("home"));
                            JSONObject away = new JSONObject(tems.getString("away"));
                            Log.e("OUTPUT Team Name------------------   -------------",home.getString("name"));
                            Log.e("OUTPUT Team ID------------------   -------------",home.getString("id"));
                            //Log.e("OUTPUT ------------------   -------------",jsonArray.getJSONObject(i).getString("home"));
                            Fixture fix = new Fixture(ID,away.getString("name"), home.getString("name"),time,away.getString("logo"),home.getString("logo"));
                            upcoming.add(fix);
                        }
                        fixtureArrayList = upcoming;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String finalTeams = teams;
                    FrontPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TextView text = findViewById(Title);
                            // Log.e("OUTPUT ------------------   -------------",finalTeams);
                            // text.setText(myResponse);
                            //TextView text = findViewById(Title);
                           // text.setText(finalTeams);
                            updateUI();
                        }
                    });
                }
            }
        });

        //Log.e("outgoing array list ------------------   -------------",upcoming.get(0).team1Name +" vs" + upcoming.get(0).Team2Name);
        //return upcoming;
    }
    protected void getAPI() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api-rugby.p.rapidapi.com/games?date=2023-02-04&league=51&season=2023")
                .get()
                .addHeader("X-RapidAPI-Key", API_KEY)
                .addHeader("X-RapidAPI-Host", "api-rugby.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String teams = "";
                if (response.isSuccessful())
                {


                    String myResponse =  response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(myResponse);

                        //JSONArray jsonArray = jsonObject.getJSONArray("teams");
                        teams = jsonObject.toString();
                        String[] sp = teams.split("\"response\":");
                        teams = sp[1];
                        JSONArray jsonArray = new JSONArray(teams);
                        for(int i = 0; i<jsonArray.length();i++){
                            // Log.e("OUTPUT ------------------   -------------",jsonArray.getJSONObject(i).toString());
                            Log.e("OUTPUT 1------------------   -------------",jsonArray.getJSONObject(i).getString("teams"));
                            //Log.e("OUTPUT ------------------   -------------",jsonArray.getJSONObject(i).getString("id"));
                            JSONObject tems = new JSONObject(jsonArray.getJSONObject(i).getString("teams"));
                            Log.e("OUTPUT 2------------------   -------------",tems.getString("home"));
                            JSONObject home = new JSONObject(tems.getString("home"));
                            Log.e("OUTPUT 2------------------   -------------",home.getString("name"));
                            //Log.e("OUTPUT ------------------   -------------",jsonArray.getJSONObject(i).getString("home"));

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String finalTeams = teams;
                    FrontPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TextView text = findViewById(Title);
                            // Log.e("OUTPUT ------------------   -------------",finalTeams);
                            // text.setText(myResponse);
                            TextView text = findViewById(Title);
                            text.setText(finalTeams);
                        }
                    });
                }
            }
        });
    }
}