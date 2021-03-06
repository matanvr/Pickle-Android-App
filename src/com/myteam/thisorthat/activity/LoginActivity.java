package com.myteam.thisorthat.activity;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import com.myteam.thisorthat.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.myteam.thisorthat.R;
import com.myteam.thisorthat.R.id;
import com.myteam.thisorthat.R.layout;
import com.myteam.thisorthat.R.string;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;



public class LoginActivity extends Activity {

	private Button mLoginButton;
	private Button mSignUpButton;
	private Button loginButton;
	private Dialog progressDialog;

	private TextView logoText;
	private TextView sloganText;

	private static final String TAG = LoginActivity.class.getSimpleName();
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		Typeface tf = Typeface.createFromAsset(
				getAssets(), "fonts/WhitneyCondensed-Book.otf");
		
		loginButton = (Button) findViewById(R.id.facebookLoginButton);
		logoText = (TextView) findViewById(R.id.title);
		sloganText = (TextView) findViewById(R.id.subtitle);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});
		mSignUpButton = (Button) findViewById(R.id.signup);

		mSignUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(intent);
			}
		});
		

		mLoginButton = (Button)findViewById(R.id.loginButton);
		
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, NativeLoginActivity.class);
				startActivity(intent);
			}
		});
		logoText.setTypeface(tf);
		sloganText.setTypeface(tf);
		mSignUpButton.setTypeface(tf);

		mLoginButton.setTypeface(tf);
		loginButton.setTypeface(tf);
		
	}

	private void onLoginButtonClicked() {
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Get Ready to Pickle...", true);
		List<String> permissions = Arrays.asList("public_profile", "email",
				"user_friends");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				LoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d(LoginActivity.TAG,
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(LoginActivity.TAG,
							"User signed up and logged in through Facebook!");
					showUserDetailsActivity();
				} else {
					Log.d(LoginActivity.TAG,
							"User logged in through Facebook!");

	
					getFacebookIdInBackground();
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
				}
			}
		});
	}
	private static void getFacebookIdInBackground() {
		  Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
		    @Override
		    public void onCompleted(GraphUser user, Response response) {
		      if (user != null) {
		        ParseUser.getCurrentUser().put("fbId", user.getId());
		        ParseUser.getCurrentUser().saveInBackground();
		      }
		    }
		  }).executeAsync();
		}
	
	private void showUserDetailsActivity() {
		Intent intent = new Intent(LoginActivity.this, FacebookRegistration.class);

		startActivity(intent);
	}
	
}