package in.buzzzz.utility;

import android.content.Context;

/**
 * Created by Navkrishna on September 25, 2015
 */
public final class SharedPreference {

    private static final String APP_PREF = "buzzzz";

    private static android.content.SharedPreferences getPref(Context context) {
        return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
    }

    public static void setString(Context context, String key, String value) {
        getPref(context).edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key) {
        return getPref(context).getString(key, "");
    }

    public static void setBoolean(Context context, String key, boolean value) {
        getPref(context).edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key) {
        return getPref(context).getBoolean(key, false);
    }

    public static void clearLoggedInInfo(Context context) {

    }
}