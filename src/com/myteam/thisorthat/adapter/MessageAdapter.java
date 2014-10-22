package com.myteam.thisorthat.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
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

import com.lylc.widget.circularprogressbar.example.CircularProgressBar;
import com.lylc.widget.circularprogressbar.example.CircularProgressBar.ProgressAnimationListener;
import com.myteam.thisorthat.R;
import com.myteam.thisorthat.activity.VotesActivity;
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
	private int[] mColorArray;

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

			mHolder.thisVoteDisplay = (RelativeLayout) convertView
					.findViewById(R.id.thisCircle);
			mHolder.thatVoteDisplay = (RelativeLayout) convertView
					.findViewById(R.id.thatCircle);
			mVotesAnimation = AnimationUtils.loadAnimation(
					mContext.getApplicationContext(), R.anim.bounce_in_bottom);
			mHolder.mThisBar = (CircularProgressBar) convertView
					.findViewById(R.id.circlethisbar);
			mHolder.mThatBar = (CircularProgressBar) convertView
					.findViewById(R.id.circlethatbar);
			mColorArray = mContext.getResources().getIntArray(
					R.array.post_colors);
			mHolder.moreOptions = (ImageView) convertView
					.findViewById(R.id.moreOptions);


			Typeface myTypeface = Typeface.createFromAsset(
					mContext.getAssets(), "fonts/WhitneyCondensed-Book.otf");
			Typeface postTypeface = Typeface.createFromAsset(
					mContext.getAssets(), "fonts/WhitneyCondensed-Medium.otf");
			Typeface myLightType = Typeface.createFromAsset(
					mContext.getAssets(), "fonts/WhitneyCondensed-Light.otf");
			Typeface lightType = Typeface.createFromAsset(mContext.getAssets(),
					"fonts/WhitneyCondensed-Medium.otf");

			mHolder.Question.setTypeface(postTypeface);

			mHolder.ThisCaption.setTypeface(myTypeface);
			mHolder.ThatCaption.setTypeface(myTypeface);
			mHolder.From.setTypeface(myLightType);

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
				userId = getUserId();
				userName = getUsername();

				postId = message.getObjectId();

				mLastPostClicked = message.getObjectId();

				thisVotes = message.getInt(ParseConstants.KEY_THIS_VOTES);
				thatVotes = message.getInt(ParseConstants.KEY_THAT_VOTES);
				int oldVote = mUserVotesMap.containsKey(postId) ? (mUserVotesMap
						.get(postId)).getInt(ParseConstants.KEY_USER_VOTE)
						: NO_SELECTION;

				if (mView.getId() == R.id.moreOptions) {
					ContextThemeWrapper cw = new ContextThemeWrapper(mContext,
							R.style.AlertDialogTheme);

					AlertDialog.Builder builder = new AlertDialog.Builder(cw);
					ParseObject vote = mUserVotesMap.get(postId);
					if (vote == null)
						builder.setItems(R.array.post_choices, mDialogListener);
					else if (vote.getBoolean(ParseConstants.KEY_IS_FLAGGED)
							&& vote.getBoolean(ParseConstants.KEY_IS_SUBSCRIBED))
						builder.setItems(R.array.post_followed_flagged_choices,
								mDialogListener);
					else if (vote.getBoolean(ParseConstants.KEY_IS_FLAGGED))
						builder.setItems(R.array.post_flagged_choices,
								mDialogListener);
					else if (vote.getBoolean(ParseConstants.KEY_IS_SUBSCRIBED))
						builder.setItems(R.array.post_followed_choices,
								mDialogListener);
					else
						builder.setItems(R.array.post_choices, mDialogListener);

					AlertDialog dialog = builder.create();
					dialog.show();

				} else {
					int newVote = (mView.getId() == R.id.ThisPicture || mView
							.getId() == R.id.thisLabel) ? THIS_IMAGE
							: THAT_IMAGE;

					if (oldVote != NO_SELECTION) {
						// launch new activity
						Intent intent = new Intent(mContext,
								VotesActivity.class);
						PostItem item = new PostItem();
						item.setSenderName((message
								.getString(ParseConstants.KEY_SENDER_NAME)));
						item.setSenderId((message
								.getString(ParseConstants.KEY_SENDER_ID)));

						item.setObjectId(postId);
						item.setThisCaption(message
								.getString(ParseConstants.KEY_THIS_CAPTION));
						item.setThatCaption(message
								.getString(ParseConstants.KEY_THAT_CAPTION));
						item.setThisVotes(message
								.getInt(ParseConstants.KEY_THIS_VOTES));
						item.setThatVotes(message
								.getInt(ParseConstants.KEY_THAT_VOTES));
						item.setQuestionText(message
								.getString(ParseConstants.KEY_QUESTION_TEXT));
						item.setThisImage(message.getString("thisUri"));
						item.setThatImage(message.getString("thatUri"));
						item.setColor(mColorArray[message.getInt("color")]);
						intent.putExtra(
								ParseConstants.KEY_USER_VOTE,
								mUserVotesMap.get(postId).getInt(
										ParseConstants.KEY_USER_VOTE));
						intent.putExtra("postItem", item);

						mContext.startActivity(intent);
					} else {

						updateVoteAtomically(userId, postId, oldVote, newVote,
								userName, mMessages.get(position));

					}
				}

			}

			public void updateVoteAtomically(String userId, String postId,
					int oldVote, int vote, String username, ParseObject message) {

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
		if (totalVotes == 0) {
			totalVotes = 1;
		}
		int thisPercentage = (thisVotes * 100) / (totalVotes);
		int thatPercentage = (thatVotes * 100) / (totalVotes);
		Integer followers = message.getInt(ParseConstants.KEY_FOLLOWERS);
		Integer commentCount = message.getInt(ParseConstants.KEY_COMMENTS);
		int randColor = mColorArray[message.getInt("color")];
		mHolder.mThatBar.setTitleColor(Color.WHITE);
		mHolder.mThatBar.setProgressColor(Color.WHITE);
		mHolder.mThisBar.setTitleColor(Color.WHITE);
		mHolder.mThisBar.setProgressColor(Color.WHITE);

		if (!mUserVotesMap.containsKey(postId)
				|| mUserVotesMap.get(postId).getInt(
						ParseConstants.KEY_USER_VOTE) == NO_SELECTION) {
			mHolder.thisVoteDisplay.setVisibility(View.INVISIBLE);
			mHolder.thatVoteDisplay.setVisibility(View.INVISIBLE);
			mHolder.ThisCaption.setBackgroundColor(Color.WHITE);
			mHolder.ThatCaption.setBackgroundColor(Color.WHITE);

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
				mHolder.ThatCaption.setBackgroundColor(Color.WHITE);
				mHolder.ThisCaption.setBackgroundColor(randColor);
				mHolder.ThisCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				mHolder.ThatCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			} else {

				mHolder.ThatCaption.setTextColor(Color.WHITE);
				mHolder.ThisCaption.setTextColor(Color.BLACK);
				mHolder.ThatCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				mHolder.ThisCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

				// set color randomly
				mHolder.ThatCaption.setBackgroundColor(randColor);
				mHolder.ThisCaption.setBackgroundColor(Color.WHITE);
			}

			if (postId.equals(mLastPostClicked)) {

				animatePercentages(thisPercentage, thatPercentage);
				mHolder.mThisBar.setTitle(thisPercentage + "%");
				mHolder.mThatBar.setTitle(thatPercentage + "%");

			} else {

				mHolder.mThisBar.setProgress(thisPercentage);
				mHolder.mThatBar.setProgress(thatPercentage);
				mHolder.mThisBar.setTitle(thisPercentage + "%");
				mHolder.mThatBar.setTitle(thatPercentage + "%");
			}
		}

		mHolder.ThisCaption.setText(""
				+ message.getString(ParseConstants.KEY_THIS_CAPTION));
		mHolder.ThatCaption.setText(""
				+ message.getString(ParseConstants.KEY_THAT_CAPTION));

		Uri thisUri;
		Uri thatUri;
		if (message.getString("thisUri") == null) {
			ParseFile This = message.getParseFile("this");
			thisUri = Uri.parse(This.getUrl());
			message.put("thisUri", thisUri.toString());
			message.saveInBackground();
		}
		if (message.getString("thatUri") == null) {
			ParseFile That = message.getParseFile("that");
			thatUri = Uri.parse(That.getUrl());
			message.put("thatUri", thatUri.toString());
			message.saveInBackground();
		}
		thisUri = Uri.parse(message.getString("thisUri"));
		thatUri = Uri.parse(message.getString("thatUri"));

		String uri = message.get(ParseConstants.KEY_FILE_THIS).toString();
		//mHolder.mThisProgress.setVisibility(View.VISIBLE);
		Picasso.with(mContext).load(thisUri.toString()).resize(480, 853)
				.centerCrop().into(mHolder.This);
		//mHolder.mThatProgress.setVisibility(View.VISIBLE);
		Picasso.with(mContext).load(thatUri.toString()).resize(480, 853)
				.centerCrop().into(mHolder.That);

		mHolder.This.setOnClickListener(onClickListener);
		mHolder.That.setOnClickListener(onClickListener);
		mHolder.ThisCaption.setOnClickListener(onClickListener);
		mHolder.ThatCaption.setOnClickListener(onClickListener);
		mHolder.moreOptions.setOnClickListener(onClickListener);

		// mHolder.heartButton.setOnClickListener(onClickListener);
		// mHolder.commentImage.setOnClickListener(onClickListener);
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
		ImageView moreOptions;
		ImageView commentImage;
		TextView commentCounter;
		CircularProgressBar mThisBar;
		CircularProgressBar mThatBar;

	}

	public void toggleAnimation(TextView textView) {
		float dest = 1;
		if (mHolder.mThisBar.getAlpha() > 0) {
			dest = 0;
		}
		ObjectAnimator animation3 = ObjectAnimator.ofFloat(textView, "alpha",
				0f, 1f);
		animation3.setDuration(2000);
		animation3.start();
	}

	private void animatePercentages(int thisPercentage, int thatPercentage) {
		mHolder.mThisBar.setTitle(thisPercentage + "%");
		mHolder.mThisBar.animateProgressTo(0, thisPercentage,
				new ProgressAnimationListener() {

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
		mHolder.mThatBar.animateProgressTo(0, thatPercentage,
				new ProgressAnimationListener() {

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

	public void refill(List<ParseObject> posts) {
		mMessages.clear();
		mMessages.addAll(posts);
		notifyDataSetChanged();
	}

	protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			int index = 0;
			// find index of item to be removed;
			for (ParseObject c : mMessages) {
				if (c.getObjectId().equals(mLastPostClicked)) {
					break;
				}
				index++;

			}
			ParseObject vote = mUserVotesMap.get(mLastPostClicked);
			if (vote == null) {
				vote = new ParseObject(ParseConstants.CLASS_USER_VOTE);
				vote.put(ParseConstants.KEY_USER_ID, getUserId());
				vote.put(ParseConstants.KEY_USERNAME, getUsername());
				vote.put(ParseConstants.KEY_POST_ID, mLastPostClicked);
			}
			switch (which) {
			case 0: // Subscribe or unsubscribe
				if (vote.getBoolean(ParseConstants.KEY_IS_SUBSCRIBED))
					vote.put(ParseConstants.KEY_IS_SUBSCRIBED, false);
				else
					vote.put(ParseConstants.KEY_IS_SUBSCRIBED, true);
				break;
			case 1: // flag post
				if (vote.getBoolean(ParseConstants.KEY_IS_FLAGGED))
					vote.put(ParseConstants.KEY_IS_FLAGGED, false);
				else
					vote.put(ParseConstants.KEY_IS_FLAGGED, true);
				break;
			case 2: // remove post
				vote.put(ParseConstants.KEY_IS_REMOVED, true);
				mMessages.remove(index);
				notifyDataSetChanged();
				break;

			}
			vote.saveInBackground();
		}

	};

	private String getUsername() {
		ParseUser currentUser = ParseUser.getCurrentUser();

		String userName = currentUser.getUsername();

		// facebook users only
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {

				if (userProfile.getString("name") != null) {
					userName = userProfile.getString("name");
				}

			} catch (JSONException er) {

			}
		}
		return userName;
	}

	private String getUserId() {
		ParseUser currentUser = ParseUser.getCurrentUser();

		String userId = currentUser.getObjectId();

		// facebook users only
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {

				if (userProfile.getString("facebookId") != null) {
					userId = userProfile.get("facebookId").toString();
				}

			} catch (JSONException er) {

			}
		}
		return userId;
	}
}
