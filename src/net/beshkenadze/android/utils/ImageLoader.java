package net.beshkenadze.android.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

public class ImageLoader {
	public interface OnImageLoadListener {
		public void onLoad();

		public void onError();
	}

	MemoryCache memoryCache = new MemoryCache();
	Download fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());

	public ImageLoader(Context с) {
		// Make the background thead low priority. This way it will not affect
		// the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		fileCache = new Download(с);
	}

	public void DisplayImage(String url, ImageView imageView, boolean bg) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			if (bg) {
				imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
			} else {
				imageView.setImageBitmap(bitmap);
			}
			if (listener != null) {
				listener.onLoad();
			}
		} else {
			queuePhoto(url, imageView, bg);
		}
	}

	private void queuePhoto(String url, ImageView imageView, boolean bg) {
		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView, bg);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.get(url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			is.close();
			bitmap = decodeFileMemory(f);
			return bitmap;
		} catch (Exception ex) {
			Log.e("pizza", "Error getting bitmap: " + ex);
			ex.printStackTrace();
			return null;
		}
	}

	public Bitmap decodeFileMemory(File f) throws InterruptedException {
		Bitmap b = null;
		try {

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream is = new FileInputStream(f);
			BitmapFactory.decodeStream(is, null, o);

			// не работает почему-то!!
			int screenWidth = 480; // default

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = screenWidth / 2;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			if (scale != 1)
				Log.i("pizza", "decodeFileMemory: resizing image with scale "
						+ scale);

			is.close();
			is = new FileInputStream(f);

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			b = BitmapFactory.decodeStream(is, null, o2);

			is.close();

			System.gc();

		} catch (OutOfMemoryError e) {
			memoryCache.clear();
			Log.e("pizza", "Error decoding file from memory: " + e);
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			Log.e("pizza", "Error decoding file from memory: " + e);
			e.printStackTrace();
		}
		return b;
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream is = new FileInputStream(f);
			BitmapFactory.decodeStream(is, null, o);

			// не работает почему-то!!
			int screenWidth = 480; // default

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = screenWidth / 2;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			if (scale != 1)
				Log.i("pizza", "decodeFile: resizing image with scale " + scale);

			is.close();
			is = new FileInputStream(f);

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, o2);
			is.close();
			return bitmap;
		} catch (OutOfMemoryError e) {
			memoryCache.clear();
			Log.e("pizza", "Error decoding file: " + e);
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			Log.e("pizza", "Error decoding file: " + e);
			e.printStackTrace();
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		private boolean bg;

		public PhotoToLoad(String u, ImageView i, boolean bg) {
			url = u;
			imageView = i;
			this.bg = bg;
		}
	}

	PhotosQueue photosQueue = new PhotosQueue();

	public void stopThread() {
		photoLoaderThread.interrupt();
	}

	// stores list of photos to download
	class PhotosQueue {
		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();

		// removes all instances of this ImageView
		public void Clean(ImageView image) {
			for (int j = 0; j < photosToLoad.size();) {
				if (photosToLoad.get(j).imageView == image)
					photosToLoad.remove(j);
				else
					++j;
			}
		}
	}

	class PhotosLoader extends Thread {
		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0)
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						Bitmap bmp = memoryCache.get(photoToLoad.url);

						if (bmp == null) {
							bmp = getBitmap(photoToLoad.url);
							if (bmp != null)
								memoryCache.put(photoToLoad.url, bmp);
						}

						String tag = imageViews.get(photoToLoad.imageView);
						if (tag != null && tag.equals(photoToLoad.url)
								&& bmp != null) {
							BitmapDisplayer bd = new BitmapDisplayer(bmp,
									photoToLoad.imageView, photoToLoad.bg);
							Activity a = (Activity) photoToLoad.imageView
									.getContext();
							a.runOnUiThread(bd);
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				// allow thread to exit
			}
		}
	}

	PhotosLoader photoLoaderThread = new PhotosLoader();
	private OnImageLoadListener listener = new OnImageLoadListener() {
		@Override
		public void onLoad() {
			
		}
		
		@Override
		public void onError() {
			
		}
	};

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;
		boolean bg;

		public BitmapDisplayer(Bitmap b, ImageView i, boolean bg) {
			bitmap = b;
			imageView = i;
			this.bg = bg;
		}

		public void run() {
			if (bitmap != null) {
				if (bg) {
					/*
					 * if (imageView.getBackground() == null) { Animation fadeIn
					 * = AnimationUtils.loadAnimation( imageView.getContext(),
					 * R.anim.fade_in); imageView.startAnimation(fadeIn); }
					 */
					imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
				} else {
					/*
					 * if (imageView.getDrawable() == null) { Animation fadeIn =
					 * AnimationUtils.loadAnimation( imageView.getContext(),
					 * R.anim.fade_in); imageView.startAnimation(fadeIn); }
					 */
					imageView.setImageBitmap(bitmap);
				}
				if (listener != null) {
					listener.onLoad();
				}
			}
		}
	}

	public void clearCache() {
		memoryCache.clear();
	}

	public void setOnImageLoadListener(OnImageLoadListener l) {
		listener = l;
	}

	public OnImageLoadListener getOnImageLoadListener() {
		return listener;
	}
}
