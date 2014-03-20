
package com.jmv.frre.moduloestudiante.activity.setup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.*;
import android.util.Log;

import java.util.Map;

import com.jmv.frre.moduloestudiante.Account;
import com.jmv.frre.moduloestudiante.K9;
import com.jmv.frre.moduloestudiante.NotificationSetting;
import com.jmv.frre.moduloestudiante.Preferences;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.activity.ColorPickerDialog;
import com.jmv.frre.moduloestudiante.activity.K9PreferenceActivity;
import com.jmv.frre.moduloestudiante.mail.Store;
import com.jmv.frre.moduloestudiante.service.MailService;

import com.jmv.frre.moduloestudiante.mail.store.StorageManager;
import com.jmv.frre.moduloestudiante.preferences.TimePickerPreference;
import com.swacorp.oncallpager.MainActivity;


public class AccountSettings extends K9PreferenceActivity {
    private static final String EXTRA_ACCOUNT = "account";

    private static final String PREFERENCE_SCREEN_INCOMING = "incoming_prefs";

    private static final String PREFERENCE_FREQUENCY = "account_check_frequency";
    private static final String PREFERENCE_DISPLAY_COUNT = "account_display_count";
    private static final String PREFERENCE_SHOW_PICTURES = "show_pictures_enum";
    private static final String PREFERENCE_NOTIFY = "account_notify";
    private static final String PREFERENCE_NOTIFY_SELF = "account_notify_self";
    private static final String PREFERENCE_VIBRATE = "account_vibrate";
    private static final String PREFERENCE_VIBRATE_PATTERN = "account_vibrate_pattern";
    private static final String PREFERENCE_VIBRATE_TIMES = "account_vibrate_times";
    private static final String PREFERENCE_RINGTONE_TIMES = "account_ringtone_times";
    private static final String PREFERENCE_RINGTONE = "account_ringtone";
    private static final String PREFERENCE_NOTIFICATION_LED = "account_led";
    private static final String PREFERENCE_CHIP_COLOR = "chip_color";
    private static final String PREFERENCE_LED_COLOR = "led_color";
    private static final String PREFERENCE_MESSAGE_AGE = "account_message_age";
    private static final String PREFERENCE_MESSAGE_SIZE = "account_autodownload_size";
    
    
    private static final String PREFERENCE_LOCAL_STORAGE_PROVIDER = "local_storage_provider";

    private Account mAccount;
    private boolean mIsPushCapable = false;

    private ListPreference mCheckFrequency;
    private ListPreference mDisplayCount;
    private ListPreference mMessageAge;
    private ListPreference mMessageSize;
    private CheckBoxPreference mAccountNotify;
    private ListPreference mAccountShowPictures;
    private CheckBoxPreference mAccountVibrate;
    private CheckBoxPreference mAccountLed;
    private ListPreference mAccountVibratePattern;
    private ListPreference mAccountVibrateTimes;
    private ListPreference mAccountRingtoneTimes;
    private RingtonePreference mAccountRingtone;
    private Preference mChipColor;
    private Preference mLedColor;

    private ListPreference mLocalStorageProvider;

