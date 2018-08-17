package blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.common.sdk.adpriority.AdPriorityManager;
import com.common.sdk.analytics.AnalyticsManager;
import com.common.sdk.analytics.ChannelData;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;


public class InstallRefererReceiver extends BroadcastReceiver {

	private final static String INSTALL_ACTION = "com.android.vending.INSTALL_REFERRER";
	private final static String LOG_TAG = "InstallRefererReceiver";

	@Override
	public void onReceive(final Context context, Intent intent) {
		try {
            LogUtil.d("install_ref", "receive INSTALL_REFERRER: ");
			AnalyticsManager.postChannelData(intent, new AnalyticsManager.ChannelDataListener() {
				@Override
				public void onChannelChanged(ChannelData newChannelData) {
					AdPriorityManager.getInstance(context.getApplicationContext()).setChannel(newChannelData.getChannel());
					AdPriorityManager.getInstance(context.getApplicationContext()).setSubChannel(newChannelData.getSubCh());
//					TestManager.getInstance(context.getApplicationContext()).updateData(newChannelData.getChannel(), newChannelData.getSubCh(), ConstantUtils.BASE_URL, ConstantUtils.PARAM_BASE_URL);
					LogUtil.d("install_ref", "channel: "+newChannelData.getChannel());
				}
			});
		} catch (Exception e) {
			LogUtil.e("install_ref", "error: "+e.getMessage());
		}

	}

}
