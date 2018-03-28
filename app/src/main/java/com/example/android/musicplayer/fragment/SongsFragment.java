package com.example.android.musicplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.musicplayer.R;
import com.example.android.musicplayer.activity.NowPlayingActivity;
import com.example.android.musicplayer.adapter.SongAdapter;
import com.example.android.musicplayer.general.Song;
import com.example.android.musicplayer.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SongsFragment extends ListFragment {

    protected FragmentActivity mActivity;
    private ArrayList<Song> mSongsList;

    public SongsFragment() {
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
        Retrieve the ArrayList of songs and the header text for this instance of the fragment
        from the arguments passed it
        */
        Bundle songArguments = getArguments();
        String headerText;
        if (songArguments != null) {
            headerText = songArguments.getString(Constants.HEADER_TEXT_KEY);
            mSongsList = songArguments.getParcelableArrayList(Constants.SONG_LIST_KEY);
        } else {
            headerText = "";
            mSongsList = new ArrayList<>();
        }

        /*
        Sort the ArrayList by song name, create an adapter with the new list and attach
        this to the ListView for this fragment
        */
        if (mSongsList != null) {
            Collections.sort(mSongsList);
            SongAdapter songAdapter = new SongAdapter(mActivity, mSongsList);
            setListAdapter(songAdapter);
        }

        /*
        Inflate the relevant layout file, retrieve the header text view and set its value to
        the correct String
        */
        View mainLayoutView = inflater.inflate(R.layout.layout_list_view, container, false);
        TextView headerTextView = mainLayoutView.findViewById(R.id.list_header);
        headerTextView.setText(headerText);

        /*
        Retrieve the shuffle button and add a click listener to start shuffling through the
        songs if it is selected
        */
        ImageButton shuffleButton = mainLayoutView.findViewById(R.id.shuffle_button);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mSongsList.isEmpty()) {
                    // Pick a starting song position at random
                    Random rnd = new Random();
                    int randomStart = rnd.nextInt(mSongsList.size());

                    // Create a shuffle sequence with this starting position and the song list
                    int[] shuffleSequence = NowPlayingActivity.generateShuffle(randomStart, mSongsList);

                    // Start the now playing activity in shuffle mode
                    Intent startShuffle = new Intent(mActivity, NowPlayingActivity.class);
                    startShuffle.putExtra(Constants.SHUFFLE_KEY, true);
                    startShuffle.putExtra(Constants.POSITION_KEY, shuffleSequence[0]);
                    startShuffle.putParcelableArrayListExtra(Constants.SONG_LIST_KEY, mSongsList);
                    startShuffle.putExtra(Constants.SHUFFLE_SEQUENCE_KEY, shuffleSequence);
                    startShuffle.putExtra(Constants.SHUFFLE_ITERATOR_KEY, 0);

                    mActivity.startActivity(startShuffle);
                } else {
                    // If there are no songs in the library, indicate this to the user
                    Toast.makeText(mActivity, getString(R.string.no_songs), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return mainLayoutView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // When a list item is clicked, start the NowPlaying activity with this Song object
        Intent intent = new Intent(mActivity, NowPlayingActivity.class);
        intent.putExtra(Constants.POSITION_KEY, position);
        intent.putParcelableArrayListExtra(Constants.SONG_LIST_KEY, mSongsList);
        mActivity.startActivity(intent);
    }
}
