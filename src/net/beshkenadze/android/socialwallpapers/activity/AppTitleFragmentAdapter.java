package net.beshkenadze.android.socialwallpapers.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.TitleProvider;

class AppTitleFragmentAdapter extends FragmentPagerAdapter implements TitleProvider {
	private String[] mTitles = {""};

	public AppTitleFragmentAdapter(FragmentManager fm, String[] titles) {
		super(fm);
		mTitles = titles;
	}

	public AppTitleFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public String getTitle(int position) {
		return mTitles[position % mTitles.length];
	}


	@Override
	public int getCount() {
		return mTitles.length;
	}

	@Override
	public Fragment getItem(int position) {
		return PageFragment.newInstance(position);
	}

	public void setContent(String[] titles) {
		mTitles = titles;
		notifyDataSetChanged();
	}
}