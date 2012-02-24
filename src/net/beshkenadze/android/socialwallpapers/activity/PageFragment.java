package net.beshkenadze.android.socialwallpapers.activity;

import java.util.ArrayList;
import java.util.List;

import net.beshkenadze.android.socialwallpapers.R;
import net.beshkenadze.android.socialwallpapers.adapter.GridAdapter;
import net.beshkenadze.android.socialwallpapers.api.Api;
import net.beshkenadze.android.socialwallpapers.api.Api.OnPhotoRequest;
import net.beshkenadze.android.socialwallpapers.api.Api.OnPhotosRequest;
import net.beshkenadze.android.socialwallpapers.api.Api.OnloadImageToWallpaper;
import net.beshkenadze.android.socialwallpapers.data.Image;
import net.beshkenadze.android.socialwallpapers.data.Photo;
import net.beshkenadze.android.utils.Debug;
import net.beshkenadze.android.utils.Utils;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ProgressBar;

public class PageFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";

	public static PageFragment newInstance(int id) {
		PageFragment fragment = new PageFragment();

		fragment.mId = id;
		return fragment;
	}

	private int mId = 0;
	private int ID_MAKEWALLPAPER = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mId = savedInstanceState.getInt(KEY_CONTENT);
		}
		final SocialWallpapersActivity activity = (SocialWallpapersActivity) getActivity();
		View layout = new LinearLayout(activity);
		if (mId == 0) {
			layout = inflater.inflate(R.layout.random_activity, null);
			final ImageView imageView = (ImageView) layout
					.findViewById(R.id.img_current_wallpaper);
			final Button btnWallpaperSet = (Button) layout
					.findViewById(R.id.btn_wallpaper_set);

			final Button btnWallpaperRerandom = (Button) layout
					.findViewById(R.id.btn_wallpaper_rerandom);
			btnWallpaperRerandom.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doRandom(activity, imageView, btnWallpaperSet,
							btnWallpaperRerandom);
				}
			});
			doRandom(activity, imageView, btnWallpaperSet, btnWallpaperRerandom);
		} else if (mId == 1) {
			layout = inflater.inflate(R.layout.top_activity, null);
			ProgressBar prgTop = (ProgressBar) layout
					.findViewById(R.id.prg_top);
			ListView lstWallpapers = (ListView) layout
					.findViewById(R.id.lst_wallpapers);

			GridAdapter gridAdapter = new GridAdapter(activity);
			lstWallpapers.setAdapter(gridAdapter);
			doTop(activity, prgTop, lstWallpapers, gridAdapter, false);

		} else if (mId == 2) {
			layout = inflater.inflate(R.layout.top_activity, null);
			ProgressBar prgTop = (ProgressBar) layout
					.findViewById(R.id.prg_top);
			ListView lstWallpapers = (ListView) layout
					.findViewById(R.id.lst_wallpapers);

			GridAdapter gridAdapter = new GridAdapter(activity);
			lstWallpapers.setAdapter(gridAdapter);
			doTop(activity, prgTop, lstWallpapers, gridAdapter, true);

		} else {
			// layout = new LinearLayout(getActivity());
			// layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
			// LayoutParams.FILL_PARENT));
			// TextView text = new TextView(getActivity());
			// text.setGravity(Gravity.CENTER);
			// text.setText("id:" + mId);
			// text.setTextSize(20 *
			// getResources().getDisplayMetrics().density);
			// text.setPadding(20, 20, 20, 20);
		}

		return layout;
	}

	int selected_image = 0;

	private void doTop(final SocialWallpapersActivity activity,
			final ProgressBar prgTop, final ListView lstWallpapers,
			final GridAdapter gridAdapter, boolean user_only) {

		prgTop.setVisibility(View.VISIBLE);
		lstWallpapers.setVisibility(View.GONE);

		final Api api = activity.getApi();
		final List<String> wallpapers = new ArrayList<String>();
		ActionItem makeWallpaperItem = new ActionItem(ID_MAKEWALLPAPER,
				"Make wallpaper");
		final QuickAction quickAction = new QuickAction(activity);
		quickAction.addActionItem(makeWallpaperItem);
		lstWallpapers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view,
					int position, long id) {
				quickAction.show(view);
				selected_image = position;
			}
		});
		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						// here we can filter which action item was clicked with
						// pos or actionId parameter
						ActionItem actionItem = quickAction.getActionItem(pos);
						if (actionItem.getActionId() == ID_MAKEWALLPAPER) {
							String photo = wallpapers.get(selected_image);
							if (photo != null) {
								setWallpaper(activity, photo);
							}
						}
					}
				});
		api.getTopPhotos(activity.getAlbum(), new OnPhotosRequest() {
			@Override
			public void onRecive(List<Photo> photos) {
				Debug.i("photos:" + photos.size());
				for (Photo photo : photos) {
					String src = null;
					String preview = null;

					for (Image image : photo.getImages()) {
						if (Integer.parseInt(image.getWidth()) >= api
								.getDisplayWidth()
								&& Integer.parseInt(image.getHeight()) >= api
										.getDisplayHeight()) {
							if (src != null) {
								src = image.getSource();
							}
							preview = image.getSource();
						}
						if (src == null) {
							src = photo.getImages().get(0).getSource();
						}
						if (preview == null) {
							preview = src;
						}
					}
					wallpapers.add(src);
					gridAdapter.setContentItem(preview);
				}
				gridAdapter.notifyDataSetChanged();
				Debug.i("gridAdapter:" + gridAdapter.getItems().size());
				prgTop.setVisibility(View.GONE);
				lstWallpapers.setVisibility(View.VISIBLE);
			}

			@Override
			public void onError() {

			}
		}, user_only);
	}

	private void doRandom(final SocialWallpapersActivity activity,
			final ImageView imageView, final Button btnWallpaperSet,
			final Button btnWallpaperRerandom) {
		final Api api = activity.getApi();
		btnWallpaperSet.setVisibility(View.INVISIBLE);
		btnWallpaperRerandom.setVisibility(View.INVISIBLE);
		imageView.setImageBitmap(null);
		api.getRandomPhoto(activity.getAlbum(), new OnPhotoRequest() {
			@Override
			public void onRecive(final String photo, String preview) {

				Utils.downloadImageInView(activity, imageView, preview);
				btnWallpaperSet.setVisibility(View.VISIBLE);
				btnWallpaperRerandom.setVisibility(View.VISIBLE);

				btnWallpaperSet.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (photo != null) {
							setWallpaper(activity, photo);
						}
					}
				});
			}

			@Override
			public void onError() {
				btnWallpaperRerandom.setVisibility(View.VISIBLE);
			}
		});
	}

	private void setWallpaper(final SocialWallpapersActivity activity,
			String photo) {
		Api api = activity.getApi();
		api.loadImageToWallpaper(photo, new OnloadImageToWallpaper() {
			@Override
			public void onSet() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
								activity,
								getResources().getString(
										R.string.msg_success_set_wallpaper),
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onError() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
								activity,
								getResources().getString(
										R.string.msg_failed_set_wallpaper),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_CONTENT, mId);
	}
}
