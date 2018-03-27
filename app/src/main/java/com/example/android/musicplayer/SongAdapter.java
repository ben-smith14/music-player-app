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

public class SongAdapter extends ArrayAdapter<Song> {

    SongAdapter(@NonNull Context context, @NonNull List<Song> objects) {
        super(context, 0, objects);
    }

    /*
    Provide a view item for our Songs ListView to display at the current position in the
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
                    R.layout.song_list_item, parent, false);
        }

        // Get the Song object located at this position in the list
        Song currentSong = getItem(position);

        if (currentSong != null) {
            /*
            Find the relevant views in the song_list_item XML file and set their contents
            to the values stored within the current Song object
            */
            ImageView albumCover = listItemView.findViewById(R.id.album_cover);
            albumCover.setImageResource(currentSong.getSongImageID());

            TextView songName = listItemView.findViewById(R.id.song_name);
            songName.setText(currentSong.getSongName());

            TextView artistName = listItemView.findViewById(R.id.artist_name);
            artistName.setText(currentSong.getArtistName());

            /*
            Use the static method from the NowPlaying activity to convert the song length to
            minutes and seconds
            */
            TextView songLength = listItemView.findViewById(R.id.song_length);
            songLength.setText(NowPlayingActivity.convertSongLength(currentSong.getSongLength()));
        }

        return listItemView;
    }
}
