package com.cesarandres.samplerss.modules;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class NetworkUtil {

	private static final String CACHE_FOLDER = "Reader";
	private static final String CACHE_FILE = "cache";

	private static final String TAG = "NetworkUtil";
	
	public static String downloadURL(URL url) {
		URL target;
		BufferedReader in = null;
		try {
			target = new URL(url.toString());
			in = new BufferedReader(new InputStreamReader(target.openStream()));
		} catch (IOException e) {
			Log.e(TAG, "IOException while opening file.");
		}

		StringBuffer content = new StringBuffer();
		String line;
		try {
			while ((line = in.readLine()) != null) {
				content.append(line);
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException while reading file.");
		}

		try {
			in.close();
		} catch (IOException e) {
			Log.e(TAG, "IOException while closing file.");
		}
		return content.toString();
	}

	public static File downloadPicture(URL url, String filename) {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), CACHE_FOLDER);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		File file = new File(mediaStorageDir.getAbsoluteFile() + File.separator
				+ filename);

		if (!file.exists()) {
			try {
				file.createNewFile();
				InputStream in = new BufferedInputStream(url.openStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int n = 0;
				while (-1 != (n = in.read(buf))) {
					out.write(buf, 0, n);
				}
				out.close();
				in.close();
				byte[] response = out.toByteArray();

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(response);
				fos.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return file;
		}
		return file;
	}

	public static File getCache() {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), CACHE_FOLDER);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ CACHE_FILE + ".xml");

		return mediaFile;
	}
}
