package com.lingzhuo.musicplayer.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lingzhuo.musicplayer.activity.MainActivity;

import java.io.File;
import java.util.List;

/**
 * Created by Wang on 2016/4/10.
 */
public class FindMusicThread implements Runnable {
    private static final int LIST_MUSIC_DONE = 1;

    private List<File> musicList;
    private Handler myHandler;
    private File rootDir, rootDir2;
    private Context context;

    public FindMusicThread(List<File> musicList, Handler myHandler, File rootDir, File rootDir2, Context context) {
        this.musicList = musicList;
        this.myHandler = myHandler;
        this.rootDir = rootDir;
        this.context = context;
        this.rootDir2 = rootDir2;
    }

    @Override
    public void run() {
        findMusicFiles(rootDir);
        findMusicFiles(rootDir2);
        MusicListFactory factory = MusicListFactory.newInstance();
        factory.setList(musicList);

        Message msg = new Message();
        msg.arg1 = LIST_MUSIC_DONE;
        myHandler.sendMessage(msg);

    }

    private void findMusicFiles(File rootDir) {
        if (rootDir.isDirectory()) {
            File[] files = rootDir.listFiles();
            if (files == null) {
            } else {
                for (File file : files) {
                    findMusicFiles(file);
                }
            }
        } else {
            if (rootDir.getName().endsWith(".mp3")) {
                musicList.add(rootDir);
            }
        }
    }

}
