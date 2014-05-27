package com.myteam.thisorthat;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.myteam.thisorthat.util.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
	RelativeLayout thisVoteDisplay;
	RelativeLayout thatVoteDisplay;
	
	SwipeRefreshLayout mSwipeRefreshLayout;
	ArrayAdapter<String> adapter;

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
		displayPost();

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
		comment = (TextView)findViewById(R.id.commentInputText);
		This = (ImageView) findViewById(R.id.ThisPicture);
		That = (ImageView) findViewById(R.id.ThatPicture);
		From = (TextView) findViewById(R.id.FromLabelView);
		thisVot = (TextView) findViewById(R.id.thisVote);
		thatVot = (TextView) findViewById(R.id.thatVote);
		ThatCaption = (TextView)findViewById(R.id.thatLabel);

		ThisCaption = (TextView) findViewById(R.id.thisLabel);
		Question = (TextView) findViewById(R.id.Question);
		heartButton = (ImageView) findViewById(R.id.heart_button_1);
		heartCounter = (TextView) findViewById(R.id.heart_counter);
	    thisVoteDisplay = (RelativeLayout) findViewById(R.id.thisCircle);
	    thatVoteDisplay = (RelativeLayout) findViewById(R.id.thatCircle);
		addComment = (Button)findViewById(R.id.addComment);
		addComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                	
                	ParseObject parse = new ParseObject("Comment");
            		parse.put("postId", postId);
            		parse.put("userId", userId);
            		parse.put("commentText", comment.getText().toString());
            		parse.put("username", userName);
            		parse.saveInBackground();
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

			

			ParseQuery<ParseObject> query = ParseQuery.getQuery("ThisOrThat");
			query.whereEqualTo("objectId", postId);

			/*
			query.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> post, com.parse.ParseException e) {
					
					
					
					thisVot.setText(post.get(0).getString(ParseConstants.KEY_THIS_VOTES));
					From.setText(post.get(0).getString(ParseConstants.KEY_SENDER_ID));
					thatVot.setText(post.get(0).getString(ParseConstants.KEY_THAT_VOTES));
					ThatCaption.setText(post.get(0).getString(ParseConstants.KEY_THAT_CAPTION));
					ThisCaption.setText(post.get(0).getString(ParseConstants.KEY_THIS_CAPTION));
					Question.setText(post.get(0).getString(ParseConstants.KEY_QUESTION_TEXT));
					heartCounter.setText(post.get(0).getString(ParseConstants.KEY_FOLLOWERS));


					Typeface myTypeface = Typeface.createFromAsset(
							getAssets(), "fonts/WhitneyCondensed-Book.otf");
					Typeface postTypeface = Typeface.createFromAsset(
							getAssets(), "fonts/WhitneyCondensed-Medium.otf");
					Typeface myThickTypeface = Typeface.createFromAsset(
							getAssets(), "fonts/WhitneyCondensed-Bold.otf");
					Typeface lightType = Typeface.createFromAsset(getAssets(),
							"fonts/WhitneyCondensed-Light.otf");
					ImageView commentImage = (ImageView) findViewById(R.id.comment_button_1);
					Question.setTypeface(postTypeface);
					thisVot.setTypeface(myTypeface);
					thatVot.setTypeface(myTypeface);
					ThisCaption.setTypeface(myTypeface);
					ThatCaption.setTypeface(myTypeface);
					From.setTypeface(myThickTypeface);

					
					
					
					
				}
			});*/
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comments, menu);
		return true;
	}
	
	private void populateListView(ArrayList<String> list){
		adapter = new ArrayAdapter<String>(this, R.layout.comment_item, list);
		ListView commentListV = (ListView) findViewById(R.id.commentListView);
		commentListV.setAdapter(adapter);
		
	}
	private void refresh(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
		query.whereEqualTo("postId", postId);

		
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> commentList, com.parse.ParseException e) {
				ArrayList<String> list = new ArrayList<String>();
				for(ParseObject comment : commentList){
					list.add(comment.getString("username")+": "+comment.getString("commentText"));
				}
				populateListView(list);
				
			}
		});
		/*
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}*/
	}

}
