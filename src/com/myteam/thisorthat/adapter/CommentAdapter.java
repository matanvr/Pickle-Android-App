package com.myteam.thisorthat.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myteam.thisorthat.R;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.ParseObject;

public class CommentAdapter extends ArrayAdapter<ParseObject>{

	
	protected Context mContext;
	protected List<ParseObject> mComments;
	private ViewHolder mHolder;
	
	public CommentAdapter(Context context, List<ParseObject> comments) {
		super(context, R.layout.comment_item, comments);
		mContext = context;
		mComments = comments;

	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.comment_item, null);
			mHolder = new ViewHolder();
			mHolder.mAuthor = (TextView) convertView
					.findViewById(R.id.commentAuthor);
			mHolder.mPost = (TextView) convertView
					.findViewById(R.id.commentPost);
			mHolder.mDate = (TextView) convertView
					.findViewById(R.id.commentDate);
			Typeface myTypeface = Typeface.createFromAsset(
					mContext.getAssets(), "fonts/WhitneyCondensed-Book.otf");
			Typeface postTypeface = Typeface.createFromAsset(
					mContext.getAssets(), "fonts/WhitneyCondensed-Medium.otf");
			Typeface myThickTypeface = Typeface.createFromAsset(
					mContext.getAssets(), "fonts/WhitneyCondensed-Bold.otf");
			Typeface lightType = Typeface.createFromAsset(mContext.getAssets(),
					"fonts/WhitneyCondensed-Light.otf");

			mHolder.mAuthor.setTypeface(lightType);
			mHolder.mPost.setTypeface(myTypeface);
			mHolder.mDate.setTypeface(myTypeface);

			convertView.setTag(mHolder);

		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		Date createdAt = mComments.get(position).getCreatedAt();
		long now = new Date().getTime();
		String convertedDate = DateUtils.getRelativeTimeSpanString(
				createdAt.getTime(), 
				now, 
				DateUtils.SECOND_IN_MILLIS).toString();
		mHolder.mAuthor.setText((mComments.get(position).getString("username")));
		mHolder.mPost.setText((mComments.get(position).getString(ParseConstants.KEY_COMMENT_TEXT)));
		mHolder.mDate.setText(convertedDate);
		

		return convertView;
	}
	
	private static class ViewHolder {
		TextView mAuthor;
		TextView mPost;
		TextView mDate;



		// TextView thisPercentage;
		// TextView thatPercentage;
		ImageView commentImage;
	}
}
