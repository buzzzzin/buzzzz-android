package in.buzzzz.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.buzzzz.R;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

/**
 * Created by Navkrishna on September 25, 2015
 */
public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView = (ImageView) findViewById(R.id.imageview_splash);

        Point point = Utility.getDisplayPoint(mActivity);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = (int) (point.x / 1.25);
        layoutParams.height = (int) (point.x / 1.25);
        imageView.setLayoutParams(layoutParams);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPreference.getBoolean(mActivity, AppConstants.PREF_KEY_IS_LOGGED_IN)) {
                    startActivity(new Intent(mActivity, InterestActivity.class));
                } else {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                }
                finish();
            }
        }, 2000);
    }
}
