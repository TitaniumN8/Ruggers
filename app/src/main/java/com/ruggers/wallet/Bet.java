package com.ruggers.wallet;

import com.google.gson.Gson;

public class Bet {


    float betPlaced;
    float winnings;
    double odds;
    String teamName, datePlaced, gameTime, outcome;
    int GameID;

    public Bet(float betPlaced,float winnings,double odds, String teamName, String datePlaced, String gameTime, String outcome, int gameID) {
        this.betPlaced = betPlaced;

        this.winnings = winnings;
        this.odds = odds;
        this.teamName = teamName;
        this.datePlaced = datePlaced;
        this.gameTime = gameTime;
        this.outcome = outcome;
        this.GameID = gameID;
    }

    public float getBetPlaced() {
        return betPlaced;
    }

    public float getWinnings() {
        return winnings;
    }

    public double getOdds() {
        return odds;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getDatePlaced() {
        return datePlaced;
    }

    public String getGameTime() {
        return gameTime;
    }

    public String getOutcome() {
        return outcome;
    }

    public int getGameID() {
        return GameID;
    }

    public static Bet createBetFromJson(String inp)
    {
        Gson gson = new Gson();

        return gson.fromJson(inp, Bet.class);

    }

    public String detailsOut()
    {
        return String.valueOf(GameID) + ": "+ teamName + " - "+ String.valueOf(odds) + "/1 , $" + String.valueOf(getBetPlaced()) +"\nWinnings: "+getWinnings()+"  -  Date:"+getDatePlaced()
                +"  -  Time:"+gameTime
                +"\nOutcome:"+outcome;
    }

    public void setName(String name)
    {
        this.teamName = name;
    }
}
 