package com.lingzhuo.musicplayer.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Wang on 2016/4/13.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

}
