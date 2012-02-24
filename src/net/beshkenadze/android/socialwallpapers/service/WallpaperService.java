package net.beshkenadze.android.socialwallpapers.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.beshkenadze.android.socialwallpapers.broadcast.WallpaperBroadcastReceiver;
import net.beshkenadze.android.utils.Debug;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WallpaperService extends Service {
	WallpaperBroadcastReceiver wbr = new WallpaperBroadcastReceiver();

	public void onCreate() {
		super.onCreate();
	}

	public void onStart(Context context, Intent intent, int startId) {
		wbr.setAlarm(context);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
