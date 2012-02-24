package net.beshkenadze.android.socialwallpapers.adapter;

import java.util.List;

import net.beshkenadze.android.socialwallpapers.activity.SocialWallpapersActivity;
import net.beshkenadze.android.utils.Debug;
import net.beshkenadze.android.utils.Utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryAdapter extends ImageAdapter {

	public GalleryAdapter(Activity a, List<String> images) {
		super(a, images);
	}

	public GalleryAdapter(Activity a) {
		super(a);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(getContext());
		String imageLink = getItem(position);
		Debug.i("imageLink:"+imageLink);
		if (imageLink != null) {
			Utils.downloadImageInView(getActivity(), imageView, imageLink);
		}

		imageView.setLayoutParams(new Gallery.LayoutParams(150, 100));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setBackgroundResource(mGalleryItemBackground);

		return imageView;
	}
}