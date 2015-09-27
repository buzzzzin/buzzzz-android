package com.ig.crop;

class Log {
    private static final String TAG = "Crop";

    public static final void e(String exifOrientation, String msg) {
        android.util.Log.e(TAG, msg);
    }

    public static final void e(String msg, Throwable e) {
        android.util.Log.e(TAG, msg, e);
    }
}
