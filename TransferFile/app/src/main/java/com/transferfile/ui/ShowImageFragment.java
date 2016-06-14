package com.transferfile.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.transferfile.Bean.ImageBean;
import com.transferfile.R;
import com.transferfile.adapter.ChildAdapter;

public class ShowImageFragment extends Fragment  {
	private GridView mGridView;
	private List<String> list;
	private ChildAdapter adapter;

	public static ShowImageFragment getSif() {
		return sif;
	}

	private static ShowImageFragment sif;
	public static WifiP2pInfo info;

	public static WifiP2pInfo getInfo() {
		return info;
	}

	public static ShowImageFragment getInstance(List<String> list) {
		sif = new ShowImageFragment();
		sif.list = list;
		return sif;
	}

//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.fragment_showimage);
//
//		mGridView = (GridView) findViewById(R.id.child_grid);
//		list = getIntent().getStringArrayListExtra("data");
//
//		adapter = new ChildAdapter(this, list, mGridView);
//		mGridView.setAdapter(adapter);
//
//	}
//
//	@Override
//	public void onBackPressed() {
////		Toast.makeText(this, "选中 " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
//		super.onBackPressed();
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_showimage, null);
		mGridView = (GridView)v. findViewById(R.id.child_grid);
		adapter = new ChildAdapter(getActivity(), list, mGridView);
		mGridView.setAdapter(adapter);
		return v;
	}

	/**清除该页面选中数据**/
	public void clearSelectData()
	{
		adapter.clearSelectDate();
	}

	/**获得所选文件**/
	public List<String> getSelectImage()
	{
		return adapter.getSelectlist();
	}
}
