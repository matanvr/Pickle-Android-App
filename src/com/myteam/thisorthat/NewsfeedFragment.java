package com.myteam.thisorthat;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.myteam.thisorthat.adapter.MessageAdapter;
import com.myteam.thisorthat.adapter.NewsfeedGridAdapter;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class NewsfeedFragment extends Fragment {


	private List<ParseObject> mMessages;
	private Context mContext;
	private GridView mGridView;
	public static final String TAG = NewsfeedFragment.class.getSimpleName();





	@Override
	public void onResume(){
		super.onResume();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View rootView = inflater.inflate(R.layout.newsfeed_gridview,
				container, false);
		mContext = getActivity();
		mGridView = (GridView) rootView.findViewById(R.id.postsGrid);
		getAllPosts();
		

		


		return rootView;
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
