package com.transferfile.adapter;

/****/

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.transferfile.Bean.MusicBean;
import com.transferfile.R;
import com.transferfile.Wifi.WiFiAdmin;
import com.transferfile.ui.MainActivity;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class WifiViewContainer{
    public TextView wifi_title;//wifi 名称


}
public class DialogAdapter extends BaseAdapter {

    private Context context;        //上下文对象引用
    private List<WifiP2pDevice> items=new ArrayList<>();   //存放WIFI引用的集合
    private WifiViewContainer vc;
    private WifiP2pDevice item;

    public DialogAdapter(Context context) {
        this.context = context;
    }
    public DialogAdapter(Context context, List<WifiP2pDevice> items) {
        this.context = context;
        this.items = items;
    }

    public  void setData( List<WifiP2pDevice> items)
    {

        this.items=items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        vc = null;
        if (convertView == null) {
            vc = new WifiViewContainer();
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_dialog_list, null);
            vc.wifi_title = (TextView) convertView.findViewById(R.id.wifiname);

            convertView.setTag(vc);
        } else {
            vc = (WifiViewContainer) convertView.getTag();
        }
        item = items.get(position);
        vc.wifi_title.setText(item.deviceName);//显示大小
        vc.wifi_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.wiFiAdmin.connectDevice(item);
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

}


