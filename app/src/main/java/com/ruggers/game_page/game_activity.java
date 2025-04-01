package com.ruggers.game_page;

import static com.ruggers.front_page.FrontPage.API_HOST;
import static com.ruggers.front_page.FrontPage.API_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ruggers.R;
import com.ruggers.front_page.Fixture;
import com.ruggers.front_page.ListAdapter;
import com.ruggers.login.Login;
import com.ruggers.wallet.Bet;
import com.ruggers.wallet.walletHome;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//24200

public class game_activity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    FirebaseDatabase database;
    int gameID;
    //Fixture fixture;
    Fixture parce;
    String status;
    OkHttpClient client;
    String detes;
    String gameTime;
    String username;
    private Context cont;
    //String sattusCheckString = "";
    String homeScore, awayScore;
    double oddsHome, oddsAway, oddsDraw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cont = game_activity.this;
        client = new OkHttpClient();
        setContentView(R.layout.activity_game_activity);
        Intent intent = getIntent();
        parce = intent.getParcelableExtra("game");
        gameTime = "";
        detes = "";
        homeScore = "0";
        awayScore = "0";
        oddsHome = 1;
        oddsAway = 1;
        oddsDraw = 1;
        Button AwayBet = findViewById(R.id.placeBetButtonAway);
        Button HomeBet = findViewById(R.id.placeBetButtonHome);
        Button DrawBet = findViewById(R.id.placeBetButtonDraw);
        TextView txt = findViewById(R.id.chosenOddsText);
        TextView det = findViewById(R.id.detesText);
        @SuppressLint("MissingInflatedId") Button redirect = findViewById(R.id.walletRedirectButton);
        det.setText(detes);
        txt.setText("ID"+ gameID);
        gameID = parce.getGameID();
        gameTime =parce.getTime();
        //getOdds(gameID);
        getGameDetails(gameID);
        username = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        getGameDetails(gameID);


        database = FirebaseDatabase.getInstance("https://rugby-ec35c-default-rtdb.europe-west1.firebasedatabase.app/");
        mDatabase = FirebaseDatabase.getInstance("https://rugby-ec35c-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cont, walletHome.class);
                cont.startActivity(intent);
            }
        });

        HomeBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeBet("Home", oddsHome);
            }
        });
        AwayBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeBet("Away", oddsAway);
            }
        });
        DrawBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeBet("Draw", oddsDraw);
            }
        });

    }

    public void getGameDetails(int id)
    {

        Request request = new Request.Builder()
                .url("https://api-rugby.p.rapidapi.com/odds?game="+id)  //+"&league=51&season=2023")
                .get()
                .addHeader("X-RapidAPI-Key", API_KEY)
                .addHeader("X-RapidAPI-Host", API_HOST)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(game_activity.this, "Error finding game", Toast.LENGTH_LONG);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful())
                {
                    String myResponse =  response.body().string();
                    Log.e("Testing new fetch?------------------   -------------",myResponse);
                    try {

                        Log.e("OUTPUT Get odds response-----------------   -------------",myResponse);
                        JSONObject jsonObject = new JSONObject(myResponse);
                        String conversion = jsonObject.toString();
                        String[] split = conversion.split("\"response\":");
                        conversion = split[1];
                        JSONArray jsonArray = new JSONArray(conversion);
                        //JSONObject bookies = new JSONObject(jsonArray.getJSONObject(0).getString("bookmakers"));
                        String books = jsonArray.getJSONObject(0).getString("bookmakers");
                        JSONArray bookies = new JSONArray(books);
                        int len =1;
                        if (bookies.length()<3)
                        {
                            len = bookies.length();
                        }
                        for(int i =0; i<len;i++) {
                            String bets = bookies.getJSONObject(i).getString("bets");
                            String name = bookies.getJSONObject(i).getString("name");
                            JSONArray finalBets = new JSONArray(bets);

                            String firstBet = finalBets.getJSONObject(0).getString("values");
                            JSONArray lastOne = new JSONArray(firstBet);
                            String homeDetes = lastOne.getJSONObject(0).getString("value") + ": " + lastOne.getJSONObject(0).getString("odd");
                            String drawDetes = lastOne.getJSONObject(1).getString("value") + ": " + lastOne.getJSONObject(1).getString("odd");
                            String awayDetes = lastOne.getJSONObject(2).getString("value") + ": " + lastOne.getJSONObject(2).getString("odd");
                            oddsHome = Double.parseDouble(lastOne.getJSONObject(0).getString("odd"));
                            oddsDraw = Double.parseDouble(lastOne.getJSONObject(1).getString("odd"));
                            oddsAway = Double.parseDouble(lastOne.getJSONObject(2).getString("odd"));
                            detes = detes+ name +":\n" +homeDetes + "\n" + drawDetes + "\n" + awayDetes + "\n \n";
                            Log.e("OUTPUT Bookies Object details-----------------   -------------", detes);
                        }

//                        JSONObject jsonObject = new JSONObject(myResponse);
//                        Log.e("Testing new fetch?------------------   -------------",myResponse);
//                        String conversion = jsonObject.toString();
//                        String[] split = conversion.split("\"response\":");
//                        conversion = split[1];
//                        JSONArray jsonArray = new JSONArray(conversion);
//                        Log.e("OUTPUT  Game ID?------------------   -------------",jsonArray.toString());
//                        String time = (jsonArray.getJSONObject(0).getString("time"));
//
//                        //Get Teams
//                        JSONObject tems = new JSONObject(jsonArray.getJSONObject(0).getString("teams"));
//                        JSONObject home = new JSONObject(tems.getString("home"));
//                        JSONObject away = new JSONObject(tems.getString("away"));
//
//                        //Get Status
//                        JSONObject stat = new JSONObject(jsonArray.getJSONObject(0).getString("status"));
//                        status = stat.getString("long");
//
//                        //Get scores
//
//                        fixture = new Fixture(gameID,away.getString("name"), home.getString("name"),time,away.getString("logo"),home.getString("logo"));
//                        JSONObject scores = new JSONObject(jsonArray.getJSONObject(0).getString("scores"));
//                        Log.e("OUTPUT  Game ID?------------------   -------------",scores.toString());

                    } catch (JSONException e) {
                        detes = "Unable to find odds details";

                        e.printStackTrace();
                    }
                    game_activity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //getOdds(id);
                          //  updateUI(fixture);
                            updateUI(parce);
                        }
                    });
                }
            }
        });



    }
