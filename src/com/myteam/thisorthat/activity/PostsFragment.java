//TODO Need to delete this eventually
package com.myteam.thisorthat.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.lylc.widget.circularprogressbar.example.CircularProgressBar;
import com.lylc.widget.circularprogressbar.example.CircularProgressBar.ProgressAnimationListener;
import com.myteam.thisorthat.R;
import com.myteam.thisorthat.util.BlurTransform;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class PostsFragment extends Fragment {
	private static String EXTRA_TITLE = "extra_title";
	protected List<ParseObject> mMessages;
	protected List<ParseObject> mUserVotes;

	protected static final String MESSAGE_KEY = "Messages";

	private int mFeed;
	private ParseUser currentUser;
	public static final String TAG = InboxFragment.class.getSimpleName();
	
	
	private TextView mQuestion;
	private ImageView mThisImage;
	private ImageView mThatImage;
	private ImageView mThisBlur;
	private ImageView mThatBlur;
	private TextView mThisCaption;
	private TextView mThatCaption;
	private TextView mThisVotes;
	private TextView mThatVotes;
	private Animation mVotesAnimation;
	public static int THIS_VOTE = 1;
	public static int THAT_VOTE = 2;
	private static int mCurrentItem = 32;
	private Uri mThisUri;
	private Uri mThatUri;

	CircularProgressBar mThisBar;
	CircularProgressBar mThatBar;


	public void getPosts() {

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstants.CLASS_DILEMMA);
		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {

				if (e == null) {
					// We found messages!

					mMessages = messages;
					ParseQuery<ParseObject> userPosts = new ParseQuery<ParseObject>(
							ParseConstants.CLASS_USER_VOTE);
					userPosts.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
					String userId = ParseUser.getCurrentUser().getObjectId();
					// facebook users only
					if (currentUser.get("profile") != null) {
						JSONObject userProfile = currentUser
								.getJSONObject("profile");
						try {
							if (userProfile.getString("facebookId") != null) {
								userId = userProfile.get("facebookId")
										.toString();
							}

						} catch (JSONException er) {

						}
					}
					userPosts.whereEqualTo(ParseConstants.KEY_USER_ID, userId);
					userPosts.findInBackground(new FindCallback<ParseObject>() {
						@Override
						public void done(List<ParseObject> userVotes,
								ParseException e) {

							if (e == null) {
								mUserVotes = userVotes;
								showPosts(mUserVotes, mMessages);
							}
						}
					});

				}
			}
		});
	}

	private void showPosts(List<ParseObject> votes, List<ParseObject> posts){
		
		ParseObject currentPost = posts.get(mCurrentItem);
		Typeface myTypeface = Typeface.createFromAsset(
				getActivity().getAssets(), "fonts/WhitneyCondensed-Book.otf");
		Typeface postTypeface = Typeface.createFromAsset(
				getActivity().getAssets(), "fonts/WhitneyCondensed-Medium.otf");
		mQuestion.setTypeface(postTypeface);
		mThisCaption.setTypeface(myTypeface);
		mThatCaption.setTypeface(myTypeface);
		mThisVotes.setTypeface(myTypeface);
		mThatVotes.setTypeface(myTypeface);
		
	
		mQuestion.setText(currentPost.getString(ParseConstants.KEY_QUESTION_TEXT));
		mThisCaption.setText(""
				+ currentPost.getString(ParseConstants.KEY_THIS_CAPTION));
		mThatCaption.setText(""
				+ currentPost.getString(ParseConstants.KEY_THAT_CAPTION));
		
		Integer thisVotes = (currentPost.getInt(ParseConstants.KEY_THIS_VOTES));
		Integer thatVotes = (currentPost.getInt(ParseConstants.KEY_THAT_VOTES));
		int totalVotes = thisVotes + thatVotes;
		if (totalVotes == 0){
			totalVotes = 1;
		}
		int thisPercentage = (thisVotes*100)/(totalVotes);
		int thatPercentage = (thatVotes*100)/(totalVotes);

		
		

		if(currentPost.getString("thisUri") == null){
			ParseFile This = currentPost.getParseFile("this");
			mThisUri = Uri.parse(This.getUrl());
			currentPost.put("thisUri", mThisUri.toString());
			currentPost.saveInBackground();
		}
		if(currentPost.getString("thatUri") == null){
			ParseFile That = currentPost.getParseFile("that");
			mThatUri = Uri.parse(That.getUrl());
			currentPost.put("thatUri", mThatUri.toString());
			currentPost.saveInBackground();
		}
		mThisUri = Uri.parse(currentPost.getString("thisUri"));
		mThatUri = Uri.parse(currentPost.getString("thatUri"));
		/*Picasso.with(getActivity()).load(mThisUri).resize(560, 980)
		.centerCrop().transform(new BlurTransform(getActivity())).into(mThisBlur);
		Picasso.with(getActivity()).load(mThatUri).resize(560, 980)
		.centerCrop().transform(new BlurTransform(getActivity())).into(mThatBlur);
		*/
		Picasso.with(getActivity()).load(mThisUri.toString()).resize(560, 980)
				.centerCrop().into(mThisImage);
	//	mThisBar.setProgress(50);
		Picasso.with(getActivity()).load(mThatUri.toString()).resize(560, 980)
				.centerCrop().into(mThatImage);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dilemma_single, container,
				false);
		
		mQuestion = (TextView) rootView.findViewById(R.id.Question);
		mThisImage = (ImageView) rootView.findViewById(R.id.ThisPicture);
		mThatImage = (ImageView) rootView.findViewById(R.id.ThatPicture);
		mThisCaption =(TextView) rootView.findViewById(R.id.thisLabel); 
		mThatCaption =(TextView) rootView.findViewById(R.id.thatLabel);
		mThisVotes = (TextView) rootView.findViewById(R.id.thisVote);
		mThatVotes = (TextView) rootView.findViewById(R.id.that_Votes);
		mThisBlur =  (ImageView) rootView.findViewById(R.id.ThisPictureBlur);
		mThatBlur =(ImageView) rootView.findViewById(R.id.ThatPictureBlur);
		mVotesAnimation  =  AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.bounce_in_bottom);
		mThisImage.setOnClickListener(new customClickListener() );
		mThisCaption.setOnClickListener(new customClickListener() );
		mThatImage.setOnClickListener(new customClickListener() );
		mThatCaption.setOnClickListener(new customClickListener() );
		
		mThisBar = (CircularProgressBar) rootView.findViewById(R.id.circlethisbar);
		mThatBar = (CircularProgressBar) rootView.findViewById(R.id.circlethatbar);
		currentUser = ParseUser.getCurrentUser();
		
		getPosts();
		
		Bundle b = getArguments();
		

	    return rootView;
	}
	public class customClickListener implements OnClickListener {
	@Override
	public void onClick(View v){
	    switch(v.getId()){
	    	case R.id.ThisPicture:
	    		updateVote(THIS_VOTE);
	    		break;
	    	case R.id.ThatPicture:
	    		updateVote(THAT_VOTE);
	    		break;
	    	case R.id.thisLabel:
	    		updateVote(THIS_VOTE);
		   	   break;
		    case R.id.thatLabel:
		    	updateVote(THAT_VOTE);
		   		break;	
	    }
	}
	}
	private void updateVote(int vote){
		//int selected = getResources().getColor(R.color.selected_color_4);
		int thisVotes = mMessages.get(mCurrentItem).getInt(ParseConstants.KEY_THIS_VOTES);
		int thatVotes = mMessages.get(mCurrentItem).getInt(ParseConstants.KEY_THAT_VOTES);
		int totalVotes = thisVotes + thatVotes;
		int thisPercentage = (thisVotes*100)/(totalVotes);
		int thatPercentage = (thatVotes*100)/(totalVotes);
		if(vote == THIS_VOTE){
			//mThisCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
			//mThatCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

			/*mThisCaption.setTextColor(Color.WHITE);
			mThatCaption.setTextColor(Color.BLACK);
			mThatCaption.setBackgroundColor(Color.WHITE);
			mThisCaption.setBackgroundColor(selected);*/
		}
		else{
			//mThatCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
			//mThisCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

			/*mThatCaption.setTextColor(Color.WHITE);
			mThisCaption.setTextColor(Color.BLACK);
			mThisCaption.setBackgroundColor(Color.WHITE);
			mThatCaption.setBackgroundColor(selected);*/
		}
	//	mThisImage.setVisibility(View.INVISIBLE);
	//	mThatImage.setVisibility(View.INVISIBLE);

		
		mThisBar.setTitle(thisPercentage + "%");
		mThisBar.animateProgressTo(0, thisPercentage, new ProgressAnimationListener() {

	        @Override
	        public void onAnimationStart() {                
	        }

	        @Override
	        public void onAnimationProgress(int progress) {
	            mThisBar.setTitle(progress + "%");
	        }

	        @Override
	        public void onAnimationFinish() {
	           
	        }
	    });
		
		mThatBar.setTitle(thatPercentage + "%");
		mThatBar.animateProgressTo(0, thatPercentage, new ProgressAnimationListener() {

	        @Override
	        public void onAnimationStart() {                
	        }

	        @Override
	        public void onAnimationProgress(int progress) {
	            mThatBar.setTitle(progress + "%");
	        }

	        @Override
	        public void onAnimationFinish() {
	           
	        }
	    });
		
		

	}
	@Override
	public void onResume() {
		super.onResume();

			
			

		}





	}


