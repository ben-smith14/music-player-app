package com.example.android.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class AlbumsFragment extends Fragment {

    private ArrayList<Song> mSongsList;
    private ArrayList<Song> mAlbumsList;

    public AlbumsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Retrieve the ArrayList of songs and header text for the fragment from the arguments
        // passed to the fragment
        Bundle args = getArguments();
        String headerText = args.getString("headerText");
        mSongsList = args.getParcelableArrayList("songsList");

        // Initialise the albums list and fill it with one of the songs from each album
        mAlbumsList = new ArrayList<>();
        for (Song currentSong : mSongsList) {
            // If the album list is empty, add the current song
            if (mAlbumsList.isEmpty()) {
                mAlbumsList.add(currentSong);
            } else {
                // If the album list is not empty, compare the album name of each song inside it
                // with the album name of the current song from the main song list. If an
                // identical album name is found, indicate that the current song should not be
                // added to the album list using a flag variable
                boolean addToList = true;
                for (Song albumSongs : mAlbumsList) {
                    if (albumSongs.getAlbumName().equals(currentSong.getAlbumName())) {
                        addToList = false;
                    }
                }

                if (addToList) {
                    mAlbumsList.add(currentSong);
                }
            }
        }

        // Sort the album list into alphabetical order based on the album names
        Collections.sort(mAlbumsList, Song.AlbumNameComparator);

        // Inflate the relevant layout file, retrieve the header text and set its value to the
        // correct String
        View view = inflater.inflate(R.layout.grid_view, container, false);
        TextView text = view.findViewById(R.id.list_header);
        text.setText(headerText);

        // Create an adapter for the album list and attach it to the GridView for this
        // fragment
        AlbumAdapter adapter = new AlbumAdapter(this.getContext(), mAlbumsList);
        GridView gridView = view.findViewById(R.id.grid);
        gridView.setAdapter(adapter);

        // Add a click listener for each of the items in the grid view
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // When a grid item is clicked, start the Album activity with the selected album
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtra("album", mAlbumsList.get(position).getAlbumName());
                intent.putParcelableArrayListExtra("songList", mSongsList);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }
}
