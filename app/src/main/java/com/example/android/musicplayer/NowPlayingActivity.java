package com.example.android.musicplayer;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NowPlayingActivity extends AppCompatActivity {

    private ArrayList<Song> mSongsList;
    private int mSongPosition;
    private int mSongLength;
    private SeekBar mSongSlider;
    private TextView mSongMin;
    private TextView mSongMax;
    private CountDownTimer cdTimer;
    private ImageButton mPlayOrPause;
    private ImageButton mRepeat;
    private ImageButton mShuffle;
    private int[] mShuffleSequence;
    private int mShuffleIterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowplaying);

        // Initialise the global view objects
        mShuffle = findViewById(R.id.shuffle_songs);
        mSongSlider = findViewById(R.id.seekbar);
        mSongMin = findViewById(R.id.song_min);
        mSongMax = findViewById(R.id.song_max);
        mPlayOrPause = findViewById(R.id.play_pause);
        mRepeat = findViewById(R.id.repeat_song);

        // Initialise the local view objects
        ImageButton mClosePlayer = findViewById(R.id.close_player);
        ImageView mSongImage = findViewById(R.id.song_image);
        TextView mSongName = findViewById(R.id.name_of_song);
        TextView mSongArtist = findViewById(R.id.song_artists);
        ImageButton mSkipBack = findViewById(R.id.skip_back);
        ImageButton mSkipForward = findViewById(R.id.skip_forward);

        // Close the now playing activity if the close button is selected
        mClosePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Retrieve the current song position and the entire list of songs from the intent
        Intent intent = getIntent();
        mSongPosition = intent.getIntExtra("currentPosition", 0);
        mSongsList = intent.getParcelableArrayListExtra("songList");

        // Set the shuffle button image resource, then check if an extra containing the state of
        // the shuffle button exists. If it does, toggle the shuffle button to on by changing its
        // colour. Otherwise, keep it toggled off by default and don't change its colour
        mShuffle.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle));
        if (intent.getBooleanExtra("shuffle", false)) {
            mShuffle.setColorFilter(getResources().getColor(R.color.colorAccent));
            mShuffle.setTag("pink");
            mShuffleSequence = intent.getIntArrayExtra("shuffleSequence");
            mShuffleIterator = intent.getIntExtra("shuffleIterator", 0);
        } else {
            mShuffle.setTag("black");
        }

        // Change the state of the shuffle button to reflect whether it has been toggled on or off
        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShuffle.getTag().equals("black")) {
                    mShuffle.setColorFilter(getResources().getColor(R.color.colorAccent));
                    mShuffle.setTag("pink");
                } else if (mShuffle.getTag().equals("pink")) {
                    mShuffle.setColorFilter(getResources().getColor(android.R.color.black));
                    mShuffle.setTag("black");
                }
            }
        });

        // Get the current song being played from the list passed via the intent
        Song currentSong = mSongsList.get(mSongPosition);

        // Set the song details to display the relevant content based on the current song object
        mSongImage.setImageResource(currentSong.getSongImageID());
        mSongName.setText(currentSong.getSongName());
        mSongArtist.setText(currentSong.getArtistName());

        // Set the slider's max value and the slider text
        mSongLength = currentSong.getSongLength();
        mSongMax.setText(convertSongLength(mSongLength));
        mSongSlider.setMax(mSongLength);

        // Start the counter to replicate the song playing
        startTimer(mSongLength);

        // Set listeners for when the slider bar progress is changed and for when the user
        // releases the slider at a new position
        mSongSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // When the slider progress is changed, change the text either side of it
                // to reflect this
                mSongMin.setText(convertSongLength(progress));
                mSongMax.setText(convertSongLength(mSongLength - progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // When the user interacts with the slider, cancel the timer to prevent the
                // slider from jumping around whilst they hold it down
                cdTimer.cancel();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // When the user releases the slider at a chosen position, retrieve the selected
                // slider value and find how much time is left in the song. Pass this to a new
                // timer and start it to continue playing the song from this point. However, the
                // player should only start again if the user hasn't paused the song
                int timeLeft = mSongLength - mSongSlider.getProgress();
                if (mPlayOrPause.getTag().equals("pause")) {
                    startTimer(timeLeft);
                }
            }
        });

        // If the user presses the skip back button, restart the song from the beginning. However,
        // if they press it within the first 5 seconds of the song, start playing the previous
        // song in the list if one is available, otherwise just restart the song again
        mSkipBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int songPosition = mSongSlider.getProgress();

                // Only restart the song if the song time position is greater than 5 seconds.
                // Otherwise, skip to the previous song in the list
                if (songPosition <= 5) {
                    // If the shuffle button is toggled on, start the previous song in the shuffle
                    // sequence if we aren't already at the beginning
                    if (mShuffle.getTag().equals("pink")) {
                        // If a shuffle sequence already exists and the shuffle iterator is not
                        // at the first index position in the shuffle sequence, decrease the
                        // iterator by one and use it to move back to the previous song in the
                        // sequence
                        if (mShuffleSequence != null) {
                            if (mShuffleIterator > 0) {
                                mShuffleIterator--;

                                Intent restartSelf = new Intent(NowPlayingActivity.this, NowPlayingActivity.class);
                                restartSelf.putExtra("shuffle", true);
                                restartSelf.putExtra("currentPosition", mShuffleSequence[mShuffleIterator]);
                                restartSelf.putParcelableArrayListExtra("songList", mSongsList);
                                restartSelf.putExtra("shuffleSequence", mShuffleSequence);
                                restartSelf.putExtra("shuffleIterator", mShuffleIterator);

                                finish();
                                startActivity(restartSelf);
                            } else {
                                restartSong();
                            }
                        } else {
                            // If a shuffle sequence doesn't exist, this is the first song in the
                            // sequence, so keep replaying it
                            restartSong();
                        }
                    } else {
                        // If the shuffle button is toggled off, use a list iterator to check if
                        // a previous element in the normal song list exists
                        ListIterator<Song> listIterator = mSongsList.listIterator(mSongPosition);

                        // If one doesn't, restart the current song. If one does, restart the
                        // whole activity with this new song as the selected member of the list
                        if (listIterator.hasPrevious()) {
                            Intent restartSelf = new Intent(NowPlayingActivity.this, NowPlayingActivity.class);
                            restartSelf.putExtra("currentPosition", mSongPosition - 1);
                            restartSelf.putParcelableArrayListExtra("songList", mSongsList);

                            finish();
                            startActivity(restartSelf);
                        } else {
                            restartSong();
                        }
                    }
                } else {
                    restartSong();
                }
            }
        });

        // Add a custom string tag to the play/pause button to indicate what image is initially
        // set on creation
        mPlayOrPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle));
        mPlayOrPause.setTag("pause");

        // Then, add functionality to pause and play the song that also changes the image and the
        // tag with each selection
        mPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayOrPause.getTag().equals("pause")) {
                    // If the user has paused the song, stop the timer and change the image
                    // resource and the custom tag of the button to the play image/text
                    cdTimer.cancel();
                    mPlayOrPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle));
                    mPlayOrPause.setTag("play");
                } else if (mPlayOrPause.getTag().equals("play")) {
                    // If the user has resumed the song, restart the timer again and change the
                    // image resource and custom tag back to the pause image/text
                    int timeLeft = mSongLength - mSongSlider.getProgress();
                    startTimer(timeLeft);
                    mPlayOrPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle));
                    mPlayOrPause.setTag("pause");
                }
            }
        });

        // If the user presses the skip forward button, run the endSong method to skip to the
        // next song or close the player
        mSkipForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the repeat button is toggled on and the user presses skip forward, we still
                // want to allow them to move onto the next song, so we can do this by changing
                // the repeat tag manually before ending the song
                mRepeat.setTag("black");
                endSong();
            }
        });

        // Toggle the repeat button off as default, using a custom tag again to indicate which
        // state it is currently in
        mRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat));
        mRepeat.setTag("black");

        // Change its colour to reflect whether it has been toggled on or off by the user
        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRepeat.getTag().equals("black")) {
                    mRepeat.setColorFilter(getResources().getColor(R.color.colorAccent));
                    mRepeat.setTag("pink");
                } else if (mRepeat.getTag().equals("pink")) {
                    mRepeat.setColorFilter(getResources().getColor(android.R.color.black));
                    mRepeat.setTag("black");
                }
            }
        });
    }

    // Convert the song length from just seconds to minutes and seconds
    public static String convertSongLength(int lengthInSeconds) {
        return (lengthInSeconds / 60) + ":" + String.format(Locale.ENGLISH, "%02d", (lengthInSeconds % 60));
    }

    // Start a new countdown timer for a given amount of time in seconds. Include functionality
    // that can change the seekbar text values to reflect the passing of time and can start a
    // new song once it completes
    private void startTimer(int maxTimeInSeconds) {
        cdTimer = new CountDownTimer(maxTimeInSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisLeft) {
                int secondsPassed = mSongLength - ((int) TimeUnit.MILLISECONDS.toSeconds(millisLeft));
                mSongSlider.setProgress(secondsPassed);
            }

            @Override
            public void onFinish() {
                endSong();
            }
        }.start();
    }

    // Restart a song from the beginning by resetting the timer and the slider progress
    private void restartSong() {
        cdTimer.cancel();
        mSongSlider.setProgress(0);
        startTimer(mSongLength);
    }

    // When the current song ends, start the next song in the list if one is available.
    // Otherwise, close the player and go back to the library activity
    private void endSong() {
        // If the repeat button is toggled on, restart the song
        if (mRepeat.getTag().equals("pink")) {
            restartSong();
        } else {
            // If the shuffle button is toggled on and the repeat button is toggled off, begin
            // or carry on shuffling through the songs randomly based on whether a shuffle
            // sequence exists or not
            if (mShuffle.getTag().equals("pink")) {
                if (mShuffleSequence != null) {
                    // If a shuffle sequence already exists and the shuffle iterator is not at the
                    // last index position in the shuffle sequence, increase the iterator by one
                    // and use it to move onto the next song in the sequence
                    if (mShuffleIterator < (mShuffleSequence.length - 1)) {
                        mShuffleIterator++;

                        Intent restartSelf = new Intent(NowPlayingActivity.this, NowPlayingActivity.class);
                        restartSelf.putExtra("shuffle", true);
                        restartSelf.putExtra("currentPosition", mShuffleSequence[mShuffleIterator]);
                        restartSelf.putParcelableArrayListExtra("songList", mSongsList);
                        restartSelf.putExtra("shuffleSequence", mShuffleSequence);
                        restartSelf.putExtra("shuffleIterator", mShuffleIterator);

                        finish();
                        startActivity(restartSelf);
                    } else {
                        // If the shuffle iterator is at the last position in the sequence, close
                        // the player
                        finish();
                    }
                } else {
                    // If the shuffle button was only toggled on in this song, and there is more
                    // than one available song in the list, create a new random shuffle sequence
                    // and use this to start the next song
                    if (mSongsList.size() > 1) {
                        int[] shuffleSequence = generateShuffle(mSongPosition, mSongsList);
                        mShuffleIterator = 1;

                        Intent restartSelf = new Intent(NowPlayingActivity.this, NowPlayingActivity.class);
                        restartSelf.putExtra("shuffle", true);
                        restartSelf.putExtra("currentPosition", shuffleSequence[mShuffleIterator]);
                        restartSelf.putParcelableArrayListExtra("songList", mSongsList);
                        restartSelf.putExtra("shuffleSequence", shuffleSequence);
                        restartSelf.putExtra("shuffleIterator", mShuffleIterator);

                        finish();
                        startActivity(restartSelf);
                    } else {
                        finish();
                    }
                }
            } else {
                // If the repeat and shuffle buttons are both toggled off, use a list iterator
                // to check if a next element in the normal song list exists. As the list
                // iterator actually exists in between elements, we need to move up by one
                // to check if a next song is available
                ListIterator<Song> listIterator = mSongsList.listIterator(mSongPosition);
                listIterator.next();

                if (listIterator.hasNext()) {
                    // If one does exist, restart the whole activity with this new Song as the
                    // selected member of the list
                    Intent restartSelf = new Intent(NowPlayingActivity.this, NowPlayingActivity.class);
                    restartSelf.putExtra("currentPosition", mSongPosition + 1);
                    restartSelf.putParcelableArrayListExtra("songList", mSongsList);

                    finish();
                    startActivity(restartSelf);
                } else {
                    finish();
                }
            }
        }
    }

    // Create a shuffle sequence that randomises the order in which the songs from the song list
    // should be played
    public static int[] generateShuffle(int currentPosition, ArrayList<Song> songList) {
        // Create an int array that is the same size as the ArrayList of songs and populate
        // it with index values in ascending order
        int[] shuffleSequence = new int[songList.size()];
        for (int i = 0; i < shuffleSequence.length; i++) {
            shuffleSequence[i] = i;
        }

        // Then, use a Fisher-Yates shuffle to randomise their order (code is from the following
        // link: https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array)

        Random rnd = new Random();
        for (int i = shuffleSequence.length - 1; i > 0; i--) {
            // Get a random index number between 0 and the array size
            int index = rnd.nextInt(i + 1);

            // Use a temporary value to store the value at this index position and then swap
            // this around with the value at the current iteration position in the for loop
            int temp = shuffleSequence[index];
            shuffleSequence[index] = shuffleSequence[i];
            shuffleSequence[i] = temp;
        }

        // Make sure that the current song, in which the shuffle button was toggled on, is the
        // first in the shuffle sequence
        for (int i = 0; i < shuffleSequence.length; i++) {
            if (shuffleSequence[i] == currentPosition) {
                int temp = shuffleSequence[0];
                shuffleSequence[0] = shuffleSequence[i];
                shuffleSequence[i] = temp;
            }
        }

        return shuffleSequence;
    }
}
