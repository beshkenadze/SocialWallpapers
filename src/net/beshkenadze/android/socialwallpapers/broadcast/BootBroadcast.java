package net.beshkenadze.android.socialwallpapers.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcast extends BroadcastReceiver{
	WallpaperBroadcastReceiver wbr = new WallpaperBroadcastReceiver();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			wbr.setAlarm(context);
		}
	}
}
