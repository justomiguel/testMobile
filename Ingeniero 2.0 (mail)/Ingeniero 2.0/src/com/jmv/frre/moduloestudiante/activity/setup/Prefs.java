package com.jmv.frre.moduloestudiante.activity.setup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.widget.Toast;

import com.jmv.frre.moduloestudiante.K9;
import com.jmv.frre.moduloestudiante.K9.NotificationHideSubject;
import com.jmv.frre.moduloestudiante.K9.NotificationQuickDelete;
import com.jmv.frre.moduloestudiante.K9.SplitViewMode;
import com.jmv.frre.moduloestudiante.Preferences;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.activity.ColorPickerDialog;
import com.jmv.frre.moduloestudiante.activity.K9PreferenceActivity;
import com.jmv.frre.moduloestudiante.controller.MessagingController;
import com.jmv.frre.moduloestudiante.helper.FileBrowserHelper;
import com.jmv.frre.moduloestudiante.helper.FileBrowserHelper.FileBrowserFailOverCallback;
import com.jmv.frre.moduloestudiante.preferences.CheckBoxListPreference;
import com.jmv.frre.moduloestudiante.preferences.TimePickerPreference;

import com.jmv.frre.moduloestudiante.service.MailService;
import com.jmv.frre.moduloestudiante.view.MessageWebView;


public class Prefs extends K9PreferenceActivity {

    /**
     * Immutable empty {@link CharSequence} array
     */
    private static final CharSequence[] EMPTY_CHAR_SEQUENCE_ARRAY = new CharSequence[0];

    /*
     * Keys of the preferences defined in res/xml/global_preferences.xml
     */
    private static final String PREFERENCE_THEME = "theme";
    private static final String PREFERENCE_MESSAGE_VIEW_THEME = "messageViewTheme";
    private static final String PREFERENCE_FIXED_MESSAGE_THEME = "fixedMessageViewTheme";
    private static final String PREFERENCE_COMPOSER_THEME = "messageComposeTheme";
    private static final String PREFERENCE_FONT_SIZE = "font_size";
    private static final String PREFERENCE_ANIMATIONS = "animations";
    private static final String PREFERENCE_CONFIRM_ACTIONS = "confirm_actions";
    private static final String PREFERENCE_NOTIFICATION_HIDE_SUBJECT = "notification_hide_subject";
    private static final String PREFERENCE_MESSAGELIST_PREVIEW_LINES = "messagelist_preview_lines";
    private static final String PREFERENCE_MESSAGELIST_STARS = "messagelist_stars";   

    private static final String PREFERENCE_SPLITVIEW_MODE = "splitview_mode";

    private static final int ACTIVITY_CHOOSE_FOLDER = 1;

    private ListPreference mTheme;
    private CheckBoxPreference mFixedMessageTheme;
    private ListPreference mMessageTheme;
    private ListPreference mComposerTheme;
    private CheckBoxPreference mAnimations;
    private CheckBoxListPreference mConfirmActions;
    private ListPreference mNotificationHideSubject;
    private ListPreference mPreviewLines;
    private CheckBoxPreference mStars;

    private ListPreference mSplitViewMode;


    public static void actionPrefs(Context context) {
        Intent i = new Intent(context, Prefs.class);
        context.startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.global_preferences);

        mTheme = setupListPreference(PREFERENCE_THEME, themeIdToName(K9.getK9Theme()));
        mFixedMessageTheme = (CheckBoxPreference) findPreference(PREFERENCE_FIXED_MESSAGE_THEME);
        mFixedMessageTheme.setChecked(K9.useFixedMessageViewTheme());
        mMessageTheme = setupListPreference(PREFERENCE_MESSAGE_VIEW_THEME,
                themeIdToName(K9.getK9MessageViewThemeSetting()));
        mComposerTheme = setupListPreference(PREFERENCE_COMPOSER_THEME,
                themeIdToName(K9.getK9ComposerThemeSetting()));

