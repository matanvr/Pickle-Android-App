package com.myteam.thisorthat.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myteam.thisorthat.R;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

public class NewsfeedGridAdapter extends ArrayAdapter<ParseObject> {
	
	
	static class ViewHolder {
		ImageView thatImage;
		ImageView thisImage;
		TextView question;
		TextView thisVotes;
		TextView thatVotes;
		TextView thisCaption;
		TextView thatCaption;
		
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
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
			holder = new ViewHolder();

			holder.thisImage = (ImageView) row.findViewById(R.id.this_pic_mini);
			holder.thatImage = (ImageView) row.findViewById(R.id.that_pic_mini);
			holder.question = (TextView) row.findViewById(R.id.question_mini_t);
			holder.thisVotes = (TextView) row.findViewById(R.id.this_votes_min);
			holder.thatVotes = (TextView) row.findViewById(R.id.that_votes_min);
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
		

		
		Picasso.with(mContext).load(Uri.parse(item.getString("thisUri"))).resize(117,200 )
		.centerCrop().into(holder.thisImage);

		Picasso.with(mContext).load(Uri.parse(item.getString("thatUri"))).resize(117, 200)
		.centerCrop().into(holder.thatImage);
		
		holder.question.setTypeface(postTypeface);
		holder.thisVotes.setTypeface(postTypeface);
		holder.thatVotes.setTypeface(postTypeface);
		holder.thisCaption.setTypeface(myTypeface);
		holder.thatCaption.setTypeface(myTypeface);
		int [] mColorArray = mContext.getResources().getIntArray(R.array.post_colors);
		holder.thisCaption.setBackgroundColor(mColorArray[item.getInt("color")]);
		holder.thatCaption.setBackgroundColor(mColorArray[item.getInt("color")]);
		holder.question.setText(item.getString(ParseConstants.KEY_QUESTION_TEXT));
		holder.thisCaption.setText(item.getString(ParseConstants.KEY_THIS_CAPTION));
		holder.thatCaption.setText(item.getString(ParseConstants.KEY_THAT_CAPTION));
		
	//	holder.thisCaption.setBackground();
		/*
		
		ImageOnClickListener onImageClickListener = new ImageOnClickListener(position) {


			@Override
	        public void onClick(View v) {
				//	Toast.makeText(GoogleImageActivity.this, position + "#Selected",
				//		Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				String url = data.get(position).getBigUrl();
				intent.putExtra("imageUrl", url);
				((Activity)context).setResult(Activity.RESULT_OK, intent);
				((Activity)context).finish();
				
				

	        	
	 
			}
		};
		holder.image.setOnClickListener(onImageClickListener);
		*/
		return row;
	};
	
}
