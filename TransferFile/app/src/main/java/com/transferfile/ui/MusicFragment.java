package com.transferfile.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.transferfile.Bean.MusicBean;
import com.transferfile.R;
import com.transferfile.adapter.MusicAdapter;
import com.transferfile.utils.FindSongs;

import java.util.List;

@SuppressLint("ValidFragment")
public class MusicFragment extends Fragment {
    public static String MusicFragment_onDestroyView="MusicFragment_onDestroyView";
    private String mTitle;
    private ListView musicList;
    private FindSongs findSongs;
    private List<MusicBean> mp3Infos;
    private MusicAdapter musicAdapter;
    public static MusicFragment musicFragment;
    public static MusicFragment getInstance(String title) {
        musicFragment = new MusicFragment();
        musicFragment.mTitle = title;
        return musicFragment;
    }

    public static MusicFragment getMusicFragment() {
        return musicFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_musiclist, null);
        musicList = (ListView) v.findViewById(R.id.musiclistview);
        findSongs = new FindSongs();
        mp3Infos = findSongs.getMp3Infos(getActivity().getContentResolver());
        musicAdapter = new MusicAdapter(getContext(), mp3Infos);
        musicList.setAdapter(musicAdapter);
//        findSongs.setListAdpter(getContext(), mp3Infos, musicList);
        return v;
    }


    /**清除该页面选中数据**/
    public void clearSelectData()
    {
        musicAdapter.clearSelectDate();
    }

    /**获得所选文件**/
    public List<MusicBean> getSelectMusicList()
    {
        return musicAdapter.getSelectMusicList();
    }
}