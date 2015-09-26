package in.buzzzz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import in.buzzzz.R;

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
                startActivity(new Intent(mActivity, ChatActivity.class));
            }
        }, 2000);
    }
}
