package net.beshkenadze.android.socialwallpapers.adapter;

import java.util.List;

import net.beshkenadze.android.utils.Utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

public class GridAdapter extends ImageAdapter {

	public GridAdapter(Activity a, List<String> images) {
		super(a, images);
	}

	public GridAdapter(Activity a) {
		super(a);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			imageView = new ImageView(getContext());
			imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}
		if(getItem(position) != null) {
			Utils.downloadImageInView(getActivity(), imageView, getItem(position));
		}
		return imageView;
	}
}