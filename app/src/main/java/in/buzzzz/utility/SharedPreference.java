package in.buzzzz.utility;

import android.content.Context;
import android.content.SharedPreferences;

import in.buzzzz.model.Login;

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

    public static void setLong(Context context, String key, long value) {
        getPref(context).edit().putLong(key, value).commit();
    }

    public static long getLong(Context context, String key) {
        return getPref(context).getLong(key, 0);
    }

    public static void clearLoggedInInfo(Context context) {
        getPref(context).edit().clear().commit();
    }

    public static void saveLoginInfo(Context context, Login login) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putBoolean(AppConstants.PREF_KEY_HAS_INTERESTS, login.hasInterests());
        editor.putString(AppConstants.PREF_KEY_AUTH_TOKEN, login.getAuthToken());
        editor.putString(AppConstants.PREF_KEY_USER_ID, login.getId());
        editor.putString(AppConstants.PREF_KEY_USER_NAME, login.getName());
        editor.putString(AppConstants.PREF_KEY_GENDER, login.getGender());
        editor.putString(AppConstants.PREF_KEY_EMAIL, login.getEmail());
        editor.putString(AppConstants.PREF_KEY_MEDIUM_TYPE, login.getMediumType());
        editor.putString(AppConstants.PREF_KEY_MEDIUM_ID, login.getMediumId());
        editor.putBoolean(AppConstants.PREF_KEY_IS_LOGGED_IN, true);
        editor.commit();
    }
}