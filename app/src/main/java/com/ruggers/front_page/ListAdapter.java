package com.ruggers.front_page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ruggers.R;
import com.ruggers.game_page.game_activity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class ListAdapter extends ArrayAdapter {
    private final Activity context;
    private final ArrayList<Fixture> fixtures;


    public  ListAdapter(Context context, ArrayList<Fixture> fixtureArrayList){
        super(context, R.layout.fragment_game_detail_f_p,fixtureArrayList);
        this.context = (Activity) context;
        this.fixtures = fixtureArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.fragment_game_detail_f_p,null);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openGame(position);
            }
        });
        TextView detailsText = rowView.findViewById(R.id.gameDetailsText);
        TextView oddsText = rowView.findViewById(R.id.gameOddsText);
        ImageView team1Icon = rowView.findViewById(R.id.team1Icon);
        ImageView team2Icon = rowView.findViewById(R.id.team2Icon);
        String url1 = fixtures.get(position).Icon1;
        String url2 = fixtures.get(position).Icon2;
        Picasso.with(context).load(url1).into(team1Icon);
        Picasso.with(context).load(url2).into(team2Icon);
        detailsText.setText(fixtures.get(position).team1Name + "\n vs \n" + fixtures.get(position).Team2Name);
        oddsText.setText(fixtures.get(position).getTime());
        return rowView;
    }

    public void openGame(int pos)
    {
        Intent intent = new Intent (context, game_activity.class);
        intent.putExtra("game", fixtures.get(pos));
        context.startActivity(intent);
    }
}
