package com.myteam.thisorthat.model;

import java.util.Date;

import android.net.Uri;

import com.parse.ParseFile;

public class PostItem {
	private String mObjectId;
	private int mThisVotes;
	private int mThatVotes;
	private String mQuestionText;
	private String mSenderId;
	private String mSenderName;
	private Uri mThisImage;
	private Uri mThatImage;
	private Date mCreatedAt;
	private String mThisCaption;
	private String mThatCaption;
	private int mFollowers;
	private int mComments;
	private Date mUpdatedAt;
	private int mColor;
	public String getObjectId() {
		return mObjectId;
	}
	public void setObjectId(String objectId) {
		mObjectId = objectId;
	}
	public int getThisVotes() {
		return mThisVotes;
	}
	public void setThisVotes(int thisVotes) {
		mThisVotes = thisVotes;
	}
	public int getThatVotes() {
		return mThatVotes;
	}
	public void setThatVotes(int thatVotes) {
		mThatVotes = thatVotes;
	}
	public String getQuestionText() {
		return mQuestionText;
	}
	public void setQuestionText(String questionText) {
		mQuestionText = questionText;
	}
	public String getSenderId() {
		return mSenderId;
	}
	public void setSenderId(String senderId) {
		mSenderId = senderId;
	}
	public String getSenderName() {
		return mSenderName;
	}
	public void setSenderName(String senderName) {
		mSenderName = senderName;
	}
	public Uri getThisImage() {
		return mThisImage;
	}
	public void setThisImage(Uri thisImage) {
		mThisImage = thisImage;
	}
	public Uri getThatImage() {
		return mThatImage;
	}
	public void setThatImage(Uri thatImage) {
		mThatImage = thatImage;
	}
	public Date getCreatedAt() {
		return mCreatedAt;
	}
	public void setCreatedAt(Date createdAt) {
		mCreatedAt = createdAt;
	}
	public String getThisCaption() {
		return mThisCaption;
	}
	public void setThisCaption(String thisCaption) {
		mThisCaption = thisCaption;
	}
	public String getThatCaption() {
		return mThatCaption;
	}
	public void setThatCaption(String thatCaption) {
		mThatCaption = thatCaption;
	}
	public int getFollowers() {
		return mFollowers;
	}
	public void setFollowers(int followers) {
		mFollowers = followers;
	}
	public int getComments() {
		return mComments;
	}
	public void setComments(int comments) {
		mComments = comments;
	}
	@Override
	public String toString() {
		return "PostItem [mObjectId=" + mObjectId + ", mThisVotes="
				+ mThisVotes + ", mThatVotes=" + mThatVotes
				+ ", mQuestionText=" + mQuestionText + ", mSenderId="
				+ mSenderId + ", mSenderName=" + mSenderName + ", mThisImage="
				+ mThisImage + ", mThatImage=" + mThatImage + ", mCreatedAt="
				+ mCreatedAt + ", mThisCaption=" + mThisCaption
				+ ", mThatCaption=" + mThatCaption + ", mFollowers="
				+ mFollowers + ", mComments=" + mComments + ", mUpdatedAt="
				+ mUpdatedAt + ", mColor=" + mColor + "]";
	}
	public Date getUpdatedAt() {
		return mUpdatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		mUpdatedAt = updatedAt;
	}
	public int getColor() {
		return mColor;
	}
	public void setColor(int color) {
		mColor = color;
	}
	
	
	
}
