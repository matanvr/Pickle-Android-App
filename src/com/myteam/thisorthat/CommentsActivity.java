package com.myteam.thisorthat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
public class CommentsActivity extends Activity {
	Button addComment;
	TextView comment;
	ListView commentList; 
	String postId;
	String userId;
	ArrayAdapter<String> adapter;
	SwipeRefreshLayout mSwipeRefreshLayout;
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
		postId = getIntent().getStringExtra("postId");
		userId = getIntent().getStringExtra("userId");
		comment = (TextView)findViewById(R.id.commentInputText);
		mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorScheme(
				R.color.swipeRefresh1,
				R.color.swipeRefresh2,
				R.color.swipeRefresh3,
				R.color.swipeRefresh4);
		refresh();
		addComment = (Button)findViewById(R.id.addComment);
		addComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                	
                	ParseObject parse = new ParseObject("Comment");
            		parse.put("postId", postId);
            		parse.put("userId", userId);
            		parse.put("commentText", comment.getText().toString());
            		parse.saveInBackground();
            		refresh();
            		comment.setText("");
            		comment.clearFocus();
            		
            		InputMethodManager imm = (InputMethodManager) view.getContext()
            			    .getSystemService(Context.INPUT_METHOD_SERVICE);
            			 imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
        });
		
		
		
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
					list.add(comment.getString("commentText"));
				}
				populateListView(list);
				
			}
		});
		
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

}
