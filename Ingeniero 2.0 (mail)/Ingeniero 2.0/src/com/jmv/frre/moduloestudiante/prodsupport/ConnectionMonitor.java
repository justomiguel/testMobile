package com.jmv.frre.moduloestudiante.prodsupport;

import com.jmv.frre.moduloestudiante.Account;
import com.jmv.frre.moduloestudiante.K9;
import com.jmv.frre.moduloestudiante.Preferences;
import com.jmv.frre.moduloestudiante.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

public class ConnectionMonitor extends BroadcastReceiver {
    
	private static final int NOTIFICACION_ID = 1;
    private Context context;
    private Intent intent;
    private boolean noConnectivity;
    private Account account;
    private NetworkInfo aNetworkInfo;
	
	@Override
	public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
		String action = intent.getAction();

		if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			return;
		}

		this.noConnectivity = intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		aNetworkInfo = (NetworkInfo) intent
				.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		
		NotificationManager notifMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

        account = Preferences.getPreferences(context).getDefaultAccount();
        boolean monitor = account!=null && account.isNotifyNewMail() && !K9.isQuietTime();
        if (monitor){
            if (!noConnectivity) {
	    		if ((aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
    					|| (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
				    // Handle connected case
				    notifMgr.cancel(NOTIFICACION_ID);
			    }
		    } else {
                Runnable mRunnable;
                Handler mHandler = new Handler();
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        verifyConnection();
                    }
                };
                mHandler.postDelayed(mRunnable, account.getNotificationSetting().getConnectivityTimesCheck() *60* 1000);
		    }
        }
	}

    private void verifyConnection(){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.aNetworkInfo = cm.getActiveNetworkInfo();
        this.noConnectivity = !(aNetworkInfo!=null&&aNetworkInfo.isConnectedOrConnecting());
        if(noConnectivity){
            if (aNetworkInfo==null||(aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                    || (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                // Handle disconnected case
                notifyConnectionStatus(context, false);
            }
        }
    }

	public void notifyConnectionStatus(Context context, boolean isConnectionUp) {
		// if(isConnected==isConnectionUp) return;
		// isConnected = isConnectionUp;
		String message;
		Notification notif;
		if (isConnectionUp) {
			message = context.getString(R.string.connection_up);
			notif = new Notification(R.drawable.stat_notify_connection_up,
					message, System.currentTimeMillis());
		} else {
			message = context.getString(R.string.connection_lost);
			notif = new Notification(R.drawable.stat_notify_connection_lost,
					message, System.currentTimeMillis());
		}
		NotificationManager notifMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		String accountNotice = context.getString(R.string.alert_header, 1, "");
		notif.setLatestEventInfo(context, accountNotice, message, null);

		Intent notificationIntent = new Intent(
				android.provider.Settings.ACTION_WIRELESS_SETTINGS);

		notif.contentIntent = PendingIntent.getActivity(context, 0,
		 notificationIntent, 0);

		long[] vibration = { 0, 5 };
		configureNotification(notif, account.getNotificationSetting().getRingtone(),
                vibration, 1, account.getNotificationSetting().getLedColor(), true);//K9.NOTIFICATION_LED_BLINK_SLOW, true);

                notifMgr.notify(NOTIFICACION_ID, notif);

	}

	private void configureNotification(final Notification notification,
			final String ringtone, final long[] vibrationPattern,
			final Integer ledColor, final int ledSpeed,

			final boolean ringAndVibrate) {

		// if it's quiet time, then we shouldn't be ringing, buzzing or flashing
//		if (K9.isQuietTime()) {
//			return;
//		}

		if (ringAndVibrate) {
			if (ringtone != null) {
				notification.sound = TextUtils.isEmpty(ringtone) ? null : Uri
						.parse(ringtone);
                notification.vibrate = vibrationPattern;
				//notification.audioStreamType = AudioManager.STREAM_NOTIFICATION;
			}
			if (vibrationPattern != null) {
				notification.vibrate = vibrationPattern;
			}
		}

        if (account.getNotificationSetting().shouldKeepPlaying()){
            notification.flags = Notification.FLAG_AUTO_CANCEL|Notification.FLAG_INSISTENT;
        } else {
            notification.flags = Notification.FLAG_AUTO_CANCEL;
        }

		if (ledColor != null) {
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			notification.ledARGB = ledColor;
			if (ledSpeed == K9.NOTIFICATION_LED_BLINK_SLOW) {
				notification.ledOnMS = K9.NOTIFICATION_LED_ON_TIME;
				notification.ledOffMS = K9.NOTIFICATION_LED_OFF_TIME;
			} else if (ledSpeed == K9.NOTIFICATION_LED_BLINK_FAST) {
				notification.ledOnMS = K9.NOTIFICATION_LED_FAST_ON_TIME;
				notification.ledOffMS = K9.NOTIFICATION_LED_FAST_OFF_TIME;
			}
		}
	}

}

