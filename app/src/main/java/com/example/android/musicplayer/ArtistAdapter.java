package com.example.android.musicplayer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArtistAdapter extends ArrayAdapter<String> {

    ArtistAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
    }

    /*
    Provide a view item for our Artists ListView to display at the current position in the
    list. Use a recycled view, stored in convertView, if one is available
    */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /*
        First, check if an existing view is being reused. If there isn't one available,
        inflate a new view from the layout file
        */
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.artist_list_item, parent, false);
        }

        // Get the artist name for the current Song at this position in the list
        String currentArtist = getItem(position);

        // Set the TextView for the current list item to display this name
        if (currentArtist != null) {
            TextView artistName = listItemView.findViewById(R.id.artist_name_item);
            artistName.setText(currentArtist);
        }

        return listItemView;
    }
}
