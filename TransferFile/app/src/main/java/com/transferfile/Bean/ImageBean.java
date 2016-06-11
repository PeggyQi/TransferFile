package com.transferfile.Bean;

public class ImageBean{
	/**
	 * 图片路径
	 */
	private String topImagePath;
	/**
	 * 文件名
	 */
	private String folderName; 
	/**
	 * 图片数
	 */
	private int imageCounts;
	
	public String getTopImagePath() {
		return topImagePath;
	}
	public void setTopImagePath(String topImagePath) {
		this.topImagePath = topImagePath;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public int getImageCounts() {
		return imageCounts;
	}
	public void setImageCounts(int imageCounts) {
		this.imageCounts = imageCounts;
	}
	
}
