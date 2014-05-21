package com.myteam.thisorthat.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myteam.thisorthat.R;
import com.myteam.thisorthat.R.id;
import com.myteam.thisorthat.model.ImageItem;
import com.squareup.picasso.Picasso;

/**
 * 
 * @author javatechig {@link http://javatechig.com}
 * 
 */
public class GridViewAdapter extends ArrayAdapter<ImageItem> {
	private Context context;
	private int layoutResourceId;
	private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

	public GridViewAdapter(Context context, int layoutResourceId,
			ArrayList<ImageItem> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();

			holder.image = (ImageView) row.findViewById(R.id.image);
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		ImageItem item = data.get(position);
		holder.image.setImageBitmap(item.getImage());
		Picasso.with(context).load(item.getUrl()).fit().into(holder.image);
		
		
		ImageOnClickListener onImageClickListener = new ImageOnClickListener(position) {


			@Override
	        public void onClick(View v) {
				//	Toast.makeText(GoogleImageActivity.this, position + "#Selected",
				//		Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				String url = data.get(position).getBigUrl();
				intent.putExtra("imageUrl", url);
				((Activity)context).setResult(Activity.RESULT_OK, intent);
				((Activity)context).finish();
				
				

	        	
	 
			}
		};
		holder.image.setOnClickListener(onImageClickListener);
		
		return row;
	}

	static class ViewHolder {
		TextView imageTitle;
		ImageView image;
		
	}
	public class ImageOnClickListener implements OnClickListener{

	     int position;
	     public ImageOnClickListener(int position) {
	          this.position = position;
	     }

	     @Override
	     public void onClick(View v) {
	    
	    	 	System.out.println("HI");

	    	 

	     }

	  };

}