package in.buzzzz.utility;

import android.util.Log;

/**
 * Created by Navkrishna on September 25, 2015
 */
public final class Logger {
    private static final boolean DEBUG = true;
    private static final String TAG = "Buzzzz";

    public static void i(String tag, String message) {
        if (DEBUG) {
            Log.e(tag + "", "" + message);
        }
    }

    public static void i(String message) {
        if (DEBUG) {
            Log.e(TAG, "" + message);
        }
    }
}