//

//    public void getOdds(int id)
//    {
//       Log.e("HOST NAME  UUUUGGGh ------------", API_KEY);
//
//        Request request = new Request.Builder()
//                .url("https://api-rugby.p.rapidapi.com/odds?game=24200")//+id)
//                .get()
//                .addHeader("X-RapidAPI-Key", API_KEY)
//                .addHeader("X-RapidAPI-Host", API_HOST)
//                .build();
//        //Log.e("HEADERS ------------- WHYYYYY------",request.headers().toString());
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Toast.makeText(game_activity.this, "Error finding game", Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful())
//                {
//                    String myResponse = response.toString();
//                    try {
//                        Log.e("OUTPUT Get odds response-----------------   -------------",myResponse);
//                        JSONObject jsonObject = new JSONObject(myResponse);
//                        String conversion = jsonObject.toString();
//                        String[] split = conversion.split("\"response\":");
//                        conversion = split[1];
//                        JSONArray jsonArray = new JSONArray(conversion);
//                        JSONObject bookies = new JSONObject(jsonArray.getJSONObject(0).getString("bookmakers"));
//                        Log.e("OUTPUT Bookies Object details-----------------   -------------",bookies.toString());
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
//    }


    /*
    Place bet on home Team
     */
    public void placeBet(String team, double odds)
    {
        //Get input from from text box
        EditText innie = findViewById(R.id.betInputBox);
        try {
            float betValue = Float.valueOf(innie.getText().toString());

            Date currentTime = Calendar.getInstance().getTime();

            //Temporary hard coded values. Will need to be updated after a check is completed
            String outcome = "N/A";
            float winnings  = 0;

            String betString = "Money: " + betValue + ", Odds: " + odds + ", Team: "+ team + ", GameID: "+ gameID +", Time: " + gameTime + ", Current Time: " + currentTime.toString() + ", Winner: "+ outcome + ", Cash Back: " + winnings;
            Log.e("Bet details: ", betString);
            Bet bet = new Bet(betValue,winnings, odds, team, currentTime.toString(), gameTime, outcome, gameID) ;
            try {
                  mDatabase.child("bets").child(username).push().setValue(bet);
                Toast.makeText(getApplicationContext(),"Bet Placed successfully",Toast.LENGTH_LONG).show();

                Log.e("Successful bet", "WHAT");
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Unable to Place Bet",Toast.LENGTH_LONG).show();
                Log.e("Failed bet", e.toString());
            }
            //Bet newBet = Bet(betValue, team, "GameID", odds);
            //Create Bet
            //ConfirmBet
            //Link to betPage
        }
        catch(Exception f)
        {
            Log.e("Betting error", f.toString());
        }

    }

    public void updateUI(Fixture fix)
    {
        ArrayList<Fixture> fixtureArrayList= new ArrayList<Fixture>();
        fixtureArrayList.add(fix);
        ListAdapter LA = new ListAdapter(game_activity.this, fixtureArrayList);
        try{
            ListView listView = findViewById(R.id.singularList);
            listView.setAdapter(LA);
            TextView details = findViewById(R.id.chosenOddsText);
            details.setText("Status: "+ status + "\nHome score: "+homeScore+"\nAway Score: "+awayScore);
            Log.e(" -- - - - - -  - - - - - -- - \n - - - - -  - -\n - - - - -- - : ",homeScore);
            TextView det = findViewById(R.id.detesText);
            det.setText(detes);

        }catch (Exception ignored){}
    }
}