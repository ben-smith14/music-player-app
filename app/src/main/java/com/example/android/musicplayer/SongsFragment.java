package com.example.android.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SongsFragment extends ListFragment {

    private ArrayList<Song> mSongsList;

    public SongsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Retrieve the ArrayList of songs and the header text for this instance of the fragment
        // from the arguments passed it
        Bundle args = getArguments();
        String headerText = args.getString("headerText");
        mSongsList = args.getParcelableArrayList("songsList");

        if (mSongsList != null) {
            // Sort the ArrayList by song name, create an adapter with the new list and attach
            // this to the ListView for this fragment
            Collections.sort(mSongsList);
            SongAdapter adapter = new SongAdapter(this.getContext(), mSongsList);
            setListAdapter(adapter);
        }

        // Inflate the relevant layout file, retrieve the header text view and set its value to
        // the correct String
        View view = inflater.inflate(R.layout.list_view, container, false);
        TextView text = view.findViewById(R.id.list_header);
        text.setText(headerText);

        // Retrieve the shuffle button and add a click listener to start shuffling through the
        // songs if it is selected
        ImageButton shuffle = view.findViewById(R.id.shuffle_button);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mSongsList.isEmpty()) {
                    // Pick a starting song position at random
                    Random rnd = new Random();
                    int randomStart = rnd.nextInt(mSongsList.size());

                    // Create a shuffle sequence with this starting position and the song list
                    int[] shuffleSequence = NowPlayingActivity.generateShuffle(randomStart, mSongsList);

                    // Start the now playing activity in shuffle mode
                    Intent startShuffle = new Intent(getActivity(), NowPlayingActivity.class);
                    startShuffle.putExtra("shuffle", true);
                    startShuffle.putExtra("currentPosition", shuffleSequence[0]);
                    startShuffle.putParcelableArrayListExtra("songList", mSongsList);
                    startShuffle.putExtra("shuffleSequence", shuffleSequence);
                    startShuffle.putExtra("shuffleIterator", 0);

                    startActivity(startShuffle);
                } else {
                    // If there are no songs in the library, indicate this to the user
                    Toast.makeText(getContext(), getString(R.string.no_songs), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // When a list item is clicked, start the NowPlaying activity with this Song object
        Intent intent = new Intent(getActivity(), NowPlayingActivity.class);
        intent.putExtra("currentPosition", position);
        intent.putParcelableArrayListExtra("songList", mSongsList);
        getActivity().startActivity(intent);
    }
}
