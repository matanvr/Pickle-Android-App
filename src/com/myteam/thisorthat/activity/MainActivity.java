package com.myteam.thisorthat.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.myteam.thisorthat.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.myteam.thisorthat.R;
import com.myteam.thisorthat.adapter.SectionsPagerAdapter;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {


	public static final String TAG = MainActivity.class.getSimpleName();

	private int MENU_HOME = 2;
	private int MENU_SUBSCRIBE = 1;
	private int MENU_SETTINGS = 0;
	private static int CURRENT_MENU;

	public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; // 10 MB
	protected Uri mMediaUri;

	protected Menu mMenu;

	private ActionBar mActionBar;
	private TextView mTitleHome;
	private ParseUser currentUser;

	private ImageView mIconMiddle; 
	// used to store app title
	private CharSequence mTitle;


	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private void navigateToLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ParseAnalytics.trackAppOpened(getIntent());
		mActionBar = getActionBar();
		mActionBar.setIcon(R.drawable.pulse);
		mActionBar.setCustomView(R.layout.actionbar_custom_view_home);
		
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		currentUser = ParseUser.getCurrentUser();

		setTitleFont();
		if (currentUser == null) {
			navigateToLogin();
		}
		else if(ParseFacebookUtils.isLinked(currentUser))
			prepareFacebookUser();
		else
			startTabs();



		




	}
	private void startTabs(){
		mSectionsPagerAdapter = new SectionsPagerAdapter(this,
				getSupportFragmentManager(), mActionBar);

		// Set up the ViewPager with the sections adapter.

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(2);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						updateMenu(position);
					}
				});
	}
	private void prepareFacebookUser() {

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
									if (user.getLocation() != null && user.getLocation().getProperty("name") != null) {
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
									startTabs();
						

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		menu.getItem(0).setVisible(true);
		menu.getItem(1).setVisible(false);
		menu.getItem(2).setVisible(false);

		mMenu = menu;
		return true;
	}

	private void setTitleFont() {
		mTitleHome = (TextView) findViewById(R.id.titleHome);
		Typeface postTypeface = Typeface.createFromAsset(this.getAssets(),
				"fonts/WhitneyCondensed-Medium.otf");
		mTitleHome.setTypeface(postTypeface);
	}

	private void updateMenu(int position) {
		if (position == MENU_HOME) {
			getActionBar().setDisplayShowHomeEnabled(true);
			mActionBar.setCustomView(R.layout.actionbar_custom_view_home);
			setTitleFont();
			mMenu.getItem(0).setVisible(true);
			mMenu.getItem(1).setVisible(false);
			mMenu.getItem(2).setVisible(false);
			getActionBar().setHomeButtonEnabled(true); 
			mActionBar.setIcon(R.drawable.pulse);
			CURRENT_MENU = MENU_HOME;
		} else if (position == MENU_SUBSCRIBE) {
			mActionBar.setCustomView(R.layout.actionbar_custom_view_feed);
			mIconMiddle = (ImageView) findViewById(R.id.pulseButton);
			
			
			mIconMiddle.setImageResource(R.drawable.pulse);
			getActionBar().setDisplayShowHomeEnabled(true);
			getActionBar().setHomeButtonEnabled(true); 
			mMenu.getItem(0).setVisible(false);
			mMenu.getItem(1).setVisible(true);
			mMenu.getItem(2).setVisible(false);
			mActionBar.setIcon(R.drawable.ic_settings);
			CURRENT_MENU = MENU_SUBSCRIBE;

		}
		else if(position == MENU_SETTINGS){
			
			
			mActionBar.setIcon(R.drawable.ic_action_space);
			getActionBar().setHomeButtonEnabled(false); 
			mMenu.getItem(0).setVisible(false);
			mMenu.getItem(1).setVisible(false);
			mMenu.getItem(2).setVisible(true);
			mIconMiddle.setImageResource(R.drawable.ic_settings);
			CURRENT_MENU = MENU_SETTINGS;
			
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		Intent intent;
		switch (itemId) {
		case android.R.id.home:
			if(CURRENT_MENU == MENU_SUBSCRIBE)
				mViewPager.setCurrentItem(0);
			else
				mViewPager.setCurrentItem(1);

			break;

		case R.id.action_profile_settings:
			mViewPager.setCurrentItem(2);

			break;

		case R.id.action_camera:

			intent = new Intent(this, NewPost.class);
			startActivity(intent);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_right_in);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			break;
		case R.id.action_pulse:
			mViewPager.setCurrentItem(1);
			break;
		}


		return super.onOptionsItemSelected(item);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.


	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
	private String getUserId(){
		String userId = currentUser.getObjectId();

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

}
