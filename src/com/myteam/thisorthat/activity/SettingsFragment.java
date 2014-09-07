package com.myteam.thisorthat.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myteam.thisorthat.R;
import com.myteam.thisorthat.R.drawable;
import com.myteam.thisorthat.util.CircularTransformation;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class SettingsFragment extends Fragment {
	private static String EXTRA_TITLE = "extra_title";
	public static final String TAG = InboxFragment.class.getSimpleName();
	private ImageView mProfilePic;
	private TextView mUserName;
	private TextView mLogOut;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.settings_fragment, container,
				false);
		
		Typeface tp = Typeface.createFromAsset(
				getActivity().getAssets(), "fonts/WhitneyCondensed-Book.otf");
		mProfilePic = (ImageView) rootView.findViewById(R.id.profile_pic);
		mUserName  = (TextView) rootView.findViewById(R.id.profile_name);
		mLogOut = (TextView) rootView.findViewById(R.id.logout_button);
		mLogOut.setTypeface(tp);
		
		mUserName.setTypeface(tp);
		String id = ParseConstants.getUserPicLarge(getUserId());
		mUserName.setText(getUsername());
		if(id == null){
			Picasso.with(getActivity()).load(drawable.defaultuser).transform(new CircularTransformation()).into(mProfilePic);
		}else{
			Picasso.with(getActivity()).load(id).transform(new CircularTransformation()).resize(80,80).into(mProfilePic);
		}
		mLogOut.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ParseUser.logOut();
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				
			}
		});
		Bundle b = getArguments();
		

	    return rootView;

	}
	private String getUsername() {
		ParseUser currentUser = ParseUser.getCurrentUser();

		String userName = currentUser.getUsername();

		// facebook users only
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {

				if (userProfile.getString("name") != null) {
					userName = userProfile.getString("name");
				}

			} catch (JSONException er) {

			}
		}
		return userName;
	}
	
	private String getUserId() {
		ParseUser currentUser = ParseUser.getCurrentUser();

		String userId = currentUser.getObjectId();

		// facebook users only
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {

				if (userProfile.getString("facebookId") != null) {
					userId = userProfile.get("facebookId").toString();
				}

			} catch (JSONException er) {

			}
		}
		return userId;
	}
	@Override
	public void onResume() {
		super.onResume();

			
			

		}





	}


