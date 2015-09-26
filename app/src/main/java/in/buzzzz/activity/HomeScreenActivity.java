package in.buzzzz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.HashMap;

import in.buzzzz.R;
import in.buzzzz.loader.APICaller;
import in.buzzzz.loader.LoaderCallback;
import in.buzzzz.model.BuzzList;
import in.buzzzz.model.Login;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.HomeBuzzParser;
import in.buzzzz.parser.LoginParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

public class HomeScreenActivity extends BaseActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getViewsId();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(mActivity, BuzzzzDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getViewsId() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //vertical swipe on home
                requestLogin();
            }
        });
    }

    private void requestLogin() {

        HashMap<String, String> params = new HashMap<>();
        params.put(ApiDetails.REQUEST_KEY_LATITUDE, "22.546548");
        params.put(ApiDetails.REQUEST_KEY_LONGITUDE, "77.334874");
        params.put(ApiDetails.REQUEST_KEY_RADIUS, "100");
        Request request = new Request(ApiDetails.ACTION_NAME.HOME_BUZZ);
        request.setUrl(Api.BASE_URL_API + ApiDetails.ACTION_NAME.HOME_BUZZ.getActionName());
        request.setShowDialog(true);
        request.setDialogMessage(getString(R.string.progress_dialog_msg));
        request.setParamMap(params);
        request.setRequestType(Request.HttpRequestType.POST);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new HomeBuzzParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {
                if (model.getStatus() == ApiDetails.STATUS_SUCCESS) {
                    if (model instanceof BuzzList) {
                        BuzzList login = (BuzzList) model;

                    }
                } else {
                    Utility.showToastMessage(mActivity, model.getMessage());
                }
            }
        });
        if (!hasNetwork) {
            Utility.showToastMessage(mActivity, getString(R.string.no_network));
        }
    }
}
