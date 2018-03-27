package com.example.android.musicplayer;

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

    private ArrayList<Song> mSongsList;
    private ArrayList<String> mArtistList;

    public ArtistsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Retrieve the ArrayList of songs and header text for the fragment from the arguments
        // passed to the fragment
        Bundle args = getArguments();
        String headerText = args.getString("headerText");
        mSongsList = args.getParcelableArrayList("songsList");

        // Initialise the artists list and fill it with the names of the artists in the song list,
        // avoiding duplicates
        mArtistList = new ArrayList<>();
        for (Song currentSong : mSongsList) {
            if (!mArtistList.contains(currentSong.getArtistName())) {
                mArtistList.add(currentSong.getArtistName());
            }
        }

        // Sort the artists list Strings into alphabetical order
        Collections.sort(mArtistList);

        // Create an adapter for this new list and attach it to the ListView for this
        // fragment
        ArtistAdapter adapter = new ArtistAdapter(this.getContext(), mArtistList);
        setListAdapter(adapter);

        // Inflate the relevant layout file, retrieve the header text and set its value to the
        // correct String
        View view = inflater.inflate(R.layout.list_view, container, false);
        TextView text = view.findViewById(R.id.list_header);
        text.setText(headerText);

        // Hide the shuffle button for the Artists fragment
        ImageButton shuffleButton = view.findViewById(R.id.shuffle_button);
        shuffleButton.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // When a list item is clicked, start the Artists activity with the selected artist
        Intent intent = new Intent(getActivity(), ArtistActivity.class);
        intent.putExtra("artist", mArtistList.get(position));
        intent.putParcelableArrayListExtra("songList", mSongsList);
        getActivity().startActivity(intent);
    }
}
