package com.myteam.thisorthat.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myteam.thisorthat.R;
import com.myteam.thisorthat.adapter.MessageAdapter;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class InboxFragment extends ListFragment {
	private static String EXTRA_TITLE = "extra_title";
	protected List<ParseObject> mMessages;
	protected List<ParseObject> mUserVotes;
	protected ArrayList<String> mFriends;
	protected static final String MESSAGE_KEY = "Messages";
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	public final static int NEWSFEED = 0;
	public final static int FAVORITES = 1;
	public final static int FRIENDS = 2;
	private int mFeed;
	private MessageAdapter mAdapter;
	private ParseUser currentUser;
	public static final String TAG = InboxFragment.class.getSimpleName();
	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			getMessages();
			
		}
	};


	public void getMessages() {

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstants.CLASS_DILEMMA);


		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {

				if (e == null) {
					// We found posts!

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
								if (mSwipeRefreshLayout.isRefreshing()) {
									mSwipeRefreshLayout.setRefreshing(false);
								}
								

								mUserVotes = userVotes;
							//	mMessages = removeDeletedPosts(mMessages,userVotes);
								if (mAdapter == null) {
									mAdapter = new MessageAdapter(
											getListView().getContext(), mMessages,
											mUserVotes, NEWSFEED);
									setListAdapter(mAdapter);
								} else {
								    ((MessageAdapter)mAdapter).refill(mMessages);
								}

							}
						}
					});

				}
			}
		});
	}
	private List<ParseObject> removeDeletedPosts(List<ParseObject> posts, List<ParseObject> votes){
		HashSet<String> deletedPosts = new HashSet<String>();
		List<ParseObject> newList = new ArrayList<ParseObject>();
		for (ParseObject uv: votes){
			if(uv.getBoolean(ParseConstants.KEY_IS_REMOVED) == true){
				deletedPosts.add(uv.getString(ParseConstants.KEY_POST_ID));
			}
		}
		if(!deletedPosts.isEmpty()){
			for (ParseObject p : posts){
				if(!deletedPosts.contains(p.getObjectId())){
					newList.add(p);
				}
					
			}
		}
		return newList;
	}
	private List<String> getPostIds(List<ParseObject> favs) {
		List<String> output = new ArrayList<String>();
		for (ParseObject i : favs) {
			output.add(i.getString(ParseConstants.KEY_POST_ID));
		}

		return output;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox, container,
				false);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorScheme(R.color.swipeRefresh1,
				R.color.swipeRefresh2, R.color.swipeRefresh3,
				R.color.swipeRefresh4);
		// Set title in Fragment for display purposes.

		Bundle b = getArguments();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		//mFeed = this.getArguments().getInt("feedType");
		currentUser = ParseUser.getCurrentUser();
		startFetch();
		
		

	}



	private void startFetch() {
		mSwipeRefreshLayout.setRefreshing(true);
		getMessages();

	}


/*
	
	private void getAllFriends() {
		Request.newMyFriendsRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserListCallback() {

					@Override
					public void onCompleted(List<GraphUser> users,
							Response response) {
						if (users != null) {
							List<String> friendsList = new ArrayList<String>();
							for (GraphUser user : users) {
								friendsList.add(user.getId());
							}

							// Construct a ParseUser query that will find
							// friends whose
							// facebook IDs are contained in the current user's
							// friend list.

							ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
							friendQuery.whereContainedIn("fbId", friendsList);

							// findObjects will return a list of ParseUsers that
							// are friends with
							// the current user
							friendQuery.findInBackground(new FindCallback<ParseUser>() {
								@Override
								public void done(List<ParseUser> users, ParseException e) {
									if (e == null) {
										mFriends = new ArrayList<String>();
										for (ParseUser u : users){
											mFriends.add(u.getString("fbId"));
										}
										getMessages();
									}
								}
							});

						}
					}
				}).executeAsync();
	}
*/
	
	
}
