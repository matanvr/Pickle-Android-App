package com.myteam.thisorthat;

import android.app.Application;

import com.myteam.thisorthat.util.ParseConstants;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class ThisOrThatApplication extends Application {
	
	@Override
	public void onCreate() {
		  Parse.initialize(this, "qEeMQNnTnwEVXk35APxR8c8C5depGkKLYPMBKhnm", "0Xjmyq5vDpl9joJegw5ogVw8KIeopLvSADj4bsJY");
		  PushService.setDefaultPushCallback(this, MainActivity.class);
		  ParseInstallation.getCurrentInstallation().saveInBackground();
		  ParseFacebookUtils.initialize(getString(R.string.app_id));
	}
	
	
	public static void updateParseInstallation(ParseUser user) {
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
		installation.saveInBackground();
	}
}
