package com.myteam.thisorthat;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.myteam.adapter.NavDrawerListAdapter;
import com.myteam.model.NavDrawerItem;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	public static final String TAG = MainActivity.class.getSimpleName();
	
	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int TAKE_VIDEO_REQUEST = 1;
	public static final int PICK_PHOTO_REQUEST = 2;
	public static final int PICK_VIDEO_REQUEST = 3;
	
	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final int MEDIA_TYPE_VIDEO = 5;
	
	private int MENU_HOME = 1;
	private int MENU_PROFILE = 0;
	private int MENU_FRIENDS = 2;
	private int MENU_STATE = MENU_HOME;
	public static final int FILE_SIZE_LIMIT = 1024*1024*10; // 10 MB
	
	protected Uri mMediaUri;
	protected Menu mMenu; 

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
 
    // nav drawer title
    private CharSequence mDrawerTitle;
 
    // used to store app title
    private CharSequence mTitle;
 
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.activity_main);
       // final ActionBar actionBar = getActionBar();
       // getActionBar().setIcon(R.drawable.action_menu);
        //actionBar.setCustomView(R.layout.actionbar_custom_view_home);
    //    actionBar.setDisplayShowCustomEnabled(true);
		//TextView logo = (TextView) findViewById(R.id.actionBarLogo);
	//	Typeface lightType = Typeface.createFromAsset(
            //    this.getAssets(),
            //    "fonts/calibri.ttf");

		//logo.setTypeface(lightType);
      //  getActionBar().setDisplayShowTitleEnabled(false);
		ParseAnalytics.trackAppOpened(getIntent());
		

		ParseUser currentUser = ParseUser.getCurrentUser();

			
		
		if (currentUser == null ) {
			navigateToLogin();
		}
		


		// Set up the action bar.
	//	final ActionBar actionBar = getActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		
		mTitle = mDrawerTitle = getTitle();
		 
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
 
        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
 
        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
         
 
        // Recycle the typed array
        navMenuIcons.recycle();
 
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
 
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
       // getActionBar().setDisplayShowHomeEnabled(false);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            if(currentUser != null)
        	displayView(1);
        }
		
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
	//	mSectionsPagerAdapter = new SectionsPagerAdapter(this, 
		//		getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		
		//commented out for menu
		//mViewPager = (ViewPager) findViewById(R.id.pager);
		//mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		
		
		/*
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});*/

		// For each of the sections in the app, add a tab to the action bar.
	/*	for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}*/
	}
	


	private void navigateToLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		if(MENU_STATE == MENU_HOME){
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
		}
		else if(MENU_STATE == MENU_PROFILE){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
		}
		else if(MENU_STATE == MENU_FRIENDS){
	        menu.getItem(0).setVisible(false);
	        menu.getItem(1).setVisible(false);
	        menu.getItem(2).setVisible(true);
	}
		
		mMenu = menu;
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		int itemId = item.getItemId();
		Intent intent;
		switch(itemId) {
			case R.id.action_profile_settings:
				ParseUser.logOut();
				navigateToLogin();
				break;
			case R.id.action_add_user:
				intent = new Intent(this, EditFriendsActivity.class);
				startActivity(intent);
				break;
			case R.id.action_camera:

				intent = new Intent(this, NewPost.class);
				startActivity(intent);
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	@Override  
	protected void onPause()  
	{  
		super.onPause();  
		System.gc();  
	}  
	  @Override
	  protected void onDestroy()
	  {
	    super.onDestroy();
	    
	    // Fixes android memory  issue 8488 :
	    // http://code.google.com/p/android/issues/detail?id=8488
	    nullViewDrawablesRecursive(mViewPager);
	    
	    mViewPager = null;
	    System.gc();
	  }
	  private void nullViewDrawablesRecursive(View view)
	  {
	    if(view != null)
	    {
	      try
	      {
	        ViewGroup viewGroup = (ViewGroup)view;
	        
	        int childCount = viewGroup.getChildCount();
	        for(int index = 0; index < childCount; index++)
	        {
	          View child = viewGroup.getChildAt(index);
	          nullViewDrawablesRecursive(child);
	        }
	      }
	      catch(Exception e)
	      {          
	      }
	      
	      nullViewDrawable(view);
	    }    
	  }

	  private void nullViewDrawable(View view)
	  {
	    try
	    {
	      view.setBackgroundDrawable(null);
	    }
	    catch(Exception e)
	    {          
	    }
	    
	    try
	    {
	      ImageView imageView = (ImageView)view;
	      imageView.setImageDrawable(null);
	      imageView.setBackgroundDrawable(null);
	    }
	    catch(Exception e)
	    {          
	    }
	  }
	   @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	        // if nav drawer is opened, hide the action items
	        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	     //   menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
	        return super.onPrepareOptionsMenu(menu);
	    }
	 
	    @Override
	    public void setTitle(CharSequence title) {
	        mTitle = title;
	        getActionBar().setTitle(mTitle);
	    }
	 
	    /**
	     * When using the ActionBarDrawerToggle, you must call it during
	     * onPostCreate() and onConfigurationChanged()...
	     */
	 
	    @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
	    }
	 
	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        // Pass any configuration change to the drawer toggls
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }
	    /**
	     * Slide menu item click listener
	     * */
	    private class SlideMenuClickListener implements
	            ListView.OnItemClickListener {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position,
	                long id) {
	            // display view for selected nav drawer item
	            displayView(position);
	        }
	    }
	 
	     /**
	     * Diplaying fragment view for selected nav drawer list item
	     * */
	    private void displayView(int position) {
	        // update the main content by replacing fragments
	        Fragment fragment = null;
	        switch (position) {
	        case 0:
	        	MENU_STATE = MENU_PROFILE;
	        	this.invalidateOptionsMenu();
	        	fragment = new ProfileFragment();
	        	
	        	break;
	        case 1:
	        	MENU_STATE = MENU_HOME;
	        	this.invalidateOptionsMenu();
	            fragment = new InboxFragment();
	            
	            break;
	        case 2:
	        	MENU_STATE = MENU_FRIENDS;
	        	this.invalidateOptionsMenu();
	            fragment = new FriendsFragment();
	            break;

	 
	        default:
	            break;
	        }
	 
	        if (fragment != null) {
	            FragmentManager fragmentManager = getSupportFragmentManager();
	            fragmentManager.beginTransaction()
	                    .replace(R.id.frame_container, fragment).commit();
	 
	            // update selected item and title, then close the drawer
	            mDrawerList.setItemChecked(position, true);
	            mDrawerList.setSelection(position);
	           // setTitle(navMenuTitles[position]);
	            mDrawerLayout.closeDrawer(mDrawerList);
	        } else {
	            // error in creating fragment
	            Log.e("MainActivity", "Error in creating fragment");
	        }
	    }

}
