package com.groepc.opdracht_6_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<Movies> {
    public ListViewAdapter(Context context, ArrayList<Movies> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Movies movie = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.textView);
        TextView ipaddress = (TextView) convertView.findViewById(R.id.textView2);
        // Populate the data into the template view using the data object
        name.setText(movie.name);
        ipaddress.setText("Ipadress: " + movie.ip);
        // Return the completed view to render on screen
        return convertView;
    }
}
