package com.transferfile.ui;

import java.util.List;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.transferfile.R;
import com.transferfile.adapter.ChildAdapter;

public class ShowImageFragment extends Fragment {
	private GridView mGridView;
	private List<String> list;
	private ChildAdapter adapter;
	public static ShowImageFragment getInstance(List<String> list) {
		ShowImageFragment sif = new ShowImageFragment();
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




}
