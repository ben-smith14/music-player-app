package com.example.android.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Collections;

public class SearchFragment extends Fragment {

    protected FragmentActivity mActivity;
    private ArrayList<Song> mSongsList;
    private View mMainLayoutView;
    private RadioGroup mSearchByGroup;
    private EditText mSearchText;
    private ViewSwitcher mViewSwitcher;
    private ArrayList<Song> mSongListToDisplay;
    private ArrayList<String> mArtistListToDisplay;
    private ArrayList<Song> mAlbumListToDisplay;

    public SearchFragment() {
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
        Bundle args = getArguments();
        String headerText;
        if (args != null) {
            headerText = args.getString(Constants.HEADER_TEXT_KEY);
            mSongsList = args.getParcelableArrayList(Constants.SONG_LIST_KEY);
        } else {
            headerText = "";
            mSongsList = new ArrayList<>();
        }

        /*
        Inflate the relevant layout file, retrieve the header text view and set its value to
        the correct String
        */
        mMainLayoutView = inflater.inflate(R.layout.search_list_view, container, false);
        TextView headerTextView = mMainLayoutView.findViewById(R.id.list_header);
        headerTextView.setText(headerText);

        // Retrieve the RadioGroup, the EditText field and the ViewSwitcher
        mSearchByGroup = mMainLayoutView.findViewById(R.id.search_by_group);
        mSearchText = mMainLayoutView.findViewById(R.id.search_input);
        mViewSwitcher = mMainLayoutView.findViewById(R.id.view_switcher);

        // Remove any text in the EditText field if the selected radio button is changed
        mSearchByGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mSearchText.getText().clear();
            }
        });

        /*
        Add a text change listener to the EditText field to change the state of the list or
        grid of items when the user changes the input text
        */
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Get the current input text, converting it to lowercase and trimming whitespace
                String currentText = editable.toString().toLowerCase().trim();

                /*
                Identify which radio button has been selected to determine what type of adapter
                should be used for the list or grid
                */
                if (mSearchByGroup.getCheckedRadioButtonId() == R.id.search_by_song) {
                    /*
                    If the By Song radio button has been selected and the edit text field is
                    not empty, sort the song names alphabetically and then run a comparison of
                    the input text with the name of each song to determine which ones should
                    be added to the displayed list
                    */
                    mSongListToDisplay = new ArrayList<>();

                    if (!currentText.isEmpty()) {
                        for (Song currentSong : mSongsList) {
                            /*
                            For each song in the library, retrieve the song name and convert it
                            to lowercase whilst also trimming whitespace. Then use the startsWith
                            method to identify whether the song name starts with the current input
                            text from the user. If it does, add the current song to the new list
                            */
                            String songName = currentSong.getSongName().toLowerCase().trim();
                            if (songName.startsWith(currentText)) {
                                mSongListToDisplay.add(currentSong);
                            }
                        }

                        // Sort the new list into alphabetical order
                        Collections.sort(mSongListToDisplay);
                    }

                    /*
                    Create an adapter with the new list and attach this to the ListView for
                    this fragment
                    */
                    SongAdapter adapter = new SongAdapter(mActivity, mSongListToDisplay);
                    ListView listView = mMainLayoutView.findViewById(R.id.list);
                    listView.setAdapter(adapter);

                    // Add a click listener for each of the items in the list view
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            /*
                            When a list item is clicked, start the NowPlaying activity with this
                            Song object
                            */
                            Intent nowPlayingIntent = new Intent(getActivity(), NowPlayingActivity.class);

                            /*
                            Get the index position of the selected song in the main song list
                            so that this can be passed to the song fragment instead of its
                            position in the reduced list. This allows the user to shuffle
                            through all of the songs from the now playing activity as normal
                            */
                            Song selectedSong = mSongListToDisplay.get(position);
                            int songPosition = mSongsList.indexOf(selectedSong);

                            nowPlayingIntent.putExtra(Constants.POSITION_KEY, songPosition);
                            nowPlayingIntent.putParcelableArrayListExtra(Constants.SONG_LIST_KEY, mSongsList);
                            mActivity.startActivity(nowPlayingIntent);
                        }
                    });

                    // Finally, show the list view if it is not currently visible in the switcher
                    if (mViewSwitcher.getCurrentView() != mMainLayoutView.findViewById(R.id.view1)) {
                        mViewSwitcher.showPrevious();
                    }
                } else if (mSearchByGroup.getCheckedRadioButtonId() == R.id.search_by_artist) {
                    /*
                    Initialise the list of artists that will be displayed based on the search
                    results
                    */
                    mArtistListToDisplay = new ArrayList<>();

                    /*
                    If the By Artist radio button has been selected and the edit text field is
                    not empty, fill the list of artists to display based on the search results
                    */
                    if (!currentText.isEmpty()) {
                        // Create the normal artist list before it is filtered by the search
                        ArrayList<String> artistList = new ArrayList<>();
                        for (Song currentSong : mSongsList) {
                            if (!artistList.contains(currentSong.getArtistName())) {
                                artistList.add(currentSong.getArtistName());
                            }
                        }

                        /*
                        Add items that start with the current input text to the list to display.
                        Convert the current artist string to lower case and trim it for the
                        comparison
                        */
                        for (String currentArtist : artistList) {
                            String artistName = currentArtist.toLowerCase().trim();
                            if (artistName.startsWith(currentText)) {
                                mArtistListToDisplay.add(currentArtist);
                            }
                        }

                        // Sort this new list into alphabetical order
                        Collections.sort(mArtistListToDisplay);
                    }

                    /*
                    Create an adapter with the new list and attach this to the ListView for
                    this fragment
                    */
                    ArtistAdapter artistAdapter = new ArtistAdapter(mActivity, mArtistListToDisplay);
                    ListView artistListView = mMainLayoutView.findViewById(R.id.list);
                    artistListView.setAdapter(artistAdapter);

                    // Add a click listener for each of the items in the list view
                    artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            /*
                            When a list item is clicked, start the Artists activity with the
                            selected artist
                            */
                            Intent artistActivityIntent = new Intent(getActivity(), ArtistActivity.class);
                            artistActivityIntent.putExtra(Constants.ARTIST_NAME_KEY, mArtistListToDisplay.get(position));
                            artistActivityIntent.putParcelableArrayListExtra(Constants.SONG_LIST_KEY, mSongsList);
                            mActivity.startActivity(artistActivityIntent);
                        }
                    });

                    // Finally, show the list view if it is not currently visible in the switcher
                    if (mViewSwitcher.getCurrentView() != mMainLayoutView.findViewById(R.id.view1)) {
                        mViewSwitcher.showPrevious();
                    }
                } else if (mSearchByGroup.getCheckedRadioButtonId() == R.id.search_by_album) {
                    /*
                    Initialise the list of albums that will be displayed based on the search
                    results
                    */
                    mAlbumListToDisplay = new ArrayList<>();

                    /*
                    If the By Album radio button has been selected and the edit text field is
                    not empty, fill the list of albums to display based on the search results
                    */
                    if (!currentText.isEmpty()) {
                        // Create the normal album list before it is filtered by the search
                        ArrayList<Song> albumsList = new ArrayList<>();
                        for (Song currentSong : mSongsList) {
                            // If the album list is empty, add the current song
                            if (albumsList.isEmpty()) {
                                albumsList.add(currentSong);
                            } else {
                                /*
                                If the album list is not empty, compare the album name of each
                                song inside it with the album name of the current song from the
                                main song list. If an identical album name is found, indicate
                                that the current song should not be added to the album list
                                using a flag variable
                                */
                                boolean addToList = true;
                                for (Song albumSongs : albumsList) {
                                    if (albumSongs.getAlbumName().equals(currentSong.getAlbumName())) {
                                        addToList = false;
                                    }
                                }

                                if (addToList) {
                                    albumsList.add(currentSong);
                                }
                            }
                        }

                        /*
                        Add items that start with the current input text to the list to display.
                        Convert the current album string to lower case and trim it for the
                        comparison
                        */
                        for (Song currentAlbum : albumsList) {
                            String albumName = currentAlbum.getAlbumName().toLowerCase().trim();
                            if (albumName.startsWith(currentText)) {
                                mAlbumListToDisplay.add(currentAlbum);
                            }
                        }

                        // Sort this new list into alphabetical order based on the album names
                        Collections.sort(mAlbumListToDisplay, Song.AlbumNameComparator);
                    }

                    /*
                    Create an adapter for the album list and attach it to the GridView for this
                    fragment
                    */
                    AlbumAdapter albumAdapter = new AlbumAdapter(mActivity, mAlbumListToDisplay);
                    GridView albumGridView = mMainLayoutView.findViewById(R.id.grid);
                    albumGridView.setAdapter(albumAdapter);

                    // Add a click listener for each of the items in the grid view
                    albumGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            /*
                            When a grid item is clicked, start the Album activity with the
                            selected album
                            */
                            Intent albumActivityIntent = new Intent(mActivity, AlbumActivity.class);
                            albumActivityIntent.putExtra(Constants.ALBUM_NAME_KEY, mAlbumListToDisplay.get(position).getAlbumName());
                            albumActivityIntent.putParcelableArrayListExtra(Constants.SONG_LIST_KEY, mSongsList);
                            mActivity.startActivity(albumActivityIntent);
                        }
                    });

                    // Finally, show the grid view if it is not currently visible in the switcher
                    if (mViewSwitcher.getCurrentView() != mMainLayoutView.findViewById(R.id.view2)) {
                        mViewSwitcher.showNext();
                    }
                }
            }
        });

        return mMainLayoutView;
    }
}
