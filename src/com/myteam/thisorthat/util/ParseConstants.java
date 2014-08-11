package com.myteam.thisorthat.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.myteam.thisorthat.InboxFragment;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public final class ParseConstants {
	// Class name
	public static final String CLASS_USER_VOTE = "UserVote";
	public static final String CLASS_DILEMMA = "Thisorthat";
	
	// Field names
	public static final String KEY_USERNAME = "username";
	public static final String KEY_OBJECT_ID = "objectId";
	public static final String KEY_FRIENDS_RELATION = "friendsRelation";
	public static final String KEY_RECIPIENT_IDS = "recipientIds";
	public static final String KEY_SENDER_ID = "senderId";
	public static final String KEY_SENDER_NAME = "senderName";
	public static final String KEY_FILE = "file";
	public static final String KEY_FILE_THIS = "this";
	public static final String KEY_FILE_THAT = "that";
	public static final String KEY_FILE_TYPE = "fileType";
	public static final String KEY_CREATED_AT = "createdAt";
	public static final String KEY_UPDATED_AT = "updatedAt";
	
	public static final String KEY_QUESTION_TEXT = "questionText";
	public static final String KEY_QUESTION_ID = "questionId";
	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_VIDEO = "video";
	public static final String KEY_THIS_VOTES = "this_votes";
	public static final String KEY_THAT_VOTES = "that_votes";
	public static final String KEY_USER_VOTE = "userVote";
	public static final String KEY_USER_ID = "userid";
	public static final String KEY_POST_ID = "postid";
	public static final String KEY_THIS_CAPTION = "thisCaption";
	public static final String KEY_THAT_CAPTION = "thatCaption";
	public static final String KEY_IS_FOLLOWER  = "follower_vote";
	public static final String KEY_FOLLOWERS  = "followers";
	public static final String KEY_COMMENTS  = "comments";
	public static final String KEY_COMMENT_ID = "commentId";
	public static final String KEY_COMMENT_TEXT = "commentText";
	
	

	
}
