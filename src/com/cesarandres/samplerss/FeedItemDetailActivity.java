package com.cesarandres.samplerss;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * @author Cesar Ramirez
 * 
 *         This class represents an activity that shows the information for a
 *         single element(detailed view).
 */
public class FeedItemDetailActivity extends FragmentActivity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeditem_detail);

		// Check if we are running 3.0 or more so we can get a reference for the
		// Actionbar. With this we can activate the UP button
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);

		}

		// Get the information and reference of the selected item even after the
		// activity has been recreated(screen rotate)
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putString(FeedItemDetailFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(FeedItemDetailFragment.ARG_ITEM_ID));
			FeedItemDetailFragment fragment = new FeedItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.feeditem_detail_container, fragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			//Handles navigatin UP in the activity stack
			NavUtils.navigateUpTo(this, new Intent(this,
					FeedItemListActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
