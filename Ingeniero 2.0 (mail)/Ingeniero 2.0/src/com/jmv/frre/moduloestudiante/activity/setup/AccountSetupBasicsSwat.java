
package com.jmv.frre.moduloestudiante.activity.setup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.jmv.frre.moduloestudiante.*;
import com.jmv.frre.moduloestudiante.activity.Accounts;
import com.jmv.frre.moduloestudiante.activity.K9Activity;
import com.jmv.frre.moduloestudiante.helper.Utility;
import com.swacorp.oncallpager.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Prompts the user for the email address and password. Also prompts for
 * "Use this account as default" if this is the 2nd+ account being set up.
 * Attempts to lookup default settings for the domain the user specified. If the
 * domain is known the settings are handed off to the AccountSetupCheckSettings
 * activity. If no settings are found the settings are handed off to the
 * AccountSetupAccountType activity.
 */
public class AccountSetupBasicsSwat extends K9Activity
    implements OnClickListener, TextWatcher {
    private final static String EXTRA_ACCOUNT = "com.jmv.frre.moduloestudiante.AccountSetupBasics.account";
    private final static int DIALOG_NOTE = 1;
    private final static String STATE_KEY_PROVIDER =
        "com.jmv.frre.moduloestudiante.AccountSetupBasics.provider";

    private Preferences mPrefs;
    private EditText mEmailView;
    private EditText mPasswordView;
    private CheckBox mDefaultView;
    private Button mNextButton;
    private Button mManualSetupButton;
    private Account mAccount;
    private Provider mProvider;
    private RadioButton mPrimaryRadioButton;
    private RadioButton mSecondaryRadioButton;
    private RadioButton mL3RadioButton;

    private EmailAddressValidator mEmailValidator = new EmailAddressValidator();
	private RadioButton mDebugRadioButton;

    public static void actionNewAccount(Context context) {
        Intent i = new Intent(context, AccountSetupBasicsSwat.class);
        context.startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setup_basics_swat);
        mPrefs = Preferences.getPreferences(this);
        mEmailView = (EditText)findViewById(R.id.account_email);
        mPasswordView = (EditText)findViewById(R.id.account_password);
        mDefaultView = (CheckBox)findViewById(R.id.account_default);
        mNextButton = (Button)findViewById(R.id.next);
        mManualSetupButton = (Button)findViewById(R.id.manual_setup);
        mPrimaryRadioButton = (RadioButton)findViewById(R.id.option_primary);
        mSecondaryRadioButton = (RadioButton)findViewById(R.id.option_secondary);
        mL3RadioButton = (RadioButton)findViewById(R.id.option_l3);

        mDebugRadioButton = (RadioButton)findViewById(R.id.debug_email_option);
        if (MainActivity.DEBUG){
        	mDebugRadioButton.setVisibility(View.VISIBLE);
        }
        
        mNextButton.setOnClickListener(this);
        mManualSetupButton.setOnClickListener(this);
        mPrimaryRadioButton.setOnClickListener(this);
        mSecondaryRadioButton.setOnClickListener(this);
        mL3RadioButton.setOnClickListener(this);
        mDebugRadioButton.setOnClickListener(this);
        
        mEmailView.addTextChangedListener(this);
        mPasswordView.addTextChangedListener(this);

        if (mPrefs.getAccounts().length > 0) {
            //mDefaultView.setVisibility(View.VISIBLE);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_ACCOUNT)) {
            String accountUuid = savedInstanceState.getString(EXTRA_ACCOUNT);
            mAccount = Preferences.getPreferences(this).getAccount(accountUuid);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_KEY_PROVIDER)) {
            mProvider = (Provider)savedInstanceState.getSerializable(STATE_KEY_PROVIDER);
        }        
    }
    
    @Override
    public void onBackPressed() {
    	MainActivity.showHome(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        validateFields();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAccount != null) {
            outState.putString(EXTRA_ACCOUNT, mAccount.getUuid());
        }
        if (mProvider != null) {
            outState.putSerializable(STATE_KEY_PROVIDER, mProvider);
        }
    }

    public void afterTextChanged(Editable s) {
        validateFields();
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    private void validateFields() {
        String email = mEmailView.getText().toString();
        boolean valid = Utility.requiredFieldValid(mEmailView)
                        && Utility.requiredFieldValid(mPasswordView)
                        && mEmailValidator.isValidAddressOnly(email);

        mNextButton.setEnabled(valid);
        mManualSetupButton.setEnabled(valid);
        /*
         * Dim the next button's icon to 50% if the button is disabled.
         * TODO this can probably be done with a stateful drawable. Check into it.
         * android:state_enabled
         */
        Utility.setCompoundDrawablesAlpha(mNextButton, mNextButton.isEnabled() ? 255 : 128);
    }

    private String getOwnerName() {
        String name = null;
        try {
            name = getDefaultAccountName();
        } catch (Exception e) {
            Log.e(K9.LOG_TAG, "Could not get default account name", e);
        }

        if (name == null) {
            name = "";
        }
        return name;
    }

    private String getDefaultAccountName() {
        String name = null;
        Account account = Preferences.getPreferences(this).getDefaultAccount();
        if (account != null) {
            name = account.getName();
        }
        return name;
    }

    @Override
    public Dialog onCreateDialog(int id) {
        if (id == DIALOG_NOTE) {
            if (mProvider != null && mProvider.note != null) {
                return new AlertDialog.Builder(this)
                       .setMessage(mProvider.note)
                       .setPositiveButton(
                           getString(R.string.okay_action),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finishAutoSetup();
                    }
                })
                       .setNegativeButton(
                           getString(R.string.cancel_action),
                           null)
                       .create();
            }
        }
        return null;
    }

    private void finishAutoSetup() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String[] emailParts = splitEmail(email);
        String user = emailParts[0];
        String domain = emailParts[1];
        URI incomingUri = null;
        URI outgoingUri = null;
        try {
            String userEnc = URLEncoder.encode(user, "UTF-8");
            String passwordEnc = URLEncoder.encode(password, "UTF-8");

            String incomingUsername = mProvider.incomingUsernameTemplate;
            incomingUsername = incomingUsername.replaceAll("\\$email", email);
            incomingUsername = incomingUsername.replaceAll("\\$user", userEnc);
            incomingUsername = incomingUsername.replaceAll("\\$domain", domain);

            URI incomingUriTemplate = mProvider.incomingUriTemplate;
            incomingUri = new URI(incomingUriTemplate.getScheme(), incomingUsername + ":"
                                  + passwordEnc, incomingUriTemplate.getHost(), incomingUriTemplate.getPort(), null,
                                  null, null);

            String outgoingUsername = mProvider.outgoingUsernameTemplate;

            URI outgoingUriTemplate = mProvider.outgoingUriTemplate;


            if (outgoingUsername != null) {
                outgoingUsername = outgoingUsername.replaceAll("\\$email", email);
                outgoingUsername = outgoingUsername.replaceAll("\\$user", userEnc);
                outgoingUsername = outgoingUsername.replaceAll("\\$domain", domain);
                outgoingUri = new URI(outgoingUriTemplate.getScheme(), outgoingUsername + ":"
                                      + passwordEnc, outgoingUriTemplate.getHost(), outgoingUriTemplate.getPort(), null,
                                      null, null);

            } else {
                outgoingUri = new URI(outgoingUriTemplate.getScheme(),
                                      null, outgoingUriTemplate.getHost(), outgoingUriTemplate.getPort(), null,
                                      null, null);


            }
            //si existe una cuenta la borro. No puedo tener dos cuentas
//            Account account = Preferences.getPreferences(this).getDefaultAccount();
//            if (account != null){
//            	Preferences.getPreferences(AccountSetupBasicsSwat.this).deleteAccount(account);
//            }
            mAccount = Preferences.getPreferences(this).newAccount();
            mAccount.setName(getOwnerName());
            mAccount.setEmail(email);
            mAccount.setStoreUri(incomingUri.toString());
            mAccount.setTransportUri(outgoingUri.toString());
            mAccount.setDraftsFolderName(getString(R.string.special_mailbox_name_drafts));
            mAccount.setTrashFolderName(getString(R.string.special_mailbox_name_trash));
            mAccount.setArchiveFolderName(getString(R.string.special_mailbox_name_archive));
            // Yahoo! has a special folder for Spam, called "Bulk Mail".
            if (incomingUriTemplate.getHost().toLowerCase().endsWith(".yahoo.com")) {
                mAccount.setSpamFolderName("Bulk Mail");
            } else {
                mAccount.setSpamFolderName(getString(R.string.special_mailbox_name_spam));
            }
            mAccount.setSentFolderName(getString(R.string.special_mailbox_name_sent));
            AccountSetupCheckSettings.actionCheckSettings(this, mAccount, true, false);
        } catch (UnsupportedEncodingException enc) {
            // This really shouldn't happen since the encoding is hardcoded to UTF-8
            Log.e(K9.LOG_TAG, "Couldn't urlencode username or password.", enc);
        } catch (URISyntaxException use) {
            /*
             * If there is some problem with the URI we give up and go on to
             * manual setup.
             */
            onManualSetup();
        }
    }

    protected void onNext() {
        String email = mEmailView.getText().toString();
        String[] emailParts = splitEmail(email);
        String domain = emailParts[1];
        mProvider = findProviderForDomain(domain);
        if (mProvider == null) {
            /*
             * We don't have default settings for this account, start the manual
             * setup process.
             */
            onManualSetup();
            return;
        }

        if (mProvider.note != null) {
            showDialog(DIALOG_NOTE);
        } else {
            finishAutoSetup();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mAccount.setDescription(mAccount.getEmail());
            mAccount.save(Preferences.getPreferences(this));
            if (mDefaultView.isChecked()) {
                Preferences.getPreferences(this).setDefaultAccount(mAccount);
            }
            K9.setServicesEnabled(this);
            //AccountSetupNames.actionSetNames(this, mAccount);
            AccountSetupNamesSwat.actionSetNames(this,mAccount);
            MainActivity.showHome(this);
            finish();
        }
    }

    private void onManualSetup() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String[] emailParts = splitEmail(email);
        String user = emailParts[0];
        String domain = emailParts[1];

        mAccount = Preferences.getPreferences(this).newAccount();
        mAccount.setName(getOwnerName());
        mAccount.setEmail(email);
        try {
            String userEnc = URLEncoder.encode(user, "UTF-8");
            String passwordEnc = URLEncoder.encode(password, "UTF-8");

            URI uri = new URI("placeholder", userEnc + ":" + passwordEnc, "mail." + domain, -1, null,
                              null, null);
            mAccount.setStoreUri(uri.toString());
            mAccount.setTransportUri(uri.toString());
        } catch (UnsupportedEncodingException enc) {
            // This really shouldn't happen since the encoding is hardcoded to UTF-8
            Log.e(K9.LOG_TAG, "Couldn't urlencode username or password.", enc);
        } catch (URISyntaxException use) {
            /*
             * If we can't set up the URL we just continue. It's only for
             * convenience.
             */
        }
        mAccount.setDraftsFolderName(getString(R.string.special_mailbox_name_drafts));
        mAccount.setTrashFolderName(getString(R.string.special_mailbox_name_trash));
        mAccount.setSentFolderName(getString(R.string.special_mailbox_name_sent));

        AccountSetupAccountType.actionSelectAccountType(this, mAccount, mDefaultView.isChecked());
        finish();
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.next:
            onNext();
            break;
        case R.id.manual_setup:
            onManualSetup();
            break;
        case R.id.option_primary:
        	mEmailView.setText(getString(R.string.primary_account_email));
    		mPasswordView.setText(getString(R.string.primary_account_password));
    		break;
        case R.id.option_secondary:
        	mEmailView.setText(getString(R.string.secondary_account_email));
    		mPasswordView.setText(getString(R.string.secondary_account_password));
        	break;
        case R.id.option_l3:
        	mEmailView.setText(getString(R.string.l3_account_email));
    		mPasswordView.setText(getString(R.string.l3_account_password));
        	break;	
        case R.id.debug_email_option:
        	mEmailView.setText(MainActivity.MAIN_DEBUG_EMAIL);
    		mPasswordView.setText(MainActivity.MAIN_DEBUG_EMAIL_PASS);
        	break;	
        }
    }
    
    /**
     * Attempts to get the given attribute as a String resource first, and if it fails
     * returns the attribute as a simple String value.
     * @param xml
     * @param name
     * @return
     */
    private String getXmlAttribute(XmlResourceParser xml, String name) {
        int resId = xml.getAttributeResourceValue(null, name, 0);
        if (resId == 0) {
            return xml.getAttributeValue(null, name);
        } else {
            return getString(resId);
        }
    }

    private Provider findProviderForDomain(String domain) {
        try {
            XmlResourceParser xml = getResources().getXml(R.xml.providers);
            int xmlEventType;
            Provider provider = null;
            while ((xmlEventType = xml.next()) != XmlResourceParser.END_DOCUMENT) {
                if (xmlEventType == XmlResourceParser.START_TAG
                        && "provider".equals(xml.getName())
                        && domain.equalsIgnoreCase(getXmlAttribute(xml, "domain"))) {
                    provider = new Provider();
                    provider.id = getXmlAttribute(xml, "id");
                    provider.label = getXmlAttribute(xml, "label");
                    provider.domain = getXmlAttribute(xml, "domain");
                    provider.note = getXmlAttribute(xml, "note");
                } else if (xmlEventType == XmlResourceParser.START_TAG
                           && "incoming".equals(xml.getName())
                           && provider != null) {
                    provider.incomingUriTemplate = new URI(getXmlAttribute(xml, "uri"));
                    provider.incomingUsernameTemplate = getXmlAttribute(xml, "username");
                } else if (xmlEventType == XmlResourceParser.START_TAG
                           && "outgoing".equals(xml.getName())
                           && provider != null) {
                    provider.outgoingUriTemplate = new URI(getXmlAttribute(xml, "uri"));
                    provider.outgoingUsernameTemplate = getXmlAttribute(xml, "username");
                } else if (xmlEventType == XmlResourceParser.END_TAG
                           && "provider".equals(xml.getName())
                           && provider != null) {
                    return provider;
                }
            }
        } catch (Exception e) {
            Log.e(K9.LOG_TAG, "Error while trying to load provider settings.", e);
        }
        return null;
    }

    private String[] splitEmail(String email) {
        String[] retParts = new String[2];
        String[] emailParts = email.split("@");
        retParts[0] = (emailParts.length > 0) ? emailParts[0] : "";
        retParts[1] = (emailParts.length > 1) ? emailParts[1] : "";
        return retParts;
    }

    static class Provider implements Serializable {
        private static final long serialVersionUID = 8511656164616538989L;

        public String id;

        public String label;

        public String domain;

        public URI incomingUriTemplate;

        public String incomingUsernameTemplate;

        public URI outgoingUriTemplate;

        public String outgoingUsernameTemplate;

        public String note;
    }
}
