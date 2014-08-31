package com.myteam.thisorthat.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lylc.widget.circularprogressbar.example.CircularProgressBar;
import com.lylc.widget.circularprogressbar.example.CircularProgressBar.ProgressAnimationListener;
import com.myteam.thisorthat.R;
import com.myteam.thisorthat.VotesActivity;
import com.myteam.thisorthat.model.PostItem;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class MessageAdapter extends ArrayAdapter<ParseObject> {

	protected Context mContext;
	protected List<ParseObject> mMessages;
	protected List<ParseObject> mUserVotes;
	protected HashMap<String, ParseObject> mUserVotesMap;
	private final static int NO_SELECTION = 0;
	private final static int THIS_IMAGE = 1;
	private final static int THAT_IMAGE = 2;
	private View mView;
	private int mFeedType;
	private ViewHolder mHolder;
	private static String mLastPostClicked;
	private Animation mVotesAnimation;
	private int [] mColorArray;

	public MessageAdapter(Context context, List<ParseObject> messages,
			List<ParseObject> userVotes, int feedType) {
		super(context, R.layout.message_item, messages);
		mContext = context;
		mMessages = messages;
		mUserVotes = userVotes;
		mUserVotesMap = new HashMap<String, ParseObject>();
		mFeedType = feedType;
		for (ParseObject curr : mUserVotes) {
			mUserVotesMap.put(curr.get(ParseConstants.KEY_POST_ID).toString(),
					curr);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.message_item, null);
			mHolder = new ViewHolder();
			mHolder.This = (ImageView) convertView
					.findViewById(R.id.ThisPicture);
			mHolder.That = (ImageView) convertView
					.findViewById(R.id.ThatPicture);
			mHolder.From = (TextView) convertView
					.findViewById(R.id.FromLabelView);
			mHolder.thisVot = (TextView) convertView
					.findViewById(R.id.thisVote);
			mHolder.thatVot = (TextView) convertView
					.findViewById(R.id.that_Votes);
			mHolder.ThatCaption = (TextView) convertView
					.findViewById(R.id.thatLabel);
			mHolder.ThisCaption = (TextView) convertView
					.findViewById(R.id.thisLabel);
			mHolder.Question = (TextView) convertView
					.findViewById(R.id.Question);
			//mHolder.heartButton = (ImageView) convertView
					//.findViewById(R.id.heart_button_1);
			//mHolder.heartCounter = (TextView) convertView
				//	.findViewById(R.id.heart_counter);
			mHolder.thisVoteDisplay = (RelativeLayout) convertView
					.findViewById(R.id.thisCircle);
			mHolder.thatVoteDisplay = (RelativeLayout) convertView
					.findViewById(R.id.thatCircle);
			mVotesAnimation  =  AnimationUtils.loadAnimation(mContext.getApplicationContext(),
			                R.anim.bounce_in_bottom);
			mHolder.mThisBar = (CircularProgressBar) convertView.findViewById(R.id.circlethisbar);
			mHolder.mThatBar = (CircularProgressBar) convertView.findViewById(R.id.circlethatbar);
			mColorArray = mContext.getResources().getIntArray(R.array.post_colors);    
			//mHolder.commentCounter = (TextView) convertView.findViewById(R.id.commentCount);
			mHolder.mThisProgress = (ProgressBar) convertView.findViewById(R.id.this_progressBar);
			mHolder.mThatProgress = (ProgressBar) convertView.findViewById(R.id.that_progressBar);
			// mHolder.thatPercentage = (TextView)
			// convertView.findViewById(R.id.that_percentage);
			// mHolder.thisPercentage = (TextView)
			// convertView.findViewById(R.id.this_percentage);
			Typeface myTypeface = Typeface.createFromAsset(
					mContext.getAssets(), "fonts/WhitneyCondensed-Book.otf");
			Typeface postTypeface = Typeface.createFromAsset(
					mContext.getAssets(), "fonts/WhitneyCondensed-Medium.otf");
			Typeface myLightType = Typeface.createFromAsset(
					mContext.getAssets(), "fonts/WhitneyCondensed-Light.otf");
			Typeface lightType = Typeface.createFromAsset(mContext.getAssets(),
					"fonts/WhitneyCondensed-Medium.otf");
			/*mHolder.commentImage = (ImageView) convertView
					.findViewById(R.id.comment_button_1);*/
			;
			mHolder.Question.setTypeface(postTypeface);
			mHolder.thisVot.setTypeface(postTypeface);
			mHolder.thatVot.setTypeface(postTypeface);
			mHolder.ThisCaption.setTypeface(myTypeface);
			mHolder.ThatCaption.setTypeface(myTypeface);
			mHolder.From.setTypeface(myLightType);
			//mHolder.heartCounter.setTypeface(lightType);
			//mHolder.commentCounter.setTypeface(lightType);
			
			convertView.setTag(mHolder);

		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		displayPost(position);

		return convertView;
	}

	private void displayPost(int position) {
		ThisThatOnClickListener onClickListener = new ThisThatOnClickListener(
				position) {
			public String userId;
			public String postId;
			public String userName;
			public int thisVotes;
			public int thatVotes;
			public int followerVote;
			public int followerVotes;
			

			@Override
			public void onClick(View v) {
				mView = v;
				 
				ParseObject message = mMessages.get(position);
				ParseUser currentUser = ParseUser.getCurrentUser();
				userId = currentUser.getObjectId();
				userName = currentUser.getUsername();
				
				// facebook users only
				if (currentUser.get("profile") != null) {
					JSONObject userProfile = currentUser.getJSONObject("profile");
					try {
						if (userProfile.getString("facebookId") != null) {
							userId = userProfile.get("facebookId")
									.toString();
						}
						if(userProfile.getString("name") != null){
							userName =  userProfile.getString("name");
						}
						
					} catch (JSONException er) {
						
					}
				}
				postId = message.getObjectId();
				/*
				if (mView.getId() == R.id.heart_button_1) {
					ParseObject curr; 
					if(mUserVotesMap.get(postId) == null){
						curr = new ParseObject(
								ParseConstants.CLASS_USER_VOTE);
						curr.put(ParseConstants.KEY_USER_ID, userId);
						curr.put(ParseConstants.KEY_POST_ID, postId);
						curr.put(ParseConstants.KEY_IS_FOLLOWER, 0);
						
						curr.saveInBackground();
					}
					else{
						curr = mUserVotesMap.get(postId);
					}
					followerVote = curr.getInt(ParseConstants.KEY_IS_FOLLOWER);
					followerVotes = message
							.getInt(ParseConstants.KEY_FOLLOWERS);

					if (followerVote == 0) {

						followerVotes++;
						mHolder.heartCounter.setText("" + followerVotes);
						message.put(ParseConstants.KEY_FOLLOWERS, followerVotes);
						message.saveInBackground();
						curr.put(ParseConstants.KEY_IS_FOLLOWER, 1);
						curr.saveInBackground();
						mUserVotesMap.put(postId, curr);

					} else {
						followerVotes--;
						mHolder.heartCounter.setText("" + followerVotes);
						message.put(ParseConstants.KEY_FOLLOWERS, followerVotes);
						message.saveInBackground();

						curr.put(ParseConstants.KEY_IS_FOLLOWER, 0);
						curr.saveInBackground();
						mUserVotesMap.put(postId, curr);
						if (mFeedType == InboxFragment.FAVORITES)
							mMessages.remove(position);
					}

					notifyDataSetChanged();
				} else if (mView.getId() == R.id.comment_button_1) {

					Intent intent = new Intent(mContext, CommentsActivity.class);
					intent.putExtra("postId", postId);
					intent.putExtra("userId", userId);
					intent.putExtra("userName", userName);
					mContext.startActivity(intent);
					
				} 
				else {*/
					mLastPostClicked =message.getObjectId();
					thisVotes = message.getInt(ParseConstants.KEY_THIS_VOTES);
					thatVotes = message.getInt(ParseConstants.KEY_THAT_VOTES);
					int oldVote = mUserVotesMap.containsKey(postId) ? (mUserVotesMap
							.get(postId)).getInt(ParseConstants.KEY_USER_VOTE)
							: NO_SELECTION;
					int newVote = (mView.getId() == R.id.ThisPicture || mView
							.getId() == R.id.thisLabel) ? THIS_IMAGE
							: THAT_IMAGE;
					//Log.d(postId, "Old vote is" + oldVote);
					/*updateVoteCounts(userId, postId, oldVote, newVote,
							mMessages.get(position));*/
					if(oldVote != NO_SELECTION){
						//launch new activity
						Intent intent = new Intent(mContext, VotesActivity.class);
						PostItem item = new PostItem();
						item.setSenderName((message.getString(ParseConstants.KEY_SENDER_NAME)));
						item.setSenderId((message.getString(ParseConstants.KEY_SENDER_ID)));
						
						item.setObjectId(postId);
						item.setThisCaption(message.getString(ParseConstants.KEY_THIS_CAPTION));
						item.setThatCaption(message.getString(ParseConstants.KEY_THAT_CAPTION));
						item.setThisVotes(message.getInt(ParseConstants.KEY_THIS_VOTES));
						item.setThatVotes(message.getInt(ParseConstants.KEY_THAT_VOTES));
						item.setQuestionText(message.getString(ParseConstants.KEY_QUESTION_TEXT));
						item.setThisImage(message.getString("thisUri"));
						item.setThatImage(message.getString("thatUri"));
						item.setColor(mColorArray[message.getInt("color")]);
						//String jsonPost = new Gson().toJson(item);
						//Log.d("HI", jsonPost);
						intent.putExtra(ParseConstants.KEY_USER_VOTE, mUserVotesMap.get(postId).getInt(ParseConstants.KEY_USER_VOTE));
						intent.putExtra("postItem", item);
///						intent.putExtra("thisUri", Uri.parse(message.getString("thisUri"));
//						intent.putExtra("thatUri", Uri.parse(message.getString("thatUri"));
						
						/*
						intent.putExtra("postId", postId);
						intent.putExtra("userId", userId);
						intent.putExtra("userName", userName);
						intent.putExtra(ParseConstants.KEY_USER_VOTE, mUserVotesMap.get(postId).getInt(ParseConstants.KEY_USER_VOTE));
						intent.putExtra(ParseConstants.KEY_THIS_CAPTION, message.getString(ParseConstants.KEY_THIS_CAPTION));
						intent.putExtra(ParseConstants.KEY_THAT_CAPTION, message.getString(ParseConstants.KEY_THAT_CAPTION));
						intent.putExtra(ParseConstants.KEY_QUESTION_TEXT, message.getString(ParseConstants.KEY_QUESTION_TEXT));
						intent.putExtra("thisUri", message.getString("thisUri"));
						intent.putExtra("thatUri", message.getString("thisUri"));
						intent.putExtra(("senderName"), message.getString("senderName"));*/
						
						mContext.startActivity(intent);
					}
					else{
						
					updateVoteAtomically(userId,postId,oldVote,newVote, userName, mMessages.get(position));
					
					}
				

			}
/*
			public void updateVoteCounts(String userId, String postId,
					int oldVote, int newVote, ParseObject message) {
				if (oldVote == NO_SELECTION) {
					if (newVote == THIS_IMAGE) {
						thisVotes++;

					} else {
						thatVotes++;

					}
					ParseObject parseObject = null;
					parseObject = mUserVotesMap.get(postId);

					if (parseObject == null) {
						parseObject = new ParseObject(
								ParseConstants.CLASS_USER_VOTE);
						parseObject.put(ParseConstants.KEY_USER_ID, userId);
						parseObject.put(ParseConstants.KEY_POST_ID, postId);
					}
					parseObject.put(ParseConstants.KEY_USER_VOTE, newVote);
					parseObject.saveInBackground();
					mLastPostClicked = postId;
					mUserVotesMap.put(postId, parseObject);

				} else if (oldVote == newVote) {

					if (newVote == THIS_IMAGE) {
						thisVotes--;
						// mHolder.thisVot.setText("" + thisVotes);
					} else {
						thatVotes--;
						// mHolder.thatVot.setText("" + thatVotes);
					}
					ParseObject curr = mUserVotesMap.get(postId);
					curr.put(ParseConstants.KEY_USER_VOTE, NO_SELECTION);
					curr.saveInBackground();
					mUserVotesMap.put(postId, curr);

				} else {
					if (newVote == THIS_IMAGE) {
						thisVotes++;
						thatVotes--;
					} else {
						thatVotes++;
						thisVotes--;
					}
					// mHolder.thisVot.setText("" + thisVotes);
					// mHolder.thatVot.setText("" + thatVotes);
					ParseObject curr = mUserVotesMap.get(postId);
					mLastPostClicked = postId;
					curr.put(ParseConstants.KEY_USER_VOTE, newVote);
					curr.saveInBackground();
					mUserVotesMap.put(postId, curr);

				}
				notifyDataSetChanged();
				message.put(ParseConstants.KEY_THIS_VOTES, thisVotes);
				message.put(ParseConstants.KEY_THAT_VOTES, thatVotes);
				message.saveInBackground();
				// update local storage

				// update message object

			}
			*/
			public void updateVoteAtomically(String userId, String postId,
					int oldVote, int vote, String username, ParseObject message){
				
				if (vote == THIS_IMAGE) {
					thisVotes++;
					message.increment(ParseConstants.KEY_THIS_VOTES, 1);

				} else {
					thatVotes++;
					message.increment(ParseConstants.KEY_THAT_VOTES, 1);

				}
				message.saveInBackground();
				
				ParseObject parseObject = null;
				parseObject = mUserVotesMap.get(postId);

				if (parseObject == null) {
					parseObject = new ParseObject(
							ParseConstants.CLASS_USER_VOTE);
					parseObject.put(ParseConstants.KEY_USER_ID, userId);
					parseObject.put(ParseConstants.KEY_USERNAME, username);
					parseObject.put(ParseConstants.KEY_POST_ID, postId);
				}
				parseObject.put(ParseConstants.KEY_USER_VOTE, vote);
				parseObject.saveInBackground();
				mLastPostClicked = postId;
				mUserVotesMap.put(postId, parseObject);
				notifyDataSetChanged();

			}
		};
		
		
		
		
		ParseObject message = mMessages.get(position);
		String postId = message.getObjectId();
		mHolder.From.setText((message.getString("senderName")));
		mHolder.Question.setText((message.getString("questionText")));

		toggleAnimation(mHolder.Question);
		Integer thisVotes = (message.getInt(ParseConstants.KEY_THIS_VOTES));
		Integer thatVotes = (message.getInt(ParseConstants.KEY_THAT_VOTES));
		int totalVotes = thisVotes + thatVotes;
		if (totalVotes == 0){
			totalVotes = 1;
		}
		int thisPercentage = (thisVotes*100)/(totalVotes);
		int thatPercentage = (thatVotes*100)/(totalVotes);
		Integer followers = message.getInt(ParseConstants.KEY_FOLLOWERS);
		Integer commentCount = message.getInt(ParseConstants.KEY_COMMENTS);
	//	mHolder.heartCounter.setText(followers.toString());
	//	mHolder.commentCounter.setText(commentCount.toString());
		//mHolder.thisVot.setText(thisPercentage + "% (" + (message.getInt(ParseConstants.KEY_THIS_VOTES)+")"));
		//mHolder.thatVot.setText(thatPercentage + "% (" + (message.getInt(ParseConstants.KEY_THAT_VOTES) + ")"));

		int randColor =  mColorArray[message.getInt("color")];
		mHolder.mThatBar.setTitleColor(Color.WHITE);
		mHolder.mThatBar.setProgressColor(Color.WHITE);
		mHolder.mThisBar.setTitleColor(Color.WHITE);
		mHolder.mThisBar.setProgressColor(Color.WHITE);
		
		if (!mUserVotesMap.containsKey(postId)
				|| mUserVotesMap.get(postId).getInt(
						ParseConstants.KEY_USER_VOTE) == NO_SELECTION) {
			mHolder.thisVoteDisplay.setVisibility(View.INVISIBLE);
			mHolder.thatVoteDisplay.setVisibility(View.INVISIBLE);
			// mHolder.extrasRow.setVisibility(View.GONE);
			mHolder.ThisCaption.setBackgroundColor(0);
			mHolder.ThatCaption.setBackgroundColor(0);
			/*
			if ((mUserVotesMap.get(postId) != null) && mUserVotesMap.get(postId)
					.getInt(ParseConstants.KEY_IS_FOLLOWER) == 1) {
				mHolder.heartButton.setImageResource(drawable.ic_heart_liked);
			} else {
				mHolder.heartButton.setImageResource(drawable.icon_heart_empty);
			}*/
			
			mHolder.ThisCaption.setTextColor(Color.BLACK);
			mHolder.ThatCaption.setTextColor(Color.BLACK);
			mHolder.ThatCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			mHolder.ThisCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


		} else {
			mHolder.thisVoteDisplay.setVisibility(View.VISIBLE);
			mHolder.thatVoteDisplay.setVisibility(View.VISIBLE);
			if ((mUserVotesMap.get(postId))
					.getInt(ParseConstants.KEY_USER_VOTE) == THIS_IMAGE) {
				mHolder.ThatCaption.setTextColor(Color.BLACK);
				mHolder.ThisCaption.setTextColor(Color.WHITE);
				mHolder.ThatCaption.setBackgroundColor(0);
				mHolder.ThisCaption.setBackgroundColor(randColor);
				mHolder.ThisCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
				mHolder.ThatCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			} else {

				mHolder.ThatCaption.setTextColor(Color.WHITE);
				mHolder.ThisCaption.setTextColor(Color.BLACK);
				mHolder.ThatCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
				mHolder.ThisCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

				//set color randomly
				mHolder.ThatCaption.setBackgroundColor(randColor);
				mHolder.ThisCaption.setBackgroundColor(0);
			}
			
			if(postId.equals(mLastPostClicked)){
				
	
				animatePercentages(thisPercentage,thatPercentage);
				mHolder.mThisBar.setTitle(thisPercentage + "%");
				mHolder.mThatBar.setTitle(thatPercentage + "%");
				mLastPostClicked=null;
			}
			else{
				
				
				mHolder.mThisBar.setProgress(thisPercentage);
				mHolder.mThatBar.setProgress(thatPercentage);
				mHolder.mThisBar.setTitle(thisPercentage + "%");
				mHolder.mThatBar.setTitle(thatPercentage + "%");
			}

/*
			if ((mUserVotesMap.get(postId))
					.getInt(ParseConstants.KEY_IS_FOLLOWER) == 1) {
				mHolder.heartButton.setImageResource(drawable.ic_heart_liked);
			} else {
				mHolder.heartButton.setImageResource(drawable.icon_heart_empty);
			}
*/
		}

		mHolder.ThisCaption.setText(""
				+ message.getString(ParseConstants.KEY_THIS_CAPTION));
		mHolder.ThatCaption.setText(""
				+ message.getString(ParseConstants.KEY_THAT_CAPTION));
		
		Uri thisUri;
		Uri thatUri;
		if(message.getString("thisUri") == null){
			ParseFile This = message.getParseFile("this");
			thisUri = Uri.parse(This.getUrl());
			message.put("thisUri", thisUri.toString());
			message.saveInBackground();
		}
		if(message.getString("thatUri") == null){
			ParseFile That = message.getParseFile("that");
			thatUri = Uri.parse(That.getUrl());
			message.put("thatUri", thatUri.toString());
			message.saveInBackground();
		}
		thisUri = Uri.parse(message.getString("thisUri"));
		thatUri = Uri.parse(message.getString("thatUri"));
		/*

		ParseFile This = message.getParseFile("this");
		ParseFile That = message.getParseFile("that");
		Uri thisUri = Uri.parse(This.getUrl());
		Uri thatUri = Uri.parse(That.getUrl());*/
		
		String uri = message.get(ParseConstants.KEY_FILE_THIS).toString();
		mHolder.mThisProgress.setVisibility(View.VISIBLE);
		Picasso.with(mContext).load(thisUri.toString()).resize(480, 853)
				.centerCrop().into(mHolder.This);
		mHolder.mThatProgress.setVisibility(View.VISIBLE);
		Picasso.with(mContext).load(thatUri.toString()).resize(480, 853)
				.centerCrop().into(mHolder.That);
	 
		mHolder.This.setOnClickListener(onClickListener);
		mHolder.That.setOnClickListener(onClickListener);
		mHolder.ThisCaption.setOnClickListener(onClickListener);
		mHolder.ThatCaption.setOnClickListener(onClickListener);

		//mHolder.heartButton.setOnClickListener(onClickListener);
		//mHolder.commentImage.setOnClickListener(onClickListener);
	}

	public class ThisThatOnClickListener implements OnClickListener {

		int position;

		public ThisThatOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ThisPicture:
				break;
			case R.id.ThatPicture:
				break;
			}

		}

	};

	private static class ViewHolder {
		TextView thatVot;
		TextView thisVot;
		ImageView This;
		ImageView That;
		TextView ThisCaption;
		TextView ThatCaption;
		TextView From;
		TextView Question;
		ImageView heartButton;
		TextView heartCounter;
		RelativeLayout thisVoteDisplay;
		RelativeLayout thatVoteDisplay;
		ProgressBar mThisProgress;
		ProgressBar mThatProgress;
		// TextView thisPercentage;
		// TextView thatPercentage;
		ImageView commentImage;
		TextView commentCounter; 
		CircularProgressBar mThisBar;
		CircularProgressBar mThatBar;

	}

	public void toggleAnimation(TextView textView) {
		float dest = 1;
		if (mHolder.thisVot.getAlpha() > 0) {
			dest = 0;
		}
		ObjectAnimator animation3 = ObjectAnimator.ofFloat(textView, "alpha",
				0f, 1f);
		animation3.setDuration(2000);
		animation3.start();
	}
	private void animatePercentages(int thisPercentage, int thatPercentage){
		mHolder.mThisBar.setTitle(thisPercentage + "%");
		mHolder.mThisBar.animateProgressTo(0, thisPercentage, new ProgressAnimationListener() {

	        @Override
	        public void onAnimationStart() {                
	        }

	        @Override
	        public void onAnimationProgress(int progress) {
	            mHolder.mThisBar.setTitle(progress + "%");
	        }

	        @Override
	        public void onAnimationFinish() {
	           
	        }
	    });
		
		mHolder.mThatBar.setTitle(thatPercentage + "%");
		mHolder.mThatBar.animateProgressTo(0, thatPercentage, new ProgressAnimationListener() {

	        @Override
	        public void onAnimationStart() {                
	        }

	        @Override
	        public void onAnimationProgress(int progress) {
	           mHolder.mThatBar.setTitle(progress + "%");
	        }

	        @Override
	        public void onAnimationFinish() {
	           
	        }
	    });
	}

}
