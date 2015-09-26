package in.buzzzz.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import in.buzzzz.R;
import in.buzzzz.loader.APICaller;
import in.buzzzz.loader.LoaderCallback;
import in.buzzzz.model.Model;
import in.buzzzz.model.MyProfile;
import in.buzzzz.model.Request;
import in.buzzzz.parser.MyProfileParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.Utility;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class MyProfileActivity extends BaseActivity {
    private static final String TAG = "MyProfileActivity";

    private RelativeLayout mRelativeLayoutProfileContainer;
    private CoordinatorLayout mCoordinatorLayout;
    private ViewPager mViewPagerProfile;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private RelativeLayout mRelativeLayoutProfileDetailContainer;
    private ImageView mImageViewProfile;
    private TabLayout mTabLayoutProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        linkViews();
        requestProfileData();
        updateDimension();
    }

    private void linkViews() {
        mRelativeLayoutProfileContainer = (RelativeLayout) findViewById(R.id.relativelayout_profile_container);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);
        mViewPagerProfile = (ViewPager) findViewById(R.id.viewpager_profile);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mRelativeLayoutProfileDetailContainer = (RelativeLayout) findViewById(R.id.relativelayout_profile_detail_container);
        mImageViewProfile = (ImageView) findViewById(R.id.imageview_profile);
        mTabLayoutProfile = (TabLayout) findViewById(R.id.tablayout_profile);
    }

    private void updateDimension() {
        Point point = Utility.getDisplayPoint(mActivity);
        int width = point.x;
        int height = (int) (point.y / 1.25);
        ViewGroup.LayoutParams layoutParams = mRelativeLayoutProfileContainer.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
    }

    private void requestProfileData() {
        Request request = new Request(ApiDetails.ACTION_NAME.MY_PROFILE);
        request.setUrl(Api.BASE_URL_API + ApiDetails.ACTION_NAME.MY_PROFILE.getActionName());
        request.setShowDialog(true);
        request.setDialogMessage(getString(R.string.progress_dialog_msg));
        request.setRequestType(Request.HttpRequestType.GET);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new MyProfileParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {
                Logger.i(TAG, "model: " + model);
                if (model.getStatus() == ApiDetails.STATUS_SUCCESS) {
                    if (model instanceof MyProfile) {
                        displayProfileData((MyProfile) model);
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

    private void displayProfileData(MyProfile myProfile) {

    }
}
