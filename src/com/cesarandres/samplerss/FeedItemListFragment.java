package com.cesarandres.samplerss;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.cesarandres.samplerss.modules.EfficientAdapter;
import com.cesarandres.samplerss.modules.NetworkUtil;
import com.cesarandres.samplerss.modules.XMLParser;

public class FeedItemListFragment extends ListFragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	public static final String target = "http://feeds.feedburner.com/TechnologyOpen-sourceAndOtherFunThings";

	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	// Adapter that is in charge of showing data in the list
	public static EfficientAdapter adapter;

	public interface Callbacks {

		public void onItemSelected(String id);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	public FeedItemListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Once the applicaion is created,
		// start the DownloadFeedTask.
		// This task will handle the process of gathering
		// information and placing it in the list.
		// Before running this task we will check if we have a reference to this
		// object already. If a reference already exists then this activity has
		// been recreated and we do not need to load the data again, just
		// reattach it.
		if (adapter == null) {
			try {
				new DownloadFeedTask().execute(new URL(target));
			} catch (MalformedURLException e) {
			}
		} else {
			setListAdapter(adapter);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		mCallbacks.onItemSelected(Integer.toString(position));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	/**
	 * @author Cesar Ramirez
	 * 
	 *         This task will offload the work of retrieveing the information
	 *         that is going to be displayed. This task will also handle
	 *         different situations and it will try to gather the most recent
	 *         information. In case the network is working, this task will check
	 *         the local cache(if any) and decide if download a new file or just
	 *         use the local cache.
	 */
	private class DownloadFeedTask extends AsyncTask<URL, Integer, Void> {

		private int taskResult = TASK_ERROR;
		private final static int TASK_ERROR = 0;
		private final static int TASK_ERROR_FALLBACK = 1;
		private final static int TASK_SUCCESSFUL = 2;

		private final static int PROGRESS_NETWORK_WORKING = 0;
		private final static int PROGRESS_NETWORK_WORKING_CACHEFOUND = 5;
		private final static int PROGRESS_NETWORK_NOTWORKING_CACHEFOUND = 1;
		private final static int PROGRESS_NETWORK_NOTWORKING_CACHENOTFOUND = 2;
		private final static int PROGRESS_NETWORK_WORKING_ERROR_DOWNLOADING = 3;
		private final static int PROGRESS_ERROR_PARSING = 4;

		protected void onProgressUpdate(Integer... progress) {
			// Different messages to show to the user so they can know what
			// is happening.
			switch (progress[0]) {
			case PROGRESS_NETWORK_WORKING:
				Toast.makeText(getActivity(), "Starting to download data...",
						Toast.LENGTH_SHORT).show();
				break;
			case PROGRESS_NETWORK_NOTWORKING_CACHEFOUND:
				Toast.makeText(getActivity(),
						"No network connection, loading cache",
						Toast.LENGTH_SHORT).show();
				break;
			case PROGRESS_NETWORK_NOTWORKING_CACHENOTFOUND:
				Toast.makeText(getActivity(),
						"No network connection, no cache found",
						Toast.LENGTH_SHORT).show();
				break;
			case PROGRESS_NETWORK_WORKING_ERROR_DOWNLOADING:
				Toast.makeText(getActivity(), "Error while downloading data",
						Toast.LENGTH_SHORT).show();
				break;
			case PROGRESS_ERROR_PARSING:
				Toast.makeText(getActivity(),
						"Error while processing downloaded data",
						Toast.LENGTH_SHORT).show();
			case PROGRESS_NETWORK_WORKING_CACHEFOUND:
				Toast.makeText(getActivity(), "Cache is up to date",
						Toast.LENGTH_SHORT).show();
			default:
				break;
			}

		}

		protected Void doInBackground(URL... url) {
			// First check the connectivity
			ConnectivityManager connMgr = (ConnectivityManager) getActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			XMLParser parser = new XMLParser();
			// If we have internet connection
			if (networkInfo != null && networkInfo.isConnected()) {
				try {
					// Check if we have a cache file. If cache exists, we will
					// check their latest update time, if cache does not exist
					// just download a new file.
					File cache = NetworkUtil.getCache();
					if (cache.exists()) {
						String cacheTime = "";
						String feedTime = "";
						// Get the data from the rss feed
						String content = parser.parseClean(NetworkUtil
								.downloadURL(new URL(target)));

						// Get the update time from the rss feed
						feedTime = parser.parseGetUpdateTime(content);
						// Get the update time from the cache file
						cacheTime = parser.parseGetUpdateTime(cache);

						// Compare update times
						//if (!cacheTime.equalsIgnoreCase(feedTime)) {
						if (true) {
							// Cache is not latest, download a new version
							publishProgress(PROGRESS_NETWORK_WORKING);
							FileWriter fstream = new FileWriter(
									NetworkUtil.getCache());
							BufferedWriter out = new BufferedWriter(fstream);
							out.write(content);
							out.close();
						} else {
							// Cache is up-to-date, just load it
							publishProgress(PROGRESS_NETWORK_WORKING_CACHEFOUND);
						}
					} else {
						// Just download a new version of the rss feed
						// and save it as a cache file
						publishProgress(PROGRESS_NETWORK_WORKING);
						String content = parser.parseClean(NetworkUtil
								.downloadURL(new URL(target)));
						FileWriter fstream = new FileWriter(
								NetworkUtil.getCache());
						BufferedWriter out = new BufferedWriter(fstream);
						out.write(content);
						out.close();
					}
					taskResult = TASK_SUCCESSFUL;
				} catch (MalformedURLException e) {
					publishProgress(PROGRESS_NETWORK_WORKING_ERROR_DOWNLOADING);
					taskResult = TASK_ERROR;
				} catch (IOException e) {
					publishProgress(PROGRESS_NETWORK_WORKING_ERROR_DOWNLOADING);
					taskResult = TASK_ERROR;
				}
			} else {// If we do not have internet connection
				File cache = NetworkUtil.getCache();
				// Check if we have a cache file, if such file exists load it.
				if (cache.exists()) {
					publishProgress(PROGRESS_NETWORK_NOTWORKING_CACHEFOUND);
					taskResult = TASK_ERROR_FALLBACK;
				} else {
					publishProgress(PROGRESS_NETWORK_NOTWORKING_CACHENOTFOUND);
					taskResult = TASK_ERROR;
				}

			}
			// If the cache file exists, then we will retrieve the information
			if (taskResult == TASK_SUCCESSFUL
					|| taskResult == TASK_ERROR_FALLBACK) {
				adapter = new EfficientAdapter(getActivity());
				// The information gathered is send to the adapter to be
				// displayed later.
				// We can not display the information in this method because we
				// can not access the UI thread form here.
				adapter.setContent(parser.parseGetObjects(NetworkUtil
						.getCache()));
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			String message = "";
			// More messages to show to the user with information about the
			// result of the task.
			switch (taskResult) {
			case TASK_ERROR:
				message = "Download error, data could not be downloaded";
				break;
			case TASK_ERROR_FALLBACK:
				message = "Download error, using older data";
				break;
			case TASK_SUCCESSFUL:
				message = "Enjoy!";
				// If the task was successful, set the adapter for the list. UI
				// Interaction can be done from here because onPostExecute runs
				// on the UI thread.
				setListAdapter(adapter);
				break;
			default:
				break;
			}

			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
		}
	}
}
