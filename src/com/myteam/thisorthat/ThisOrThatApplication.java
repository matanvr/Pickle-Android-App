package com.myteam.thisorthat;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class ThisOrThatApplication extends Application {
	
	@Override
	public void onCreate() {
		  Parse.initialize(this, "qEeMQNnTnwEVXk35APxR8c8C5depGkKLYPMBKhnm", "0Xjmyq5vDpl9joJegw5ogVw8KIeopLvSADj4bsJY");
		//  ParseFacebookUtils.initialize(getString(R.string.app_id));
	}
}