    public static void actionSettings(Context context, Account account) {
        Intent i = new Intent(context, AccountSettings.class);
        i.putExtra(EXTRA_ACCOUNT, account.getUuid());
        context.startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String accountUuid = getIntent().getStringExtra(EXTRA_ACCOUNT);
        mAccount = Preferences.getPreferences(this).getAccount(accountUuid);

        try {
            final Store store = mAccount.getRemoteStore();
            mIsPushCapable = store.isPushCapable();
        } catch (Exception e) {
            Log.e(K9.LOG_TAG, "Could not get remote store", e);
        }

        addPreferencesFromResource(R.xml.account_settings_preferences);

        mCheckFrequency = (ListPreference) findPreference(PREFERENCE_FREQUENCY);
        mCheckFrequency.setValue(String.valueOf(mAccount.getAutomaticCheckIntervalMinutes()));
        mCheckFrequency.setSummary(mCheckFrequency.getEntry());
        mCheckFrequency.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String summary = newValue.toString();
                int index = mCheckFrequency.findIndexOfValue(summary);
                mCheckFrequency.setSummary(mCheckFrequency.getEntries()[index]);
                mCheckFrequency.setValue(summary);
                saveSettings();
                return false;
            }
        });

        mDisplayCount = (ListPreference) findPreference(PREFERENCE_DISPLAY_COUNT);
        mDisplayCount.setValue(String.valueOf(mAccount.getDisplayCount()));
        mDisplayCount.setSummary(mDisplayCount.getEntry());
        mDisplayCount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String summary = newValue.toString();
                int index = mDisplayCount.findIndexOfValue(summary);
                mDisplayCount.setSummary(mDisplayCount.getEntries()[index]);
                mDisplayCount.setValue(summary);
                saveSettings();
                return false;
            }
        });

        mMessageAge = (ListPreference) findPreference(PREFERENCE_MESSAGE_AGE);

        if (!mAccount.isSearchByDateCapable()) {
            ((PreferenceScreen) findPreference(PREFERENCE_SCREEN_INCOMING)).removePreference(mMessageAge);
       } else {
	        mMessageAge.setValue(String.valueOf(mAccount.getMaximumPolledMessageAge()));
	        mMessageAge.setSummary(mMessageAge.getEntry());
	        mMessageAge.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
	            public boolean onPreferenceChange(Preference preference, Object newValue) {
	                final String summary = newValue.toString();
	                int index = mMessageAge.findIndexOfValue(summary);
	                mMessageAge.setSummary(mMessageAge.getEntries()[index]);
	                mMessageAge.setValue(summary);
	                saveSettings();
	                return false;
	            }
	        });
	
        }

        mMessageSize = (ListPreference) findPreference(PREFERENCE_MESSAGE_SIZE);
        mMessageSize.setValue(String.valueOf(mAccount.getMaximumAutoDownloadMessageSize()));
        mMessageSize.setSummary(mMessageSize.getEntry());
        mMessageSize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String summary = newValue.toString();
                int index = mMessageSize.findIndexOfValue(summary);
                mMessageSize.setSummary(mMessageSize.getEntries()[index]);
                mMessageSize.setValue(summary);
                saveSettings();
                return false;
            }
        });

        mAccountShowPictures = (ListPreference) findPreference(PREFERENCE_SHOW_PICTURES);
        mAccountShowPictures.setValue("" + mAccount.getShowPictures());
        mAccountShowPictures.setSummary(mAccountShowPictures.getEntry());
        mAccountShowPictures.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String summary = newValue.toString();
                int index = mAccountShowPictures.findIndexOfValue(summary);
                mAccountShowPictures.setSummary(mAccountShowPictures.getEntries()[index]);
                mAccountShowPictures.setValue(summary);
                saveSettings();
                return false;
            }
        });


        mLocalStorageProvider = (ListPreference) findPreference(PREFERENCE_LOCAL_STORAGE_PROVIDER);
        {
            final Map<String, String> providers;
            providers = StorageManager.getInstance(K9.app).getAvailableProviders();
            int i = 0;
            final String[] providerLabels = new String[providers.size()];
            final String[] providerIds = new String[providers.size()];
            for (final Map.Entry<String, String> entry : providers.entrySet()) {
                providerIds[i] = entry.getKey();
                providerLabels[i] = entry.getValue();
                i++;
            }
            mLocalStorageProvider.setEntryValues(providerIds);
            mLocalStorageProvider.setEntries(providerLabels);
            mLocalStorageProvider.setValue(mAccount.getLocalStorageProviderId());
            mLocalStorageProvider.setSummary(providers.get(mAccount.getLocalStorageProviderId()));

            mLocalStorageProvider.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    mLocalStorageProvider.setSummary(providers.get(newValue));
                    saveSettings();
                    return true;
                }
            });
        }


        mAccountNotify = (CheckBoxPreference) findPreference(PREFERENCE_NOTIFY);
        mAccountNotify.setChecked(mAccount.isNotifyNewMail());
        mAccountNotify.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
            	mAccountNotify.setChecked(Boolean.valueOf(String.valueOf(newValue)));
            	saveSettings();
                return false;
            }
        });

       
        mAccountRingtone = (RingtonePreference) findPreference(PREFERENCE_RINGTONE);

        // XXX: The following two lines act as a workaround for the RingtonePreference
        //      which does not let us set/get the value programmatically
        SharedPreferences prefs = mAccountRingtone.getPreferenceManager().getSharedPreferences();
        String currentRingtone = (!mAccount.getNotificationSetting().shouldRing() ? null : mAccount.getNotificationSetting().getRingtone());
        prefs.edit().putString(PREFERENCE_RINGTONE, currentRingtone).commit();

        mAccountVibrate = (CheckBoxPreference) findPreference(PREFERENCE_VIBRATE);
        mAccountVibrate.setChecked(mAccount.getNotificationSetting().shouldVibrate());
        mAccountVibrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
            	mAccountVibrate.setChecked(Boolean.valueOf(String.valueOf(newValue)));
                saveSettings();
                return false;
            }
        });
        
        mAccountVibratePattern = (ListPreference) findPreference(PREFERENCE_VIBRATE_PATTERN);
        mAccountVibratePattern.setValue(String.valueOf(mAccount.getNotificationSetting().getVibratePattern()));
        mAccountVibratePattern.setSummary(mAccountVibratePattern.getEntry());
        mAccountVibratePattern.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String summary = newValue.toString();
                int index = mAccountVibratePattern.findIndexOfValue(summary);
                mAccountVibratePattern.setSummary(mAccountVibratePattern.getEntries()[index]);
                mAccountVibratePattern.setValue(summary);
                doVibrateTest(preference);
                saveSettings();
                return false;
            }
        });

        mAccountVibrateTimes = (ListPreference) findPreference(PREFERENCE_VIBRATE_TIMES);
        mAccountVibrateTimes.setValue(String.valueOf(mAccount.getNotificationSetting().getVibrateTimes()));
        mAccountVibrateTimes.setSummary(String.valueOf(mAccount.getNotificationSetting().getVibrateTimes()));
        mAccountVibrateTimes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String value = newValue.toString();
                mAccountVibrateTimes.setSummary(value);
                mAccountVibrateTimes.setValue(value);
                doVibrateTest(preference);
                saveSettings();
                return false;
            }
        });
        

        mAccountRingtoneTimes = (ListPreference) findPreference(PREFERENCE_RINGTONE_TIMES);
        mAccountRingtoneTimes.setValue(mAccount.getNotificationSetting().shouldKeepPlaying()?"Always":"1");
        mAccountRingtoneTimes.setSummary(mAccount.getNotificationSetting().shouldKeepPlaying()?"Always":"1");
        mAccountRingtoneTimes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //final boolean value = String.valueOf(newValue).equalsIgnoreCase("Always")?true:false;
                mAccountRingtoneTimes.setSummary(String.valueOf(newValue));
                mAccountRingtoneTimes.setValue(String.valueOf(newValue));
                saveSettings();
                return false;
            }
        });

        mAccountLed = (CheckBoxPreference) findPreference(PREFERENCE_NOTIFICATION_LED);
        mAccountLed.setChecked(mAccount.getNotificationSetting().isLed());
        mAccountLed.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
            	mAccountLed.setChecked(Boolean.valueOf(String.valueOf(newValue)));
            	saveSettings();
                return false;
            }
        });

        mChipColor = findPreference(PREFERENCE_CHIP_COLOR);
        mChipColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                onChooseChipColor();
                saveSettings();
                return false;
            }
        });

        mLedColor = findPreference(PREFERENCE_LED_COLOR);
        mLedColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                onChooseLedColor();
                saveSettings();
                return false;
            }
        });
        

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void saveSettings() {

        mAccount.setNotifyNewMail(mAccountNotify.isChecked());
       
        mAccount.setDisplayCount(Integer.parseInt(mDisplayCount.getValue()));
        mAccount.setMaximumAutoDownloadMessageSize(Integer.parseInt(mMessageSize.getValue()));
        if (mAccount.isSearchByDateCapable()) {
            mAccount.setMaximumPolledMessageAge(Integer.parseInt(mMessageAge.getValue()));
        }
        mAccount.getNotificationSetting().setVibrate(mAccountVibrate.isChecked());
        mAccount.getNotificationSetting().setVibratePattern(Integer.parseInt(mAccountVibratePattern.getValue()));
        mAccount.getNotificationSetting().setVibrateTimes(Integer.parseInt(mAccountVibrateTimes.getValue()));
        
        final boolean value = String.valueOf(mAccountRingtoneTimes.getValue()).equalsIgnoreCase("Always")?true:false;
        mAccount.getNotificationSetting().setKeepPlaying(value);

        mAccount.getNotificationSetting().setLed(mAccountLed.isChecked());


        boolean needsRefresh = mAccount.setAutomaticCheckIntervalMinutes(Integer.parseInt(mCheckFrequency.getValue()));

        SharedPreferences prefs = mAccountRingtone.getPreferenceManager().getSharedPreferences();
        String newRingtone = prefs.getString(PREFERENCE_RINGTONE, null);
        if (newRingtone != null) {
            mAccount.getNotificationSetting().setRing(true);
            mAccount.getNotificationSetting().setRingtone(newRingtone);
        } else {
            if (mAccount.getNotificationSetting().shouldRing()) {
                mAccount.getNotificationSetting().setRingtone(null);
            }
        }

        mAccount.setShowPictures(Account.ShowPictures.valueOf(mAccountShowPictures.getValue()));
        mAccount.save(Preferences.getPreferences(this));

	    if (mIsPushCapable) {
	        if (needsRefresh) {
	            MailService.actionReschedulePoll(this, null);
	        }
	    }
	    
	    SharedPreferences preferences = Preferences.getPreferences(this).getPreferences();
        Editor editor = preferences.edit();
        K9.save(editor);
        editor.commit();
        // TODO: refresh folder list here
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        saveSettings();
        MainActivity.showHome(this);
    }

    public void onChooseChipColor() {
        new ColorPickerDialog(this, new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                mAccount.setChipColor(color);
            }
        },
        mAccount.getChipColor()).show();
    }

    public void onChooseLedColor() {
        new ColorPickerDialog(this, new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                mAccount.getNotificationSetting().setLedColor(color);
            }
        },
        mAccount.getNotificationSetting().getLedColor()).show();
    }


    private void doVibrateTest(Preference preference) {
        // Do the vibration to show the user what it's like.
        Vibrator vibrate = (Vibrator)preference.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrate.vibrate(NotificationSetting.getVibration(
                            Integer.parseInt(mAccountVibratePattern.getValue()),
                            Integer.parseInt(mAccountVibrateTimes.getValue())), -1);
    }
}
