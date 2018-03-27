package com.example.android.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private ArrayList<Song> mSongsList;
    private View inflatedView;
    private RadioGroup searchByGroup;
    private EditText searchText;
    private ViewSwitcher viewSwitcher;
    private ArrayList<Song> songListToDisplay;
    private ArrayList<String> artistListToDisplay;
    private ArrayList<Song> albumListToDisplay;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Retrieve the ArrayList of songs and the header text for this instance of the fragment
        // from the arguments passed it
        Bundle args = getArguments();
        String headerText = args.getString("headerText");
        mSongsList = args.getParcelableArrayList("songsList");

        // Inflate the relevant layout file, retrieve the header text view and set its value to
        // the correct String
        inflatedView = inflater.inflate(R.layout.search_list_view, container, false);
        TextView text = inflatedView.findViewById(R.id.list_header);
        text.setText(headerText);

        // Retrieve the RadioGroup, the EditText field and the ViewSwitcher
        searchByGroup = inflatedView.findViewById(R.id.search_by_group);
        searchText = inflatedView.findViewById(R.id.search_input);
        viewSwitcher = inflatedView.findViewById(R.id.view_switcher);

        // Remove any text in the EditText field if the selected radio button is changed
        searchByGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                searchText.getText().clear();
            }
        });

        // Add a text change listener to the EditText field to change the state of the list or
        // grid of items when the user changes the input text
        searchText.addTextChangedListener(new TextWatcher() {
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

                // Identify which radio button has been selected to determine what type of adapter
                // should be used for the list or grid
                if (searchByGroup.getCheckedRadioButtonId() == R.id.search_by_song) {
                    // If the By Song radio button has been selected and the edit text field is
                    // not empty, sort the song names alphabetically and then run a comparison of
                    // the input text with the name of each song to determine which ones should
                    // be added to the displayed list
                    songListToDisplay = new ArrayList<>();

                    if (!currentText.isEmpty()) {
                        for (Song currentSong : mSongsList) {
                            // For each song in the library, retrieve the song name and convert it
                            // to lowercase whilst also trimming whitespace. Then use the startsWith
                            // method to identify whether the song name starts with the current input
                            // text from the user. If it does, add the current song to the new list
                            String songName = currentSong.getSongName().toLowerCase().trim();
                            if (songName.startsWith(currentText)) {
                                songListToDisplay.add(currentSong);
                            }
                        }

                        // Sort the new list into alphabetical order
                        Collections.sort(songListToDisplay);
                    }

                    // Create an adapter with the new list and attach this to the ListView for
                    // this fragment
                    SongAdapter adapter = new SongAdapter(SearchFragment.this.getContext(), songListToDisplay);
                    ListView listView = inflatedView.findViewById(R.id.list);
                    listView.setAdapter(adapter);

                    // Add a click listener for each of the items in the list view
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            // When a list item is clicked, start the NowPlaying activity with this
                            // Song object
                            Intent intent = new Intent(getActivity(), NowPlayingActivity.class);

                            // Get the index position of the selected song in the main song list
                            // so that this can be passed to the song fragment instead of its
                            // position in the reduced list. This allows the user to shuffle
                            // through all of the songs from the now playing activity as normal
                            Song selectedSong = songListToDisplay.get(position);
                            int i = mSongsList.indexOf(selectedSong);

                            intent.putExtra("currentPosition", i);
                            intent.putParcelableArrayListExtra("songList", mSongsList);
                            getActivity().startActivity(intent);
                        }
                    });

                    // Finally, show the list view if it is not currently visible in the switcher
                    if (viewSwitcher.getCurrentView() != inflatedView.findViewById(R.id.view1)) {
                        viewSwitcher.showPrevious();
                    }
                } else if (searchByGroup.getCheckedRadioButtonId() == R.id.search_by_artist) {
                    // Initialise the list of artists that will be displayed based on the search
                    // results
                    artistListToDisplay = new ArrayList<>();

                    // If the By Artist radio button has been selected and the edit text field is
                    // not empty, fill the list of artists to display based on the search results
                    if (!currentText.isEmpty()) {
                        // Create the normal artist list before it is filtered by the search
                        ArrayList<String> artistList = new ArrayList<>();
                        for (Song currentSong : mSongsList) {
                            if (!artistList.contains(currentSong.getArtistName())) {
                                artistList.add(currentSong.getArtistName());
                            }
                        }

                        // Add items that start with the current input text to the list to display.
                        // Convert the current artist string to lower case and trim it for the
                        // comparison
                        for (String currentArtist : artistList) {
                            String artistName = currentArtist.toLowerCase().trim();
                            if (artistName.startsWith(currentText)) {
                                artistListToDisplay.add(currentArtist);
                            }
                        }

                        // Sort this new list into alphabetical order
                        Collections.sort(artistListToDisplay);
                    }

                    // Create an adapter with the new list and attach this to the ListView for
                    // this fragment
                    ArtistAdapter adapter = new ArtistAdapter(SearchFragment.this.getContext(), artistListToDisplay);
                    ListView listView = inflatedView.findViewById(R.id.list);
                    listView.setAdapter(adapter);

                    // Add a click listener for each of the items in the list view
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            // When a list item is clicked, start the Artists activity with the
                            // selected artist
                            Intent intent = new Intent(getActivity(), ArtistActivity.class);
                            intent.putExtra("artist", artistListToDisplay.get(position));
                            intent.putParcelableArrayListExtra("songList", mSongsList);
                            getActivity().startActivity(intent);
                        }
                    });

                    // Finally, show the list view if it is not currently visible in the switcher
                    if (viewSwitcher.getCurrentView() != inflatedView.findViewById(R.id.view1)) {
                        viewSwitcher.showPrevious();
                    }
                } else if (searchByGroup.getCheckedRadioButtonId() == R.id.search_by_album) {
                    // Initialise the list of albums that will be displayed based on the search
                    // results
                    albumListToDisplay = new ArrayList<>();

                    // If the By Album radio button has been selected and the edit text field is
                    // not empty, fill the list of albums to display based on the search results
                    if (!currentText.isEmpty()) {
                        // Create the normal album list before it is filtered by the search
                        ArrayList<Song> albumsList = new ArrayList<>();
                        for (Song currentSong : mSongsList) {
                            // If the album list is empty, add the current song
                            if (albumsList.isEmpty()) {
                                albumsList.add(currentSong);
                            } else {
                                // If the album list is not empty, compare the album name of each
                                // song inside it with the album name of the current song from the
                                // main song list. If an identical album name is found, indicate
                                // that the current song should not be added to the album list
                                // using a flag variable
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

                        // Add items that start with the current input text to the list to display.
                        // Convert the current album string to lower case and trim it for the
                        // comparison
                        for (Song currentAlbum : albumsList) {
                            String albumName = currentAlbum.getAlbumName().toLowerCase().trim();
                            if (albumName.startsWith(currentText)) {
                                albumListToDisplay.add(currentAlbum);
                            }
                        }

                        // Sort this new list into alphabetical order based on the album names
                        Collections.sort(albumListToDisplay, Song.AlbumNameComparator);
                    }

                    // Create an adapter for the album list and attach it to the GridView for this
                    // fragment
                    AlbumAdapter adapter = new AlbumAdapter(SearchFragment.this.getContext(), albumListToDisplay);
                    GridView gridView = inflatedView.findViewById(R.id.grid);
                    gridView.setAdapter(adapter);

                    // Add a click listener for each of the items in the grid view
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            // When a grid item is clicked, start the Album activity with the
                            // selected album
                            Intent intent = new Intent(getActivity(), AlbumActivity.class);
                            intent.putExtra("album", albumListToDisplay.get(position).getAlbumName());
                            intent.putParcelableArrayListExtra("songList", mSongsList);
                            getActivity().startActivity(intent);
                        }
                    });

                    // Finally, show the grid view if it is not currently visible in the switcher
                    if (viewSwitcher.getCurrentView() != inflatedView.findViewById(R.id.view2)) {
                        viewSwitcher.showNext();
                    }
                }
            }
        });

        return inflatedView;
    }
}
