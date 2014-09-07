package com.myteam.thisorthat.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.myteam.thisorthat.R;
import com.myteam.thisorthat.R.id;
import com.myteam.thisorthat.R.layout;
import com.myteam.thisorthat.R.string;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class FacebookRegistration extends Activity {

	private ProfilePictureView userProfilePictureView;
	private TextView userNameView;
	private EditText userPassword;
	private Button signupButton;
	ParseUser newUser;
	public static final String TAG = FacebookRegistration.class.getSimpleName();

	private void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							// Create a JSON object to hold the profile info
							JSONObject userProfile = new JSONObject();
							try {
								// Populate the JSON object
								userProfile.put("facebookId", user.getId());
								userProfile.put("name", user.getName());

								// Save the user profile info in a user property
								ParseUser currentUser = ParseUser
										.getCurrentUser();
								currentUser.put("profile", userProfile);
								currentUser.saveInBackground();

								// Show the user info
								updateViewsWithProfileInfo();
							} catch (JSONException e) {
								Log.d(FacebookRegistration.TAG,
										"Error parsing returned user data.");
							}

						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.d(FacebookRegistration.TAG,
										"The facebook session was invalidated.");
								onLogoutButtonClicked();
							} else {
								Log.d(FacebookRegistration.TAG,
										"Some other error: "
												+ response.getError()
														.getErrorMessage());
							}
						}
					}
				});
		request.executeAsync();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.userdetails);

		userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
		userNameView = (TextView) findViewById(R.id.userName);
		userPassword = (EditText) findViewById(R.id.facebookPassword);
		signupButton = (Button) findViewById(R.id.facebookCont);

		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				registerUser();
			}

		});
		// Fetch Facebook user info if the session is active
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			makeMeRequest();
		}
	}

	private void onLogoutButtonClicked() {
		// Log the user out
		ParseUser.logOut();

		// Go to the login view
		startLoginActivity();
	}

	@Override
	public void onResume() {
		super.onResume();

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			// Check if the user is currently logged
			// and show any cached content
			updateViewsWithProfileInfo();
		} else {
			// If the user is not logged in, go to the
			// activity showing the login view.
			startLoginActivity();
		}
	}

	public void registerUser(){
		
		String password = userPassword.getText().toString();
		password = password.trim();
			ParseUser currentUser = ParseUser
			.getCurrentUser();

			if (password.isEmpty() && currentUser != null
					&& currentUser.get("profile") != null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(FacebookRegistration.this);
				builder.setMessage(R.string.signup_error_message)
					.setTitle(R.string.signup_error_title)
					.setPositiveButton(android.R.string.ok, null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			else {
				newUser = new ParseUser();
				
				if (!ParseFacebookUtils.isLinked(newUser)) {
					  ParseFacebookUtils.link(newUser, this, new SaveCallback() {
					    @Override
					    public void done(ParseException ex) {
					      if (ParseFacebookUtils.isLinked(newUser)) {
								String username = userNameView.getText().toString();
								String password = userPassword.getText().toString();
								username = username.trim();
								password = password.trim();
					    	  	newUser.setUsername(username);
								newUser.setPassword(password);
								newUser.signUpInBackground(new SignUpCallback() {
									@Override
									public void done(ParseException e) {
										setProgressBarIndeterminateVisibility(false);
										
										if (e == null) {
											// Success!
											ThisOrThatApplication.updateParseInstallation(ParseUser.getCurrentUser());
											Intent intent = new Intent(FacebookRegistration.this, MainActivity.class);
											intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
											startActivity(intent);
										}
										else {
											AlertDialog.Builder builder = new AlertDialog.Builder(FacebookRegistration.this);
											builder.setMessage(e.getMessage())
												.setTitle(R.string.signup_error_title)
												.setPositiveButton(android.R.string.ok, null);
											AlertDialog dialog = builder.create();
											dialog.show();
										}
									}
								});
					      }
					    }
					  });
				// create the new user!
				setProgressBarIndeterminateVisibility(true);
			}
		}

	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void updateViewsWithProfileInfo() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {
				if (userProfile.getString("facebookId") != null) {
					String facebookId = userProfile.get("facebookId")
							.toString();
					userProfilePictureView.setProfileId(facebookId);
				} else {
					// Show the default, blank user profile picture
					userProfilePictureView.setProfileId(null);
				}
				if (userProfile.getString("name") != null) {
					userNameView.setText(userProfile.getString("name"));
				} else {
					userNameView.setText("");
				}

			} catch (JSONException e) {
				Log.d(FacebookRegistration.TAG,
						"Error parsing saved user data.");
			}

		}
	}
}
