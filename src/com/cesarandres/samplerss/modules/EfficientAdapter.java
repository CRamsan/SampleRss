package com.cesarandres.samplerss.modules;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cesarandres.samplerss.R;

/**
 * @author Cesar Ramirez
 * 
 *         This class works as an interface between the data and the information
 *         to be displayed on the screen. This adapter is efficient because not
 *         all data is loaded at one. Rather we will only inflate the elements
 *         that are shown on the screen from our array of items.
 */
public class EfficientAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<FeedItem> contentList;
	private Bitmap fallBack;

	public EfficientAdapter(Context context) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		// In case an image can not be loaded or the item does not include an
		// image, we will use by default.
		fallBack = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
	}

	public void setContent(ArrayList<FeedItem> list) {
		this.contentList = list;
	}

	public int getCount() {
		return contentList.size();
	}

	public Object getItem(int position) {
		return contentList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// This method will be called for all elements that are about to be
		// displayed. This has to be the most efficient part of the adapter.
		final ViewHolder holder;

		// Inflate the item's layout if it does nto exist
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_layout, null);

			holder = new ViewHolder();
			holder.text = (TextView) convertView
					.findViewById(R.id.textview_title);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.imageview_thumbnail);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Set the main text
		holder.text.setText(contentList.get(position).getTitle());

		// Now we will load the thumbnail for the item to be loaded.
		// Check if this item has a thumbnail
		if (!contentList.get(position).getThumbnail().equalsIgnoreCase("")) {
			// Create a thread to get(read or download) the bitmap
			new Thread(new Runnable() {
				public void run() {
					final File thumbnail;
					try {
						// This method will get the file, depending on the
						// situation it will be downloaded from the server of
						// read from the cache.
						thumbnail = NetworkUtil.downloadPicture(new URL(
								contentList.get(position).getThumbnail()),
								contentList.get(position).getId());
						// Once we have the file, we will set the bitmap. This
						// operation cannot be done from here becase we cannot
						// access the UI thread from another threads.
						holder.icon.post(new Runnable() {
							public void run() {
								//This portion of code will be run in the UI thread.
								//The thumbnail will be displayed.
								holder.icon.setImageBitmap(BitmapFactory
										.decodeFile(thumbnail.getAbsolutePath()));
							}
						});

					} catch (MalformedURLException e) {
					}
				}
			}).start();
		} else {
			// If no thumbnail is used, use the fedault one.
			holder.icon.setImageBitmap(fallBack);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView text;
		ImageView icon;
	}
}