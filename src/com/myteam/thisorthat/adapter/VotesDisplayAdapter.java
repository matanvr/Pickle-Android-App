package com.myteam.thisorthat.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myteam.thisorthat.R;
import com.myteam.thisorthat.R.drawable;
import com.myteam.thisorthat.model.UserBubbleRow;
import com.myteam.thisorthat.util.CircularTransformation;
import com.myteam.thisorthat.util.ParseConstants;
import com.squareup.picasso.Picasso;

public class VotesDisplayAdapter  extends ArrayAdapter<UserBubbleRow>{
	protected Context mContext;
	protected List<UserBubbleRow> mVotes;
	private ViewHolder mHolder;
	private static final int THIS_IMAGE = 1;
	private static final int THAT_IMAGE = 2;
	private static class ViewHolder {
		TextView mThisUser;
		TextView mThatUser;
		TextView mThisDate;
		TextView mThatDate;
		ImageView mThisThumb;
		ImageView mThatThumb;

	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.votes_row, null);
			mHolder = new ViewHolder();
			mHolder.mThisUser = (TextView) convertView
					.findViewById(R.id.thisUser);
			mHolder.mThatUser = (TextView) convertView
					.findViewById(R.id.thatUser);
			mHolder.mThatThumb = (ImageView) convertView.findViewById(R.id.thatThumb);
			mHolder.mThisThumb = (ImageView) convertView.findViewById(R.id.thisThumb);

			Typeface myTypeface = Typeface.createFromAsset(
					mContext.getAssets(), "fonts/WhitneyCondensed-Book.otf");

			mHolder.mThisUser.setTypeface(myTypeface);
			mHolder.mThatUser.setTypeface(myTypeface);

			convertView.setTag(mHolder);
		}
		else{
			mHolder = (ViewHolder) convertView.getTag();
		}
		


		loadImage(mVotes.get(position).getThisUser(), THIS_IMAGE, position);
		loadImage(mVotes.get(position).getThatUser(), THAT_IMAGE, position);

		return convertView;
	}
	private void loadImage(String id, int PicSelected, int position){
		if(PicSelected == THIS_IMAGE){
			if(mVotes.get(position).getThisUser() == null)
				return;
			mHolder.mThisUser.setText(mVotes.get(position).getThisUser());
			
			String thisThumb = ParseConstants.
				getUserPic(mVotes.get(position).getThisUserid());
			if(thisThumb == null){
				Picasso.with(mContext).load(drawable.defaultuser).transform(new CircularTransformation()).into(mHolder.mThisThumb);
			}else{
				Picasso.with(mContext).load(thisThumb).transform(new CircularTransformation()).into(mHolder.mThisThumb);
			}	
		}
		
		if(PicSelected == THAT_IMAGE){
			if(mVotes.get(position).getThatUser() == null)
				return;
			
			mHolder.mThatUser.setText(mVotes.get(position).getThatUser());
			
			String thatThumb = ParseConstants.
				getUserPic(mVotes.get(position).getThatUserid());
			if(thatThumb == null){
				Picasso.with(mContext).load(drawable.defaultuser).transform(new CircularTransformation()).into(mHolder.mThatThumb);
			}else{
				Picasso.with(mContext).load(thatThumb).transform(new CircularTransformation()).into(mHolder.mThatThumb);
			}	
		}
	}
	public VotesDisplayAdapter(List<UserBubbleRow> votes, Context context){
		super(context, R.layout.votes_row, votes);
		mContext = context;
		mVotes = votes;
	}
	

	
}
