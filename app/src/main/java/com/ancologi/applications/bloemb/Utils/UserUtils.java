package com.ancologi.applications.bloemb.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;


import java.util.Locale;

public class UserUtils {
    private static final String LANGUAGE_SHARED_PREFERENCS = "LANGUAGE_SHARED_PREFS";
    private static final String APPLICATION_LANGUAGE = "APPLICATION_LANGUAGE";

    public static void setApplicationLanguage(@NonNull Context context, String localeCode) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(LANGUAGE_SHARED_PREFERENCS, 0);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(APPLICATION_LANGUAGE, localeCode.toLowerCase());
        mEditor.apply();
    }

    public static String getApplicationLanguage(@NonNull Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(LANGUAGE_SHARED_PREFERENCS, 0);
        return mSharedPreferences.getString(APPLICATION_LANGUAGE, Locale.getDefault().getLanguage().toLowerCase().split("-")[0]);
    }
}
