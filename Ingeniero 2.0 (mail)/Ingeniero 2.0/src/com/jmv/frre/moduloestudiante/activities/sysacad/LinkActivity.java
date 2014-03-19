package com.jmv.frre.moduloestudiante.activities.sysacad;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Function;
import com.jmv.frre.moduloestudiante.MainScreenActivity;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.dialogs.ConfirmationDialog;
import com.jmv.frre.moduloestudiante.net.HTMLParser;
import com.jmv.frre.moduloestudiante.net.HTTPScraper;
import com.jmv.frre.moduloestudiante.utils.FRReUtils;

/**
 * Created by Cleo on 12/10/13.
 */
public class LinkActivity extends Activity{

    public static final String CONTEXT = "context";

    public static final String LINK_EXTRA = "link_extra";

    protected static final int DIALOG_SHOW_ERROR = 0;
    protected static final int DIALOG_SHOW_ERROR_SYSACAD_NO_ANDA = 8;
    protected static final int DIALOG_SHOW_INFO = 1;
    protected static final int DIALOG_SHOW_ABOUT = 5;
    protected static final int DIALOG_SHOW_DETAILS_EXAM = 2;
    protected static final int DIALOG_SHOW_INSCRIBIRSE_EXAM = 3;
    protected static final int DIALOG_SHOW_INSCRIBIRSE_CONFIRM = 4;
    protected static final int DIALOG_SHOW_DELETE_CONFIRM = 7;
    protected static final int DIALOG_SHOW_DELETE_EXAM = 6;
    protected static final int DIALOG_SHOW_DELETE_EXAM_EMPTY = 9;
    

    protected View mHomeFormView;
    protected View mLoginStatusView;
    protected TextView mHomeStatusMessageView;

    protected HTTPScraper scraper;

    protected String currentError;

    protected Function<Activity,String> getRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scraper = new HTTPScraper();
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mHomeFormView.setVisibility(View.GONE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                            mHomeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mHomeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
        	case DIALOG_SHOW_ERROR_SYSACAD_NO_ANDA:
            return ConfirmationDialog.create(this, id,
                    R.string.dialog_tittle_label, currentError,
                    R.string.dialog_confirm_response_has_errors_button,
                    new Runnable() {
                        @Override
                        public void run() {
                        	MainScreenActivity.showHome(LinkActivity.this);
                        }
                    });
            case DIALOG_SHOW_ERROR:
                return ConfirmationDialog.create(this, id,
                        R.string.dialog_tittle_label, currentError,
                        R.string.dialog_confirm_response_has_errors_button,
                        new Runnable() {
                            @Override
                            public void run() {
                            	//MainActivity.showHome(LinkActivity.this);
                            }
                        });
            case DIALOG_SHOW_INFO:
                return ConfirmationDialog.create(this, id,
                        R.string.dialog_tittle_label, getString(R.string.dialog_confirm_nothing_to_show),
                        R.string.dialog_confirm_response_info_button,
                        new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
            case DIALOG_SHOW_ABOUT:
                return ConfirmationDialog.create(this, id,
                        R.string.dialog_tittle_label_about, getString(R.string.dialog_about_details),
                        R.string.dialog_confirm_response_info_button,
                        new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
        }

        return super.onCreateDialog(id);
    }

    protected Function<Activity, String> makeGetRequest(final String link) {
        return new Function<Activity, String>() {
            @Override
            public String apply(Activity context) {
                String response = scraper.fecthHtmlGet(link);

                return response;
            }
        };
    }

    protected boolean checkForErrors(HTMLParser parser, String response, Context parent) {
        if (FRReUtils.isEmpty(response) || parser.containsError()) {
            currentError = FRReUtils.isEmpty(response) ? "Empty Response" : parser.getError();
            showDialog(DIALOG_SHOW_ERROR);
            return true;
        } else  if (response.equalsIgnoreCase(HTTPScraper.MAINTENANCE_MODE_ON)){
			MaintenanceMode.showHome(parent);
			return true;
		}
        return false;
    }


}
