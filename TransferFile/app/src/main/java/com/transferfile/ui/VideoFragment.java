package com.transferfile.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
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
    public static String VideoFragment_onDestroyView="VideoFragment_onDestroyView";
    private String mTitle;
    private ListView videoList;
    private FindVideos findVideos;
    private List<VideoBean> videoInfos;
    private VideoAdapter videoAdapter;
    public static VideoFragment videoFragment;
    public static VideoFragment getInstance(String title) {
        videoFragment = new VideoFragment();
        videoFragment.mTitle = title;
        return videoFragment;
    }

    public static VideoFragment getVideoFragment() {
        return videoFragment;
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
        videoList.setAdapter(videoAdapter);
//        findVideos.setListAdpter(getContext(), videoInfos, videoList);
        return v;
    }

    /**清除该页面选中数据**/
    public void clearSelectData()
    {
        videoAdapter.clearSelectDate();
    }
}