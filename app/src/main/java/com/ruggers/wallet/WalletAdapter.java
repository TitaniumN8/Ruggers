package com.ruggers.wallet;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ruggers.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WalletAdapter extends ArrayAdapter {
    private final Activity context;
    private final ArrayList<Bet> bets;

    public WalletAdapter(@NonNull Context context, ArrayList<Bet> betsList) {
        super(context, R.layout.fragment_bet,betsList);
        this.context = (Activity) context;
        this.bets = betsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.fragment_bet,null);
        TextView detailsText = rowView.findViewById(R.id.gameDetailsText);
        TextView oddsText = rowView.findViewById(R.id.gameOddsText);
        ImageView team1Icon = rowView.findViewById(R.id.team1Icon);
        ImageView team2Icon = rowView.findViewById(R.id.team2Icon);
        String[] seperate = bets.get(position).teamName.split("\\#");
       // String url1 = fixtures.get(position).Icon1;
       // String url2 = fixtures.get(position).Icon2;
       // Picasso.with(context).load(url1).into(team1Icon);
       // Picasso.with(context).load(url2).into(team2Icon);
        String name = bets.get(position).teamName;
        if(name.equals("Home")||name.equals("Away")||name.equals("Draw")) {
            detailsText.setText(bets.get(position).betPlaced + " placed on: " + bets.get(position).teamName + "\nDate:" + bets.get(position).gameTime);
        }
        else{
            detailsText.setText(bets.get(position).betPlaced + " placed on: " + seperate[0] + "\nDate:" + bets.get(position).gameTime);
            String url1 = seperate[1];
            Picasso.with(context).load(url1).into(team1Icon);
            String url2 = seperate[2];
            Picasso.with(context).load(url2).into(team2Icon);
        }
        oddsText.setText("With an odds of " + bets.get(position).odds + ". You won: "+ bets.get(position).winnings);

        return rowView;
    }
}
