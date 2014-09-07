package com.myteam.thisorthat.activity;

import java.util.List;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.myteam.thisorthat.R;
import com.parse.ParseObject;



public class ProfileFragment extends Fragment  {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile,
				container, false);

        // Set title in Fragment for display purposes.
       
        Bundle b = getArguments();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		System.gc(); 

	}

    


 


		

}








