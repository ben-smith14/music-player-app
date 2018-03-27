package com.example.android.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class AlbumsFragment extends Fragment {

    protected FragmentActivity mActivity;
    private ArrayList<Song> mSongsList;
    private ArrayList<Song> mAlbumsList;

    public AlbumsFragment() {
    }

    /*
    To prevent calls to getActivity() returning null in onCreateView, we can store a reference
    to the activity in onAttach, which is called before onCreateView, and then use this in
    onCreateView to retrieve the activity
    */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    /*
    To then prevent memory leaks, we need to dereference the activity pointer when the
    fragment is detached from its parent activity or destroyed
    */
    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
        Retrieve the ArrayList of songs and header text for the fragment from the arguments
        passed to the fragment. If no arguments were passed, set the header text and songs list
        to be empty
        */
        Bundle albumArguments = getArguments();
        String headerText;
        if (albumArguments != null) {
            headerText = albumArguments.getString(Constants.HEADER_TEXT_KEY);
            mSongsList = albumArguments.getParcelableArrayList(Constants.SONG_LIST_KEY);
        } else {
            headerText = "";
            mSongsList = new ArrayList<>();
        }

        // Initialise the albums list and fill it with one of the songs from each album
        mAlbumsList = new ArrayList<>();
        for (Song currentSong : mSongsList) {
            // If the album list is empty, add the current song
            if (mAlbumsList.isEmpty()) {
                mAlbumsList.add(currentSong);
            } else {
                /*
                If the album list is not empty, compare the album name of each song inside it
                with the album name of the current song from the main song list. If an
                identical album name is found, indicate that the current song should not be
                added to the album list using a flag variable
                */
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

        /*
        Inflate the relevant layout file, retrieve the header text and set its value to the
        correct String
        */
        View mainLayoutView = inflater.inflate(R.layout.grid_view, container, false);
        TextView headerTextView = mainLayoutView.findViewById(R.id.list_header);
        headerTextView.setText(headerText);

        // Create an adapter for the album list and attach it to the GridView for this fragment
        AlbumAdapter albumAdapter = new AlbumAdapter(mActivity, mAlbumsList);
        GridView albumGridView = mainLayoutView.findViewById(R.id.grid);
        albumGridView.setAdapter(albumAdapter);

        // Add a click listener for each of the items in the grid view
        albumGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // When a grid item is clicked, start the Album activity with the selected album
                Intent albumActivityIntent = new Intent(mActivity, AlbumActivity.class);
                albumActivityIntent.putExtra(Constants.ALBUM_NAME_KEY, mAlbumsList.get(position).getAlbumName());
                albumActivityIntent.putParcelableArrayListExtra(Constants.SONG_LIST_KEY, mSongsList);
                mActivity.startActivity(albumActivityIntent);
            }
        });

        return mainLayoutView;
    }
}
