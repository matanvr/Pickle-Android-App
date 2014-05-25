package com.myteam.thisorthat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
import com.myteam.thisorthat.util.InternalStorageContentProvider;
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

import eu.janmuller.android.simplecropimage.CropImage;




public class NewPost extends Activity  {
	public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
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
    
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_GALLERY      = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE   = 0x3;

    public static final int REQUEST_GOOGLE_IMAGE = 0x4;
    private ImageView mImageView;
	
	private File      mFileTemp;
	
	protected DialogInterface.OnClickListener mDialogListener = 
			new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which) {
				case 0: // Take picture
					takePicture();

					break;
				case 1: // Choose picture
					openGallery();
					break;
				case 2:
					Intent googleImagesIntent = new Intent(NewPost.this,GoogleImageActivity.class);
					startActivityForResult(googleImagesIntent,REQUEST_GOOGLE_IMAGE);
					break;

			}
		}

	};

	
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
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {

            return;
        }

        Bitmap bitmap;

        switch (requestCode) {

            case REQUEST_CODE_GALLERY:

                try {

                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();

                    startCropImage();

                } catch (Exception e) {

                    Log.e(TAG, "Error while creating temp file", e);
                }

                break;
            case REQUEST_CODE_TAKE_PICTURE:

                startCropImage();
                break;
            case REQUEST_CODE_CROP_IMAGE:

                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {

                    return;
                }

                bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
	            bitmap = getResizedBitmap(bitmap, 602, 402); //RESIZE
	            if(mImageClicked == THIS_IMAGE){
	            	
	            	mBackgroundImageThis.setImageBitmap(bitmap);
	            	mThisBitmap = bitmap;
	            }
	            else if(mImageClicked == THAT_IMAGE){
	            	mBackgroundImageThat.setImageBitmap(bitmap);
	            	mThatBitmap = bitmap;
	            }
                break;
            case REQUEST_GOOGLE_IMAGE :
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
	            	
	            	Picasso.with(this).load(url).resize(402, 600).centerCrop().into(mBackgroundImageThis);
	            	mThisBitmap = photo; //save bitmap
	            }
	            else if(mImageClicked == THAT_IMAGE){
	            	Picasso.with(this).load(url).resize(402, 600).centerCrop().into(mBackgroundImageThat);
	            	mThatBitmap = photo; //save bitmap
	            }
            	
            	break;
            	
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
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
		String state = Environment.getExternalStorageState();
    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    		mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
    	}
    	else {
    		mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
    	}

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
	protected void onPause()  
	{  
		super.onPause();  
		System.gc();  
	}

    protected void onResume()  
	{  
		System.gc();  
		super.onResume();  
	}

    private void openGallery() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
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
	
	
    protected void sendPushNotifications() {
		ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
		query.whereEqualTo(ParseConstants.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
		
		// send push notification
		ParsePush push = new ParsePush();
		push.setQuery(query);
		push.setMessage(getString(R.string.push_message, 
				ParseUser.getCurrentUser().getUsername()));
		push.sendInBackground();
	}
	private void startCropImage() {

        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 2);
        intent.putExtra(CropImage.ASPECT_Y, 3);

        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }
	
	private void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
        	Uri mImageCaptureUri = null;
        	String state = Environment.getExternalStorageState();
        	if (Environment.MEDIA_MOUNTED.equals(state)) {
        		mImageCaptureUri = Uri.fromFile(mFileTemp);
        	}
        	else {
	        	/*
	        	 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
	        	 */
	        	mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
        	}	
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {

            Log.d(TAG, "cannot take picture", e);
        }
    }
}
	



