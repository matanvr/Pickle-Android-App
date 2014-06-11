package com.myteam.thisorthat.sqlite;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.myteam.thisorthat.model.PostItem;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.ParseObject;
 
public class MySQLiteHelper extends SQLiteOpenHelper {
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "PostDB";
 
    
    private static final String CREATE_POST_TABLE = "CREATE TABLE posts ( " +
            "id TEXT PRIMARY KEY, " + 
            "question TEXT, "+
            "senderId TEXT, "+
            "senderName TEXT, "+
            "thisVotes INTEGER, "+
            "thatVotes INTEGER, "+
            "thisFile TEXT, "+
            "thatFile TEXT, "+
            "createdAt INTEGER, "+
            "updatedAt INTEGER, " + 
            "thisCaption TEXT, "+
            "thatCaption TEXT, "+
            "followers INTEGER, "+
            "comments INTEGER )";
    /*
    
    private static final String CREATE_REFRESH_TABLE = "CREATE TABLE lastrefresh ( " +
            "user TEXT, " + 
            "lastRefresh INTEGER )";*/
    
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create Post table


        // create Posts table
        db.execSQL(CREATE_POST_TABLE);
        
        
     //   db.execSQL(CREATE_REFRESH_TABLE);
        
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older Posts table if existed
        db.execSQL("DROP TABLE IF EXISTS PostDB");
 
