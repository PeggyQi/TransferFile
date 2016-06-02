package com.transferfile.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.Toast;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.transferfile.R;
import com.transferfile.utils.MyImageView;
import com.transferfile.utils.NativeImageLoader;

public class ChildAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);//������װImageView�Ŀ�͸ߵĶ���

	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private GridView mGridView;
	private List<String> list;
	private List<ViewHolder> viewHolderList=new ArrayList<ViewHolder>();//所有图片的viewholder
	private List<String> selectlist=new ArrayList<String>();//存储选中文件
	protected LayoutInflater mInflater;
    private Context context;
    private boolean firstSelect=false;//标记首次选中图片，隐藏fab,显示snackbar
	public List<String> getSelectlist() {
		return selectlist;
	}

	public ChildAdapter(Context context, List<String> list, GridView mGridView) {
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
		this.context=context;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.item_photo_gridchild, null);
			viewHolder = new ViewHolder();
			viewHolderList.add(viewHolder);
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);

			viewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		viewHolder.mImageView.setTag(path);
		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
					addAnimation(viewHolder.mCheckBox);
				}
				mSelectMap.put(position, isChecked);
				if(isChecked==true)
				{
					selectlist.add(list.get(position));
				}
				else {
					selectlist.remove(list.get(position));
				}

				Intent intentnum=new Intent();//选中状态改变发广播
				intentnum.setAction("ChildAdapter_CheckBoxChange");
				intentnum.putExtra("selectimagenum",String.valueOf(getSelectlist().size()));
				context.sendBroadcast(intentnum);

				if(selectlist.size()==0)
				{
					 firstSelect=false;
					 Intent intent=new Intent();
				     intent.setAction("ChildAdapter_CheckBoxUnClick");
                     context.sendBroadcast(intent);
				}
				if(firstSelect==false&&selectlist.size()==1)
				{
					Intent intent=new Intent();
					intent.setAction("ChildAdapter_CheckBoxClick");
					context.sendBroadcast(intent);
					firstSelect=true;
				}

			}
		});
		
		viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);

		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageLoader.NativeImageCallBack() {
			
			@Override
			public void onImageLoader(Bitmap bitmap, String path) {
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if(bitmap != null && mImageView != null){
					mImageView.setImageBitmap(bitmap);
				}
			}
		});
		
		if(bitmap != null){
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		return convertView;
	}
	
	/**
	 * ��CheckBox�ӵ�����������ÿ�Դ��nineoldandroids���ö��� 
	 * @param view
	 */
	private void addAnimation(View view){
		float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules), 
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
				set.setDuration(150);
		set.start();
	}
	
	
	/**
	 * ��ȡѡ�е�Item��position
	 * @return
	 */
	public List<Integer> getSelectItems(){
		List<Integer> list = new ArrayList<Integer>();
		for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
			Map.Entry<Integer, Boolean> entry = it.next();
			if(entry.getValue()){
				list.add(entry.getKey());
			}
		}
		
		return list;
	}

	/**清除选中数据**/
	public void clearSelectDate()
	{
		for(int i=0;i<viewHolderList.size();i++)
		{
			viewHolderList.get(i).mCheckBox.setChecked(false);
		}
		notifyDataSetChanged();
	}
	
	
	public static class ViewHolder{
		public MyImageView mImageView;
		public CheckBox mCheckBox;
	}



}
