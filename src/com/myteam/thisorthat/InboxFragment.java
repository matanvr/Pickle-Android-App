package com.myteam.thisorthat;

import java.util.List;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
	protected static final String MESSAGE_KEY = "Messages";
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox,
				container, false);

		
		mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorScheme(
				R.color.swipeRefresh1,
				R.color.swipeRefresh2,
				R.color.swipeRefresh3,
				R.color.swipeRefresh4);
        // Set title in Fragment for display purposes.
       
        Bundle b = getArguments();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		System.gc(); 
		getMessages();
	}
	

    
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id) {
//		super.onListItemClick(l, v, position, id);
//		
//
//		ParseObject message = mMessages.get(position);
//		
//		ParseFile This = message.getParseFile("This");
//		Uri thisUri = Uri.parse(This.getUrl());
//		ParseFile That = message.getParseFile("That");
//		Uri thatUri = Uri.parse(This.getUrl());
//		Message m = new Message();
//		m.setQuestion(message.get(ParseConstants.KEY_QUESTION_TEXT).toString());
////		m.setRecipient(message.get(ParseConstants.KEY_RECIPIENT_IDS).toString());
//		m.setSender(message.get(ParseConstants.KEY_SENDER_ID).toString());
//		Gson gS = new Gson();
//		String target = gS.toJson(m);
//		Message m2 = gS.fromJson(target, new Message().getClass());
//		
//		
//		
//		// view the image
//		Intent intent = new Intent(getActivity(), ViewImageActivity.class);
//		
//		intent.putExtra("thisUri", thisUri.toString());
//		intent.putExtra("thatUri", thatUri.toString());
//		intent.putExtra("Thisorthat",target);
//		startActivity(intent);
//
//	}
    
    
	public void getMessages(){
		getActivity().setProgressBarIndeterminateVisibility(true);
		
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_DILEMMA);
//		query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				
				if (e == null) {
					// We found messages!
					
					if (mSwipeRefreshLayout.isRefreshing()) {
						mSwipeRefreshLayout.setRefreshing(false);
					}
					
					mMessages = messages;
					/*
					String[] usernames = new String[mMessages.size()];
					int i = 0;
					for(ParseObject message : mMessages) {
						usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
						i++;
					}*/
					ParseQuery<ParseObject> userPosts = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER_VOTE);
					userPosts.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
					userPosts.whereEqualTo(ParseConstants.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
					userPosts.findInBackground(new FindCallback<ParseObject>(){
						@Override
						public void done(List<ParseObject> userVotes, ParseException e) {
							getActivity().setProgressBarIndeterminateVisibility(false);
							if( e== null){
								MessageAdapter adapter = new MessageAdapter(
										getListView().getContext(), 
										mMessages,userVotes);
								setListAdapter(adapter);
							}
						}
					});

				}
			}
		});
	}
	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			getMessages();
		}
	};


		

}








