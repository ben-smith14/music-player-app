package com.example.android.musicplayer;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class ArtistsFragment extends ListFragment {

    protected FragmentActivity mActivity;
    private ArrayList<Song> mSongsList;
    private ArrayList<String> mArtistList;

    public ArtistsFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
        Retrieve the ArrayList of songs and header text for the fragment from the arguments
        passed to the fragment. If no arguments were passed, set the header text and songs list
        to be empty
        */
        Bundle artistArguments = getArguments();
        String headerText;
        if (artistArguments != null) {
            headerText = artistArguments.getString(Constants.HEADER_TEXT_KEY);
            mSongsList = artistArguments.getParcelableArrayList(Constants.SONG_LIST_KEY);
        } else {
            headerText = "";
            mSongsList = new ArrayList<>();
        }

        /*
        Initialise the artists list and fill it with the names of the artists in the song list,
        avoiding duplicates
        */
        mArtistList = new ArrayList<>();
        for (Song currentSong : mSongsList) {
            if (!mArtistList.contains(currentSong.getArtistName())) {
                mArtistList.add(currentSong.getArtistName());
            }
        }

        // Sort the artists list Strings into alphabetical order
        Collections.sort(mArtistList);

        // Create an adapter for this new list and attach it to the ListView for this fragment
        ArtistAdapter artistAdapter = new ArtistAdapter(mActivity, mArtistList);
        setListAdapter(artistAdapter);

        /*
        Inflate the relevant layout file, retrieve the header text and set its value to the
        correct String
        */
        View mainLayoutView = inflater.inflate(R.layout.list_view, container, false);
        TextView headerTextView = mainLayoutView.findViewById(R.id.list_header);
        headerTextView.setText(headerText);

        // Hide the shuffle button for the Artists fragment
        ImageButton shuffleButton = mainLayoutView.findViewById(R.id.shuffle_button);
        shuffleButton.setVisibility(View.INVISIBLE);

        return mainLayoutView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // When a list item is clicked, start the Artists activity with the selected artist
        Intent artistActivityIntent = new Intent(mActivity, ArtistActivity.class);
        artistActivityIntent.putExtra(Constants.ARTIST_NAME_KEY, mArtistList.get(position));
        artistActivityIntent.putParcelableArrayListExtra(Constants.SONG_LIST_KEY, mSongsList);
        mActivity.startActivity(artistActivityIntent);
    }
}
