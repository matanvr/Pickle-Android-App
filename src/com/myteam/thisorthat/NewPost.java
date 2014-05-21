package com.myteam.thisorthat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.myteam.thisorthat.util.FileHelper;
import com.myteam.thisorthat.util.ParseConstants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;




public class NewPost extends Activity  {
	ImageView mCameraButton;
	ImageView mRandomButton;
	EditText mQuestionText;
	ImageView mBackgroundImageThis;
	ImageView mBackgroundImageThat;
	EditText mThisCaption;
	EditText mThatCaption;
	CheckBox mCheckBox;
	
	public static final String TAG = NewPost.class.getSimpleName();
	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int PICK_PHOTO_REQUEST = 1;
	public static final int MEDIA_TYPE_IMAGE = 2;
	public static final int CROP_FROM_CAMERA = 3;	
	public static final int THIS_IMAGE = 4;
	public static final int THAT_IMAGE = 5;
	public static final int GOOGLE_IMAGE_REQUEST = 6;	
	public static final String QUESTION_TEXT = "QuestionText";
	public static final String ANONYMOUS = "Anonymous";
	private String mThatUri;
	private String mThisUri;
	private Bitmap mThisBitmap;
	private Bitmap mThatBitmap;
	private Uri PhotoId;
	private int mImageAngle;
	private int mImageClicked = 0;
	
	protected String mFileType = ParseConstants.TYPE_IMAGE;
	public static final int FILE_SIZE_LIMIT = 1024*1024*10; // 10 MB
	
	protected Uri mMediaUri;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.gc(); 
		setContentView(R.layout.activity_new_post);

