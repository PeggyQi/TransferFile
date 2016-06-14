package com.transferfile.adapter;

import android.content.Context;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.transferfile.R;
import com.transferfile.ui.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ApkViewHolder{
    public TextView apk_title;//apk 名称
    public ImageView apk_icon;//apk 图标
    public TextView apk_size;//apk 大小
    public CheckBox apk_checkbox;//apk 复选框
}

public class ApkAdapter extends BaseAdapter {

    private Context context;        //上下文对象引用
    private List<PackageInfo> packageInfos;   //存放packageInfo引用的集合
    private PackageInfo packageInfo;        //packageInfo对象引用
    private ApplicationInfo applicationInfo;
    private int pos = -1;           //列表位置
    private ApkViewHolder viewHolder;

    private List<ApkViewHolder> viewHolderList=new ArrayList<ApkViewHolder>();//所有文件的viewholder
    private List<PackageInfo> selectApkList=new ArrayList<PackageInfo>();//存放选择的文件
    private boolean firstSelect=false;//标记首次选中文件，隐藏fab,显示snackbar

    public List<PackageInfo> getSelectApkList() {
        return selectApkList;
    }

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
            viewHolderList.add(viewHolder);
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_apk_list, null);
            viewHolder.apk_title = (TextView) convertView.findViewById(R.id.apk_title);
            viewHolder.apk_icon = (ImageView) convertView.findViewById(R.id.apk_icon);
            viewHolder.apk_size = (TextView) convertView.findViewById(R.id.apk_size);
            viewHolder.apk_checkbox=(CheckBox)convertView.findViewById(R.id.apk_checkbox);
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

        viewHolder.apk_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked==true)
                {
                    selectApkList.add(packageInfo);
                }
                else {
                    selectApkList.remove(packageInfo);
                }

                Intent intentnum=new Intent();//选中状态改变发广播
                intentnum.setAction(MainActivity.Adapter_CheckBoxChange);
                intentnum.putExtra(MainActivity.Adapter_SelectNum,String.valueOf(getSelectApkList().size()));
                context.sendBroadcast(intentnum);

                if(selectApkList.size()==0)
                {
                    firstSelect=false;
                    Intent intent=new Intent();
                    intent.setAction(MainActivity.Adapter_CheckBoxUnClick);
                    context.sendBroadcast(intent);
                }
                if(firstSelect==false&&selectApkList.size()==1)
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

    /**清除选中数据**/
    public void clearSelectDate()
    {
        for(int i=0;i<viewHolderList.size();i++)
        {
            viewHolderList.get(i).apk_checkbox.setChecked(false);
        }
        notifyDataSetChanged();
    }

}

