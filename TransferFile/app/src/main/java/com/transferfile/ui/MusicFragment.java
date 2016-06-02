package com.transferfile.ui;

import android.annotation.SuppressLint;
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
    private String mTitle;
    private ListView musicList;
    private FindSongs findSongs;
    private List<MusicBean> mp3Infos;
    private MusicAdapter musicAdapter;
    public static MusicFragment getInstance(String title) {
        MusicFragment sf = new MusicFragment();
        sf.mTitle = title;
        return sf;
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
        findSongs.setListAdpter(getContext(), mp3Infos, musicList);
        return v;
    }
}