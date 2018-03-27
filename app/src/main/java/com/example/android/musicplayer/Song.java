package com.example.android.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Comparator;

public class Song implements Parcelable, Comparable<Song> {

    private String mSongName;
    private String mArtistName;
    private String mAlbumName;
    private int mSongLength;
    private int mSongImageID;

    public Song(String songName, String artistName, String albumName, int songLength, int songImage) {
        this.mSongName = songName;
        this.mArtistName = artistName;
        this.mAlbumName = albumName;
        this.mSongLength = songLength;
        this.mSongImageID = songImage;
    }

    /*
    Implement the Parcelable interface so that we can bundle our ArrayList of Songs and pass
    it between the various Fragments used in the MainActivity.
    (See https://developer.android.com/reference/android/os/Parcelable.html)
    */
    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mSongName);
        out.writeString(mArtistName);
        out.writeString(mAlbumName);
        out.writeInt(mSongLength);
        out.writeInt(mSongImageID);
    }

    private Song(Parcel in) {
        this.mSongName = in.readString();
        this.mArtistName = in.readString();
        this.mAlbumName = in.readString();
        this.mSongLength = in.readInt();
        this.mSongImageID = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    /*
    Implement the Comparable interface so that we can sort the ArrayList of Songs by the
    song name, the artist(s) name and the album name when required.
    (See: https://developer.android.com/reference/java/lang/Comparable.html)
    */
    @Override
    public int compareTo(@NonNull Song song) {
        return this.getSongName().compareTo(song.getSongName());
    }

    /*
    The default compareTo method compares the song by their name. If we want to compare by
    album name instead, we need to implement the following anonymous class with its own
    compare methods. To then use this instead of the default, we use
    Collections.sort(songsList, Song.AlbumNameComparator);
    */
    public static Comparator<Song> AlbumNameComparator = new Comparator<Song>() {
        public int compare(Song song1, Song song2) {
            String songAlbum1 = song1.getAlbumName();
            String songAlbum2 = song2.getAlbumName();

            // Return in ascending order (descending order can be obtained by reversing
            // the Strings in this statement)
            return songAlbum1.compareTo(songAlbum2);
        }
    };

    public String getSongName() {
        return mSongName;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public int getSongLength() {
        return mSongLength;
    }

    public int getSongImageID() {
        return mSongImageID;
    }
}
