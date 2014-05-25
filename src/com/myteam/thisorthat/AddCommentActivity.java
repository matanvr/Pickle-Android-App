package com.myteam.thisorthat;

import com.parse.ParseObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AddCommentActivity extends Activity {
	Button add;
	TextView comment;
	String postId;
	String userId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_comment);
		postId = getIntent().getStringExtra("postId");
		userId = getIntent().getStringExtra("userId");
		add = (Button)findViewById(R.id.addCommentButton);
		
		add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	
            	comment = (TextView)findViewById(R.id.commentInput);
            	
            	ParseObject parse = new ParseObject("Comment");
        		parse.put("postId", postId);
        		parse.put("userId", userId);
        		parse.put("commentText", comment.getText().toString());
        		parse.saveInBackground();
            	
            	Intent intent = new Intent(AddCommentActivity.this, CommentsActivity.class);
				intent.putExtra("postId", postId);
				intent.putExtra("userId", postId);
				startActivity(intent);
            }
    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_comment, menu);
		return true;
	}

}
