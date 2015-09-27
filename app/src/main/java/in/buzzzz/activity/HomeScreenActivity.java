package in.buzzzz.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.buzzzz.R;
import in.buzzzz.adapter.BuzzAdapter;
import in.buzzzz.loader.APICaller;
import in.buzzzz.loader.LoaderCallback;
import in.buzzzz.model.BuzzList;
import in.buzzzz.model.BuzzPreview;
import in.buzzzz.model.Interest;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.HomeBuzzParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

public class HomeScreenActivity extends BaseActivity implements ResultCallback<LocationSettingsResult> {
    public static final String TAG = "HomeScreenActivity";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private int spinnerBugFistTimeLoad = 0;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerViewBuzz;
    private LinearLayout linearHorizontalView;
    List<Interest> mInterestList;
    List<BuzzPreview> mBuzzPreviewList;
    private String mRadius = "5000";
    List<String> mRadiusArrayList = new ArrayList<String>();
    List<String> mRadiusArrayToSend = new ArrayList<String>();


    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi mFusedLocationProviderApi = LocationServices.FusedLocationApi;
    private Location mLocation;
    private LocationSettingsRequest mLocationSettingsRequest;
    private ProgressBar progressBar;
    private ArrayAdapter<String> dataAdapter;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Logger.i(TAG, "Location: " + location);
            if (location != null) {
                mLocation = location;

                requestHomeBuzz(true, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                mFusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
            }
        }
    };

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Logger.i(TAG, "onConnected" + bundle);
           /* if (Utility.isLocationProviderEnabled(mActivity)) {
                createLocationRequest();
            } else {
//                checkLocationSettings();*/


            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mFusedLocationProviderApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, mLocationListener);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            mLocationSettingsRequest = builder.build();
            checkLocationSettings();


        }

        @Override
        public void onConnectionSuspended(int i) {
            Logger.i(TAG, "onConnectionSuspended : " + i);
            if (Utility.isLocationProviderEnabled(mActivity)) {
                mGoogleApiClient.connect();
            }
        }
    };


    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationProviderApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, mLocationListener);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        mLocationSettingsRequest = builder.build();
        checkLocationSettings();
    }

    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Logger.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                    + connectionResult.getErrorCode());
            if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
                // An API requested for GoogleApiClient is not available. The device's current
                // configuration might not be supported with the requested API or a required component
                // may not be installed, such as the Android Wear application. You may need to use a
                // second GoogleApiClient to manage the application's optional APIs.
                Logger.i(TAG, "API Unavailable.");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mGoogleApiClient = buildGoogleApiClient();
        mGoogleApiClient.connect();
        getViewsId();
        addItemOnSpinner();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(mActivity, CreateBuzzActivity.class);
                startActivity(intent);
            }
        });
    }

    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(mActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .build();
    }

    private void getViewsId() {

      /*  mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //vertical swipe on home
                requestHomeBuzz(false);
            }
        });*/
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewBuzz = (RecyclerView) findViewById(R.id.recyclerview_buzz);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerViewBuzz.setLayoutManager(mLinearLayoutManager);
        recyclerViewBuzz.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }
        });
        linearHorizontalView = (LinearLayout) findViewById(R.id.horizontal_linear);

        Button buttonProfile = (Button) findViewById(R.id.btn_profile);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedPreference.getBoolean(mActivity, AppConstants.PREF_KEY_IS_LOGGED_IN)) {
                    //:TODO show profile screen
                } else {
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    startActivity(intent);
                }


            }
        });

        Button buttonTrending = (Button) findViewById(R.id.btn_trending);
        buttonTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //:TODO show trending screen
                Intent intent = new Intent(mActivity, BuzzListActivity.class);
                intent.putExtra(AppConstants.EXTRA_FROM, HomeScreenActivity.TAG);
                startActivity(intent);

            }
        });


    }

    private void requestHomeBuzz(boolean showProgressDialog, String latitude, String longitude) {

        progressBar.setVisibility(View.GONE);

        Logger.i("lat lat", latitude + "  " + longitude);

        HashMap<String, String> params = new HashMap<>();

        if (!latitude.isEmpty() && !longitude.isEmpty()) {
            params.put(ApiDetails.REQUEST_KEY_LATITUDE, String.valueOf(latitude));
            params.put(ApiDetails.REQUEST_KEY_LONGITUDE, String.valueOf(longitude));
        }

        params.put(ApiDetails.REQUEST_KEY_RADIUS, mRadius);
        Request request = new Request(ApiDetails.ACTION_NAME.HOME_BUZZ);
        request.setUrl(Api.BASE_URL_API + ApiDetails.ACTION_NAME.HOME_BUZZ.getActionName());
        request.setShowDialog(showProgressDialog);
        request.setDialogMessage(getString(R.string.progress_dialog_msg));
        request.setParamMap(params);
        request.setRequestType(Request.HttpRequestType.POST);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new HomeBuzzParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {
                hideProgressBar();
                if (model.getStatus() == ApiDetails.STATUS_SUCCESS) {
                    if (model instanceof BuzzList) {
                        BuzzList buzzList = (BuzzList) model;
                        Logger.i("interestList", buzzList.getInterestList().toString());
                        Logger.i("buzzlist", buzzList.getBuzzPreviewList().toString());
                        mInterestList = buzzList.getInterestList();
                        mBuzzPreviewList = buzzList.getBuzzPreviewList();
                        setData();
                        setInterestData();
                    }
                } else {
                    Utility.showToastMessage(mActivity, model.getMessage());
                }
            }
        });
        if (!hasNetwork) {
            Utility.showToastMessage(mActivity, getString(R.string.no_network));
            hideProgressBar();
        }
    }

    private void setData() {
        BuzzAdapter interestAdapter = new BuzzAdapter(mActivity, mBuzzPreviewList);
        recyclerViewBuzz.setAdapter(interestAdapter);
    }

    private void setInterestData() {

        for (int i = 0; i < mInterestList.size(); i++) {
            Interest interest = mInterestList.get(i);
            View child = getLayoutInflater().inflate(R.layout.home_interest_header, null);
            linearHorizontalView.addView(child);

            LinearLayout linearLayoutInterest = (LinearLayout) child.findViewById(R.id.shapeLayout);
            linearLayoutInterest.setTag(i);
            TextView textviewName = (TextView) child.findViewById(R.id.textview_interest_name);
            textviewName.setText(interest.getName());
            ImageView imageViewInterestPics = (ImageView) child.findViewById(R.id.imageview_interest_pic);
            Utility.setImageFromUrl(Api.BASE_URL_CLOUDINARY_BUZZZZ + mInterestList.get(i).getImageName(), imageViewInterestPics, R.mipmap.ic_launcher);
            linearLayoutInterest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = (Integer) v.getTag();

                    Interest pickedInterest = mInterestList.get(position);
                    Intent intent = new Intent(mActivity, BuzzListActivity.class);

                    //sending Interest tag because both Interest activity and Home has same interest list
                    intent.putExtra(AppConstants.EXTRA_FROM, InterestActivity.TAG);
                    intent.putExtra(AppConstants.EXTRA_INTEREST_NAME, pickedInterest.getName());
                    intent.putExtra(AppConstants.EXTRA_INTEREST_ID, pickedInterest.getId());
                    mActivity.startActivity(intent);
                }
            });


        }

    }

    private void hideProgressBar() {
//        mSwipeRefreshLayout.setRefreshing(false);
    }


    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );

        result.setResultCallback(HomeScreenActivity.this);
    }


    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                // NO need to show the dialog;

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //unable to execute request
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are inadequate, and cannot be fixed here. Dialog not created
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        progressBar.setVisibility(View.VISIBLE);

                        break;
                    case Activity.RESULT_CANCELED:
                        requestHomeBuzz(true, "", "");
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_skip) {

            Intent intent = new Intent(mActivity, HomeScreenActivity.class);
            startActivity(intent);
            finish();
            return true;

        }*/
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setAdapter(dataAdapter); // set the adapter to provide layout of rows and content
        spinner.setOnItemSelectedListener(onItemSelectedListener); // set the listener, to perform actions based on item selection
        return true;
    }

    // add items into spinner dynamically
    public void addItemOnSpinner() {
        mRadiusArrayList.add("Radius:1000 m");
        mRadiusArrayList.add("Radius:2000 m");
        mRadiusArrayList.add("Radius:3000 m");
        mRadiusArrayList.add("Radius:4000 m");
        mRadiusArrayList.add("Radius:5000 m");

        mRadiusArrayToSend.add("1000");
        mRadiusArrayToSend.add("2000");
        mRadiusArrayToSend.add("3000");
        mRadiusArrayToSend.add("4000");
        mRadiusArrayToSend.add("5000");

        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mRadiusArrayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerBugFistTimeLoad = spinnerBugFistTimeLoad + 1;
                if (spinnerBugFistTimeLoad > 1) {
                    mRadius = mRadiusArrayToSend.get(position);
                    String lat = "", lon = "";
                    if (mLocation != null) {
                        lat = String.valueOf(mLocation.getLatitude());
                        lon = String.valueOf(mLocation.getLongitude());

                    }
                    requestHomeBuzz(true, lat, lon);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }
}
