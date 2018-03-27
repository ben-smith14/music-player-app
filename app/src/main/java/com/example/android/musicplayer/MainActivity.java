package com.example.android.musicplayer;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ArrayList<Song> mSongsList;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise the global variables and add the test set of songs to the ArrayList
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mSongsList = new ArrayList<>();
        addSongsTestSet(mSongsList);

        // Replace the standard action bar for this activity with a custom toolbar and add
        // the title text to it
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.library_text));
        setSupportActionBar(toolbar);

        // Add an action bar drawer toggle icon to the layout that can be used to open and close
        // the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // If the app is being restored from a previous state then we don't need to do
        // anything and should return or else we could end up with overlapping fragments
        if (savedInstanceState == null) {
            // If there is no previous state, create a bundle and add the parcelable ArrayList
            // and the fragment header text to it so that they can be passed to the fragment when
            // it is opened
            Bundle bundle = new Bundle();
            bundle.putString("headerText", getString(R.string.songs_text));
            bundle.putParcelableArrayList("songsList", mSongsList);

            // If the app is being started cold, create a new fragment to be placed in the
            // activity layout's fragment container and pass the relevant arguments to it
            SongsFragment firstFragment = new SongsFragment();
            firstFragment.setArguments(bundle);

            // Add the fragment to the FrameLayout container
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }

        // Set the current checked item to the songs text and add a navigation item selected
        // listener to the navigation view. The methods for this listener are included within
        // the MainActivity class, so the input is just this
        mNavigationView.setCheckedItem(R.id.songs);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    // Add the search icon to the toolbar by inflating the relevant menu resource
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Change the functionality of the modified "home" button so that it now opens the drawer
    // layout when selected. Also, include functionality for the search icon so that it opens the
    // search fragment when selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // The input simply sets the drawer to open using the default
                // animation for this feature
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.search_icon:
                // If the search fragment is already open when the search icon is clicked, open
                // the songs library fragment instead. Otherwise, open the search fragment
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("SEARCH");

                if (currentFragment != null && currentFragment.isVisible()) {
                    // Create a bundle and add the parcelable ArrayList and the fragment header text to it
                    // so that they can be passed to the fragment when it is opened
                    Bundle bundle = new Bundle();
                    bundle.putString("headerText", getString(R.string.songs_text));
                    bundle.putParcelableArrayList("songsList", mSongsList);

                    // Create a new fragment to be placed in the activity layout's fragment container
                    // and pass the relevant arguments to it
                    SongsFragment songFragment = new SongsFragment();
                    songFragment.setArguments(bundle);

                    // Open the song library fragment and set the relevant navigation drawer item
                    // to be selected
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, songFragment).commit();
                    mNavigationView.getMenu().findItem(R.id.songs).setChecked(true);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("headerText", getString(R.string.search_icon));
                    bundle.putParcelableArrayList("songsList", mSongsList);

                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment, "SEARCH").commit();

                    // If opening the search fragment, un-check all items in navigation drawer
                    for (int i = 0; i < mNavigationView.getMenu().size(); i++) {
                        mNavigationView.getMenu().getItem(i).setChecked(false);
                    }
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Deal with one of the items in the navigation drawer being clicked
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Create a new bundle and add the current state of the ArrayList to it so
        // that it can be passed to the fragments when they are opened
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("songsList", mSongsList);

        // Store the header text for the relevant fragment in the bundle and instantiate
        // it based on the drawer item clicked
        Fragment newFragment;
        switch (item.getItemId()) {
            case R.id.artists:
                bundle.putString("headerText", getString(R.string.artists_text));
                newFragment = new ArtistsFragment();
                break;
            case R.id.albums:
                bundle.putString("headerText", getString(R.string.albums_text));
                newFragment = new AlbumsFragment();
                break;
            default:
                bundle.putString("headerText", getString(R.string.songs_text));
                newFragment = new SongsFragment();
        }

        // Replace the existing fragment with the new one and close the drawer
        newFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).commit();
        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // This adds a test set of songs to the ArrayList. All song information and pictures were
    // taken from wikipedia.org
    public void addSongsTestSet(ArrayList<Song> songsList) {
        songsList.add(new Song("Bonkers (Radio Edit)",
                "Dizzee Rascal & Armand Van Helden",
                "Bonkers Single", 176, R.drawable.bonkers_art));

        songsList.add(new Song("Castle on the Hill",
                "Ed Sheeran",
                "Divide", 261, R.drawable.divide_art));

        songsList.add(new Song("Shape of You",
                "Ed Sheeran",
                "Divide", 233, R.drawable.divide_art));

        songsList.add(new Song("Galway Girl",
                "Ed Sheeran",
                "Divide", 170, R.drawable.divide_art));

        songsList.add(new Song("Something from Nothing",
                "Foo Fighters",
                "Sonic Highways", 289, R.drawable.sonic_highways_art));

        songsList.add(new Song("I Am a River",
                "Foo Fighters",
                "Sonic Highways", 429, R.drawable.sonic_highways_art));

        songsList.add(new Song("Solo Dance",
                "Martin Jensen",
                "Solo Dance Single", 174, R.drawable.solo_dance_art));

        songsList.add(new Song("Human",
                "Rag'n'Bone Man",
                "Human", 200, R.drawable.human_art));

        songsList.add(new Song("Skin",
                "Rag'n'Bone Man",
                "Human", 239, R.drawable.human_art));

        songsList.add(new Song("Arrow",
                "Rag'n'Bone Man",
                "Human", 201, R.drawable.human_art));

        songsList.add(new Song("Hotter than Hell",
                "Dua Lipa",
                "Dua Lipa", 187, R.drawable.dua_lipa_art));

        songsList.add(new Song("IDGAF",
                "Dua Lipa",
                "Dua Lipa", 218, R.drawable.dua_lipa_art));

        // This is a test song that is used to try out different list item features
        // (image from http://glee-new-beginnings.wikia.com/wiki/File:Unknown_Album.png)
        songsList.add(new Song("Test Song",
                "Ben Smith",
                "Good Songs", 180, R.drawable.unknown_art));
    }
}
