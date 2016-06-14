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
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
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
import com.transferfile.ui.MainActivity;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class ViewContainer{
    public TextView music_title;//music 名称
    public TextView music_artist;//music 艺术家
    public TextView music_duration;//music 时长
    public ImageView music_album;//music 封面
    public TextView music_size;//music 大小
    public CheckBox music_checkbox;//checkbox
}

public class MusicAdapter extends BaseAdapter {

    private Context context;        //上下文对象引用
    private List<MusicBean> mp3Infos;   //存放Mp3Info引用的集合
    private MusicBean mp3Info;        //Mp3Info对象引用
    private int pos = -1;           //列表位置

    private List<ViewContainer> viewHolderList=new ArrayList<ViewContainer>();//所有音乐的viewholder
    private List<MusicBean> selectMusicList=new ArrayList<MusicBean>();//存放选择的音乐
    private boolean firstSelect=false;//标记首次选中文件，隐藏fab,显示snackbar
    public List<MusicBean> getSelectMusicList() {
        return selectMusicList;
    }

    public MusicAdapter(Context context, List<MusicBean> mp3Infos) {
        this.context = context;
        this.mp3Infos = mp3Infos;
    }


    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewContainer vc;

        if (convertView == null) {

            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_music_list, null);
            vc = new ViewContainer();
            viewHolderList.add(vc);
            vc.music_title = (TextView) convertView.findViewById(R.id.music_title);
            vc.music_artist = (TextView) convertView.findViewById(R.id.music_artist);
            vc.music_duration = (TextView) convertView.findViewById(R.id.music_duration);
            vc.music_album = (ImageView) convertView.findViewById(R.id.music_album);
            vc.music_size = (TextView) convertView.findViewById(R.id.music_size);
            vc.music_checkbox=(CheckBox)convertView.findViewById(R.id.music_checkbox);
            convertView.setTag(vc);
        } else {
            vc = (ViewContainer) convertView.getTag();

        }
        mp3Info = mp3Infos.get(position);
        Bitmap bm = getArtwork(context, mp3Info.getId(), mp3Info.getAlbum_id(), true);
        if (bm != null) {
            vc.music_album.setImageBitmap(toRoundBitmap(bm));
        }
        else
            vc.music_album.setImageDrawable(context.getResources().getDrawable(R.drawable.musicicon));//设置默认图标

        vc.music_size.setText(String.valueOf(formatSize(mp3Info.getSize()) + "MB"));//显示大小
        vc.music_title.setText(mp3Info.getTitle());         //显示标题
        vc.music_artist.setText(mp3Info.getArtist());       //显示艺术家
        vc.music_duration.setText(String.valueOf(formatTime(mp3Info.getDuration()))); //显示长度

        vc.music_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked==true)
                {
                    boolean exitInlist=false;//列表中是否存在
                    for(int i=0;i<selectMusicList.size();i++) {
                        Log.i("MusicAdapter","List:"+selectMusicList.get(i).getTitle()+"id:"+selectMusicList.get(i).getId());
                        if(mp3Infos.get(position).getId()==selectMusicList.get(i).getId())
                        {
                            exitInlist=true;
                            Log.e("MusicAdapter","==:"+mp3Infos.get(position).getTitle());
                        }
                    }
                    if(exitInlist==false)
                    {
                        selectMusicList.add(mp3Infos.get(position));
                        Log.e("MusicAdapter","add:"+mp3Infos.get(position).getTitle()+"id:"+mp3Infos.get(position).getId());
                    }
                }
                else {
                    for(int i=0;i<selectMusicList.size();i++) {
                        if(mp3Infos.get(position).getId()==selectMusicList.get(i).getId()) {
                            selectMusicList.remove(mp3Infos.get(position));
                            Log.e("MusicAdapter","remove:"+mp3Infos.get(position).getTitle());
                        }
                    }

                }

                Intent intentnum=new Intent();//选中状态改变发广播
                intentnum.setAction(MainActivity.Adapter_CheckBoxChange);
                intentnum.putExtra(MainActivity.Adapter_SelectNum,String.valueOf(getSelectMusicList().size()));
                context.sendBroadcast(intentnum);
                if(selectMusicList.size()==0)
                {
                    firstSelect=false;
                    Intent intent=new Intent();
                    intent.setAction(MainActivity.Adapter_CheckBoxUnClick);
                    context.sendBroadcast(intent);
                }
                if(firstSelect==false&&selectMusicList.size()==1)
                {
                    Intent intent=new Intent();
                    intent.setAction(MainActivity.Adapter_CheckBoxClick);
                    context.sendBroadcast(intent);
                    firstSelect=true;
                }

            }
        });

        boolean selectflag=false;//标记该文件是否被选中
        for(int i=0;i<getSelectMusicList().size();i++)//view的重复使用，需重置该CheckBox
        {
            if(mp3Info.getId()==selectMusicList.get(i).getId()) {
                vc.music_checkbox.setChecked(true);
                selectflag=true;
            }
        }
        if(selectflag==false) {
            vc.music_checkbox.setChecked(false);
        }
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
        long sizel = Math.round(size1);
        double sized = sizel / 100.0;
        return sized;
    }

    /**
     * 从文件当中获取专辑封面位图
     *
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static Bitmap mCachedBit = null;
    public static Bitmap getArtwork(Context context, long song_id, long album_id,
                                    boolean allowdefault) {
        if (album_id < 0) {
            // This is something that is not in the database, so get the album art directly
            // from the file.
            if (song_id >= 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
//            if (allowdefault) {
//                return getDefaultArtwork(context);
//            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the user deleted it, or
                // maybe it never existed to begin with.
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
//                        if (bm == null && allowdefault) {
//                            return getDefaultArtwork(context);
//                        }
                    }
                }
//                else if (allowdefault) {
//                    bm = getDefaultArtwork(context);
//                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return null;
    }

    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        Bitmap bm = null;
        byte[] art = null;
        String path = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (FileNotFoundException ex) {

        }
        if (bm != null) {
            mCachedBit = bm;
        }
        return bm;
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**清除选中数据**/
    public void clearSelectDate()
    {
        for(int i=0;i<viewHolderList.size();i++)
        {
            viewHolderList.get(i).music_checkbox.setChecked(false);
        }
        notifyDataSetChanged();
    }
}


