package com.myteam.thisorthat.model;

import android.graphics.Bitmap;

/**
 * @author javatechig {@link http://javatechig.com}
 * 
 */
public class ImageItem {
	private Bitmap image;
	private String title;
	private String url;
	private String bigUrl;

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBigUrl() {
		return bigUrl;
	}

	public void setBigUrl(String bigUrl) {
		this.bigUrl = bigUrl;
	}
}
