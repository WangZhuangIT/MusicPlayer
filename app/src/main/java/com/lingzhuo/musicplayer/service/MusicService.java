package com.lingzhuo.musicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.lingzhuo.musicplayer.R;
import com.lingzhuo.musicplayer.activity.MainActivity;
import com.lingzhuo.musicplayer.utils.MusicListFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private String nowFilePath = null;
    private static int startTime = 0;
    private RemoteViews rv;
    private List<File> musicList;
    private int nowPosition;
    private MySefReceiver mySefReceiver;
    private IntentFilter mySefFilter;
    private boolean flag = true;
    private boolean isOrderPlay = true;

    public MusicService() {
        this.musicList = MusicListFactory.newInstance().getList();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        initMySefReceiver();
    }

    private void initMySefReceiver() {
        mySefReceiver = new MySefReceiver();
        mySefFilter = new IntentFilter();
        mySefFilter.addAction("MUSIC_EXIT");
        mySefFilter.addAction("SEEKBAR_UPDATE");
        mySefFilter.addAction("MUSICBAR_PLAY");
        mySefFilter.addAction("MUSICBAR_LEFT");
        mySefFilter.addAction("MUSICBAR_RIGHT");
        mySefFilter.addAction("UPDATE_PLAY_ORDER");
        mySefFilter.addAction("UPDATE_UI");
        registerReceiver(mySefReceiver, mySefFilter);
    }

    private void showMusicBar(String singer, String songName) {
        Notification notification = new Notification();

        notification.icon = R.mipmap.stateline_ic;
        rv = new RemoteViews(getPackageName(), R.layout.layout_musicbar);
        PendingIntent intent_play = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("MUSICBAR_PLAY"), 0);
        rv.setOnClickPendingIntent(R.id.musicbar_play, intent_play);
        PendingIntent intent_left = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("MUSICBAR_LEFT"), 0);
        rv.setOnClickPendingIntent(R.id.musicbar_left, intent_left);
        PendingIntent intent_right = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("MUSICBAR_RIGHT"), 0);
        rv.setOnClickPendingIntent(R.id.musicbar_right, intent_right);
        PendingIntent intent_exit = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("MUSIC_EXIT"), 0);
        rv.setOnClickPendingIntent(R.id.musicbar_exit, intent_exit);


        rv.setTextViewText(R.id.musicbar_songname, (nowPosition + 1) + "." + songName + "-" + singer);
        if (mediaPlayer.isPlaying()) {
            rv.setImageViewResource(R.id.musicbar_play, R.mipmap.play_pic);
        } else {
            rv.setImageViewResource(R.id.musicbar_play, R.mipmap.pause_pic);
        }
        notification.contentView = rv;
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, MainActivity.class), 0);
        notification.contentIntent = pendingIntent;
        startForeground(1, notification);
    }

    private void init() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if (intent == null) {
            //必须加入对intent是否为空的判断
        } else {
            nowPosition = intent.getIntExtra("POSITION", 0);
            String filePath = musicList.get(nowPosition).getAbsolutePath();
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(filePath);
            String singer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String songName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            getMusicDuration(retriever);
            if (TextUtils.isEmpty(filePath)) {

            } else if (!filePath.equals(nowFilePath)) {
                nowFilePath = filePath;
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    startTime = 0;
                    Intent intent1 = new Intent("IS_PLAYING");
                    sendBroadcast(intent1);
                    showNotification("开始播放：" + (nowPosition + 1) + "." + songName);
                    sendBroadcast(new Intent("NEW_SONG"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    Intent intent1 = new Intent("NOT_PLAYING");
                    sendBroadcast(intent1);
                    showNotification("暂停播放" + (nowPosition + 1) + "." + songName);
                    sendBroadcast(new Intent("PAUSE_SONG"));
                } else {
                    mediaPlayer.start();
                    Intent intent1 = new Intent("IS_PLAYING");
                    sendBroadcast(intent1);
                    showNotification("继续播放：" + (nowPosition + 1) + "." + songName);
                    sendBroadcast(new Intent("COUNTINUE_SONG"));
                }
            }
            showMusicBar(singer, songName);
        }

        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    sendBroadcast(new Intent("MUSICBAR_RIGHT"));
                }
            });
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void getMusicDuration(MediaMetadataRetriever retriever) {
        int allSeconds = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
        Intent intent = new Intent("ALL_SECONDS");
        intent.putExtra("allSeconds", allSeconds);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showNotification(String msg) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker(msg);
        Notification notification = builder.build();
        manager.notify(2, notification);
        manager.cancel(2);
    }

    class MySefReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "MUSIC_EXIT":
                    flag = false;
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    sendBroadcast(new Intent("PAUSE_SONG"));
                    Intent intent1 = new Intent("NOT_PLAYING");
                    sendBroadcast(intent1);
                    stopSelf();
                    unregisterReceiver(mySefReceiver);
                    break;
                case "SEEKBAR_UPDATE":
                    startTime = intent.getIntExtra("seekBar_pargress", 0);
                    mediaPlayer.seekTo(startTime * 1000);
                    break;
                case "MUSICBAR_PLAY":
                    Intent intent2 = new Intent(getApplicationContext(), MusicService.class);
                    intent2.putExtra("POSITION", nowPosition);
                    startService(intent2);
                    break;
                case "MUSICBAR_LEFT":
                    if (isOrderPlay) {
                        if (nowPosition <= 0) {
                            showNotification("已经到列表第一首");
                        } else {
                            nowPosition--;
                        }
                    } else {
                        nowPosition=(int)(Math.random()*musicList.size());
                    }
                    Intent intent3 = new Intent(getApplicationContext(), MusicService.class);
                    intent3.putExtra("POSITION", nowPosition);
                    startService(intent3);
                    Intent left_intent=new Intent("UPDATE_POSITION");
                    left_intent.putExtra("POSITION", nowPosition);
                    sendBroadcast(left_intent);
                    break;
                case "MUSICBAR_RIGHT":
                    if (isOrderPlay){
                        if (nowPosition == musicList.size() - 1) {
                            showNotification("已经到列表最后一首");
                        } else {
                            nowPosition++;
                        }
                    }else {
                        nowPosition=(int)(Math.random()*musicList.size());
                    }
                    Intent intent4 = new Intent(getApplicationContext(), MusicService.class);
                    intent4.putExtra("POSITION", nowPosition);
                    startService(intent4);
                    Intent right_intent=new Intent("UPDATE_POSITION");
                    right_intent.putExtra("POSITION", nowPosition);
                    sendBroadcast(right_intent);
                    break;
                case "UPDATE_PLAY_ORDER":
                    isOrderPlay=intent.getBooleanExtra("isOrderPlay",true);
                    break;
                case "UPDATE_UI":
                    Intent intent5=new Intent("BACK_UPDATE_UI");
                    intent5.putExtra("nowPosition",nowPosition);
                    intent5.putExtra("MEDIA_IS_PLAYING",mediaPlayer.isPlaying());
                    intent5.putExtra("isOrderPlay",isOrderPlay);
                    intent5.putExtra("nowDuration",mediaPlayer.getCurrentPosition()/1000);
                    intent5.putExtra("duration",mediaPlayer.getDuration()/1000);
                    sendBroadcast(intent5);
                    break;
            }
        }
    }
}
