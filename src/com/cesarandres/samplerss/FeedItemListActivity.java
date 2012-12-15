package com.cesarandres.samplerss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * @author Cesar Ramirez
 * 
 *         Activity that will abstract to either one or two panes based on
 *         screen size. This activity will start the required fragments.
 */
public class FeedItemListActivity extends FragmentActivity implements
		FeedItemListFragment.Callbacks {

	private boolean mTwoPane;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeditem_list);
		
		//If the second container has been created by the system, 
		//then we know we have a double plane layout.
		if (findViewById(R.id.feeditem_detail_container) != null) {
			mTwoPane = true;
			((FeedItemListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.feeditem_list))
					.setActivateOnItemClick(true);
		}
	}

	@Override
	public void onItemSelected(String id) {
		//Choose action based on layout
		if (mTwoPane) {
			//Start transaction to create the detailed view fragment.
			//Save the index in the bundle and pass it as an argument.
			Bundle arguments = new Bundle();
			arguments.putString(FeedItemDetailFragment.ARG_ITEM_ID, id);
			FeedItemDetailFragment fragment = new FeedItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.feeditem_detail_container, fragment).commit();

		} else {
			//We are in a single pane view, so the detailed view is another activity.
			//Send the index of the item selected and call the intent to create new activity.
			Intent detailIntent = new Intent(this, FeedItemDetailActivity.class);
			detailIntent.putExtra(FeedItemDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
