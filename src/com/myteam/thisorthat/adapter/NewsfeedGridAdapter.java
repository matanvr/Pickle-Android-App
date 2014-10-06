package com.myteam.thisorthat.adapter;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lylc.widget.circularprogressbar.example.CircularProgressBar;
import com.myteam.thisorthat.R;
import com.myteam.thisorthat.activity.VotesActivity;
import com.myteam.thisorthat.model.PostItem;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

public class NewsfeedGridAdapter extends ArrayAdapter<ParseObject> {
	
	
	static class ViewHolder {
		ImageView thatImage;
		ImageView thisImage;
		TextView question;
		//TextView thisVotes;
		//TextView thatVotes;
		TextView thisCaption;
		TextView thatCaption;
		CircularProgressBar thisBar;
		CircularProgressBar thatBar;
		
	}
	
	private List<ParseObject> mPosts; 
	private Context mContext;
	private int mLayoutResourceId;
	private HashMap <String,ParseObject> userVotesMap;
	
	public NewsfeedGridAdapter(Context context, int layoutResourceId,
			List <ParseObject> posts, List<ParseObject> userVotes) {
		super(context, layoutResourceId, posts);
		mLayoutResourceId = layoutResourceId;
		mContext = context;
		mPosts = posts;
		userVotesMap = new HashMap<String,ParseObject>();
		for (ParseObject uv : userVotes){
			userVotesMap.put(uv.getString(ParseConstants.KEY_POST_ID), uv);
		}
	}
	private int [] mColorArray;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
			holder = new ViewHolder();

			holder.thisImage = (ImageView) row.findViewById(R.id.this_pic_min);
			holder.thatImage = (ImageView) row.findViewById(R.id.that_pic_min);
			holder.question = (TextView) row.findViewById(R.id.question_mini_t);

			holder.thisBar = (CircularProgressBar) row.findViewById(R.id.circle_result);

			
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		ParseObject item = mPosts.get(position);
		Typeface postTypeface = Typeface.createFromAsset(
				mContext.getAssets(), "fonts/WhitneyCondensed-Medium.otf");
		Typeface myTypeface = Typeface.createFromAsset(
				mContext.getAssets(), "fonts/WhitneyCondensed-Book.otf");
		
		mColorArray = mContext.getResources().getIntArray(R.array.post_colors);    
		
		Picasso.with(mContext).load(Uri.parse(item.getString("thisUri"))).resize(630,528)
		.centerCrop().into(holder.thisImage);

		Picasso.with(mContext).load(Uri.parse(item.getString("thatUri"))).resize(315, 264)
		.centerCrop().into(holder.thatImage);
		int thisVotes = item.getInt(ParseConstants.KEY_THIS_VOTES);
		int thatVotes = item.getInt(ParseConstants.KEY_THAT_VOTES);
		int totalVotes = thisVotes + thatVotes;
		if(totalVotes == 0) totalVotes = 1;
		int thisPercentage = (thisVotes*100)/totalVotes;
		int thatPercentage = (thatVotes*100)/totalVotes;
		
		holder.question.setTypeface(postTypeface);

		int [] mColorArray = mContext.getResources().getIntArray(R.array.post_colors);

		holder.question.setText(item.getString(ParseConstants.KEY_QUESTION_TEXT));

		int displayPercentage = thisPercentage;
		String displayCaption = item.getString(ParseConstants.KEY_THIS_CAPTION);
		if(thisPercentage < thatPercentage){
			displayCaption = item.getString(ParseConstants.KEY_THAT_CAPTION);
			displayPercentage = thatPercentage;
		}
		holder.thisBar.setTitleColor(Color.WHITE);
		holder.thisBar.setSubTitleColor(Color.WHITE);
		holder.thisBar.setProgressColor(Color.WHITE);
		holder.thisBar.setSubTitle(displayPercentage+"%");
		holder.thisBar.setTitle(displayCaption);
		holder.thisBar.setProgress(displayPercentage);
		holder.thisImage.setOnClickListener(new ItemOnClickListener(position));
		holder.thatImage.setOnClickListener(new ItemOnClickListener(position));

		
		

		return row;
	};
	
	public class ItemOnClickListener implements OnClickListener {

		int position;

		public ItemOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, VotesActivity.class);
			PostItem item = new PostItem();
			ParseObject message = mPosts.get(position);
			String postId = message.getObjectId();
			int userVote = userVotesMap.get(postId).getInt(ParseConstants.KEY_USER_VOTE);
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
			intent.putExtra(ParseConstants.KEY_USER_VOTE, userVote); //TODO FIX
			intent.putExtra("postItem", item);

			
			mContext.startActivity(intent);

		}

	};
}
