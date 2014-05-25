package com.myteam.thisorthat;

import java.util.List;
import android.view.View.OnClickListener;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
public class CommentsActivity extends Activity {
	Button addComment;
	TextView postMsg; 
	String postId;
	String userId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);
		postId = getIntent().getStringExtra("postId");
		userId = getIntent().getStringExtra("userId");
		addComment = (Button)findViewById(R.id.addComment);
		addComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                	Intent intent = new Intent(CommentsActivity.this, AddCommentActivity.class);
					intent.putExtra("postId", postId);
					intent.putExtra("userId", postId);
					startActivity(intent);
                }
        });

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
		query.whereEqualTo("postId", postId);
		
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> commentList, com.parse.ParseException e) {
				String comments = "";
				for(ParseObject comment : commentList){
					comments += comment.getString("commentText")+"\n";
				}
				postMsg = (TextView)findViewById(R.id.postMessage);
				postMsg.setText(comments);
				
			}
		}); 
		
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comments, menu);
		return true;
	}

}
