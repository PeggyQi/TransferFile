package com.transferfile.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.transferfile.Bean.MusicBean;
import com.transferfile.Bean.VideoBean;
import com.transferfile.R;
import com.transferfile.adapter.MusicAdapter;
import com.transferfile.adapter.VideoAdapter;
import com.transferfile.utils.FindSongs;
import com.transferfile.utils.FindVideos;

import java.util.List;

@SuppressLint("ValidFragment")
public class VideoFragment extends Fragment {
    private String mTitle;
    private ListView videoList;
    private FindVideos findVideos;
    private List<VideoBean> videoInfos;
    private VideoAdapter videoAdapter;
    public static VideoFragment getInstance(String title) {
        VideoFragment sf = new VideoFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_videolist, null);
        videoList = (ListView) v.findViewById(R.id.videolistview);
        findVideos=new FindVideos();
        videoInfos = findVideos.getVideoInfos(getActivity().getContentResolver());
        videoAdapter = new VideoAdapter(getContext(), videoInfos);
        findVideos.setListAdpter(getContext(), videoInfos, videoList);
        return v;
    }
}