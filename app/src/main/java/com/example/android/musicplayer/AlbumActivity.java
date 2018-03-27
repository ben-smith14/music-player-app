package com.example.android.musicplayer;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_album);

        // Retrieve the selected artist's name and the entire list of songs
        Intent albumIntent = getIntent();
        String albumName = albumIntent.getStringExtra(Constants.ALBUM_NAME_KEY);
        ArrayList<Song> songList = albumIntent.getParcelableArrayListExtra(Constants.SONG_LIST_KEY);

        /*
        Replace the standard action bar for this activity with the custom toolbar and add
        the title text to it
        */
        Toolbar customToolbar = findViewById(R.id.toolbar_artists_albums);
        customToolbar.setTitle(getString(R.string.albums_text));
        setSupportActionBar(customToolbar);

        /*
        Add a "home" button to the toolbar that can be used to navigate back to the list of
        artists in the library activity
        */
        ActionBar newActionBar = getSupportActionBar();
        if (newActionBar != null) {
            newActionBar.setDisplayHomeAsUpEnabled(true);
            newActionBar.setDisplayShowHomeEnabled(true);
            newActionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back));
        }

        // Create a new ArrayList and populate it with songs that are only in the selected album
        ArrayList<Song> albumSongList = new ArrayList<>();
        for (Song currentSong : songList) {
            if (currentSong.getAlbumName().equals(albumName)) {
                albumSongList.add(currentSong);
            }
        }

        /*
        If the app is being restored from a previous state then we don't need to do
        anything and should return or else we could end up with overlapping fragments
        */
        if (savedInstanceState != null) {
            return;
        }

        /*
        If there is no previous state, create a bundle and add the album's name and song
        list to it so that they can be passed to the fragment when it is opened
        */
        Bundle albumBundle = new Bundle();
        albumBundle.putString(Constants.HEADER_TEXT_KEY, albumName);
        albumBundle.putParcelableArrayList(Constants.SONG_LIST_KEY, albumSongList);

        /*
        Create a new fragment to be placed in the activity layout's fragment container
        and pass the relevant arguments to it
        */
        SongsFragment songsFragment = new SongsFragment();
        songsFragment.setArguments(albumBundle);

        // Add the fragment to the FrameLayout container
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_artists_albums, songsFragment).commit();
    }

    // Allows the back button to navigate to the previous activity when selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
