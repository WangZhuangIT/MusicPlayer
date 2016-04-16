package com.lingzhuo.musicplayer.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lingzhuo.musicplayer.R;

import java.io.File;
import java.util.List;

/**
 * Created by Wang on 2016/4/10.
 */
public class MusicAdapter extends BaseAdapter {
    private Context context;
    private List<File> musicList;

    public MusicAdapter(Context context, List<File> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file=musicList.get(position);
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.layout_music_item,null);
            viewHolder.textView_name= (TextView) convertView.findViewById(R.id.music_name);
            viewHolder.textView_singer= (TextView) convertView.findViewById(R.id.music_singer);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());
        String singer=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String songName=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        viewHolder.textView_name.setText((position+1)+"."+songName);
        viewHolder.textView_singer.setText(singer);
        return convertView;
    }

    class ViewHolder{
        TextView textView_name;
        TextView textView_singer;
    }

}
