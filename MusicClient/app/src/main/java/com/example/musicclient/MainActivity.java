package com.example.musicclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musiccentral.MusicAIDL;
import com.example.musiccentral.SongInfo;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity Client";
    private MusicAIDL musicAIDL;
    private boolean mBound = false;
    private MediaPlayer mediaPlayer;
    ListView list;

    String[] Songtitle = new String[6];
    String[] Composer = new String[6];
    Bitmap[] bf = new Bitmap[6];
    byte[][] b = new byte[6][];

    private int NOT_STARTED = 404;
    private int WAITING = 444;
    private int PLAYING = 200;
    private int PAUSED = 300;
    int currentState;

    private Handler mHandler = new Handler() ;
    private ProgressBar mProgressBar ;
    SongInfo s1 = null;
    SongInfo s2 = null;
    SongInfo s3 = null;
    SongInfo s4 = null;
    SongInfo s5 = null;
    SongInfo s6 = null;

    private TextView t;
    ImageButton stopSong;
    ImageButton pauseSong;
    ImageButton playSong;

    EditText np;
    TextView songtv;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicAIDL = MusicAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicAIDL = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar) ;

        t = findViewById(R.id.status);

        stopSong = findViewById(R.id.stopSong);
        pauseSong = findViewById(R.id.pauseSong);
        playSong = findViewById(R.id.playSong);

        np = findViewById(R.id.np);
        songtv = findViewById(R.id.songinfotv);

        currentState = NOT_STARTED;

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Intent in = new Intent("com.example.service.AIDL");
                    if (mConnection != null) {
                        boolean b = bindService(convertImptoExp(in), mConnection, Context.BIND_AUTO_CREATE);
                        if (b) {

                            Thread t1 = new Thread(new CreateList());
                            t1.start();
                            TextView t = findViewById(R.id.status);
                            t.setText("Service binding ....");
                            mBound = true;
                        }
                        else {
                            mBound = false;
                            TextView t = findViewById(R.id.status);
                            t.setText("Service not bound");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Service not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // The toggle is disabled
                    if (mBound) {
                        unbindService(mConnection);
                        mBound = false;
                        t.setText("Service not bound");
                        if (currentState == PLAYING) {
                            mediaPlayer.stop();
                        }
                        list.setVisibility(View.INVISIBLE);
                        np.setVisibility(EditText.INVISIBLE);
                        songtv.setVisibility(TextView.INVISIBLE);
                        playSong.setEnabled(false);
                        pauseSong.setEnabled(false);
                        stopSong.setEnabled(false);
                        playSong.setAlpha(0f);
                        pauseSong.setAlpha(0f);
                        stopSong.setAlpha(0f);
                    }
                }
            }
        });

        stopSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mediaPlayer.stop();
                    t.setText("Song stopped");
                    currentState = WAITING;
                    pauseSong.setAlpha(0.5f);
                    pauseSong.setEnabled(false);
                    playSong.setAlpha(0.5f);
                    playSong.setEnabled(false);
                    stopSong.setAlpha(0.5f);
                    stopSong.setEnabled(false);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        pauseSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    t.setText("Song Paused");
                    mediaPlayer.pause();
                    currentState = PAUSED;
                    playSong.setAlpha(1f);
                    playSong.setEnabled(true);
                    pauseSong.setAlpha(0.5f);
                    pauseSong.setEnabled(false);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        playSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(currentState != WAITING){
                        mediaPlayer.start();
                        currentState = PLAYING;
                        t.setText("Song resumed");
                        pauseSong.setAlpha(1f);
                        pauseSong.setEnabled(true);
                        playSong.setAlpha(0.5f);
                        playSong.setEnabled(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    public class CreateList implements Runnable {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                public void run() {
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    mProgressBar.setProgress(0) ;
                }
            } ) ;

            try {  Thread.sleep(2000); }
            catch (InterruptedException e) { System.out.println("Thread interrupted!") ; } ;

            // again...
            mHandler.post(new Runnable() {
                public void run() {
                    mProgressBar.setProgress(33) ;
                }
            } );

            // Now get picture and bind it to mBitmap
            try {
                s1 = musicAIDL.getN(1);
                s2 = musicAIDL.getN(2);
                s3 = musicAIDL.getN(3);
                s4 = musicAIDL.getN(4);
                s5 = musicAIDL.getN(5);
                s6 = musicAIDL.getN(6);
            }
            catch (Exception e) {System.out.println("Could not read!") ; }

//          ... and again
            try {  Thread.sleep(2000); }
            catch (InterruptedException e) { System.out.println("Thread interrupted!") ; } ;

            // ... and again
            mHandler.post(new Runnable() {
                public void run() {
                    mProgressBar.setProgress(66) ;
                }
            } );

            // Tell UI thread to display picture and hide progress bar
            mHandler.post(new Runnable() {
                public void run() {
                    if(list!=null)
                        list.setVisibility(View.VISIBLE);
                    np.setVisibility(EditText.VISIBLE);
                    songtv.setVisibility(TextView.VISIBLE);
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE) ;
                    stopSong.isClickable();
                    Songtitle[0] = s1.songName;
                    Songtitle[1] = s2.songName;
                    Songtitle[2] = s3.songName;
                    Songtitle[3] = s4.songName;
                    Songtitle[4] = s5.songName;
                    Songtitle[5] = s6.songName;
                    Composer[0] = s1.composer;
                    Composer[1] = s2.composer;
                    Composer[2] = s3.composer;
                    Composer[3] = s4.composer;
                    Composer[4] = s5.composer;
                    Composer[5] = s6.composer;

                    Bundle[] img = new Bundle[6];

                    img[0] = s1.b;
                    b[0] = img[0].getByteArray(s1.songName);

                    img[1] = s2.b;
                    b[1] = img[1].getByteArray(s2.songName);

                    img[2] = s3.b;
                    b[2] = img[2].getByteArray(s3.songName);

                    img[3] = s4.b;
                    b[3] = img[3].getByteArray(s4.songName);

                    img[4] = s5.b;
                    b[4] = img[4].getByteArray(s5.songName);

                    img[5] = s6.b;
                    b[5] = img[5].getByteArray(s6.songName);

                    bf[0] = BitmapFactory.decodeByteArray(b[0], 0, b[0].length);
                    bf[1] = BitmapFactory.decodeByteArray(b[1], 0, b[1].length);
                    bf[2] = BitmapFactory.decodeByteArray(b[2], 0, b[2].length);
                    bf[3] = BitmapFactory.decodeByteArray(b[3], 0, b[3].length);
                    bf[4] = BitmapFactory.decodeByteArray(b[4], 0, b[4].length);
                    bf[5] = BitmapFactory.decodeByteArray(b[5], 0, b[5].length);
                    SongListAdapter adapter = new SongListAdapter(MainActivity.this, Songtitle, Composer, bf);
                    list = (ListView) findViewById(R.id.ls);
                    list.setAdapter(adapter);
                    t.setText("Service bound");

                    mediaPlayer = new MediaPlayer();

                    np.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            int val = Integer.parseInt(np.getText().toString())-1;
                            if(val >=0 && val < 6){ //If given number is valid
                                Drawable image = new BitmapDrawable(getResources(),BitmapFactory.decodeByteArray(b[val], 0, b[val].length));
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setIcon(image)
                                        .setTitle(Songtitle[val])
                                        .setMessage(Composer[val])
                                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                            }
                            else{   //If given input is not valid
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Not Found")
                                        .setMessage("Please enter valid Number")
                                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                            }
                            return true;
                        }
                    });

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // TODO Auto-generated method stub
                            playSong.setEnabled(false);
                            pauseSong.setEnabled(true);
                            stopSong.setEnabled(true);
                            playSong.setAlpha(0.5f);
                            pauseSong.setAlpha(1f);
                            stopSong.setAlpha(1f);

                            if(currentState != PLAYING) //If music player is not playing
                                mediaPlayer = new MediaPlayer();
                            else{
                                mediaPlayer.stop();     //If playing stop that song and start new clicked song
                                mediaPlayer = new MediaPlayer();
                            }


                            try {
                                mediaPlayer.setDataSource(musicAIDL.getURL(position));
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                                {
                                    @Override
                                    public void onPrepared(MediaPlayer mp)
                                    {
                                        t.setText("Song playing");
                                        pauseSong.setAlpha(1f);
                                        pauseSong.setEnabled(true);
                                        playSong.setAlpha(0.5f);
                                        playSong.setEnabled(false);
                                        mediaPlayer.start();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            currentState=PLAYING;

                        }
                    });
                }
            } );
        }
    } ;

    @Override
    protected void onResume() {
        super.onResume();
        if(mBound) {
            TextView t = findViewById(R.id.status);
            t.setText("Service bound");
            playSong.setEnabled(true);
            pauseSong.setEnabled(true);
            stopSong.setEnabled(true);
            playSong.setAlpha(1f);
            pauseSong.setAlpha(1f);
            stopSong.setAlpha(1f);
        }
        else{
            TextView t = findViewById(R.id.status);
            t.setText("Service not bound");
            playSong.setEnabled(false);
            pauseSong.setEnabled(false);
            stopSong.setEnabled(false);
            playSong.setAlpha(0f);
            pauseSong.setAlpha(0f);
            stopSong.setAlpha(0f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if(mBound)
                unbindService(mConnection);
            t.setText("Stopped and unbinded service");
            currentState = NOT_STARTED;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Intent convertImptoExp(Intent im){
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(im,0);
        if (resolveInfoList == null || resolveInfoList.size() != 1){
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName,serviceInfo.serviceInfo.name);
        Intent ex = new Intent(im);
        ex.setComponent(component);
        return ex;
    }

}
