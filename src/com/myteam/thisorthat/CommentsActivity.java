package com.myteam.thisorthat;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myteam.thisorthat.adapter.CommentAdapter;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

public class CommentsActivity extends Activity {
	Button addComment;
	TextView comment; 
	String postId;
	String userId;
	String userName;
	
	ImageView This;
	ImageView That;
	TextView From;
	TextView thisVot;
	TextView thatVot;
	TextView ThatCaption;
	
	TextView ThisCaption ;
	TextView Question ;
	ImageView heartButton ;
	TextView heartCounter;
	TextView commentCounter;
	RelativeLayout thisVoteDisplay;
	RelativeLayout thatVoteDisplay;
	LinearLayout mPostItem;
	ParseObject mPost;
	
	SwipeRefreshLayout mSwipeRefreshLayout;
	ArrayAdapter<String> adapter;
	ListView commentListV;
	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			 
				refresh();
		
		}
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		/*mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorScheme(
				R.color.swipeRefresh1,
				R.color.swipeRefresh2,
				R.color.swipeRefresh3,
				R.color.swipeRefresh4);*/

		postId = getIntent().getStringExtra("postId");
		userId = getIntent().getStringExtra("userId");
		userName = getIntent().getStringExtra("userName");
		
		//commentCounter = (TextView) findViewById(R.id.commentCount);
		comment = (TextView)findViewById(R.id.commentInputText);
		This = (ImageView) findViewById(R.id.ThisPicture);
		That = (ImageView) findViewById(R.id.ThatPicture);
		From = (TextView) findViewById(R.id.FromLabelView);
		thisVot = (TextView) findViewById(R.id.thisVote);
		thatVot = (TextView) findViewById(R.id.that_Votes);
		ThatCaption = (TextView)findViewById(R.id.thatLabel);
		
		mPostItem = (LinearLayout) findViewById(R.id.message_it);
		commentListV = (ListView) findViewById(R.id.commentListView);
		ThisCaption = (TextView) findViewById(R.id.thisLabel);
		Question = (TextView) findViewById(R.id.Question);
	//	heartButton = (ImageView) findViewById(R.id.heart_button_1);
		//heartCounter = (TextView) findViewById(R.id.heart_counter);
	    thisVoteDisplay = (RelativeLayout) findViewById(R.id.thisCircle);
	    thatVoteDisplay = (RelativeLayout) findViewById(R.id.thatCircle);
		addComment = (Button)findViewById(R.id.addComment);
		Typeface myTypeface = Typeface.createFromAsset(
				getAssets(), "fonts/WhitneyCondensed-Book.otf");
		Typeface postTypeface = Typeface.createFromAsset(
				getAssets(), "fonts/WhitneyCondensed-Medium.otf");
		Typeface myThickTypeface = Typeface.createFromAsset(
				getAssets(), "fonts/WhitneyCondensed-Bold.otf");
		Typeface lightType = Typeface.createFromAsset(getAssets(),
				"fonts/WhitneyCondensed-Light.otf");

		//ImageView commentImage = (ImageView) findViewById(R.id.comment_button_1);
		Question.setTypeface(postTypeface);
		thisVot.setTypeface(myTypeface);
		thatVot.setTypeface(myTypeface);
		ThisCaption.setTypeface(myTypeface);
		ThatCaption.setTypeface(myTypeface);
		From.setTypeface(lightType);
		heartCounter.setTypeface(postTypeface);
		commentCounter.setTypeface(postTypeface);
		addComment.setTypeface(lightType);
		comment.setTypeface(lightType);
		displayPost();
		addComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                	
                	ParseObject parse = new ParseObject("Comment");
            		parse.put("postId", postId);
            		parse.put("userId", userId);
            		parse.put("commentText", comment.getText().toString());
            		parse.put("username", userName);
            		parse.saveInBackground(new SaveCallback() {
            			@Override
            			public void done(ParseException e) {
            				if (e == null) {
            					// success!
            					Toast.makeText(CommentsActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
                        		refresh();
                        		
            				}
            				else {
            					AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);
            					builder.setMessage(R.string.error_sending_message)
            						.setTitle(R.string.error_selecting_file_title)
            						.setPositiveButton(android.R.string.ok, null);
            					AlertDialog dialog = builder.create();
            					dialog.show();
            				}
            			}
            		});
            		mPost.put(ParseConstants.KEY_COMMENTS, mPost.getInt(ParseConstants.KEY_COMMENTS) + 1);
            		mPost.saveInBackground();
            	//	mPost.put(ParseConstants., value);
            	/*	LayoutParams list = (LayoutParams) mPostItem.getLayoutParams();
            		   list.height = 400;//like int  200
            		   mPostItem.setLayoutParams(list);*/
            		refresh();
            		comment.setText("");
            		comment.clearFocus();
            		
            		InputMethodManager imm = (InputMethodManager) view.getContext()
            			    .getSystemService(Context.INPUT_METHOD_SERVICE);
            			 imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
        });
		refresh();
		displayPost();
		
		
		

		
		
		
	}
	public void displayPost(){

			

			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_DILEMMA);
			query.whereEqualTo("objectId", postId);

			query.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> post, com.parse.ParseException e) {
					
					
					mPost = post.get(0);
					thisVot.setText(post.get(0).getInt(ParseConstants.KEY_THIS_VOTES)+" Votes");
					From.setText(post.get(0).getString(ParseConstants.KEY_SENDER_NAME));
					thatVot.setText(post.get(0).getInt(ParseConstants.KEY_THAT_VOTES)+" Votes");
					ThatCaption.setText(post.get(0).getString(ParseConstants.KEY_THAT_CAPTION));
					ThisCaption.setText(post.get(0).getString(ParseConstants.KEY_THIS_CAPTION));
					Question.setText(post.get(0).getString(ParseConstants.KEY_QUESTION_TEXT));
					heartCounter.setText(""+post.get(0).getInt(ParseConstants.KEY_FOLLOWERS));
					commentCounter.setText(""+post.get(0).getInt(ParseConstants.KEY_COMMENTS));


					ParseObject message = post.get(0);
					ParseFile ThisFile = post.get(0).getParseFile("this");
					ParseFile ThatFile = message.getParseFile("that");
					Uri thatUri = Uri.parse(ThisFile.getUrl()); 
					Uri thisUri = Uri.parse(ThatFile.getUrl());
					String uri = message.get(ParseConstants.KEY_FILE_THIS).toString();
					Picasso.with(CommentsActivity.this).load(thatUri.toString()).resize(480, 853)
							.centerCrop().into(This);

					Picasso.with(CommentsActivity.this).load(thisUri.toString()).resize(480, 853)
							.centerCrop().into(That);
					
					
					
					
				}
			});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comments, menu);
		return true;
	}
	
	private void refresh(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
		query.whereEqualTo("postId", postId);

		
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> commentList, com.parse.ParseException e) {
			CommentAdapter adapter = new CommentAdapter(
						CommentsActivity.this, 
						commentList);
			
			commentListV.setAdapter(adapter);

				
			}
		});
		/*
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}*/
	}

}
