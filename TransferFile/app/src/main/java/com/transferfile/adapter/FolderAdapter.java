package com.transferfile.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.transferfile.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

class FolderViewHolder{
    public TextView folder_title;//文件 名称
    public TextView folder_num;//文件数
    public ImageView folder_icon;//图标
    public TextView folder_size;//文件 大小

}

public class FolderAdapter extends BaseAdapter {

    private Context context;        //上下文对象引用
    private File[] files;   //存放File引用的集合
    private File file;
    private FolderViewHolder viewHolder;


    public FolderAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
    }


    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       Log.e("FolderAdapter","FolderAdapter");
        viewHolder = null;
        if (convertView == null) {
            viewHolder = new FolderViewHolder();
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_folder_list, null);
            viewHolder.folder_title = (TextView) convertView.findViewById(R.id.folder_title);
            viewHolder.folder_num = (TextView) convertView.findViewById(R.id.folder_num);
            viewHolder.folder_icon = (ImageView) convertView.findViewById(R.id.folder_icon);
            viewHolder.folder_size = (TextView) convertView.findViewById(R.id.folder_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FolderViewHolder) convertView.getTag();
        }
        file = files[position];
        // 如果当前File是文件夹，使用folder图标；否则使用file图标
        if (file.isDirectory())
        {
            viewHolder.folder_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.foldericon));
            if(file.listFiles().length!=0)
                viewHolder.folder_num.setText("    "+file.listFiles().length);
            try {
                viewHolder.folder_size.setText(FormetFileSize(getFileSize(file)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            viewHolder.folder_num.setText("    ");//view的重复使用，需重置该TextView
            String type=getMIMEType(file);//获取文件类型
            if(type.equals("application/vnd.android.package-archive"))
            {

                try {
                    PackageInfo packageInfo=context.getPackageManager().getPackageArchiveInfo(file.getCanonicalPath(),0);//通过File地址获取PackageInfo 得到packageName
                    Log.e("FolderAdapter","packname:"+packageInfo.packageName);
                    Drawable drawable=context.getPackageManager().getApplicationIcon(packageInfo.packageName);//通过packageName获得图标
                    viewHolder.folder_icon.setImageDrawable(drawable);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if(type.equals("image/jpeg")||type.equals("image/png"))
            {
                try {
                    viewHolder.folder_icon.setImageBitmap(BitmapFactory.decodeFile(file.getCanonicalPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(type.equals("video/mp4"))
            {
                try {
                    viewHolder.folder_icon.setImageBitmap(getVideoThumbnail(file.getCanonicalPath(), 75, 70,
                            MediaStore.Images.Thumbnails.MICRO_KIND));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(type.equals("audio/x-mpeg"))
            {
                viewHolder.folder_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.musicicon));
            }

            else
                viewHolder.folder_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.fileicon));

            try {
                viewHolder.folder_size.setText(FormetFileSize(getFileSizes(file)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        viewHolder.folder_title.setText(file.getName());

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

    /**获取文件大小**/
    public long getFileSizes(File f) throws Exception {//取得文件大小
        long s=0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s= fis.available();
        } else {
//            f.createNewFile();
            System.out.println("文件不存在");
        }
        return s;
    }

    /**获取文件夹大小**/
    public long getFileSize(File f)throws Exception//取得文件夹大小
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++)
        {
            if (flist[i].isDirectory())
            {
                size = size + getFileSize(flist[i]);
            } else
            {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**转换单位**/
    public String FormetFileSize(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS == 0) {
            fileSizeString = "0.00B";
        } else if (fileS < 1024 && fileS>0) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
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

    /**获取文件类型**/
    private String getMIMEType(File file)
    {
        String type="*/*";
        String fName=file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
    /* 获取文件的后缀名 */
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    //建立一个MIME类型与文件后缀名的匹配表
    private final String[][] MIME_MapTable={
            //{后缀名，    MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",      "image/bmp"},
            {".c",        "text/plain"},
            {".class",    "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",        "application/x-gzip"},
            {".h",        "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",        "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",        "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml",    "text/plain"},
            {".z",        "application/x-compress"},
            {".zip",    "application/zip"},
            {"",        "*/*"}
    };
}

