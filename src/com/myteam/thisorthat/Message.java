package com.myteam.thisorthat;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class Message{
	@SerializedName("question")
	protected String mQuestion;
	

	
	@SerializedName("sender")
	protected String mSender;
	


	public String getSender() {
		return mSender;
	}
	public void setSender(String sender) {
		mSender = sender;
	}
	
	public String getQuestion() {
		return mQuestion;
	}
	public void setQuestion(String question) {
		mQuestion = question;
	}
	
	
}
