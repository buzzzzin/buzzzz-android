package in.buzzzz.utility;

import android.util.Log;

/**
 * Created by Navkrishna on September 25, 2015
 */
public final class Logger {
    private static final boolean DEBUG = true;
    private static final String TAG = "Buzzzz";

    public static void i(String tag, Object message) {
        if (DEBUG) {
            Log.e(tag + "", "" + message);
        }
    }

    public static void i(Object message) {
        if (DEBUG) {
            Log.e(TAG, "" + message);
        }
    }

    public static void e(String tag, Object message, Throwable throwable) {
        if (DEBUG) {
            Log.e(tag, "" + message, throwable);
        }
    }
}
