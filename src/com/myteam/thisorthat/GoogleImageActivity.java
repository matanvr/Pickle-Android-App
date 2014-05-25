package com.myteam.thisorthat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.myteam.thisorthat.adapter.GridViewAdapter;
import com.myteam.thisorthat.model.ImageItem;
import com.parse.ParseObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author javatechig {@link http://javatechig.com}
 * 
 */
public class GoogleImageActivity extends Activity {
	public class getImagesTask extends AsyncTask<Void, Void, Void>
	   {
		   JSONObject json;
		   ProgressDialog dialog;
		   
		   @Override
		   protected Void doInBackground(Void... params) {
			   // TODO Auto-generated method stub
			   
			   URL url;
			   try {
				   url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +
				   	"v=1.0&q="+strSearch+"&rsz=8&imgtype=photo"); //&key=ABQIAAAADxhJjHRvoeM2WF3nxP5rCBRcGWwHZ9XQzXD3SWg04vbBlJ3EWxR0b0NVPhZ4xmhQVm3uUBvvRF-VAA&userip=192.168.0.172");
			   
			   URLConnection connection = url.openConnection();
			   connection.addRequestProperty("Referer", "http://google.com");
			   System.out.println(url);
			   String line;
			   StringBuilder builder = new StringBuilder();
			   BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			   while((line = reader.readLine()) != null) {
				   builder.append(line);
			   }
	
			   
			   
			   json = new JSONObject(builder.toString());
			   } catch (MalformedURLException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
			   } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   return null;
		   }
		   
		   @Override
		   protected void onPostExecute(Void result) {
			   // TODO Auto-generated method stub
			   super.onPostExecute(result);
			   
			   if(dialog.isShowing())
			   {
				   dialog.dismiss();
			   }
			   
			   try {
				   JSONObject responseObject = json.getJSONObject("responseData");
				   JSONArray resultArray = responseObject.getJSONArray("results");
				   
				   listImages = getImageList(resultArray);
				   SetListViewAdapter(listImages);
				 //  System.out.println("Result array length => "+resultArray.length());
			   } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   
		   }
		   
		   @Override
		   protected void onPreExecute() {
			   // TODO Auto-generated method stub
			   super.onPreExecute();
			   
			   dialog = ProgressDialog.show(GoogleImageActivity.this, "", "Please wait...");
		   }
	   }
	private GridView gridView;
	private GridViewAdapter customGridAdapter;	
	private EditText mSearchText;
	private String strSearch = null;
	 
	private ArrayList<ImageItem> listImages;

	    
   public void btnSearchClick(View v)
   {
	   strSearch = mSearchText.getText().toString();
	   strSearch = Uri.encode(strSearch);
	   
	   System.out.println("Search string => "+strSearch);
	   new getImagesTask().execute();
   }
   
   public ArrayList<ImageItem> getImageList(JSONArray resultArray)
   {
	   ArrayList<ImageItem> listImages = new ArrayList<ImageItem>();
	   ImageItem bean;

		try 
		{
			for(int i=0; i<resultArray.length(); i++)
			{
				JSONObject obj;
				obj = resultArray.getJSONObject(i);
				bean = new ImageItem();
			   
				
				bean.setUrl(obj.getString("tbUrl"));
				bean.setBigUrl(obj.getString("url"));
				System.out.println("Thumb URL => "+obj.getString("tbUrl"));
				
				listImages.add(bean);
			   
			} 
			return listImages;
		 }
		 catch (JSONException e) 
		 {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }
		 
		 return null;
	}
   
   @Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_google_image);

	gridView = (GridView) findViewById(R.id.imageGrid);
	mSearchText = (EditText) findViewById(R.id.txtViewSearch);



}
   

   
   
   public void SetListViewAdapter(ArrayList<ImageItem> images)
   {
		customGridAdapter = new GridViewAdapter(this, R.layout.row_grid, images);
		gridView.setAdapter(customGridAdapter);

   }

	
	

}
