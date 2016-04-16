package com.lingzhuo.musicplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lingzhuo.musicplayer.R;
import com.lingzhuo.musicplayer.adapter.MusicAdapter;
import com.lingzhuo.musicplayer.service.MusicService;
import com.lingzhuo.musicplayer.utils.MusicListFactory;

import java.io.File;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {

    private List<File> musicList;
    private ListView listView_music;
    private TextView textView_duration;
    private TextView textView_nowDuration;
    private SeekBar seekBar;
    private int nowPosition = 0;
    private Button button_play, button_left, button_right,button_shunxu;
    private UtilsReceiver utilsReceiver;
    private IntentFilter utilsFilter;

    private boolean isPlaying=false;
    private int nowDuration=0;
    private boolean flag=true;
    private boolean isOrderPlay=true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicList = MusicListFactory.newInstance().getList();
        init();
        initUtilsReceiver();
        MusicAdapter adapter = new MusicAdapter(getApplicationContext(), musicList);
        listView_music.setAdapter(adapter);
        listView_music.setOnItemClickListener(this);
        Intent intent=new Intent("UPDATE_UI");
        sendBroadcast(intent);
    }


    private void initUtilsReceiver() {
        utilsReceiver = new UtilsReceiver();
        utilsFilter = new IntentFilter();
        utilsFilter.addAction("IS_PLAYING");
        utilsFilter.addAction("NOT_PLAYING");
        utilsFilter.addAction("ALL_SECONDS");
        utilsFilter.addAction("UPDATE_DURATION");
        utilsFilter.addAction("NEW_SONG");
        utilsFilter.addAction("PAUSE_SONG");
        utilsFilter.addAction("COUNTINUE_SONG");
        utilsFilter.addAction("BACK_UPDATE_UI");
        utilsFilter.addAction("UPDATE_POSITION");

        registerReceiver(utilsReceiver, utilsFilter);

        Thread countThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isPlaying){
                        nowDuration++;
                    }
                    Intent intent=new Intent("UPDATE_DURATION");
                    intent.putExtra("nowSeconds",nowDuration);
                    sendBroadcast(intent);
                }
            }
        });
        countThread.start();

    }

    private void init() {
        listView_music = (ListView) findViewById(R.id.listView_music);
        textView_duration = (TextView) findViewById(R.id.textView_duration);
        textView_nowDuration = (TextView) findViewById(R.id.textView_nowDuration);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        button_play = (Button) findViewById(R.id.button_play);
        button_left = (Button) findViewById(R.id.button_left);
        button_right = (Button) findViewById(R.id.button_right);
        button_right.setOnClickListener(this);
        button_left.setOnClickListener(this);
        button_play.setOnClickListener(this);
        button_shunxu= (Button) findViewById(R.id.button_shunxu);
        button_shunxu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button_play:
                intent = new Intent(this, MusicService.class);
                intent.putExtra("POSITION", nowPosition);
                startService(intent);
                break;
            case R.id.button_left:
                clickLeftButton();
                break;
            case R.id.button_right:
                clickRightButton();
                break;
            case R.id.button_shunxu:
                changePlayOrder();
                break;
        }
    }

    private void changePlayOrder() {
        isOrderPlay=!isOrderPlay;
        if (isOrderPlay){
            Toast.makeText(MainActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            button_shunxu.setBackgroundResource(R.mipmap.shunxu_shunxu);
        }else {
            Toast.makeText(MainActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
            button_shunxu.setBackgroundResource(R.mipmap.shunxu_sujip);
        }
        Intent intent=new Intent("UPDATE_PLAY_ORDER");
        intent.putExtra("isOrderPlay",isOrderPlay);
        sendBroadcast(intent);
    }

    private void clickLeftButton() {
        Intent intent;
        if (isOrderPlay) {
            if (nowPosition == 0) {
                Toast.makeText(MainActivity.this, "当前已经第一首", Toast.LENGTH_SHORT).show();
            }else{
                nowPosition--;
            }
        }else {
            nowPosition=(int)(Math.random()*musicList.size());
        }
        intent = new Intent(this, MusicService.class);
        intent.putExtra("POSITION", nowPosition);
        startService(intent);
    }

    private void clickRightButton() {
        Intent intent;
        if (isOrderPlay){
            if (nowPosition == musicList.size()-1) {
                isPlaying=false;
                Toast.makeText(MainActivity.this, "当前已经最后一首", Toast.LENGTH_SHORT).show();
            }else{
                nowPosition++;
            }
        }else {
            nowPosition=(int)(Math.random()*musicList.size());
        }
        intent = new Intent(this, MusicService.class);
        intent.putExtra("POSITION", nowPosition);
        startService(intent);
    }


    class UtilsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "IS_PLAYING":
                    button_play.setBackgroundResource(R.mipmap.play_pic);
                    break;
                case "NOT_PLAYING":
                    button_play.setBackgroundResource(R.mipmap.pause_pic);
                    break;
                case "ALL_SECONDS":
                    int allSeconds=intent.getIntExtra("allSeconds",0);
                    seekBar.setMax(allSeconds);
                    textView_duration.setText(allSeconds/60+":"+allSeconds%60);
                    break;
                case "UPDATE_DURATION":
                    int nowSeconds=intent.getIntExtra("nowSeconds",0);
                    seekBar.setProgress(nowSeconds);
                    textView_nowDuration.setText(nowSeconds/60+":"+nowSeconds%60);
                    break;
                case "NEW_SONG":
                    nowDuration=0;
                    isPlaying=true;
                    break;
                case "PAUSE_SONG":
                    isPlaying=false;
                    break;
                case "COUNTINUE_SONG":
                    isPlaying=true;
                    break;
                case "UPDATE_POSITION":
                    nowPosition=intent.getIntExtra("POSITION",0);
                    Log.d("***","888888"+"**"+nowPosition);
                    break;
                case "BACK_UPDATE_UI":
                    isOrderPlay=intent.getBooleanExtra("isOrderPlay",true);
                    nowPosition=intent.getIntExtra("nowPosition",0);
                    isPlaying=intent.getBooleanExtra("MEDIA_IS_PLAYING",false);
                    nowDuration=intent.getIntExtra("nowDuration",0);
                    int duration=intent.getIntExtra("duration",0);
                    textView_duration.setText(duration/60+":"+duration%60);
                    textView_nowDuration.setText(nowDuration/60+":"+nowDuration%60);
                    seekBar.setMax(duration);
                    if (isPlaying){
                        button_play.setBackgroundResource(R.mipmap.play_pic);
                    }else {
                        button_play.setBackgroundResource(R.mipmap.pause_pic);
                    }
                    if (isOrderPlay){
                        button_shunxu.setBackgroundResource(R.mipmap.shunxu_shunxu);
                    }else {
                        button_shunxu.setBackgroundResource(R.mipmap.shunxu_sujip);
                    }
                    break;
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        nowPosition=position;
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("POSITION", nowPosition);
        startService(intent);
    }

    //seekBar中用不到的两个方法
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Intent intent=new Intent("SEEKBAR_UPDATE");
        intent.putExtra("seekBar_pargress",seekBar.getProgress());
        sendBroadcast(intent);
        nowDuration=seekBar.getProgress();
        textView_nowDuration.setText(seekBar.getProgress()/60+":"+seekBar.getProgress()%60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag=false;
        unregisterReceiver(utilsReceiver);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
