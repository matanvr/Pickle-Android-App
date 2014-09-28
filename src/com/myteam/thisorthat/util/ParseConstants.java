package com.myteam.thisorthat.util;

import android.util.Log;

import com.parse.codec.binary.StringUtils;



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
	public static final String KEY_IS_REMOVED = "isRemoved";
	public static final String KEY_IS_SUBSCRIBED = "isSubscribed";
	public static final String KEY_IS_FLAGGED = "isFlagged";
	
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
	public static final String KEY_POST_COLOR = "color";
	
	public static String getUserPic(String userID) {
	    String imageURL;
	    if(!isNumeric(userID)){
	    	return null;
	    }
	    imageURL = "https://graph.facebook.com/"+userID+"/picture?type=normal";
	    Log.d("profile user", imageURL);
	    return imageURL;
	}
	public static boolean isNumeric(String str)
	{
	    return str.matches("-?\\d+(.\\d+)?");
	}
	public static String getUserPicLarge(String userID) {
	    String imageURL;
	    if(!isNumeric(userID)){
	    	return null;
	    }
	    imageURL = "https://graph.facebook.com/"+userID+"/picture?type=large";
	    Log.d("profile user", imageURL);
	    return imageURL;
	}
	
}
