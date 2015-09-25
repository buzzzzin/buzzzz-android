package in.buzzzz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import in.buzzzz.R;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.SharedPreference;

/**
 * Created by Navkrishna on September 25, 2015
 */
public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPreference.getBoolean(mActivity, AppConstants.PREF_KEY_IS_LOGGED_IN)) {
                    startActivity(new Intent(mActivity, MainActivity.class));
                } else {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                }
            }
        }, 2000);
    }
}
