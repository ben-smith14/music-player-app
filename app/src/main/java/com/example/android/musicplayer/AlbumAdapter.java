package com.example.android.musicplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AlbumAdapter extends ArrayAdapter<Song> {

    AlbumAdapter(@NonNull Context context, @NonNull List<Song> objects) {
        super(context, 0, objects);
    }

    // Provide a view item for our Album GridView to display at the current position in the
    // grid. Use a recycled view, stored in convertView, if one is available
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // First, check if an existing view is being reused. If there isn't one available,
        // inflate a new view from the layout file
        View gridItemView = convertView;
        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.album_grid_item, parent, false);
        }

        // Get the artist's name for the current Song in the list
        Song currentAlbum = getItem(position);

        // Set the ImageView and TextViews for the current grid item to display the contents
        // of the selected song
        if (currentAlbum != null) {
            ImageView albumArt = gridItemView.findViewById(R.id.grid_song_image);
            albumArt.setImageResource(currentAlbum.getSongImageID());

            TextView albumName = gridItemView.findViewById(R.id.grid_album_name);
            albumName.setText(currentAlbum.getAlbumName());

            TextView artistName = gridItemView.findViewById(R.id.grid_artist_name);
            artistName.setText(currentAlbum.getArtistName());
        }

        return gridItemView;
    }
}
