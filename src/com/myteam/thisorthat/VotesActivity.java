package com.myteam.thisorthat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lylc.widget.circularprogressbar.example.CircularProgressBar;
import com.myteam.thisorthat.adapter.CustomListAdapter;
import com.myteam.thisorthat.adapter.VotesDisplayAdapter;
import com.myteam.thisorthat.model.PostItem;
import com.myteam.thisorthat.model.UserBubbleRow;
import com.myteam.thisorthat.util.ParseConstants;
import com.nirhart.parallaxscroll.views.ParallaxListView;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

public class VotesActivity extends Activity {

	int mUserVote;
	private PostItem mPost;
	
	private String postId;
	private String userId;
	private String userName;
	
	private ImageView This;
	private ImageView That;
	private TextView From;
	private TextView thisVot;
	private TextView thatVot;
	private TextView ThatCaption;
	
	
	private ParallaxListView listView;
	private TextView ThisCaption ;
	private TextView Question ;
	private RelativeLayout thisVoteDisplay;
	private RelativeLayout thatVoteDisplay;
	private LinearLayout mPostItem;
	private CircularProgressBar mThisBar;
	private CircularProgressBar mThatBar;
	private ListView mVotesDisplay; 
	private static final int THIS_VOTE = 1;
	private static final int THAT_VOTE = 2;
	

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_votes);
		
		listView = (ParallaxListView) findViewById(R.id.list_view);

		CustomListAdapter adapter = new CustomListAdapter(LayoutInflater.from(this));

        View view = LayoutInflater.from(this).inflate(
                R.layout.message_item, null);

		listView.addParallaxedHeaderView(view);

		ActionBar actionBar = getActionBar();
	
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setIcon(R.drawable.close_post);
		
		This = (ImageView) findViewById(R.id.ThisPicture);
		That = (ImageView) findViewById(R.id.ThatPicture);
		From = (TextView) findViewById(R.id.FromLabelView);
		thisVot = (TextView) findViewById(R.id.thisVote);
		thatVot = (TextView) findViewById(R.id.that_Votes);
		ThatCaption = (TextView)findViewById(R.id.thatLabel);
		mThisBar = (CircularProgressBar) findViewById(R.id.circlethisbar);
		mThatBar = (CircularProgressBar) findViewById(R.id.circlethatbar);
		mPostItem = (LinearLayout) findViewById(R.id.message_it);
		ThisCaption = (TextView) findViewById(R.id.thisLabel);
		Question = (TextView) findViewById(R.id.Question);
	    thisVoteDisplay = (RelativeLayout) findViewById(R.id.thisCircle);
	    thatVoteDisplay = (RelativeLayout) findViewById(R.id.thatCircle);

		Typeface myTypeface = Typeface.createFromAsset(
				getAssets(), "fonts/WhitneyCondensed-Book.otf");
		Typeface postTypeface = Typeface.createFromAsset(
				getAssets(), "fonts/WhitneyCondensed-Medium.otf");
		Typeface myThickTypeface = Typeface.createFromAsset(
				getAssets(), "fonts/WhitneyCondensed-Bold.otf");
		Typeface lightType = Typeface.createFromAsset(getAssets(),
				"fonts/WhitneyCondensed-Light.otf");

		Question.setTypeface(postTypeface);
		thisVot.setTypeface(myTypeface);
		thatVot.setTypeface(myTypeface);
		ThisCaption.setTypeface(myTypeface);
		ThatCaption.setTypeface(myTypeface);
		From.setTypeface(lightType);
		
		Intent intent = getIntent();
		mPost = (PostItem) intent.getExtras().getSerializable("postItem");
		mUserVote = intent.getExtras().getInt(ParseConstants.KEY_USER_VOTE);
		postId = mPost.getObjectId();
		ThatCaption.setText(mPost.getThatCaption());
		ThisCaption.setText(mPost.getThisCaption());
		From.setText(mPost.getSenderName());
		Question.setText(mPost.getQuestionText());
		Picasso.with(this).load(mPost.getThisImage()).resize(480, 853)
		.centerCrop().into(This);
		Picasso.with(this).load(mPost.getThatImage()).resize(480, 853)
		.centerCrop().into(That);
		
		int postColor =mPost.getColor();
		mThatBar.setTitleColor(Color.WHITE);
		mThatBar.setProgressColor(Color.WHITE);
		mThisBar.setTitleColor(Color.WHITE);
		mThisBar.setProgressColor(Color.WHITE);
		int thisVotes = mPost.getThisVotes();
		int thatVotes = mPost.getThatVotes();
		int totalVotes = thisVotes + thatVotes;
		if(totalVotes== 0) totalVotes =Integer.MAX_VALUE;
		int thisPercentage = (thisVotes*100)/(totalVotes);
		int thatPercentage = (thatVotes*100)/(totalVotes);
		mThisBar.setProgress(thisPercentage);
		mThatBar.setProgress(thatPercentage);
		mThisBar.setTitle(thisPercentage + "%");
		mThatBar.setTitle(thatPercentage+"%");
		
		if(mUserVote == 1){
			ThatCaption.setTextColor(Color.BLACK);
			ThisCaption.setTextColor(Color.WHITE);
			ThatCaption.setBackgroundColor(Color.WHITE);
			ThisCaption.setBackgroundColor(postColor);
			ThisCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
			ThatCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		}
		else{
			ThisCaption.setTextColor(Color.BLACK);
			ThatCaption.setTextColor(Color.WHITE);
			ThisCaption.setBackgroundColor(Color.WHITE);
			ThatCaption.setBackgroundColor(postColor);
			ThatCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
			ThisCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		}
		showVotes();

		
	}
		

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	private void showVotes(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_USER_VOTE);
		//query.whereEqualTo("postid", postId);

		
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> votesList, com.parse.ParseException e) {
			LinkedList<ParseObject> thisVotes = new LinkedList<ParseObject>();
			LinkedList<ParseObject> thatVotes = new LinkedList<ParseObject>();
			List<UserBubbleRow> rowDisplay = new ArrayList<UserBubbleRow>();
			for (ParseObject c : votesList){
				if(c.getInt(ParseConstants.KEY_USER_VOTE) == THIS_VOTE ){
					thisVotes.add(c);
				}
				else if(c.getInt(ParseConstants.KEY_USER_VOTE) == THAT_VOTE){
					thatVotes.add(c);
				}
			}
			while (!thatVotes.isEmpty() || !thisVotes.isEmpty()){
				UserBubbleRow row = new UserBubbleRow();
				if(!thisVotes.isEmpty()){
					row.setThisUser(thisVotes.getFirst().getString(ParseConstants.KEY_USERNAME));
					row.setThisUserid(thisVotes.getFirst().getString(ParseConstants.KEY_USER_ID));
					thisVotes.removeFirst();
				}
				if(!thatVotes.isEmpty()){
					row.setThatUser(thatVotes.getFirst().getString(ParseConstants.KEY_USERNAME));
					row.setThatUserid(thatVotes.getFirst().getString(ParseConstants.KEY_USER_ID));
					thatVotes.removeFirst();
				}
				rowDisplay.add(row);
			}
			VotesDisplayAdapter adapter = new VotesDisplayAdapter(rowDisplay,
					VotesActivity.this);
			
			listView.setAdapter(adapter);
			
			

				
			}
		});

	}
	
	
}
