package com.transferfile.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.ListView;

import com.transferfile.Bean.MusicBean;
import com.transferfile.adapter.MusicAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FindSongs {

    public List<MusicBean> getMp3Infos(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<MusicBean> mp3Infos = new ArrayList<MusicBean>();
        for (int i = 0; i < cursor.getCount(); i++) {
            MusicBean mp3Info = new MusicBean();                               //新建一个歌曲对象,将从cursor里读出的信息存放进去,直到取完cursor里面的内容为止.
            cursor.moveToNext();

            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));	//音乐id

            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题

            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家

            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长

            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));	//文件大小

            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));	//文件路径

            String album = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM)); //唱片图片

            long album_id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)); //唱片图片ID

            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐

            if (isMusic != 0 && duration/(1000 * 60) >= 0) {		//只把0分钟以上的音乐添加到集合当中
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setAlbum_id(album_id);
                mp3Infos.add(mp3Info);
            }
        }
        return mp3Infos;
    }

    public void setListAdpter(Context context, List<MusicBean> mp3Infos, ListView mMusicList) {

        List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
       MusicAdapter mAdapter = new MusicAdapter(context, mp3Infos);
        mMusicList.setAdapter(mAdapter);
    }
}

