package com.transferfile.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.transferfile.R;
import com.transferfile.adapter.ApkAdapter;
import com.transferfile.adapter.FolderAdapter;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressLint("ValidFragment")
public class FolderFragment extends Fragment {
    ListView listView;
    TextView textView;
    // 记录当前的父文件夹
    File currentParent;
    // 记录当前路径下的所有文件的文件数组
    File[] currentFiles;
    FolderAdapter folderAdapter;
    static FolderFragment folderFragment;

    public static FolderFragment getFolderFragment() {
        return folderFragment;
    }

    public static FolderFragment getInstance(String title) {
        folderFragment = new FolderFragment();
        return folderFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_folderlist, null);
        // 获取列出全部文件的ListView
        listView = (ListView) v.findViewById(R.id.folderlistview);
        textView = (TextView) v.findViewById(R.id.path);
        // 获取系统的SD卡的目录
        File root = new File("/mnt/sdcard/");
        // 如果 SD卡存在
        if (root.exists())
        {
            currentParent = root;
            currentFiles = root.listFiles();
            // 使用当前目录下的全部文件、文件夹来填充ListView
            try {
                inflateListView(currentFiles);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 为ListView的列表项的单击事件绑定监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                // 用户单击了文件，直接返回，不做任何处理
                if (currentFiles[position].isFile()) return;
                // 获取用户点击的文件夹下的所有文件
                File[] tmp = currentFiles[position].listFiles();
                if (tmp == null || tmp.length == 0)
                {
                    Toast.makeText(getActivity()
                            , "当前路径不可访问或该路径下没有文件",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
                    currentParent = currentFiles[position]; // ②
                    // 保存当前的父文件夹内的全部文件和文件夹
                    currentFiles = tmp;
                    // 再次更新ListView
                    try {
                        inflateListView(currentFiles);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return v;
    }

    private void inflateListView(File[] files) throws Exception  // ①
    {
        // 创建一个SimpleAdapter
        folderAdapter = new FolderAdapter(getContext(),files);
        // 为ListView设置Adapter
        listView.setAdapter(folderAdapter);
        try
        {
            textView.setText("当前路径为："
                    + currentParent.getCanonicalPath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void backMenu()
    {
        try
        {
            if (!currentParent.getCanonicalPath()
                    .equals("/mnt/sdcard"))
            {
                // 获取上一级目录
                currentParent = currentParent.getParentFile();
                // 列出当前目录下所有文件
                currentFiles = currentParent.listFiles();
                // 再次更新ListView
                inflateListView(currentFiles);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}