		mBackgroundImageThis = (ImageView) findViewById(R.id.backgroundPictureThis);
		mBackgroundImageThat = (ImageView) findViewById(R.id.backgroundPictureThat);
		mThisCaption = (EditText) findViewById(R.id.ThisCaption);
		mThatCaption = (EditText) findViewById(R.id.ThatCaption);
		mQuestionText = (EditText) findViewById(R.id.questionString);
		mCheckBox = (CheckBox) findViewById(R.id.anonymousCheckBox);
		mBackgroundImageThis.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(NewPost.this);
				builder.setItems(R.array.camera_choices, mDialogListener);
				AlertDialog dialog = builder.create();
				mImageClicked = THIS_IMAGE;
				dialog.show();
			}
		});
		
		mBackgroundImageThat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(NewPost.this);
				builder.setItems(R.array.camera_choices, mDialogListener);
				AlertDialog dialog = builder.create();
				mImageClicked = THAT_IMAGE;
				dialog.show();
			}
		});
		//	mCameraButton = (ImageView) findViewById(R.id.cameraButton);
		/*mCameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NewPost.this,AndroidCustomGalleryActivity.class);
				startActivityForResult(intent,MEDIA_TYPE_IMAGE);
			}
		});
		*/
		

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_post, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		switch(itemId) {
			case R.id.action_next:
				//go to recipients
				ParseObject message = createMessage();
				if (message == null) {
					// error
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(R.string.error_selecting_file)
						.setTitle(R.string.error_selecting_file_title)
						.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				else {
					send(message);
					finish();
				}
				break;

		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {			
			if (requestCode == PICK_PHOTO_REQUEST) {
				if (data == null) {
					Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
				}
				else {
					mMediaUri = data.getData();
					rotationAngle(mMediaUri);
					Intent intent = new Intent("com.android.camera.action.CROP");
					//intent.setClassName("com.android.gallery", "com.android.camera.CropImage");
					intent.setDataAndType(mMediaUri, "image/*");
			      //  intent.setData(mMediaUri);
			        intent.putExtra("outputX", 402);
			        intent.putExtra("outputY", 602);
			        intent.putExtra("aspectX", 1);
			        intent.putExtra("aspectY", 1);
			        intent.putExtra("scale", true);
			        intent.putExtra("return-data", true);            
			        startActivityForResult(intent, CROP_FROM_CAMERA);
				}
				
				Log.i(TAG, "Media URI: " + mMediaUri);
		
			}
			else if(requestCode == CROP_FROM_CAMERA) {
		        //Wysie_Soh: After a picture is taken, it will go to PICK_FROM_CAMERA, which will then come here
		        //after the image is cropped.
		
		        final Bundle extras = data.getExtras();
		
		        if (extras != null) {
		            Bitmap photo = extras.getParcelable("data");
		            photo = RotateBitmap(photo,mImageAngle);
		            photo = getResizedBitmap(photo, 602, 402);
		            if(mImageClicked == THIS_IMAGE){
		            	
		            	mBackgroundImageThis.setImageBitmap(photo);
		            	mThisBitmap = photo;
		            }
		            else if(mImageClicked == THAT_IMAGE){
		            	mBackgroundImageThat.setImageBitmap(photo);
		            	mThatBitmap = photo;
		            }
		/*
		            mPhoto = photo;
		            mPhotoChanged = true;
		            mPhotoImageView.setImageBitmap(photo);
		            setPhotoPresent(true);*/
		        }
		
		        //Wysie_Soh: Delete the temporary file                        
		        File f = new File(mMediaUri.getPath());            
		        if (f.exists()) {
		            f.delete();
		        }
		
			}
			else if(requestCode == GOOGLE_IMAGE_REQUEST){
		        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
		        StrictMode.setThreadPolicy(policy);
		        BitmapFactory.Options bmOptions;
		        bmOptions = new BitmapFactory.Options();
		        bmOptions.inSampleSize = 1;
		        String url = data.getStringExtra("imageUrl");
		        System.out.println(url);
				Bitmap photo = FileHelper.loadBitmap(url, bmOptions);
	            
	            if(mImageClicked == THIS_IMAGE){
	            	//mBackgroundImageThis.getLayoutParams().height = 1500;
	            	//mBackgroundImageThis.getLayoutParams().width = 1500;
	            	//mBackgroundImageThis.setImageBitmap(photo);
	            	
	            	Picasso.with(this).load(url).resize(402, 600).centerCrop().into(mBackgroundImageThis);
	            	//mBackgroundImageThis.setImageBitmap(photo); //set image
	            	mThisBitmap = photo; //save bitmap
	            }
	            else if(mImageClicked == THAT_IMAGE){
	            	Picasso.with(this).load(url).resize(402, 600).centerCrop().into(mBackgroundImageThat);
	            	//mBackgroundImageThis.setImageBitmap(photo); //set image
	            	mThatBitmap = photo; //save bitmap
	            }

	        
			}
			else {
				rotationAngle(mMediaUri);
				Intent intent = new Intent("com.android.camera.action.CROP");
				//intent.setClassName("com.android.gallery", "com.android.camera.CropImage");
				intent.setDataAndType(mMediaUri, "image/*");
		      //  intent.setData(mMediaUri);
		        intent.putExtra("outputX", 400);
		        intent.putExtra("outputY", 400);
		        intent.putExtra("aspectX", 1);
		        intent.putExtra("aspectY", 1);
		        intent.putExtra("scale", true);
		        intent.putExtra("return-data", true);            
		        startActivityForResult(intent, CROP_FROM_CAMERA);
			}
			
		}
		else if (resultCode != RESULT_CANCELED) {
			Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
		}
	}
	
	private void rotationAngle(Uri uri){
		try {
	        File f = new File(uri.toString());
	        ExifInterface exif = new ExifInterface(f.getPath());
	        int orientation = exif.getAttributeInt(
	                ExifInterface.TAG_ORIENTATION,
	                ExifInterface.ORIENTATION_NORMAL);

	        int angle = 0;

	        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
	            angle = 90;
	        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
	            angle = 180;
	        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
	            angle = 270;
	        }
	        mImageAngle = angle;
	        


	    } catch (IOException e) {
	        Log.w("TAG", "-- Error in setting image");
	    } catch (OutOfMemoryError oom) {
	        Log.w("TAG", "-- OOM Error in setting image");
	    }
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle)
	{
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	      
	}
	
	protected ParseObject createMessage() {
		ParseObject message = new ParseObject(ParseConstants.CLASS_DILEMMA);
		message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
		
		if(mCheckBox.isChecked())
			message.put(ParseConstants.KEY_SENDER_NAME,ANONYMOUS);
		else
			message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
		
		message.put(ParseConstants.KEY_QUESTION_TEXT, mQuestionText.getText().toString());
		message.put(ParseConstants.KEY_THIS_VOTES, 0);
		message.put(ParseConstants.KEY_THAT_VOTES, 0);
		message.put(ParseConstants.KEY_THIS_CAPTION, mThisCaption.getText().toString());
		message.put(ParseConstants.KEY_THAT_CAPTION, mThatCaption.getText().toString());
		
		if(mThisBitmap == null || mThatBitmap == null){
			return null;
		}
		ParseFile fileThis = getParseFile(mThisBitmap);

		ParseFile fileThat = getParseFile(mThatBitmap);
		if(fileThat == null || fileThis == null){
			return null;
		}
		else{
			message.put(ParseConstants.KEY_FILE_THIS, fileThis);
			message.put(ParseConstants.KEY_FILE_THAT, fileThat);
		}
			
			

		

		return message;
	}
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
	/*

	
	public ParseFile getParseFile(String filePath){
		
		Bitmap bm = ShrinkBitmap(filePath, 200, 200);

		Bitmap bThat = RotateBitmap(bm,90);
//		Bitmap bThat = BitmapFactory.decodeFile(filePath);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bThat.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		String fileName = FileHelper.getFileName(this.getApplicationContext(), Uri.parse(filePath), mFileType);
		byte[] scaledData = bos.toByteArray();
		if(scaledData == null)
			return null;
		ParseFile photoFile = new ParseFile(fileName, scaledData);
		return photoFile;
		// Save the scaled image to Parse

	}*/
	
	public ParseFile getParseFile(Bitmap file){
		
	
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		file.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		String fileName = UUID.randomUUID() + ".PNG";
		byte[] scaledData = bos.toByteArray();
		if(scaledData == null)
			return null;
		ParseFile photoFile = new ParseFile(fileName, scaledData);
		return photoFile;
		// Save the scaled image to Parse

	}	
	
	protected void send(ParseObject message) {
		message.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					// success!
					Toast.makeText(NewPost.this, R.string.success_message, Toast.LENGTH_LONG).show();
					
				}
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(NewPost.this);
					builder.setMessage(R.string.error_sending_message)
						.setTitle(R.string.error_selecting_file_title)
						.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		});
	}
	
	protected void onResume()  
	{  
		System.gc();  
		super.onResume();  
	}  
	@Override  
	protected void onPause()  
	{  
		super.onPause();  
		System.gc();  
	}
	
	

	protected DialogInterface.OnClickListener mDialogListener = 
			new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which) {
				case 0: // Take picture
					Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
					if (mMediaUri == null) {
						// display an error
						Toast.makeText(NewPost.this, R.string.error_external_storage,
								Toast.LENGTH_LONG).show();
					}
					else {
						takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
						startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
					}
					break;
				case 1: // Choose picture
					Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
					choosePhotoIntent.setType("image/*");
					startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
					break;
				case 2:
					Intent googleImagesIntent = new Intent(NewPost.this,GoogleImageActivity.class);
					startActivityForResult(googleImagesIntent,GOOGLE_IMAGE_REQUEST);
					break;

			}
		}

		private Uri getOutputMediaFileUri(int mediaType) {
			// To be safe, you should check that the SDCard is mounted
		    // using Environment.getExternalStorageState() before doing this.
			if (isExternalStorageAvailable()) {
				// get the URI
				
				// 1. Get the external storage directory
				String appName = NewPost.this.getString(R.string.app_name);
				File mediaStorageDir = new File(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						appName);
				
				// 2. Create our subdirectory
				if (! mediaStorageDir.exists()) {
					if (! mediaStorageDir.mkdirs()) {
						Log.e(TAG, "Failed to create directory.");
						return null;
					}
				}
				
				// 3. Create a file name
				// 4. Create the file
				File mediaFile;
				Date now = new Date();
				String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
				
				String path = mediaStorageDir.getPath() + File.separator;
				if (mediaType == MEDIA_TYPE_IMAGE) {
					mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
				}
				else {
					return null;
				}
				
				Log.d(TAG, "File: " + Uri.fromFile(mediaFile));
				
				// 5. Return the file's URI				
				return Uri.fromFile(mediaFile);
			}
			else {
				return null;
			}
		}
		
		private boolean isExternalStorageAvailable() {
			String state = Environment.getExternalStorageState();
			
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				return true;
			}
			else {
				return false;
			}
		}
	};
	
	protected void sendPushNotifications() {
		ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
		query.whereEqualTo(ParseConstants.KEY_USER_ID, ParseUser.getCurrentUser().getUsername());
		
		// send push notification
		ParsePush push = new ParsePush();
		push.setQuery(query);
		push.setMessage(getString(R.string.push_message, 
				ParseUser.getCurrentUser().getUsername()));
		push.sendInBackground();
	}
}
	



