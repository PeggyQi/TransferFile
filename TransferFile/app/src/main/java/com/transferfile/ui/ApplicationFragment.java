package com.transferfile.ui;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.transferfile.R;
import com.transferfile.adapter.ApkAdapter;

import java.util.List;

@SuppressLint("ValidFragment")
public class ApplicationFragment extends Fragment {
    private ListView mListView;

    private ApkAdapter apkAdapter;
    public static ApplicationFragment applicationFragment;
    public static ApplicationFragment getInstance(String title) {
        applicationFragment= new ApplicationFragment();
        return applicationFragment;
    }

    public static ApplicationFragment getApplicationFragment() {
        return applicationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_applictionlist, null);
        mListView = (ListView) v.findViewById(R.id.apklistview);
        List<PackageInfo> packs = getActivity().getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++)
        {
            PackageInfo packageInfo = packs.get(i);

            ApplicationInfo applicationInfo=packageInfo.applicationInfo;
            if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {//区分是否为第三方应用
                packs.remove(i--);

            }

        }
        apkAdapter=new ApkAdapter(getContext(),packs);
        mListView.setAdapter(apkAdapter);

        return v;
    }

    /**清除该页面选中数据**/
    public void clearSelectData()
    {
        apkAdapter.clearSelectDate();
    }

    /**获得所选文件**/
    public List<PackageInfo> getSelectApkList()
    {
        return apkAdapter.getSelectApkList();
    }
}