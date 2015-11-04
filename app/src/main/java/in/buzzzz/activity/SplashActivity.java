package in.buzzzz.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;

import in.buzzzz.R;
import in.buzzzz.loader.APICaller;
import in.buzzzz.loader.LoaderCallback;
import in.buzzzz.model.Config;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.ConfigParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

/**
 * Created by Navkrishna on September 25, 2015
 */
public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    private static final int SECOND = 60 * 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;

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

        long lastLoadedTime = SharedPreference.getLong(mActivity, AppConstants.PREF_KEY_LAST_CONFIG_CALL);
        if (System.currentTimeMillis() - lastLoadedTime > DAY) {
            requestConfig();
        } else {
            moveToNext(1500);
        }
    }

    private void requestConfig() {
        HashMap<String, String> params = new HashMap<>();
        Request request = new Request(ApiDetails.ACTION_NAME.CONFIG);
        request.setUrl(Api.BASE_URL_CONFIG);
        request.setShowDialog(true);
        request.setDialogMessage(getString(R.string.progress_dialog_msg));
        request.setParamMap(params);
        request.setRequestType(Request.HttpRequestType.GET);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new ConfigParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {
            @Override
            public void onComplete(Model model) {
                if (model instanceof Config) {
                    Config config = (Config) model;
                    SharedPreference.setString(mActivity, AppConstants.PREF_KEY_URL_API, config.getUrl().getApi());
                    SharedPreference.setString(mActivity, AppConstants.PREF_KEY_URL_CHAT, config.getUrl().getChat());
                    SharedPreference.setLong(mActivity, AppConstants.PREF_KEY_LAST_CONFIG_CALL, System.currentTimeMillis());
                    moveToNext(1500);
                }
            }
        });
        if (!hasNetwork) {
            Utility.showToastMessage(mActivity, getString(R.string.no_network));
        }
    }

    private void moveToNext(long inDuration) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!SharedPreference.getBoolean(mActivity, AppConstants.PREF_KEY_IS_LOGGED_IN)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else if (!SharedPreference.getBoolean(mActivity, AppConstants.PREF_KEY_HAS_INTERESTS)) {
                    startActivity(new Intent(mActivity, InterestActivity.class));
                } else {
                    startActivity(new Intent(mActivity, HomeScreenActivity.class));
                }
                finish();
            }
        }, inDuration);
    }
}