        findPreference(PREFERENCE_FONT_SIZE).setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                onFontSizeSettings();
                return true;
            }
        });

        mAnimations = (CheckBoxPreference)findPreference(PREFERENCE_ANIMATIONS);
        mAnimations.setChecked(K9.showAnimations());

        mConfirmActions = (CheckBoxListPreference) findPreference(PREFERENCE_CONFIRM_ACTIONS);

        boolean canDeleteFromNotification = MessagingController.platformSupportsExtendedNotifications();
        CharSequence[] confirmActionEntries = new CharSequence[canDeleteFromNotification ? 4 : 3];
        boolean[] confirmActionValues = new boolean[canDeleteFromNotification ? 4 : 3];
        int index = 0;

        confirmActionEntries[index] = getString(R.string.global_settings_confirm_action_delete);
        confirmActionValues[index++] = K9.confirmDelete();
        confirmActionEntries[index] = getString(R.string.global_settings_confirm_action_delete_starred);
        confirmActionValues[index++] = K9.confirmDeleteStarred();
        if (canDeleteFromNotification) {
            confirmActionEntries[index] = getString(R.string.global_settings_confirm_action_delete_notif);
            confirmActionValues[index++] = K9.confirmDeleteFromNotification();
        }
        confirmActionEntries[index] = getString(R.string.global_settings_confirm_action_spam);
        confirmActionValues[index++] = K9.confirmSpam();

        mConfirmActions.setItems(confirmActionEntries);
        mConfirmActions.setCheckedItems(confirmActionValues);

        mNotificationHideSubject = setupListPreference(PREFERENCE_NOTIFICATION_HIDE_SUBJECT,
                K9.getNotificationHideSubject().toString());

        mPreviewLines = setupListPreference(PREFERENCE_MESSAGELIST_PREVIEW_LINES,
                                            Integer.toString(K9.messageListPreviewLines()));

        mStars = (CheckBoxPreference)findPreference(PREFERENCE_MESSAGELIST_STARS);
        mStars.setChecked(K9.messageListStars());

        mSplitViewMode = (ListPreference) findPreference(PREFERENCE_SPLITVIEW_MODE);
        initListPreference(mSplitViewMode, K9.getSplitViewMode().name(),
                mSplitViewMode.getEntries(), mSplitViewMode.getEntryValues());
    }

    private static String themeIdToName(K9.Theme theme) {
        switch (theme) {
            case DARK: return "dark";
            case USE_GLOBAL: return "global";
            default: return "light";
        }
    }

    private static K9.Theme themeNameToId(String theme) {
        if (TextUtils.equals(theme, "dark")) {
            return K9.Theme.DARK;
        } else if (TextUtils.equals(theme, "global")) {
            return K9.Theme.USE_GLOBAL;
        } else {
            return K9.Theme.LIGHT;
        }
    }

    private void saveSettings() {
        SharedPreferences preferences = Preferences.getPreferences(this).getPreferences();

        K9.setK9Theme(themeNameToId(mTheme.getValue()));
        K9.setUseFixedMessageViewTheme(mFixedMessageTheme.isChecked());
        K9.setK9MessageViewThemeSetting(themeNameToId(mMessageTheme.getValue()));
        K9.setK9ComposerThemeSetting(themeNameToId(mComposerTheme.getValue()));

        K9.setAnimations(mAnimations.isChecked());
        K9.setNotificationHideSubject(NotificationHideSubject.valueOf(mNotificationHideSubject.getValue()));

        int index = 0;
        K9.setConfirmDelete(mConfirmActions.getCheckedItems()[index++]);
        K9.setConfirmDeleteStarred(mConfirmActions.getCheckedItems()[index++]);
        if (MessagingController.platformSupportsExtendedNotifications()) {
            K9.setConfirmDeleteFromNotification(mConfirmActions.getCheckedItems()[index++]);
        }
        K9.setConfirmSpam(mConfirmActions.getCheckedItems()[index++]);

        K9.setMessageListPreviewLines(Integer.parseInt(mPreviewLines.getValue()));
        K9.setMessageListStars(mStars.isChecked());
        K9.setThreadedViewEnabled(false);
        
        K9.setMobileOptimizedLayout(true);

        K9.setSplitViewMode(SplitViewMode.valueOf(mSplitViewMode.getValue()));

        Editor editor = preferences.edit();
        K9.save(editor);
        editor.commit();
    }

    @Override
    protected void onPause() {
        saveSettings();
        super.onPause();
    }

    private void onFontSizeSettings() {
        FontSizeSettings.actionEditSettings(this);
    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case ACTIVITY_CHOOSE_FOLDER:
            if (resultCode == RESULT_OK && data != null) {
                // obtain the filename
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    String filePath = fileUri.getPath();
                    if (filePath != null) {
                        K9.setAttachmentDefaultPath(filePath.toString());
                    }
                }
            }
            break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
