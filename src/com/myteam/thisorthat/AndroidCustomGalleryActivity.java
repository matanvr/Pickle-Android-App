package com.myteam.thisorthat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class AndroidCustomGalleryActivity extends Activity {
	public ImageAdapter imageAdapter;
	private final static int TAKE_IMAGE = 1;
	private final static int UPLOAD_IMAGES = 2;
	private final static int VIEW_IMAGE = 3;
	private Uri imageUri;
	private String mPath;
	private MediaScannerConnection mScanner;
	public GridView imagegrid;
	private long lastId;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		imageAdapter = new ImageAdapter();
		imageAdapter.initialize();
		imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
		imagegrid.setAdapter(imageAdapter);

		final Button selectBtn = (Button) findViewById(R.id.selectBtn);
		selectBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				final int len = imageAdapter.images.size();
				int cnt = 0;
				String selectImages = "";
				for (int i = 0; i < len; i++) {
					if (imageAdapter.images.get(i).selection) {
						cnt++;
						selectImages = selectImages
								+ imageAdapter.images.get(i).id + ",";
					}
				}

				if (cnt != 2) {
					Toast.makeText(getApplicationContext(),
							"Please select two images",
							Toast.LENGTH_LONG).show();
				} else {
					selectImages = selectImages.substring(0,selectImages.lastIndexOf(","));
					Intent intent = new Intent();
					intent.putExtra("Ids", selectImages);
					intent.putExtra("photoId", "1234"); //it's just a value that required in my database, you can ingore it
					
					setResult(Activity.RESULT_OK, intent);
					finish();
					
				}
				
			}
		});
		final Button captureBtn = (Button) findViewById(R.id.captureBtn);
		captureBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
				String fileName = "IMG_" + sdf.format(new Date()) + ".jpg";
				String appName = AndroidCustomGalleryActivity.this.getString(R.string.app_name);
				File myDirectory = new File(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						appName);
				
				myDirectory.mkdirs();
				
				File file = new File(myDirectory, fileName);
				imageUri = Uri.fromFile(file);
				mPath = imageUri.getPath();

				Intent intent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				
				startActivityForResult(intent, TAKE_IMAGE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_IMAGE:
			try {
				if (resultCode == RESULT_OK) {
					new AsyncTaskRunner().execute(UPLOAD_IMAGES);

					
		
					// we need to update the gallery by starting MediaSanner service.
				
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case UPLOAD_IMAGES:
			if (resultCode == RESULT_OK){
				//do some code where you integrate this project
			}
			break;
		}
	}

	public void updateUI() {
		imageAdapter.checkForNewImages();
	}

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		public ArrayList<ImageItem> images = new ArrayList<ImageItem>();

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void initialize() {
			images.clear();
			final String[] columns = { MediaStore.Images.Thumbnails._ID };
			final String orderBy = MediaStore.Images.Media._ID;
			Cursor imagecursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			if(imagecursor != null){
				int image_column_index = imagecursor
						.getColumnIndex(MediaStore.Images.Media._ID);
				int count = imagecursor.getCount();
				for (int i = 0; i < count; i++) {
					imagecursor.moveToPosition(i);
					int id = imagecursor.getInt(image_column_index);
					ImageItem imageItem = new ImageItem();
					imageItem.id = id;
					lastId = id;
					imageItem.img = MediaStore.Images.Thumbnails.getThumbnail(
							getApplicationContext().getContentResolver(), id,
							MediaStore.Images.Thumbnails.MICRO_KIND, null);
					images.add(imageItem);
				}
				imagecursor.close();
			}
			notifyDataSetChanged();
		}
		
		public void checkForNewImages(){
			//Here we'll only check for newer images
			final String[] columns = { MediaStore.Images.Thumbnails._ID };
			final String orderBy = MediaStore.Images.Media._ID;
			Cursor imagecursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					MediaStore.Images.Media._ID + " > " + lastId , null, orderBy);
			int image_column_index = imagecursor
					.getColumnIndex(MediaStore.Images.Media._ID);
			int count = imagecursor.getCount();
			for (int i = 0; i < count; i++) {
				imagecursor.moveToPosition(i);
				int id = imagecursor.getInt(image_column_index);
				ImageItem imageItem = new ImageItem();
				imageItem.id = id;
				lastId = id;
				imageItem.img = MediaStore.Images.Thumbnails.getThumbnail(
						getApplicationContext().getContentResolver(), id,
						MediaStore.Images.Thumbnails.MICRO_KIND, null);
				imageItem.selection = true; //newly added item will be selected by default
				images.add(imageItem);
			}
			imagecursor.close();
			notifyDataSetChanged();
		}

		public int getCount() {
			return images.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.galleryitem, null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView
						.findViewById(R.id.itemCheckBox);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ImageItem item = images.get(position);
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.checkbox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (images.get(id).selection) {
						cb.setChecked(false);
						images.get(id).selection = false;
					} else {
						cb.setChecked(true);
						images.get(id).selection = true;
					}
				}
			});
			holder.imageview.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					int id = v.getId();
					ImageItem item = images.get(id);
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					final String[] columns = { MediaStore.Images.Media.DATA };
					Cursor imagecursor = managedQuery(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
							MediaStore.Images.Media._ID + " = " + item.id, null, MediaStore.Images.Media._ID);
					if (imagecursor != null && imagecursor.getCount() > 0){
						imagecursor.moveToPosition(0);
						String path = imagecursor.getString(imagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
						File file = new File(path);
						imagecursor.close();
						intent.setDataAndType(
								Uri.fromFile(file),
								"image/*");
						startActivityForResult(intent, VIEW_IMAGE);
					}
				}
			});
			holder.imageview.setImageBitmap(item.img);
			holder.checkbox.setChecked(item.selection);
			return convertView;
		}
	}

	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
	}

	class ImageItem {
		boolean selection;
		int id;
		Bitmap img;
	}


	 private class AsyncTaskRunner extends AsyncTask<Integer, Void, String> {


	  @Override

	  protected String doInBackground(Integer ... params) {	 
		  Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			mediaScanIntent.setData(imageUri);
			sendBroadcast(mediaScanIntent);
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
			return null;
	  }
		@Override
		protected void onPostExecute(String result) {
			updateUI();
		}

	 }
}