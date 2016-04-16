package com.lingzhuo.musicplayer.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang on 2016/4/13.
 */
public class ActivityCollectior {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void removeAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

}
