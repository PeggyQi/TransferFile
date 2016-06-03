package com.transferfile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.transferfile.Bean.VideoBean;
import com.transferfile.R;

import java.util.List;

class ViewHolder{
    public TextView video_title;//music 名称
    public TextView video_duration;//music 时长
    public ImageView video_album;//music 封面
    public TextView video_size;//music 大小

}

public class VideoAdapter extends BaseAdapter {

    private Context context;        //上下文对象引用
    private List<VideoBean> videoInfos;   //存放Mp3Info引用的集合
    private VideoBean videoInfo;        //Mp3Info对象引用
    private int pos = -1;           //列表位置
    private ViewHolder viewHolder;


    public VideoAdapter(Context context, List<VideoBean> videoInfos) {
        this.context = context;
        this.videoInfos = videoInfos;
    }


    @Override
    public int getCount() {
        return videoInfos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_video_list, null);
            viewHolder.video_title = (TextView) convertView.findViewById(R.id.video_title);
            viewHolder.video_duration = (TextView) convertView.findViewById(R.id.video_duration);
            viewHolder.video_album = (ImageView) convertView.findViewById(R.id.video_album);
            viewHolder.video_size = (TextView) convertView.findViewById(R.id.video_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        videoInfo = videoInfos.get(position);
        viewHolder.video_album.setImageBitmap(getVideoThumbnail(videoInfo.getUrl(), 75, 70,
                MediaStore.Images.Thumbnails.MICRO_KIND));

        viewHolder.video_size.setText(String.valueOf(formatSize(videoInfo.getSize()) + "MB"));//显示大小
        viewHolder.video_title.setText(videoInfo.getTitle());         //显示标题
        viewHolder.video_duration.setText(String.valueOf(formatTime(videoInfo.getDuration()))); //显示长度
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    public static String formatTime(Long time) {                     //将歌曲的时间转换为分秒的制度
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";

        if (min.length() < 2)
            min = "0" + min;
        switch (sec.length()) {
            case 4:
                sec = "0" + sec;
                break;
            case 3:
                sec = "00" + sec;
                break;
            case 2:
                sec = "000" + sec;
                break;
            case 1:
                sec = "0000" + sec;
                break;
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    public double formatSize(long size)//大小转换单位
    {
        long size1 = (long) (size / 1024.0 / 1024.0 * 100);
        Log.e("MusicDemo", String.valueOf(size1));
        long sizel = Math.round(size1);
        Log.e("MusicDemo", String.valueOf(sizel));
        double sized = sizel / 100.0;
        Log.e("MusicDemo", String.valueOf(sized));
        return sized;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w"+bitmap.getWidth());
        System.out.println("h"+bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}

