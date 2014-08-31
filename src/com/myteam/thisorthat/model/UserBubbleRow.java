package com.myteam.thisorthat.model;

public class UserBubbleRow {
	String mThisUser;
	String mThatUser;
	String mThisUserid;
	String mThatUserid;
	String mThisUri;
	String mThatUri;
	String mThisTimeStamp;
	String mThatTimeStamp;
	public String getThisUser() {
		return mThisUser;
	}
	public void setThisUser(String thisUser) {
		mThisUser = thisUser;
	}
	public String getThatUser() {
		return mThatUser;
	}
	public void setThatUser(String thatUser) {
		mThatUser = thatUser;
	}
	public String getThisUri() {
		return mThisUri;
	}
	public void setThisUri(String thisUri) {
		mThisUri = thisUri;
	}
	public String getThatUri() {
		return mThatUri;
	}
	public void setThatUri(String thatUri) {
		mThatUri = thatUri;
	}
	public String getThisTimeStamp() {
		return mThisTimeStamp;
	}
	public void setThisTimeStamp(String thisTimeStamp) {
		mThisTimeStamp = thisTimeStamp;
	}
	public String getThatTimeStamp() {
		return mThatTimeStamp;
	}
	public void setThatTimeStamp(String thatTimeStamp) {
		mThatTimeStamp = thatTimeStamp;
	}
	@Override
	public String toString() {
		return "UserBubbleRow [mThisUser=" + mThisUser + ", mThatUser="
				+ mThatUser + ", mThisUri=" + mThisUri + ", mThatUri="
				+ mThatUri + ", mThisTimeStamp=" + mThisTimeStamp
				+ ", mThatTimeStamp=" + mThatTimeStamp + "]";
	}
	public String getThisUserid() {
		return mThisUserid;
	}
	public void setThisUserid(String thisUserid) {
		mThisUserid = thisUserid;
	}
	public String getThatUserid() {
		return mThatUserid;
	}
	public void setThatUserid(String thatUserid) {
		mThatUserid = thatUserid;
	}
	
}
