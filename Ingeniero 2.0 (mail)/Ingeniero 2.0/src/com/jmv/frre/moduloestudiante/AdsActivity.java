package com.jmv.frre.moduloestudiante;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;

public class AdsActivity extends Activity {

	/** The view to show the ad. */
	private AdView adView;

	/* Your ad unit id. Replace with your actual ad unit id. */
	private static final String AD_UNIT_ID = "ca-app-pub-8879245812439666/5618462436";

	public void addAdds(int id) {
		// Create an ad.
		// / the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			adView = new AdView(this);
			adView.setAdSize(AdSize.BANNER);
			adView.setAdUnitId(AD_UNIT_ID);

			// Add the AdView to the view hierarchy. The view will have no size
			// until the ad is loaded.
			LinearLayout layout = (LinearLayout) findViewById(id);
			layout.addView(adView);

			final TelephonyManager tm = (TelephonyManager) getBaseContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();

			// Create an ad request. Check logcat output for the hashed device
			// ID to
			// get test ads on a physical device.
			AdRequest adRequest = new AdRequest.Builder()
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
					.addTestDevice(deviceid).build();

			// Start loading the ad in the background.
			adView.loadAd(adRequest);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
	}

	@Override
	public void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() {
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

}
