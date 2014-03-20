package com.jmv.frre.moduloestudiante.activity.setup;

import android.content.Context;
import android.graphics.Color;

import com.jmv.frre.moduloestudiante.Account;
import com.jmv.frre.moduloestudiante.NotificationSetting;
import com.jmv.frre.moduloestudiante.Preferences;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.activity.Accounts;
import com.jmv.frre.moduloestudiante.crypto.None;
import com.jmv.frre.moduloestudiante.helper.Utility;
import com.swacorp.oncallpager.MainActivity;

public class AccountSetupNamesSwat {

	public static void actionSetNames(Context context, Account mAccount) {
		// TODO Auto-generated method stub
		if (mAccount.getEmail().equals(
				context.getString(R.string.primary_account_email))) {
			mAccount.setDescription(context
					.getString(R.string.primary_account_description));
			mAccount.setName(context.getString(R.string.primary_account_name));
		} else if (mAccount.getEmail().equals(
				context.getString(R.string.secondary_account_email))) {
			mAccount.setDescription(context
					.getString(R.string.secondary_account_description));
			mAccount.setName(context.getString(R.string.secondary_account_name));
		} else if (mAccount.getEmail().equals(
				context.getString(R.string.l3_account_email))) {
			mAccount.setDescription(context
					.getString(R.string.l3_account_description));
			mAccount.setName(context.getString(R.string.l3_account_name));
		} else if (mAccount.getEmail().equals(
				context.getString(R.string.iem_account_email))) {
			mAccount.setDescription(context
					.getString(R.string.iem_account_description));
			mAccount.setName(context.getString(R.string.iem_account_name));
		} else if (mAccount.getEmail().equals(
				context.getString(R.string.lar_account_email))) {
			mAccount.setDescription(context
					.getString(R.string.lar_account_description));
			mAccount.setName(context.getString(R.string.lar_account_name));
		} else {
			mAccount.setDescription(context
					.getString(R.string.tsp_account_description));
			mAccount.setName(context.getString(R.string.tsp_account_name));
		}

		mAccount.setPushPollOnConnect(true);
		mAccount.setIdleRefreshMinutes(5);
		mAccount.setMaxPushFolders(1);
		mAccount.setDeletePolicy(0);
		mAccount.setEnabled(true);
		mAccount.setCryptoAutoEncrypt(false);
		mAccount.setCryptoApp(None.NAME);
		mAccount.setFolderSyncMode(Account.FolderMode.FIRST_CLASS);
		mAccount.setNotifyNewMail(true);
		mAccount.setShowOngoing(true);
		mAccount.setExpungePolicy(Account.EXPUNGE_MANUALLY);
		mAccount.setSyncRemoteDeletions(false);
		mAccount.setDeletePolicy(Account.DELETE_POLICY_NEVER);
		mAccount.setMarkMessageAsReadOnView(false);
		mAccount.setGoToUnreadMessageSearch(true);
		mAccount.setNotificationShowsUnreadCount(true);
		NotificationSetting n = mAccount.getNotificationSetting();
		n.setKeepPlaying(true);
		n.setLed(true);
		n.setLedColor(Color.RED);
		n.setVibrate(true);
		n.setVibratePattern(1);
		n.setVibrateTimes(3);
		n.setConnectivityTimesCheck(1);
		mAccount.setFolderPushMode(Account.FolderMode.FIRST_CLASS);
		mAccount.save(Preferences.getPreferences(context));
	}

}
