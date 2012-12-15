package com.cesarandres.samplerss;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.cesarandres.samplerss.modules.FeedItem;
import com.cesarandres.samplerss.modules.NetworkUtil;
import com.cesarandres.samplerss.modules.XMLParser;

/**
 * @author Cesar Ramirez
 * 
 *         Fragment that contains the elements for the detailed view. It will
 *         receive the index for the item to retrieve, get the object from the
 *         adapter and pass the strings to the textviews.
 */
public class FeedItemDetailFragment extends Fragment {

	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * Reference to the currently selected item
	 */
	private static FeedItem mItem;

	public FeedItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// The important information is the index(or id) that will be used for
		// retrieving the object.
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			int position = Integer.parseInt(getArguments().getString(
					ARG_ITEM_ID));
			mItem = (FeedItem) FeedItemListFragment.adapter.getItem(position);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the root of the layout.
		View rootView = inflater.inflate(R.layout.fragment_feeditem_detail,
				container, false);
		if (mItem != null) {
			// Inflate the UI using the information from the object retrieved.
			((TextView) rootView.findViewById(R.id.textViewTitle))
					.setText(mItem.getTitle());
			((TextView) rootView.findViewById(R.id.textViewAuthor))
					.setText("Author: " + mItem.getAuthor());
			((TextView) rootView.findViewById(R.id.textViewTime))
					.setText("Published on: " + mItem.getPublished());
			// Set the onClickEvent for the buttons
			rootView.findViewById(R.id.button1).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {

							if (mItem != null) {
								// This intent will ask the OS for any
								// application
								// that handles opening websites.
								Intent browserIntent = new Intent(
										Intent.ACTION_VIEW, Uri.parse(mItem
												.getOriginalLink()));
								startActivity(browserIntent);
							}
						}

					});
			rootView.findViewById(R.id.button2).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {

							if (mItem != null) {
								// This intent will ask the OS for any
								// application
								// that handles opening websites.
								Intent browserIntent = new Intent(
										Intent.ACTION_VIEW, Uri.parse(mItem
												.getOriginalLink()));
								startActivity(browserIntent);
							}
						}

					});
		}

		new GetContentTask().execute();
		return rootView;
	}

	private class GetContentTask extends AsyncTask<Void, Void, Void> {

		private String content = "";

		protected Void doInBackground(Void... args) {
			XMLParser parser = new XMLParser();
			content = parser.parseGetContent(NetworkUtil.getCache(),
					mItem.getId());
			return null;
		}

		protected void onPostExecute(Void result) {
			((WebView) getActivity().findViewById(R.id.webView1)).loadData(
					content, "text/html", "UTF-8");
		}
	}
}
