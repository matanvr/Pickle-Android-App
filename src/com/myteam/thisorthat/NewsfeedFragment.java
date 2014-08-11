package com.myteam.thisorthat;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.myteam.thisorthat.adapter.NewsfeedGridAdapter;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class NewsfeedFragment extends Fragment {


	private List<ParseObject> mMessages;
	private Context mContext;
	private GridView mGridView;
	private ParseUser mCurrentUser;
	private String mUserId;
	SwipeRefreshLayout mSwipeRefreshLayout;
	public static final String TAG = NewsfeedFragment.class.getSimpleName();





	@Override
	public void onResume(){
		super.onResume();
		
		
	}
	
	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			getAllUserPosts();
			
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		View rootView = inflater.inflate(R.layout.newsfeed_gridview,
				container, false);
		mCurrentUser = ParseUser.getCurrentUser();
		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorScheme(R.color.swipeRefresh1,
				R.color.swipeRefresh2, R.color.swipeRefresh3,
				R.color.swipeRefresh4);
		mContext = getActivity();
		mGridView = (GridView) rootView.findViewById(R.id.postsGrid);
		
		// facebook users only
		if (mCurrentUser.get("profile") != null) {
			JSONObject userProfile = mCurrentUser
					.getJSONObject("profile");
			try {
				if (userProfile.getString("facebookId") != null) {
					mUserId = userProfile.get("facebookId")
							.toString();
				}

			} catch (JSONException er) {

			}
		}//other users 
		else{
			mUserId = ParseUser.getCurrentUser().getObjectId();
		}
		getAllUserPosts();
		

		


		return rootView;
	}

	public void getAllUserPosts() {

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstants.CLASS_USER_VOTE);

		query.addDescendingOrder(ParseConstants.KEY_UPDATED_AT);
		
		query.whereEqualTo(ParseConstants.KEY_USER_ID,mUserId);
		query.whereNotEqualTo(ParseConstants.KEY_USER_VOTE, 0);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> userVotes, ParseException e) {

				if (e == null) {
					ParseQuery <ParseObject> postQ = new ParseQuery<ParseObject>(ParseConstants.CLASS_DILEMMA);
					postQ.whereContainedIn(ParseConstants.KEY_OBJECT_ID, getPostIds(userVotes));
					postQ.findInBackground(new FindCallback<ParseObject>() {
						@Override
						public void done(List<ParseObject> posts, ParseException e) {

							if (e == null) {
								mMessages = posts;
								for (ParseObject post : mMessages){
									Uri thisUri;
									Uri thatUri;
									if(post.getString("thisUri") == null){
										ParseFile This = post.getParseFile("this");
										thisUri = Uri.parse(This.getUrl());
										post.put("thisUri", thisUri.toString());
										post.saveInBackground();
										thisUri = Uri.parse(post.getString("thisUri"));
									}
									if(post.getString("thatUri") == null){
										ParseFile That = post.getParseFile("that");
										thatUri = Uri.parse(That.getUrl());
										post.put("thatUri", thatUri.toString());
										post.saveInBackground();
									}

									
								}
								if (mSwipeRefreshLayout.isRefreshing()) {
									mSwipeRefreshLayout.setRefreshing(false);
								}
								NewsfeedGridAdapter adapter = new NewsfeedGridAdapter(mContext, R.layout.mini_posts,
										mMessages);

								mGridView.setAdapter(adapter);
							}
						}
						

					});
						
				}

					


			
			}
		});
	}
	private List<String> getPostIds(List<ParseObject> userVotes){
		List<String> postIds = new ArrayList<String>();
		for(ParseObject u : userVotes){
			postIds.add(u.getString(ParseConstants.KEY_POST_ID));
		}
		return postIds;
	}
	
	public void getAllPosts() {

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstants.CLASS_DILEMMA);

		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {

				if (e == null) {
					// We found messages!

					mMessages = messages;
					for (ParseObject post : mMessages){
						Uri thisUri;
						Uri thatUri;
						if(post.getString("thisUri") == null){
							ParseFile This = post.getParseFile("this");
							thisUri = Uri.parse(This.getUrl());
							post.put("thisUri", thisUri.toString());
							post.saveInBackground();
							thisUri = Uri.parse(post.getString("thisUri"));
						}
						if(post.getString("thatUri") == null){
							ParseFile That = post.getParseFile("that");
							thatUri = Uri.parse(That.getUrl());
							post.put("thatUri", thatUri.toString());
							post.saveInBackground();
						}

						
					}
					NewsfeedGridAdapter adapter = new NewsfeedGridAdapter(mContext, R.layout.mini_posts,
							mMessages);

					mGridView.setAdapter(adapter);
					


				}
			}
		});
	}




}
