package com.myteam.thisorthat.adapter;

import java.util.Locale;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.myteam.thisorthat.R;
import com.myteam.thisorthat.activity.InboxFragment;
import com.myteam.thisorthat.activity.NewsfeedFragment;
import com.myteam.thisorthat.activity.SettingsFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

	protected Context mContext;
	protected ActionBar mActionBar;

	public SectionsPagerAdapter(Context context, FragmentManager fm,
			ActionBar actionBar) {
		super(fm);
		mContext = context;
		mActionBar = actionBar;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		Fragment f = null;
		switch (position) {
		case 0:
			f = new SettingsFragment();
			break;
		case 1:
			f = new NewsfeedFragment();
			break;
		case 2:
			f = new InboxFragment();
			break;
		}

		return f;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return mContext.getString(R.string.title_section3).toUpperCase(l);
		case 1:
			return mContext.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return mContext.getString(R.string.title_section1).toUpperCase(l);
		}
		return null;
	}
}