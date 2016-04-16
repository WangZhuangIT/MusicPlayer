package com.lingzhuo.musicplayer.utils;

import java.io.File;
import java.util.List;

/**
 * Created by Wang on 2016/4/13.
 */
public class MusicListFactory {
    private static List<File> musicList;
    private static MusicListFactory musicListFactory;

    private MusicListFactory(){
    }

    public synchronized static MusicListFactory newInstance() {
        if (musicListFactory==null){
            musicListFactory=new MusicListFactory();
        }
        return musicListFactory;
    }

    public  List<File> getList(){
        return musicList;
    }

    public void setList(List<File> musicList){
        this.musicList=musicList;
    }
}
