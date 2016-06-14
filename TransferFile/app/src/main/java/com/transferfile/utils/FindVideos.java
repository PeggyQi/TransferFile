package com.transferfile.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.ListView;

import com.transferfile.Bean.VideoBean;
import com.transferfile.adapter.VideoAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FindVideos {

    public List<VideoBean> getVideoInfos(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        List<VideoBean> videoInfos = new ArrayList<VideoBean>();
        for (int i = 0; i < cursor.getCount(); i++) {
            VideoBean videoInfo = new VideoBean();                               //新建一个视频对象,将从cursor里读出的信息存放进去,直到取完cursor里面的内容为止.
            cursor.moveToNext();


            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Video.Media._ID));	//视频id

            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Video.Media.TITLE)));//视频标题

            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Video.Media.DURATION));//时长

            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Video.Media.SIZE));	//文件大小

            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media.DATA));	//文件路径

            String album = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media.ALBUM)); //唱片图片

            if ( duration/(1000 * 60) >= 0) {		//只把0分钟以上的音乐添加到集合当中
                videoInfo.setId(id);
                videoInfo.setTitle(title);
                videoInfo.setDuration(duration);
                videoInfo.setSize(size);
                videoInfo.setUrl(url);
                videoInfos.add(videoInfo);
            }
        }
        return videoInfos;
    }

//    public void setListAdpter(Context context, List<VideoBean> videoInfos, ListView mMusicList) {
//
//        List<HashMap<String, String>> videolist = new ArrayList<HashMap<String, String>>();
//        VideoAdapter mAdapter = new VideoAdapter(context, videoInfos);
//        mMusicList.setAdapter(mAdapter);
//    }
}

