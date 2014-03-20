package com.jmv.frre.moduloestudiante.helper;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.ClipboardManager;

/**
 * Access the system clipboard using the new {@link ClipboardManager} introduced with API 11
 */
@TargetApi(11)
public class ClipboardManagerApi11 extends com.jmv.frre.moduloestudiante.helper.ClipboardManager {

    public ClipboardManagerApi11(Context context) {
        super(context);
    }

    @Override
    public void setText(String label, String text) {
        ClipboardManager clipboardManager =
                (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboardManager.setPrimaryClip(clip);
    }
}
