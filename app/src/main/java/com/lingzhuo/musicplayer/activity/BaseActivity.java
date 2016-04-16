package com.lingzhuo.musicplayer.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.lingzhuo.musicplayer.utils.ActivityCollectior;

/**
 * Created by Wang on 2016/4/13.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        ActivityCollectior.addActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCollectior.removeActivity(this);
    }
}
