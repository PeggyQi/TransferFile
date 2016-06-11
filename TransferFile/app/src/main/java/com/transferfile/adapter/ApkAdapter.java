package com.transferfile.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.transferfile.R;

import java.io.File;
import java.util.List;

class ApkViewHolder{
    public TextView apk_title;//apk 名称
    public ImageView apk_icon;//apk 图标
    public TextView apk_size;//apk 大小

}

public class ApkAdapter extends BaseAdapter {

    private Context context;        //上下文对象引用
    private List<PackageInfo> packageInfos;   //存放packageInfo引用的集合
    private PackageInfo packageInfo;        //packageInfo对象引用
    private ApplicationInfo applicationInfo;
    private int pos = -1;           //列表位置
    private ApkViewHolder viewHolder;


    public ApkAdapter(Context context, List<PackageInfo> packageInfos) {
        this.context = context;
        this.packageInfos = packageInfos;
    }


    @Override
    public int getCount() {
        return packageInfos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder = null;
        if (convertView == null) {
            viewHolder = new ApkViewHolder();
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_apk_list, null);
            viewHolder.apk_title = (TextView) convertView.findViewById(R.id.apk_title);
            viewHolder.apk_icon = (ImageView) convertView.findViewById(R.id.apk_icon);
            viewHolder.apk_size = (TextView) convertView.findViewById(R.id.apk_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ApkViewHolder) convertView.getTag();
        }
        packageInfo = packageInfos.get(position);

        applicationInfo=packageInfo.applicationInfo;

        Drawable drawable=context.getPackageManager().getApplicationIcon(applicationInfo);
        viewHolder.apk_icon.setImageDrawable(drawable);
        String dir=applicationInfo.publicSourceDir;
        int size= Integer.valueOf((int)new File(dir).length());
        viewHolder.apk_size.setText(formatSize(size)+"MB");//显示大小
        viewHolder.apk_title.setText(context.getPackageManager().getApplicationLabel(applicationInfo).toString());         //显示标题
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

}

