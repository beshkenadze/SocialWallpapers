package net.beshkenadze.android.socialwallpapers.activity;

import net.beshkenadze.android.socialwallpapers.R;
import net.beshkenadze.android.socialwallpapers.api.Api;
import net.beshkenadze.android.socialwallpapers.api.Api.OnAlbumRequest;
import net.beshkenadze.android.socialwallpapers.data.Album;
import net.beshkenadze.android.utils.Debug;
import net.hockeyapp.android.CheckUpdateTask;
import net.hockeyapp.android.CrashManager;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class SocialWallpapersActivity extends FragmentSherlockActivity {
	private static final String PAGE_ID = "320494928001935";
	private static final String APP_ID = "03204524e90cf660d5004f881d418aa5";
	Facebook facebook = new Facebook("134276193361014");
	private SharedPreferences mPrefs;
	private Api mApi;
	private ViewPager mPager;
	private AppTitleFragmentAdapter mAdapter;
	private Album mAlbum = new Album();
	private CheckUpdateTask checkUpdateTask;

	private void checkForCrashes() {
		CrashManager.register(this, APP_ID);
	}

	private void checkForUpdates() {
		checkUpdateTask = (CheckUpdateTask) getLastNonConfigurationInstance();
		if (checkUpdateTask != null) {
			checkUpdateTask.attach(this);
		} else {
			checkUpdateTask = new CheckUpdateTask(this,
					"https://rink.hockeyapp.net/", APP_ID);
			checkUpdateTask.execute();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkForCrashes();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Debug.i("onCreate");
		setContentView(R.layout.start);
		authFacebook();
		checkForUpdates();
	}

	private void doLoadApp() {
		Debug.i("doLoadApp");

		setContentView(R.layout.main);
		mAdapter = new AppTitleFragmentAdapter(getSupportFragmentManager());
		doGetCurrentAlbum();
	}

	private void doGetCurrentAlbum() {
		mApi.getCurrentWeekAlbum(new OnAlbumRequest() {

			@Override
			public void onError() {

			}

			@Override
			public void onRecive(Album album) {
				setAlbum(album);
				doReadyAlbum();
			}
		});
	}

	private void doReadyAlbum() {
		mPager = (ViewPager) findViewById(R.id.pager);
		String[] mTitles = getResources().getStringArray(R.array.titles);
		mAdapter.setContent(mTitles);
		mPager.setAdapter(mAdapter);

		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		indicator.setFooterIndicatorStyle(IndicatorStyle.Triangle);

		setTitle(getString(R.string.app_name) + " / " + getAlbum().getName());
	}

	private void authFacebook() {
		Debug.i("authFacebook");
		setApi(new Api(this, facebook, PAGE_ID));
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}
		if (!facebook.isSessionValid()) {
			Debug.i("Need authorize");
			String[] permissions = { "read_stream" };
			facebook.authorize(this, permissions, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					Debug.i("Recive token");
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires",
							facebook.getAccessExpires());
					editor.commit();
					doLoadApp();
				}

				@Override
				public void onFacebookError(FacebookError error) {
					Debug.e(error.toString());
				}

				@Override
				public void onError(DialogError e) {
					Debug.e(e.toString());
				}

				@Override
				public void onCancel() {
					Debug.i("Cancel");
				}
			});
		} else {
			Debug.i("No need authorize");
			doLoadApp();
		}
	}

	public Api getApi() {
		return mApi;
	}

	public void setApi(Api mApi) {
		this.mApi = mApi;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	public Album getAlbum() {
		return mAlbum;
	}

	public void setAlbum(Album mAlbum) {
		this.mAlbum = mAlbum;
	}
}