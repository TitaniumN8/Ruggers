package com.ruggers.wallet;

import static com.ruggers.R.id.walletBetListview;
import static com.ruggers.front_page.FrontPage.API_HOST;
import static com.ruggers.front_page.FrontPage.API_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ruggers.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class walletHome extends AppCompatActivity {

    private TextView winningsText;
    private TextView depositText;
    private TextView balanceText;
    private Button refreshButton;
    private DatabaseReference mDatabase;
    FirebaseDatabase database;
    String username;
    ArrayList<String> betIds = new ArrayList<>();
    ArrayList<Bet> betValues = new ArrayList<>();
    private Context cont;
    OkHttpClient client;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cont = walletHome.this;
        setContentView(R.layout.activity_wallet_home);
        winningsText = findViewById(R.id.betReturnsTextView);
        depositText = findViewById(R.id.depositTextView);
        balanceText = findViewById(R.id.balanceTextView);
        username = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance("https://rugby-ec35c-default-rtdb.europe-west1.firebasedatabase.app/");
        mDatabase = FirebaseDatabase.getInstance("https://rugby-ec35c-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("bets").child(username);
        refreshButton = findViewById(R.id.refreshWalletbtn);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });
        client = new OkHttpClient();

        getBets();

        //Populate List with all of user's bets
            //Get username
                //input all bets into adapter

        //Loop through all bets
            //Check if outcome == N/a
                //Check date of bet
                    //Get winner from gameID, check outcome, update winnings and outcome in database


    }


    public void getBets()
    {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    betIds.clear();
                    betValues.clear();
                    Toast.makeText(getApplicationContext(),snapshot.getValue().toString() ,Toast.LENGTH_LONG).show();
                    Log.e("Fetch outcome: ",snapshot.getValue().toString() );
                    for (DataSnapshot betSnapshot : snapshot.getChildren()) {
                        Map<String, Object> data = (Map<String, Object>) betSnapshot.getValue();
                        Log.d("DEBUG_DATA", "Raw gameID: " + data.get("gameID"));
                        Gson gson = new Gson();
                        String json = gson.toJson(data);
                        Log.d("DEBUG_JSON", "Converted JSON: " + json);
                        Bet bet = gson.fromJson(json, Bet.class);
                        Log.d("BET_OBJECT", bet.toString());
                        //Game ID is stored as an integer locally, but as a long on firebase
                        bet.GameID = ((Number) data.get("gameID")).intValue();
                        String id = betSnapshot.getKey();  // Extract the ID
                        Log.e("Stored IDs:  ",id);
                        betIds.add(id);
                        String value = betSnapshot.getValue().toString();

                        betValues.add(bet);
                        Log.e("Stored Bet Details:  ",bet.detailsOut());
                    }
                    for (int i = 0; i <betIds.size();i++) {
                        Log.d("FirebaseData", "Bet ID: " + betIds.get(i) + ": -  "+ betValues.get(i));

                    }
                    updateBets();

                }
                else {
                    Toast.makeText(getApplicationContext(),"Wrong Key",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void updateUI()
    {
        WalletAdapter WA = new WalletAdapter((Activity)cont, betValues);
        try{
            ListView listView = findViewById(walletBetListview);
            listView.setAdapter(WA);

        }catch (Exception ignored){}
        float TotalWinnings = 0 ;
        float TotalSpent = 0;
        for (int i =0; i<betValues.size();i++)
        {
            TotalSpent = TotalSpent + betValues.get(i).betPlaced;
            TotalWinnings = TotalWinnings + betValues.get(i).winnings;
        }
        winningsText.setText("Bet returns: "+ TotalWinnings);
        depositText.setText("Money Deposited: "+ TotalSpent);
        float bal = TotalWinnings - TotalSpent;
        balanceText.setText("Balance: "+bal);


    }

    public void updateBets()
    {
        //Loop through bets if outcome is not known, get GameID's.
        //check API for game winners
        //loop through betIDs and update firebase
        for (int i = 0; i<betValues.size();i++)
        {
            float winnings =(float) (betValues.get(i).odds * betValues.get(i).betPlaced);
            String betTeam = betValues.get(i).teamName;
            String recordID = betIds.get(i);
            double odds = betValues.get(i).odds;
            double placed = betValues.get(i).betPlaced;


                //Log.e("Outcome Value : ",betValues.get(i).outcome);


                String outcome;
                //Check API
                int GameID = betValues.get(i).GameID;
                //Run ID through API - check if winners = home or away
                Log.e("GameID - ", String.valueOf(GameID));
                Request request = new Request.Builder()
                        .url("https://api-rugby.p.rapidapi.com/games?id="+GameID)  //+"&league=51&season=2023")
                        .get()
                        .addHeader("X-RapidAPI-Key", API_KEY)
                        .addHeader("X-RapidAPI-Host", API_HOST)
                        .build();
                int finalI = i;
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Toast.makeText(walletHome.this, "Error finding game", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful())
                        {
                            String myResponse =  response.body().string();
                            Log.e("Testing new fetch?------------------   -------------",myResponse);
                            try
                            {
                                JSONObject jsonObject = new JSONObject(myResponse);
                            String conversion = jsonObject.toString();
                            String[] split = conversion.split("\"response\":");
                            conversion = split[1];
                            JSONArray jsonArray = new JSONArray(conversion);
                            //String scores = jsonArray.getJSONObject(0).getString("scores");
                            JSONObject scores = new JSONObject(jsonArray.getJSONObject(0).getString("scores"));
                            String homeScore = scores.getString("home");
                            String awayScore = scores.getString("away");
                            //JSONObject homeScore = new JSONObject(scores.getString("home"));
                            //JSONObject awayScore = new JSONObject(scores.getString("away"));
                            JSONObject status = new JSONObject(jsonArray.getJSONObject(0).getString("status"));
                            String finalStatus = status.getString("short");
                            Log.e("Visualise Json - ",homeScore +" to "+ awayScore);
                            Log.e("Visualise Json - ", finalStatus);
                                Log.e("Outcome Detail in loop ",betValues.get(finalI).outcome );
                                if(betValues.get(finalI).outcome.equals("N/A"))
                                {
                                    Log.e("Final status check","true: " + finalStatus);
                                    if(finalStatus.equals("FT")) {
                                        int homeInt = Integer.parseInt(homeScore);
                                        int awayInt = Integer.parseInt(awayScore);
                                        int finalScore = homeInt - awayInt;
                                        Log.e("Final Scores - ", "Final: "+ finalScore + " - home: "+ homeInt+" - Away: "+ awayInt);

                                        String winner = "";
                                        if (finalScore > 0) {
                                            winner = "Home";
                                        } else if (finalScore < 0) {
                                            winner = "Away";
                                        } else {
                                            winner = "Draw";
                                        }
                                        Log.e("Win check ", "placed: "+betTeam + " - expected: "+winner);
                                        if (!(betTeam.equals(winner))) {

                                            updateRecords(recordID, "Loss",0);

                                        } else {
                                            double out = (odds*placed);
                                            updateRecords(recordID, "Win", out);

                                        }
                                    }
                                }

                        String UIUpdate = "";
                        JSONObject teams = new JSONObject(jsonArray.getJSONObject(0).getString("teams"));
                        JSONObject homeDetes = new JSONObject(teams.getString("home"));
                        JSONObject awayDetes = new JSONObject(teams.getString("away"));
                        if(betTeam.equals("Home"))
                        {
                            UIUpdate = "";
                            UIUpdate = UIUpdate + homeDetes.getString("name") + "#"+homeDetes.getString("logo")+"#"+ awayDetes.getString("logo");
                        }else if (betTeam.equals("Away"))
                        {
                            UIUpdate = "";
                            UIUpdate = UIUpdate + awayDetes.getString("name") + "#"+homeDetes.getString("logo")+"#"+ awayDetes.getString("logo");
                        }
                        else
                        {
                            UIUpdate = "";
                            UIUpdate = UIUpdate + "on a Draw" + "#"+homeDetes.getString("logo")+"#"+ awayDetes.getString("logo");
                        }
                        betValues.get(finalI).setName(UIUpdate);



                            }
                            catch (JSONException e)
                            {

                               e.printStackTrace();
                            }

                        }

                    }

                });


            updateUI();
        }
    }

    public void updateRecords(String ID, String status, double wins)
    {

        Map<String,Object> updates = new HashMap<>();
        updates.put("outcome",status);
        updates.put("winnings",wins);

        DatabaseReference dataRef = database.getReference().child("bets").child(username).child(ID);
        dataRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d("FIREBASE_UPDATE", "Bet updated successfully!"))
                .addOnFailureListener(e -> Log.e("FIREBASE_UPDATE", "Failed to update bet", e));

    }



}