package in.buzzzz.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Navkrishna on September 25, 2015
 */
public final class Utility {

    public Utility(Context context) {

    }

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
    }
}
