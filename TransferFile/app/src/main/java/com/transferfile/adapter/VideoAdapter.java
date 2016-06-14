package com.transferfile.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.transferfile.Bean.VideoBean;
import com.transferfile.R;
import com.transferfile.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

class ViewHolder{
    public TextView video_title;//video 名称
    public TextView video_duration;//video 时长
    public ImageView video_album;//video 封面
    public TextView video_size;//video 大小
    public CheckBox video_checkbox;//video 复选框
}

public class VideoAdapter extends BaseAdapter {
    private Context context;        //上下文对象引用
    private List<VideoBean> videoInfos;   //存放Video引用的集合
    private VideoBean videoInfo;        //video对象引用
    private int pos = -1;           //列表位置
    private ViewHolder viewHolder;

    private List<ViewHolder> viewHolderList=new ArrayList<ViewHolder>();//所有文件的viewholder
    private List<VideoBean> selectVideoList=new ArrayList<VideoBean>();//存放选择的文件
    private boolean firstSelect=false;//标记首次选中文件，隐藏fab,显示snackbar

    public List<VideoBean> getSelectVideoList() {
        return selectVideoList;
    }

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
            viewHolderList.add(viewHolder);
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_video_list, null);
            viewHolder.video_title = (TextView) convertView.findViewById(R.id.video_title);
            viewHolder.video_duration = (TextView) convertView.findViewById(R.id.video_duration);
            viewHolder.video_album = (ImageView) convertView.findViewById(R.id.video_album);
            viewHolder.video_size = (TextView) convertView.findViewById(R.id.video_size);
            viewHolder.video_checkbox=(CheckBox)convertView.findViewById(R.id.video_checkbox);
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

        viewHolder.video_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked==true)
                {
                    selectVideoList.add(videoInfo);
                }
                else {
                    selectVideoList.remove(videoInfo);
                }

                Intent intentnum=new Intent();//选中状态改变发广播
                intentnum.setAction(MainActivity.Adapter_CheckBoxChange);
                intentnum.putExtra(MainActivity.Adapter_SelectNum,String.valueOf(getSelectVideoList().size()));
                context.sendBroadcast(intentnum);

                if(selectVideoList.size()==0)
                {
                    firstSelect=false;
                    Intent intent=new Intent();
                    intent.setAction(MainActivity.Adapter_CheckBoxUnClick);
                    context.sendBroadcast(intent);
                }
                if(firstSelect==false&&selectVideoList.size()==1)
                {
                    Intent intent=new Intent();
                    intent.setAction(MainActivity.Adapter_CheckBoxClick);
                    context.sendBroadcast(intent);
                    firstSelect=true;
                }

            }
        });

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

    /**清除选中数据**/
    public void clearSelectDate()
    {
        for(int i=0;i<viewHolderList.size();i++)
        {
            viewHolderList.get(i).video_checkbox.setChecked(false);
        }
        notifyDataSetChanged();
    }
}

