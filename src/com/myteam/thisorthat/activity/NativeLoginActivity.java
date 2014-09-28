package com.myteam.thisorthat.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import com.myteam.thisorthat.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * A login screen that offers login via email/password.
 */
public class NativeLoginActivity extends Activity{

	private EditText mUsername;
	private EditText mPassword;
	private Button mLoginButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_native_login);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setIcon(R.drawable.close_post);

		Typeface tf = Typeface.createFromAsset(
				getAssets(), "fonts/WhitneyCondensed-Book.otf");

		mUsername = (EditText)findViewById(R.id.usernameField);
		mPassword = (EditText)findViewById(R.id.passwordField);
		mLoginButton = (Button)findViewById(R.id.loginButton);
		mUsername.setTypeface(tf);
		mPassword.setTypeface(tf);
		mLoginButton.setTypeface(tf);

		
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = mUsername.getText().toString();
				String password = mPassword.getText().toString();
				
				username = username.trim();
				password = password.trim();
				
				if (username.isEmpty() || password.isEmpty()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(NativeLoginActivity.this);
					builder.setMessage(R.string.login_error_message)
						.setTitle(R.string.login_error_title)
						.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				else {
					// Login
					setProgressBarIndeterminateVisibility(true);
					
					ParseUser.logInInBackground(username, password, new LogInCallback() {
						@Override
						public void done(ParseUser user, ParseException e) {
							setProgressBarIndeterminateVisibility(false);
							
							if (e == null) {
								// Success!
								ThisOrThatApplication.updateParseInstallation(user);
								Intent intent = new Intent(NativeLoginActivity.this, MainActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							}
							else {
								AlertDialog.Builder builder = new AlertDialog.Builder(NativeLoginActivity.this);
								builder.setMessage(e.getMessage())
									.setTitle(R.string.login_error_title)
									.setPositiveButton(android.R.string.ok, null);
								AlertDialog dialog = builder.create();
								dialog.show();
							}
						}
					});
				}
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
