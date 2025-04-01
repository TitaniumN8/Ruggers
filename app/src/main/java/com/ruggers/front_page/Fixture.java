package com.ruggers.front_page;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Fixture implements Parcelable {
    String team1Name;
    int GameID;
    String Team2Name;
    String Time;
    String Icon1;
    String Icon2;


    public Fixture(int gameID, String team2Name, String team1Name, String time, String icon2, String icon1) {
        GameID = gameID;
        this.team1Name = team1Name;
        Team2Name = team2Name;
        Time = time;
        Icon1 =icon1;
        Icon2 =icon2;
    }


    protected Fixture(Parcel in) {
        team1Name = in.readString();
        GameID = in.readInt();
        Team2Name = in.readString();
        Time = in.readString();
        Icon1 = in.readString();
        Icon2 = in.readString();
    }

    public static final Creator<Fixture> CREATOR = new Creator<Fixture>() {
        @Override
        public Fixture createFromParcel(Parcel in) {
            return new Fixture(in);
        }

        @Override
        public Fixture[] newArray(int size) {
            return new Fixture[size];
        }
    };

    public int getGameID() {
        return GameID;
    }

    public void setGameID(int gameID) {
        GameID = gameID;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public void setTeam1Name(String team1Name) {
        this.team1Name = team1Name;
    }

    public String getTeam2Name() {
        return Team2Name;
    }

    public void setTeam2Name(String team2Name) {
        Team2Name = team2Name;
    }


    public String getIcon1() {
        return Icon1;
    }

    public void setIcon1(String icon1) {
        this.Icon1 = icon1;
    }

    public String getIcon2() {
        return Icon2;
    }

    public void setIcon2(String icon2) {
        this.Icon2 = icon2;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(team1Name);
        parcel.writeInt(GameID);
        parcel.writeString(Team2Name);
        parcel.writeString(Time);
        parcel.writeString(Icon1);
        parcel.writeString(Icon2);
    }
}
