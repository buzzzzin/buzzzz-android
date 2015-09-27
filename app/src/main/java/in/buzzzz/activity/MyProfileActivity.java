package in.buzzzz.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;

import java.util.HashMap;

import in.buzzzz.R;
import in.buzzzz.loader.APICaller;
import in.buzzzz.loader.LoaderCallback;
import in.buzzzz.model.BuzzPreview;
import in.buzzzz.model.Interest;
import in.buzzzz.model.Model;
import in.buzzzz.model.MyProfile;
import in.buzzzz.model.Request;
import in.buzzzz.parser.MessageParser;
import in.buzzzz.parser.MyProfileParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class MyProfileActivity extends BaseActivity {


    private static final String TAG = "MyProfileActivity";


//    private RelativeLayout mRelativeLayoutProfileContainer;
    private CoordinatorLayout mCoordinatorLayout;
    private ImageView mImageViewProfile;
    private TextView mTexViewGender, mTextViewEmail, mTextViewPhone, mTextViewCountry, mTextViewMyInterest, mTextViewBuzz;
    private Button mButtonLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        linkViews();
        requestProfileData();
//        updateDimension();
    }

    private void linkViews() {
        mImageViewProfile = (ImageView) findViewById(R.id.imageview_profile);
        mTexViewGender = (TextView) findViewById(R.id.textview_gender);
        mTextViewEmail = (TextView) findViewById(R.id.textview_email);
        mTextViewPhone = (TextView) findViewById(R.id.textview_phone);
        mTextViewCountry = (TextView) findViewById(R.id.textview_country);

        mTextViewMyInterest = (TextView) findViewById(R.id.textview_interest);
        mTextViewBuzz = (TextView) findViewById(R.id.textview_total_buzz);

        mButtonLogout = (Button) findViewById(R.id.button_logout);
        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestLogout();
            }
        });
    }


    private void requestLogout() {
        HashMap<String, String> params = new HashMap<>();
        Request request = new Request(ApiDetails.ACTION_NAME.AUTH_LOGOUT);
        request.setUrl(Api.BASE_URL_API + ApiDetails.ACTION_NAME.AUTH_LOGOUT.getActionName());
        request.setParamMap(params);
        request.setShowDialog(true);
        request.setDialogMessage(getString(R.string.progress_dialog_msg));
        request.setRequestType(Request.HttpRequestType.POST);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new MessageParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {
                Logger.i(TAG, "model: " + model);
                if (model.getStatus() == ApiDetails.STATUS_SUCCESS) {

                    SharedPreference.clearLoggedInInfo(mActivity);
                    logoutFromSocialNtwork();


                } else {
                    Utility.showToastMessage(mActivity, model.getMessage());
                }
            }
        });
        if (!hasNetwork) {
            Utility.showToastMessage(mActivity, getString(R.string.no_network));
        }
    }

    private void logoutFromSocialNtwork() {

        try {

            LoginManager loginManager = LoginManager.getInstance();
            loginManager.logOut();


        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(mActivity, LoginActivity.class);
        startActivity(intent);
        finish();
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
                        MyProfile myProfile = (MyProfile) model;
                        displayProfileData(myProfile);
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

        String imageName = Api.BASE_URL_CLOUDINARY_SOCIAL
                + SharedPreference.getString(mActivity, AppConstants.PREF_KEY_MEDIUM_TYPE).toLowerCase()
                + "/"
                + SharedPreference.getString(mActivity, AppConstants.PREF_KEY_MEDIUM_ID);
        Utility.setImageFromUrl(imageName, mImageViewProfile, R.mipmap.ic_launcher);


        if (myProfile.getName() != null) {
//            mCollapsingToolbar.setTitle(myProfile.getName());
        } else {
//            mCollapsingToolbar.setTitle("My Profile");
        }

        if (myProfile.getGender() != null && !myProfile.getGender().name().isEmpty()) {
            mTexViewGender.setText(myProfile.getGender().name());
        } else {
            mTexViewGender.setVisibility(View.GONE);
        }

        if (myProfile.getEmail() != null && !myProfile.getEmail().isEmpty()) {
            mTextViewEmail.setText(myProfile.getEmail());
        } else {
            mTextViewEmail.setVisibility(View.GONE);
        }

        if (myProfile.getMobile() != null && !myProfile.getMobile().isEmpty()) {
            mTextViewPhone.setText(myProfile.getMobile());
        } else {
            mTextViewPhone.setVisibility(View.GONE);
        }

        if (myProfile.getCountry() != null && !myProfile.getCountry().isEmpty()) {
            mTextViewCountry.setText(myProfile.getCountry());
        } else {
            mTextViewCountry.setVisibility(View.GONE);
        }

        if (myProfile.getInterests() != null & myProfile.getInterests().size() > 0) {

            String allInterest = "";
            for (int i = 0; i < myProfile.getInterests().size(); i++) {
                Interest interest = myProfile.getInterests().get(0);
                allInterest = allInterest + interest.getName() + " , ";

            }
            if (allInterest.endsWith(" , ")) {
                allInterest = allInterest.substring(0, allInterest.length() - 3);
            }
            mTextViewMyInterest.setText("My Interests: " + allInterest);


        } else {
            mTextViewMyInterest.setVisibility(View.GONE);

        }

        if (myProfile.getBuzzs() != null & myProfile.getBuzzs().size() > 0) {

            String buzzCreated = "";
            for (int i = 0; i < myProfile.getBuzzs().size(); i++) {
                BuzzPreview buzzPreview = myProfile.getBuzzs().get(0);
                buzzCreated = buzzCreated + buzzPreview.getName() + " , ";

            }

            if (buzzCreated.endsWith(" , ")) {
                buzzCreated = buzzCreated.substring(0, buzzCreated.length() - 3);
            }
            mTextViewBuzz.setText("My Buzzzz: " + buzzCreated);


        } else {
            mTextViewBuzz.setVisibility(View.GONE);

        }


    }


}
