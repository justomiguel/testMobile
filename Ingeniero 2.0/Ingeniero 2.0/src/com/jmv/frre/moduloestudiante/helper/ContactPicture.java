package com.jmv.frre.moduloestudiante.helper;

import android.content.Context;
import android.util.TypedValue;

import com.jmv.frre.moduloestudiante.K9;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.activity.misc.ContactPictureLoader;

public class ContactPicture {

    public static ContactPictureLoader getContactPictureLoader(Context context) {
        final int defaultBgColor;
        if (!K9.isColorizeMissingContactPictures()) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.contactPictureFallbackDefaultBackgroundColor,
                    outValue, true);
            defaultBgColor = outValue.data;
        } else {
            defaultBgColor = 0;
        }

        return new ContactPictureLoader(context, defaultBgColor);
    }
}
