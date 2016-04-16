package com.lingzhuo.musicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.lingzhuo.musicplayer.R;
import com.lingzhuo.musicplayer.utils.FindMusicThread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartFlashActivity extends BaseActivity {
    private static final int LIST_MUSIC_DONE=1;

    private List<File> musicList;
    private File rootDir,rootDir2;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_flash);
        init();
        new Thread(new FindMusicThread(musicList,myHandler,rootDir,rootDir2,getApplicationContext())).start();

    }

    private void init() {
        musicList=new ArrayList<>();
        rootDir=Environment.getExternalStorageDirectory();
        rootDir2=new File("/storage/extSdCard");
    }


    private Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1){
                case LIST_MUSIC_DONE:
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

}