        // create fresh Posts table
        this.onCreate(db);
    }
 
    
    //---------------------------------------------------------------------
    
    /**
     * CRUD operations (create "add", read "get", update, delete) Post + get all Posts + delete all Posts
     */
 
    // Posts table name
    private static final String TABLE_POSTS = "posts";
    private static final String TABLE_REFRESH = "lastrefresh";
    
    // Posts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_SENDER_ID = "senderId";
    private static final String KEY_SENDER_NAME = "senderName";
    private static final String KEY_THIS_VOTES = "thisVotes";
    private static final String KEY_THAT_VOTES = "thatVotes";
    private static final String KEY_THIS_URI = "thisFile";
    private static final String KEY_THAT_URI = "thatFile";
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_THIS_CAPTION = "thisCaption";
    private static final String KEY_THAT_CAPTION = "thatCaption";
    private static final String KEY_FOLLOWERS = "followers";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_UPDATED_AT = "updatedAt";
    
    //lastRefresh column names
    private static final String KEY_USER_ID = "user";
    private static final String KEY_LAST_REFRESH = "lastRefresh";
    
   
 

    private static final String[] COLUMNS = {KEY_ID,KEY_QUESTION, KEY_SENDER_ID, KEY_SENDER_NAME,
    	KEY_THIS_VOTES, KEY_THAT_VOTES, KEY_THIS_URI, KEY_THAT_URI, KEY_CREATED_AT, KEY_THIS_CAPTION,
    	KEY_THAT_CAPTION, KEY_FOLLOWERS, KEY_COMMENTS, KEY_UPDATED_AT};
 
    public void addPost(ParseObject post, Uri thisUri, Uri thatUri){
        Log.d("addPost", post.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        String thisCaption = post.getString(ParseConstants.KEY_THIS_CAPTION);
        ContentValues values = new ContentValues();
        values.put(KEY_ID, post.getObjectId()); // get title 
        values.put(KEY_QUESTION, post.getString(ParseConstants.KEY_QUESTION_TEXT));
        values.put(KEY_SENDER_ID, post.getString(ParseConstants.KEY_SENDER_ID));
        values.put(KEY_SENDER_NAME, post.getString(ParseConstants.KEY_SENDER_NAME));
        values.put(KEY_THIS_VOTES, post.getInt(ParseConstants.KEY_THIS_VOTES));
        values.put(KEY_THAT_VOTES, post.getInt(ParseConstants.KEY_THAT_VOTES));
        values.put(KEY_THIS_URI, thisUri.toString());
        values.put(KEY_THAT_URI, thatUri.toString());
        values.put(KEY_CREATED_AT, post.getCreatedAt().getTime());
        values.put(KEY_UPDATED_AT, post.getUpdatedAt().getTime());
        values.put(KEY_THIS_CAPTION, thisCaption);
        values.put(KEY_THAT_CAPTION, post.getString(ParseConstants.KEY_THAT_CAPTION));
        values.put(KEY_FOLLOWERS, post.getInt(ParseConstants.KEY_FOLLOWERS));
        values.put(KEY_COMMENTS, post.getInt(ParseConstants.KEY_COMMENTS));

 
        // 3. insert
        db.insert(TABLE_POSTS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
 
        // 4. close
        db.close(); 
    }
 
    public PostItem getPost(String id){
 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
 
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_POSTS, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections 
                new String[] { id }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
 
        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build Post object
        String thisCaption = cursor.getString(9);
        
        PostItem postItem = new PostItem();
        postItem.setObjectId(cursor.getString(0));
        postItem.setQuestionText(cursor.getString(1));
        postItem.setSenderId(cursor.getString(2));
        postItem.setSenderName(cursor.getString(3));
        postItem.setThisVotes(Integer.parseInt(cursor.getString(4)));
        postItem.setThatVotes(Integer.parseInt(cursor.getString(5)));
        postItem.setThisImage(Uri.parse(cursor.getString(6)));
        postItem.setThatImage(Uri.parse(cursor.getString(7)));

        postItem.setCreatedAt(new Date(cursor.getInt(8)));
        postItem.setThisCaption(cursor.getString(9));
        postItem.setThatCaption(cursor.getString(10));
        postItem.setFollowers(Integer.parseInt(cursor.getString(11)));
        postItem.setComments(Integer.parseInt(cursor.getString(12)));
        postItem.setUpdatedAt(new Date(cursor.getLong(13)));


 
        Log.d("getPost("+id+")", postItem.toString());
 
        // 5. return Post
        return postItem;
    }
 
    // Get All Posts
    public List<PostItem> getAllPosts() {
        List<PostItem> allPosts = new LinkedList<PostItem>();
 
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_POSTS +
        		" ORDER BY " + KEY_CREATED_AT + " DESC";
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, build Post and add it to list
        PostItem postItem = null;
        if (cursor.moveToFirst()) {
            do {
            	
            
                postItem = new PostItem();
                postItem.setObjectId(cursor.getString(0));
                postItem.setQuestionText(cursor.getString(1));
                postItem.setSenderId(cursor.getString(2));
                postItem.setSenderName(cursor.getString(3));
                postItem.setThisVotes(Integer.parseInt(cursor.getString(4)));
                postItem.setThatVotes(Integer.parseInt(cursor.getString(5)));
                postItem.setThisImage(Uri.parse(cursor.getString(6)));
                postItem.setThatImage(Uri.parse(cursor.getString(7)));
                
                postItem.setCreatedAt(new Date(cursor.getLong(8)));
                postItem.setUpdatedAt(new Date(cursor.getLong(9)));
                postItem.setThisCaption(cursor.getString(10));
                postItem.setThatCaption(cursor.getString(11));
                
                postItem.setFollowers(cursor.getInt(11));
                postItem.setComments(cursor.getInt(12));
     
 
                // Add post to posts
                allPosts.add(postItem);
            } while (cursor.moveToNext());
        }
 
        Log.d("getAllPosts()", allPosts.toString());
        db.close();
        // return posts
        return allPosts;
    }
 
     // Updating single post
    public int updatePost(ParseObject post) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, post.getObjectId()); // get title 
        values.put(KEY_QUESTION, post.getString(ParseConstants.KEY_QUESTION_TEXT));
        values.put(KEY_SENDER_ID, post.getString(ParseConstants.KEY_SENDER_ID));
        values.put(KEY_SENDER_NAME, post.getString(ParseConstants.KEY_SENDER_NAME));
        values.put(KEY_THIS_VOTES, post.getInt(ParseConstants.KEY_THIS_VOTES));
        values.put(KEY_THAT_VOTES, post.getInt(ParseConstants.KEY_THAT_VOTES));
        values.put(KEY_CREATED_AT, post.getCreatedAt().getTime());
        values.put(KEY_UPDATED_AT, post.getUpdatedAt().getTime());
        values.put(KEY_THIS_CAPTION, post.getString(ParseConstants.KEY_THIS_CAPTION));
        values.put(KEY_THAT_CAPTION, post.getString(ParseConstants.KEY_THAT_CAPTION));
        values.put(KEY_FOLLOWERS, post.getInt(ParseConstants.KEY_FOLLOWERS));
        values.put(KEY_COMMENTS, post.getInt(ParseConstants.KEY_COMMENTS));
 
        // 3. updating row
        int i = db.update(TABLE_POSTS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { post.getObjectId() }); //selection args
 
        // 4. close
        db.close();
 
        return i;
 
    }
 
    // Deleting single Post
    public void deletePost(PostItem postItem) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_POSTS,
                KEY_ID+" = ?",
                new String[] { postItem.getObjectId() });
 
        // 3. close
        db.close();
 
        Log.d("deletePost", postItem.toString());
 
    }
    
    public void deleteAll(){
    	 SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(TABLE_POSTS, null, null);
    	db.delete(TABLE_REFRESH, null, null);
    }
    /*
    public long getLastRefresh(){
        SQLiteDatabase db = this.getReadableDatabase();
        
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_REFRESH, // a. table
                new String [] {KEY_LAST_REFRESH}, // b. column names
                "user = ?", // c. selections 
                new String [] {KEY_USER_ID}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"); // h. limit
 
        // 3. if we got results get the first one
        if (cursor != null && cursor.moveToFirst())
        {

	        long lastRefresh = cursor.getLong(0);
	        Log.d("saving refresh ",lastRefresh +" ms");
	        return lastRefresh;
        }
        
       
       return -1;
    }
    public void saveLastRefresh(){
    	long currentTime = Calendar.getInstance().get(Calendar.MILLISECOND);
        Log.d("saving refresh",currentTime+ " ms");
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value

        ContentValues values = new ContentValues();
        values.put(KEY_LAST_REFRESH, currentTime); // get title 
        values.put(KEY_USER_ID, KEY_USER_ID);


 
        // 3. updating row
        db.update(TABLE_REFRESH, //table
                values, // column/value
                KEY_USER_ID+" = ?", // selections
                new String[] { KEY_USER_ID }); //selection args
 
        // 4. close
        db.close();	
    }*/
}
