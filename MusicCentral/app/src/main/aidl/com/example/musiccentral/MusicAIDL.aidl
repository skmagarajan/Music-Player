// MusicAIDL.aidl
package com.example.musiccentral;
// Declare any non-default types here with import statements
import com.example.musiccentral.SongInfoAIDL;

interface MusicAIDL {
    SongInfo getN(int k);
    String getURL(int k);
}
