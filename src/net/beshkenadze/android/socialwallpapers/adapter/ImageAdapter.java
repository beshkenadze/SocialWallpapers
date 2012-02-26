package net.beshkenadze.android.socialwallpapers.adapter;

import java.util.ArrayList;
import java.util.List;

import net.beshkenadze.android.socialwallpapers.R;
import net.beshkenadze.android.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;
	private List<String> mImages = new ArrayList<String>();
	private Activity mActivity;

	private LayoutInflater mInflater;

	static class ViewKeeper {
		ImageView image;
	}

	public ImageAdapter(Activity a, List<String> images) {
		setContext((Context) a);
		setActivity(a);
		mImages = images;
		mInflater = (LayoutInflater) a
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public ImageAdapter(Activity a) {
		setContext((Context) a);
		setActivity(a);
		mInflater = (LayoutInflater) a
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public int getCount() {
		return mImages.size();
	}

	public String getItem(int position) {
		String imageLink = mImages.get(position);
		return imageLink;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewKeeper keeper;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.grid_item, null);
			keeper = new ViewKeeper();
			keeper.image = (ImageView) convertView
					.findViewById(R.id.img_wallpaper);
			convertView.setTag(keeper);
		} else {
			keeper = (ViewKeeper) convertView.getTag();
			keeper.image.setImageBitmap(null);
			String imageLink = getItem(position);
			if (imageLink != null) {
				Utils.downloadImageInView(mActivity, keeper.image, imageLink);
			}
		}

		return convertView;
	}

	public List<String> getItems() {
		return mImages;
	}

	public void setContentItem(String src) {
		mImages.add(src);
	}
	public void setContent(List<String> images) {
		mImages = images;
	}
	public void clear() {
		mImages.clear();
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	public Activity getActivity() {
		return mActivity;
	}

	public void setActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}
}