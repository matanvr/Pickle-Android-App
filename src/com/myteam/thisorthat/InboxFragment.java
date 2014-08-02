package com.myteam.thisorthat;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.myteam.thisorthat.adapter.MessageAdapter;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
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
	private ParseUser currentUser;
	public static final String TAG = InboxFragment.class.getSimpleName();
	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			getMessages();
			
			/*
			switch (mFeed) {
			case InboxFragment.NEWSFEED:
				getMessages();
				break;
			case InboxFragment.FAVORITES:
				// getFavorites();
				break;
			}*/
		}
	};
/*
	public void getFavorites() {

		ParseQuery<ParseObject> favoritesQuery = new ParseQuery<ParseObject>(
				ParseConstants.CLASS_USER_VOTE);
		favoritesQuery.whereEqualTo(ParseConstants.KEY_IS_FOLLOWER, 1);
		favoritesQuery.whereEqualTo(ParseConstants.KEY_USER_ID, ParseUser
				.getCurrentUser().getObjectId());
		favoritesQuery.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> favoritePosts, ParseException e) {
				if (e == null) {

					ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
							ParseConstants.CLASS_DILEMMA);
					// query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS,
					// ParseUser.getCurrentUser().getObjectId());
					query.whereContainedIn(ParseConstants.KEY_OBJECT_ID,
							getPostIds(favoritePosts));
					query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);

					query.findInBackground(new FindCallback<ParseObject>() {
						@Override
						public void done(List<ParseObject> messages,
								ParseException e) {

							if (e == null) {
								// We found messages!

								mMessages = messages;
								ParseQuery<ParseObject> userPosts = new ParseQuery<ParseObject>(
										ParseConstants.CLASS_USER_VOTE);
								userPosts
										.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
								userPosts
										.whereEqualTo(
												ParseConstants.KEY_USER_ID,
												ParseUser.getCurrentUser()
														.getObjectId());
								userPosts
										.findInBackground(new FindCallback<ParseObject>() {
											@Override
											public void done(
													List<ParseObject> userVotes,
													ParseException e) {

												if (mSwipeRefreshLayout
														.isRefreshing()) {
													mSwipeRefreshLayout
															.setRefreshing(false);
												}

												if (e == null) {

													MessageAdapter adapter = new MessageAdapter(
															getListView()
																	.getContext(),
															mMessages,
															userVotes,
															FAVORITES);

													setListAdapter(adapter);
												}
											}
										});

							}
						}
					});
				}
			}
		});

	}
*/
	// @Override
	// public void onListItemClick(ListView l, View v, int position, long id) {
	// super.onListItemClick(l, v, position, id);
	//
	//
	// ParseObject message = mMessages.get(position);
	//
	// ParseFile This = message.getParseFile("This");
	// Uri thisUri = Uri.parse(This.getUrl());
	// ParseFile That = message.getParseFile("That");
	// Uri thatUri = Uri.parse(This.getUrl());
	// Message m = new Message();
	// m.setQuestion(message.get(ParseConstants.KEY_QUESTION_TEXT).toString());
	// //
	// m.setRecipient(message.get(ParseConstants.KEY_RECIPIENT_IDS).toString());
	// m.setSender(message.get(ParseConstants.KEY_SENDER_ID).toString());
	// Gson gS = new Gson();
	// String target = gS.toJson(m);
	// Message m2 = gS.fromJson(target, new Message().getClass());
	//
	//
	//
	// // view the image
	// Intent intent = new Intent(getActivity(), ViewImageActivity.class);
	//
	// intent.putExtra("thisUri", thisUri.toString());
	// intent.putExtra("thatUri", thatUri.toString());
	// intent.putExtra("Thisorthat",target);
	// startActivity(intent);
	//
	// }

	public void getMessages() {

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstants.CLASS_DILEMMA);
		// query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS,
		// ParseUser.getCurrentUser().getObjectId());
		if(mFeed == FRIENDS){
			query.whereContainedIn(ParseConstants.KEY_SENDER_ID, mFriends);
		}
		else if(mFeed == FAVORITES){
			query.whereEqualTo(ParseConstants.KEY_IS_FOLLOWER, 1);
			query.whereEqualTo(ParseConstants.KEY_USER_ID, ParseUser
					.getCurrentUser().getObjectId());
		}
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
								if (mSwipeRefreshLayout.isRefreshing()) {
									mSwipeRefreshLayout.setRefreshing(false);
								}
								mUserVotes = userVotes;
								MessageAdapter adapter = new MessageAdapter(
										getListView().getContext(), mMessages,
										mUserVotes, NEWSFEED);
								setListAdapter(adapter);
							}
						}
					});

				}
			}
		});
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
		if (ParseFacebookUtils.isLinked(currentUser)) {

			checkFacebookUser();
		} else {
			startFetch();
		}

	}

	private void startFetch() {
		mSwipeRefreshLayout.setRefreshing(true);
		getMessages();
		/*
		mFeed = this.getArguments().getInt("feedType");

		switch (mFeed) {
		case InboxFragment.NEWSFEED:
			getMessages();
			break;
		case InboxFragment.FAVORITES:
			getFavorites();
			break;
		case InboxFragment.FRIENDS:
			getAllFriends();
			
			break;
		}*/
	}

	private void checkFacebookUser() {

		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			Request request = Request.newMeRequest(
					ParseFacebookUtils.getSession(),
					new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							if (user != null) {
								// Create a JSON object to hold the profile info
								JSONObject userProfile = new JSONObject();
								try {
									// Populate the JSON object
									userProfile.put("facebookId", user.getId());
									userProfile.put("name", user.getName());
									if (user.getLocation().getProperty("name") != null) {
										userProfile.put("location",
												(String) user.getLocation()
														.getProperty("name"));
									}
									if (user.getProperty("gender") != null) {
										userProfile.put("gender", (String) user
												.getProperty("gender"));
									}
									if (user.getBirthday() != null) {
										userProfile.put("birthday",
												user.getBirthday());
									}
									if (user.getProperty("relationship_status") != null) {
										userProfile
												.put("relationship_status",
														(String) user
																.getProperty("relationship_status"));
									}

									// Save the user profile info in a user
									// property
									ParseUser currentUser = ParseUser
											.getCurrentUser();
									currentUser.put("profile", userProfile);
									currentUser.saveInBackground();
									startFetch();
									// Show the user info

								} catch (JSONException e) {
									Log.d(InboxFragment.TAG,
											"Error parsing returned user data.");
								}

							} else if (response.getError() != null) {
								if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
										|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
									Log.d(InboxFragment.TAG,
											"The facebook session was invalidated.");

								} else {
									Log.d(InboxFragment.TAG,
											"Some other error: "
													+ response.getError()
															.getErrorMessage());
								}
							}
						}
					});
			request.executeAsync();

		}
	}

	
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

}
