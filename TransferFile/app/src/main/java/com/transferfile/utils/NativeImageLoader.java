package com.transferfile.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

/**
 * ����ͼƬ������,���õ����첽��������ͼƬ������ģʽ����getInstance()��ȡNativeImageLoaderʵ��
 * ����loadNativeImage()�������ر���ͼƬ���������Ϊһ�����ر���ͼƬ�Ĺ�����
 * 
 * @blog http://blog.csdn.net/xiaanming
 * 
 * @author xiaanming
 *
 */
public class NativeImageLoader {
	private LruCache<String, Bitmap> mMemoryCache;
	private static NativeImageLoader mInstance = new NativeImageLoader();
	private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(1);
	
	
	
	private NativeImageLoader(){
		//��ȡӦ�ó��������ڴ�
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		//������ڴ��1/unselectimage���洢ͼƬ
		final int cacheSize = maxMemory / 4;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			
			//��ȡÿ��ͼƬ�Ĵ�С
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
	}
	
	/**
	 * ͨ��˷�������ȡNativeImageLoader��ʵ��
	 * @return
	 */
	public static NativeImageLoader getInstance(){
		return mInstance;
	}
	
	
	/**
	 * ���ر���ͼƬ����ͼƬ�����вü�
	 * @param path
	 * @param mCallBack
	 * @return
	 */
	public Bitmap loadNativeImage(final String path, final NativeImageCallBack mCallBack){
		return this.loadNativeImage(path, null, mCallBack);
	}
	
	/**
	 * �˷��������ر���ͼƬ�������mPoint��������װImageView�Ŀ�͸ߣ����ǻ���ImageView�ؼ��Ĵ�С���ü�Bitmap
	 * ����㲻��ü�ͼƬ������loadNativeImage(final String path, final NativeImageCallBack mCallBack)������
	 * @param path
	 * @param mPoint
	 * @param mCallBack
	 * @return
	 */
	public Bitmap loadNativeImage(final String path, final Point mPoint, final NativeImageCallBack mCallBack){
		//�Ȼ�ȡ�ڴ��е�Bitmap
		Bitmap bitmap = getBitmapFromMemCache(path);
		
		final Handler mHander = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				mCallBack.onImageLoader((Bitmap)msg.obj, path);
			}
			
		};
		
		//����Bitmap�����ڴ滺���У��������߳�ȥ���ر��ص�ͼƬ������Bitmap���뵽mMemoryCache��
		if(bitmap == null){
			mImageThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					//�Ȼ�ȡͼƬ������ͼ
					Bitmap mBitmap = decodeThumbBitmapForFile(path, mPoint == null ? 0: mPoint.x, mPoint == null ? 0: mPoint.y);
					Message msg = mHander.obtainMessage();
					msg.obj = mBitmap;
					mHander.sendMessage(msg);
					
					//��ͼƬ���뵽�ڴ滺��
					addBitmapToMemoryCache(path, mBitmap);
				}
			});
		}
		return bitmap;
		
	}

	
	
	/**
	 * ���ڴ滺�������Bitmap
	 * 
	 * @param key
	 * @param bitmap
	 */
	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * ���key����ȡ�ڴ��е�ͼƬ
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}
	
	
	/**
	 * ���View(��Ҫ��ImageView)�Ŀ�͸�����ȡͼƬ������ͼ
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
		//����Ϊtrue,��ʾ����Bitmap���󣬸ö���ռ�ڴ�
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		//�������ű���
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		
		//����Ϊfalse,����Bitmap������뵽�ڴ���
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFile(path, options);
	}
	
	
	/**
	 * ���View(��Ҫ��ImageView)�Ŀ�͸�������Bitmap���ű���Ĭ�ϲ�����
	 * @param options
	 * @param width
	 * @param height
	 */
	private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){
		int inSampleSize = 1;
		if(viewWidth == 0 || viewWidth == 0){
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;
		
		//����Bitmap�Ŀ�Ȼ�߶ȴ��������趨ͼƬ��View�Ŀ�ߣ���������ű���
		if(bitmapWidth > viewWidth || bitmapHeight > viewWidth){
			int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);
			
			//Ϊ�˱�֤ͼƬ�����ű��Σ�����ȡ��߱�����С���Ǹ�
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}
	
	
	/**
	 * ���ر���ͼƬ�Ļص��ӿ�
	 * 
	 * @author xiaanming
	 *
	 */
	public interface NativeImageCallBack{
		/**
		 * �����̼߳������˱��ص�ͼƬ����Bitmap��ͼƬ·���ص��ڴ˷�����
		 * @param bitmap
		 * @param path
		 */
		public void onImageLoader(Bitmap bitmap, String path);
	}
}
