package in.buzzzz.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.List;

import in.buzzzz.R;
import in.buzzzz.adapter.InterestAdapter;
import in.buzzzz.loader.APICaller;
import in.buzzzz.loader.LoaderCallback;
import in.buzzzz.model.Interest;
import in.buzzzz.model.InterestInfo;
import in.buzzzz.model.Login;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.InterestParser;
import in.buzzzz.parser.LoginParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

public class InterestActivity extends BaseActivity {

    List<Interest> mInterestList;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getViewsId();
        requestInterest();
    }

    private void getViewsId() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_interest);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                finish();
                return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void requestInterest() {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiDetails.REQUEST_KEY_NAME, ApiDetails.ACTION_NAME.INTEREST.name());
        Request request = new Request(ApiDetails.ACTION_NAME.INTEREST);
        request.setUrl(Api.BASE_URL_API + ApiDetails.ACTION_NAME.INTEREST.getActionName());
        request.setShowDialog(true);
        request.setDialogMessage(getString(R.string.progress_dialog_msg));
        request.setParamMap(params);
        request.setRequestType(Request.HttpRequestType.GET);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new InterestParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {

                if (model.getStatus() == 1) {
                    if (model instanceof InterestInfo) {
                        InterestInfo interestInfo = (InterestInfo) model;
                        mInterestList = interestInfo.getInterestList();
                        setData();

                    }
                }

            }
        });
        if (!hasNetwork) {
            Utility.showToastMessage(mActivity, getString(R.string.no_network));
        }
    }

    private void setData() {
        InterestAdapter interestAdapter = new InterestAdapter(mActivity, mInterestList);
        mRecyclerView.setAdapter(interestAdapter);
    }
}
