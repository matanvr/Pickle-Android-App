package com.myteam.thisorthat.adapter;

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
import com.myteam.thisorthat.CommentsActivity;
import com.myteam.thisorthat.R;
import com.myteam.thisorthat.VotesActivity;
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
	public NewsfeedGridAdapter(Context context, int layoutResourceId,
			List <ParseObject> posts) {
		super(context, layoutResourceId, posts);
		mLayoutResourceId = layoutResourceId;
		mContext = context;
		mPosts = posts;
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

			holder.thisBar = (CircularProgressBar) row.findViewById(R.id.circle_this);
			holder.thatBar = (CircularProgressBar) row.findViewById(R.id.circle_that);
			holder.thisCaption = (TextView) row.findViewById(R.id.this_mini_caption);
			holder.thatCaption = (TextView) row.findViewById(R.id.that_mini_caption);
			
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
		//holder.thisVotes.setTypeface(postTypeface);
		//holder.thatVotes.setTypeface(postTypeface);
		holder.thisCaption.setTypeface(myTypeface);
		holder.thatCaption.setTypeface(myTypeface);
		int [] mColorArray = mContext.getResources().getIntArray(R.array.post_colors);
		holder.thisCaption.setBackgroundColor(mColorArray[item.getInt("color")]);
		holder.thatCaption.setBackgroundColor(mColorArray[item.getInt("color")]);
		holder.question.setText(item.getString(ParseConstants.KEY_QUESTION_TEXT));
		holder.thisCaption.setText(item.getString(ParseConstants.KEY_THIS_CAPTION));
		holder.thatCaption.setText(item.getString(ParseConstants.KEY_THAT_CAPTION));
		//holder.thisVotes.setText(Integer.toString(thisPercentage)+"%");
		//holder.thatVotes.setText(Integer.toString(thatPercentage)+"%");
		holder.thatBar.setTitleColor(Color.WHITE);
		holder.thatBar.setProgressColor(Color.WHITE);
		holder.thisBar.setTitleColor(Color.WHITE);
		holder.thisBar.setProgressColor(Color.WHITE);
		holder.thisBar.setTitle(thisPercentage+"%");
		holder.thisBar.setProgress(thisPercentage);
		holder.thatBar.setTitle(thatPercentage+"%");
		holder.thatBar.setProgress(thatPercentage);
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
			intent.putExtra(ParseConstants.KEY_USER_VOTE, 1); //TODO FIX
			intent.putExtra("postItem", item);

			
			mContext.startActivity(intent);

		}

	};
}
