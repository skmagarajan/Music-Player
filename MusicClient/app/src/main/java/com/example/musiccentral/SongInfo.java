package com.example.musiccentral;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class SongInfo implements Parcelable {
    public String songName;
    public String composer;
    public String songURL;
    public Bundle b = new Bundle();

    public SongInfo(String songName, String composer, String songURL, byte[] thumbnail) {
        this.songName =  songName;
        this.composer =  composer;
        this.songURL =  songURL;
        b.putByteArray(songName,thumbnail);
    }

    public static final Parcelable.Creator<SongInfo> CREATOR = new Parcelable.Creator<SongInfo>() {
        public SongInfo createFromParcel(Parcel in) {
            return new SongInfo(in);
        }

        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };

    public SongInfo() {
    }

    private SongInfo(Parcel in) {
        readFromParcel(in);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(songName);
        out.writeString(songURL);
        out.writeString(composer);
        out.writeBundle(b);
    }

    public void readFromParcel(Parcel in) {
        songName = in.readString();
        songURL = in.readString();
        composer = in.readString();
        b = in.readBundle();
    }

    public int describeContents() {
        return 0;
    }
}
