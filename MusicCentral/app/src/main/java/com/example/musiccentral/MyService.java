package com.example.musiccentral;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;



public class MyService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private Notification notification ;
    private static String CHANNEL_ID = "Music Central" ;
    private MyImpl impl = new MyImpl();

    private Map<Integer, SongInfo> ss = new HashMap<Integer, SongInfo>();

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    // UB 11-12-2018:  Now Oreo wants communication channels...
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Central notification";
            String description = "The channel for music central notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        final Intent notificationIntent = new Intent(getApplicationContext(),
                MainActivity.class);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0) ;

        notification =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setChannelId(CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setOngoing(true).setContentTitle("Music Client")
                        .setContentText("Service connected")
                        .setTicker("Hellooo !")
                        .setFullScreenIntent(pendingIntent, false)
                        .build();

        startForeground(NOTIFICATION_ID, notification);

    }
    @Override
    public IBinder onBind(Intent intent) {
        return impl;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MyImpl extends MusicAIDL.Stub {

        public SongInfo getN(int k) throws RemoteException{
            synchronized (this) {
                String[] Songs = getResources().getStringArray(R.array.songs);
                String[] Composer = getResources().getStringArray(R.array.com);
                String[] URL = getResources().getStringArray(R.array.URL);
                ss.put(1, new SongInfo(Songs[0], Composer[0], URL[0], ImagetoByteArr(R.drawable.smile)));
                ss.put(2, new SongInfo(Songs[1], Composer[1], URL[1], ImagetoByteArr(R.drawable.ukulele)));
                ss.put(3, new SongInfo(Songs[2], Composer[2], URL[2], ImagetoByteArr(R.drawable.sweet)));
                ss.put(4, new SongInfo(Songs[3], Composer[3], URL[3], ImagetoByteArr(R.drawable.happiness)));
                ss.put(5, new SongInfo(Songs[4], Composer[4], URL[4], ImagetoByteArr(R.drawable.acousticbreeze)));
                ss.put(6, new SongInfo(Songs[5], Composer[5], URL[5], ImagetoByteArr(R.drawable.buddy)));
                return ss.get(k);
            }
        }

        @Override
        public String getURL(int k) throws RemoteException {
            synchronized (this) {
                String[] URL = getResources().getStringArray(R.array.URL);
                return URL[k];
            }
        }
    }

    private byte[] ImagetoByteArr(int img){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),img);;
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

}
