package com.myteam.thisorthat;

import java.util.HashMap;
import java.util.List;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
	private ViewHolder mHolder;
	
	public MessageAdapter(Context context, List<ParseObject> messages, List<ParseObject> userVotes) {
		super(context, R.layout.message_item, messages);
		mContext = context;
		mMessages = messages;
		mUserVotes = userVotes;
		mUserVotesMap = new HashMap<String,ParseObject>();
		for (ParseObject curr : mUserVotes){
			mUserVotesMap.put(curr.get(ParseConstants.KEY_POST_ID).toString(), curr);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
			mHolder = new ViewHolder();
			mHolder.This = (ImageView)convertView.findViewById(R.id.ThisPicture);
			mHolder.That = (ImageView)convertView.findViewById(R.id.ThatPicture);
			mHolder.From = (TextView)convertView.findViewById(R.id.FromLabelView);
			mHolder.thisVot = (TextView)convertView.findViewById(R.id.thisVotes);
			mHolder.thatVot = (TextView)convertView.findViewById(R.id.thatVotes);
			mHolder.ThatCaption = (TextView) convertView.findViewById(R.id.thatLabel); 
			mHolder.ThisCaption = (TextView) convertView.findViewById(R.id.thisLabel); 
			mHolder.Question = (TextView)convertView.findViewById(R.id.Question);
			
			Typeface myTypeface = Typeface.createFromAsset(
                    mContext.getAssets(),
                    "fonts/Roboto-Regular.ttf");
			Typeface myThickTypeface = Typeface.createFromAsset(
                    mContext.getAssets(),
                    "fonts/Roboto-Bold.ttf");
			Typeface lightType = Typeface.createFromAsset(
                    mContext.getAssets(),
                    "fonts/Roboto-LightItalic.ttf");

			mHolder.Question.setTypeface(myTypeface);
			mHolder.thisVot.setTypeface(myTypeface);
			mHolder.thatVot.setTypeface(myTypeface);
			mHolder.ThisCaption.setTypeface(myTypeface);
			mHolder.ThatCaption.setTypeface(myTypeface);
			mHolder.From.setTypeface(myThickTypeface);

			convertView.setTag(mHolder);
			
		}
		else {
			mHolder = (ViewHolder)convertView.getTag();
		}
		displayPost(position);
		

			return convertView;
		}

	private void displayPost(int position) {
		ParseObject message = mMessages.get(position);
		String postId = message.getObjectId();
		mHolder.From.setText((message.getString("senderName")) );
		mHolder.Question.setText((message.getString("questionText")));
		
		mHolder.thisVot.setText(""+(message.getInt(ParseConstants.KEY_THIS_VOTES)));
		mHolder.thatVot.setText(""+(message.getInt(ParseConstants.KEY_THAT_VOTES)));
		if(!mUserVotesMap.containsKey(postId) || 
				mUserVotesMap.get(postId).getInt(ParseConstants.KEY_USER_VOTE) == NO_SELECTION){
			mHolder.thisVot.setVisibility(View.GONE);
			mHolder.thatVot.setVisibility(View.GONE);
		}
		else{
			mHolder.thatVot.setVisibility(View.VISIBLE);
			mHolder.thisVot.setVisibility(View.VISIBLE);
			toggleAnimation(mHolder.thisVot);//.setVisibility(View.VISIBLE);
			toggleAnimation(mHolder.thatVot);//	mHolder.thatVot.setisibility(View.VISIBLE);
			if(message.getInt(ParseConstants.KEY_USER_VOTE) == THIS_IMAGE){
				mHolder.ThisCaption.setTextColor(mContext.getResources().getColor(android.R.color.black));
			}

		}
		mHolder.ThisCaption.setText(""+message.getString(ParseConstants.KEY_THIS_CAPTION));
		mHolder.ThatCaption.setText(""+message.getString(ParseConstants.KEY_THAT_CAPTION));


		ParseFile This = message.getParseFile("this");
		ParseFile That = message.getParseFile("that");
		Uri thatUri = Uri.parse(This.getUrl());
		Uri thisUri = Uri.parse(That.getUrl());
		String uri = message.get(ParseConstants.KEY_FILE_THIS).toString();
		Picasso.with(mContext).load(thatUri.toString()).resize(402, 600).centerCrop().into(mHolder.This);
		
		Picasso.with(mContext).load(thisUri.toString()).resize(402, 600).centerCrop().into(mHolder.That);
		
		ThisThatOnClickListener onClickListener = new ThisThatOnClickListener(position) {
        	public String userId;
        	public String postId;
        	public int thisVotes;
        	public int thatVotes;

			@Override
	        public void onClick(View v) {
				mView = v;
	        	ParseObject message = mMessages.get(position);
	        	ParseUser currentUser = ParseUser.getCurrentUser();
	        	userId = currentUser.getObjectId();
	        	postId = message.getObjectId();
	        	thisVotes = message.getInt(ParseConstants.KEY_THIS_VOTES);
	        	thatVotes = message.getInt(ParseConstants.KEY_THAT_VOTES);
    			int oldVote = mUserVotesMap.containsKey(postId) ? (mUserVotesMap.get(postId)).getInt(ParseConstants.KEY_USER_VOTE) : NO_SELECTION;

	        	int newVote = mView.getId() == R.id.ThisPicture ? THIS_IMAGE : THAT_IMAGE;
	        	updateVoteCounts(userId,postId,oldVote,newVote,mMessages.get(position));
	        	
	 
			}
			public void updateVoteCounts(String userId, String postId,
					int oldVote, int newVote, ParseObject message){
				if(oldVote == NO_SELECTION){
					if(newVote == THIS_IMAGE){
						thisVotes++;
						
					}
					else{
						thatVotes++;
						
					}
					ParseObject parseObject = null;
					parseObject = mUserVotesMap.get(postId);

	    			if(parseObject == null){
	    				parseObject = new ParseObject(ParseConstants.CLASS_USER_VOTE);
	    				parseObject.put(ParseConstants.KEY_USER_ID, userId);
	    				parseObject.put(ParseConstants.KEY_POST_ID, postId);
	    			}
					parseObject.put(ParseConstants.KEY_USER_VOTE,newVote);
					parseObject.saveInBackground();
					mUserVotesMap.put(postId, parseObject);
					
				}
				else if(oldVote == newVote){
					
					if(newVote == THIS_IMAGE){
						thisVotes--;
						mHolder.thisVot.setText(""+ thisVotes);
					}
					else{
						thatVotes--;
						mHolder.thatVot.setText(""+thatVotes);
					}
					ParseObject curr = mUserVotesMap.get(postId);
					curr.put(ParseConstants.KEY_USER_VOTE, NO_SELECTION);
					curr.saveInBackground();

					
				}
				else{
					if(newVote == THIS_IMAGE){
						thisVotes++;
						thatVotes--;
					}
					else{
						thatVotes++;
						thisVotes--;
					}
					mHolder.thisVot.setText(""+thisVotes);
					mHolder.thatVot.setText(""+thatVotes);
					ParseObject curr = mUserVotesMap.get(postId);
					curr.put(ParseConstants.KEY_USER_VOTE, newVote);
					curr.saveInBackground();

					
				}
				notifyDataSetChanged();
				message.put(ParseConstants.KEY_THIS_VOTES,thisVotes);
				message.put(ParseConstants.KEY_THAT_VOTES, thatVotes);
				message.saveInBackground();
				//update local storage
				
				//update message object 
				
			}
		};
		mHolder.This.setOnClickListener(onClickListener);
		mHolder.That.setOnClickListener(onClickListener);
	}
	

	
	public class ThisThatOnClickListener implements OnClickListener{

	     int position;
	     public ThisThatOnClickListener(int position) {
	          this.position = position;
	     }

	     @Override
	     public void onClick(View v) {
	    	 switch(v.getId()){
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
		}
		
		public void refill(List<ParseObject> messages) {
			mMessages.clear();
			mMessages.addAll(messages);
			notifyDataSetChanged();
		}
		public void toggleAnimation(TextView textView){
	      float dest = 1;
	      if (mHolder.thisVot.getAlpha() > 0) {
	        dest = 0;
	      }
	      ObjectAnimator animation3 = ObjectAnimator.ofFloat(textView,
	          "alpha", 0f, 1f);
	      animation3.setDuration(2000);
	      animation3.start();
		}
	        	
}


