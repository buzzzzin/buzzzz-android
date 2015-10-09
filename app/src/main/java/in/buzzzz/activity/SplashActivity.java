package in.buzzzz.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import in.buzzzz.R;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

/**
 * Created by Navkrishna on September 25, 2015
 */
public class SplashActivity extends BaseActivity {

    private EditText editTextApiUrl;
    private EditText editTextChatUrl;
    private Button buttonNext;
    private ImageView imageViewBuzzzz;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        linkViewId();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!SharedPreference.getBoolean(mActivity, AppConstants.PREF_KEY_IS_LOGGED_IN)) {
//
//                    startActivity(new Intent(mActivity, LoginActivity.class));
//                } else if (!SharedPreference.getBoolean(mActivity, AppConstants.PREF_KEY_HAS_INTERESTS)) {
//                    startActivity(new Intent(mActivity, InterestActivity.class));
//                } else {
//                    startActivity(new Intent(mActivity, HomeScreenActivity.class));
//                }
//                finish();
//            }
//        }, 2000);
    }

    private void linkViewId() {
        editTextApiUrl = (EditText) findViewById(R.id.edittext_api_url);
        editTextChatUrl = (EditText) findViewById(R.id.edittext_chat_url);
        buttonNext = (Button) findViewById(R.id.button_next);
        imageViewBuzzzz = (ImageView) findViewById(R.id.imageview_splash);

        Point point = Utility.getDisplayPoint(mActivity);
        ViewGroup.LayoutParams layoutParams = imageViewBuzzzz.getLayoutParams();
        layoutParams.width = (int) (point.x / 1.25);
        layoutParams.height = (int) (point.x / 1.25);
        imageViewBuzzzz.setLayoutParams(layoutParams);

        imageViewBuzzzz.setOnClickListener(mOnClickListener);

        buttonNext.setOnClickListener(mOnClickListener);

        String apiUrl = SharedPreference.getString(mActivity, AppConstants.PREF_KEY_URL_API);
        if (!apiUrl.isEmpty()) {
            editTextApiUrl.setText(apiUrl);
        }

        String chatUrl = SharedPreference.getString(mActivity, AppConstants.PREF_KEY_URL_CHAT);
        if (!chatUrl.isEmpty()) {
            editTextChatUrl.setText(chatUrl);
        }

        updateView();
    }

    boolean showConfig = false;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_next:
                    String apiUrl = Utility.getText(editTextApiUrl);
                    String chatUrl = Utility.getText(editTextChatUrl);

                    Api.BASE_URL_API = apiUrl;
                    Api.CHAT_HOST_URL = chatUrl;

                    SharedPreference.setString(mActivity, AppConstants.PREF_KEY_URL_API, apiUrl);
                    SharedPreference.setString(mActivity, AppConstants.PREF_KEY_URL_CHAT, chatUrl);

                    if (!SharedPreference.getBoolean(mActivity, AppConstants.PREF_KEY_IS_LOGGED_IN)) {
                        startActivity(new Intent(mActivity, LoginActivity.class));
                    } else if (!SharedPreference.getBoolean(mActivity, AppConstants.PREF_KEY_HAS_INTERESTS)) {
                        startActivity(new Intent(mActivity, InterestActivity.class));
                    } else {
                        startActivity(new Intent(mActivity, HomeScreenActivity.class));
                    }
                    finish();

                    break;
                case R.id.imageview_splash:
                    if (showConfig) {
                        updateView();
                    } else {
                        showConfig = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showConfig = false;
                            }
                        }, 1500);
                    }
                    break;
            }
        }
    };

    private void updateView() {
        editTextChatUrl.setVisibility(showConfig ? View.VISIBLE : View.GONE);
        editTextApiUrl.setVisibility(showConfig ? View.VISIBLE : View.GONE);
        buttonNext.setVisibility(showConfig ? View.VISIBLE : View.GONE);
    }
}
