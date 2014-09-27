package com.myteam.thisorthat.activity;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;

import com.myteam.thisorthat.R;
import com.myteam.thisorthat.adapter.GridViewAdapter;
import com.myteam.thisorthat.model.ImageItem;

/**
 * 
 * @author javatechig {@link http://javatechig.com}
 * 
 */
public class FlickrActivity extends Activity {
	// String to create Flickr API urls
	private static final String FLICKR_BASE_URL = "https://api.flickr.com/services/rest/?method=";
	private static final String FLICKR_PHOTOS_SEARCH_STRING = "flickr.photos.search";
	private static final String FLICKR_GET_SIZES_STRING = "flickr.photos.getSizes";
	public static final int FLICKR_PHOTOS_SEARCH_ID = 1;
	private static final int FLICKR_GET_SIZES_ID = 2;
	private static final int NUMBER_OF_PHOTOS = 20;
	
	//You can set here your API_KEY
	private static final String APIKEY_SEARCH_STRING = "&api_key=6a0eeae0133bb6d3f27395453ce88ead";
	
	private static final String TAGS_STRING = "&text=";
	private static final String PHOTO_ID_STRING = "&photo_id=";
	private static final String FORMAT_STRING = "&sort=relevance&format=json";
	public static final int PHOTO_THUMB = 111;
	public static final int PHOTO_LARGE = 222;
	
	
	private GridView gridView;
	private GridViewAdapter customGridAdapter;	
	private EditText mSearchText;
	private String strSearch = null;
	
	
	public static String createURL(int methodId, String parameter) {
		String method_type = "";
		String url = null;
		switch (methodId) {
		case FLICKR_PHOTOS_SEARCH_ID:
			method_type = FLICKR_PHOTOS_SEARCH_STRING;
			url = FLICKR_BASE_URL + method_type + APIKEY_SEARCH_STRING + TAGS_STRING + parameter + FORMAT_STRING + "&per_page="+NUMBER_OF_PHOTOS+"&media=photos";
			break;
		case FLICKR_GET_SIZES_ID:
			method_type = FLICKR_GET_SIZES_STRING;
			url = FLICKR_BASE_URL + method_type + PHOTO_ID_STRING + parameter + APIKEY_SEARCH_STRING + FORMAT_STRING;
			break;
		}
		return url;
	}
	
	public class getImagesTask extends AsyncTask<Void, Void, Void>
	   {
		   JSONObject json;
		   ProgressDialog dialog;
		   
		   @Override
		   protected Void doInBackground(Void... params) {
			   // TODO Auto-generated method stub
			   
			   URL url;
			   try {
				   url =new URL(createURL(FLICKR_PHOTOS_SEARCH_ID, strSearch));
				
			   URLConnection connection = url.openConnection();
			   String line;
			   StringBuilder builder = new StringBuilder();
			   BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			   while((line = reader.readLine()) != null) {
				   builder.append(line);
			   }
	
			   
			   String output = (builder.toString()).replace("jsonFlickrApi(", "").replace(")", "");
			   json = new JSONObject(output);
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
					JSONObject photos = json.getJSONObject("photos");
					JSONArray resultArray = photos.getJSONArray("photo");
				   
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
			   
			   dialog = ProgressDialog.show(FlickrActivity.this, "", "Please wait...");
		   }
	   }

	 
	private ArrayList<ImageItem> listImages;

	    
   public void btnSearchClick(View v)
   {
	   strSearch = mSearchText.getText().toString();
	   strSearch = Uri.encode(strSearch);
	   
	   System.out.println("Search string => "+strSearch);
	   new getImagesTask().execute();
   }

 //helper method, to construct the url from the json object. You can define the size of the image that you want, with the size parameter. 
 //  Be aware that not all images on flickr are available in all sizes.
   public static String constructFlickrImgUrl(JSONObject input, Enum size) throws JSONException {
           String FARMID = input.getString("farm");
           String SERVERID = input.getString("server");
           String SECRET = input.getString("secret");
           String ID = input.getString("id");
    
           StringBuilder sb = new StringBuilder();
    
           sb.append("http://farm");
           sb.append(FARMID);
           sb.append(".static.flickr.com/");
           sb.append(SERVERID);
           sb.append("/");
           sb.append(ID);
           sb.append("_");
           sb.append(SECRET);
           sb.append(size.toString());                    
           sb.append(".jpg");
    
           return sb.toString();
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
			   
				
				bean.setUrl(constructFlickrImgUrl(obj, size._m));
				bean.setBigUrl(constructFlickrImgUrl(obj, size._z));
			
				
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
	String keyword =  getIntent().getStringExtra("keywords");
	if(keyword != null && !keyword.equals("")){
		   strSearch = Uri.encode(keyword);
		   mSearchText.setText(keyword);
		   System.out.println("Search string => "+strSearch);
		   new getImagesTask().execute();
	}


}
   

   
   
   public void SetListViewAdapter(ArrayList<ImageItem> images)
   {
		customGridAdapter = new GridViewAdapter(this, R.layout.row_grid, images);
		gridView.setAdapter(customGridAdapter);

   }

   public static enum size {
       _s , _t ,_m,_z
};
	

}
