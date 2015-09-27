package in.buzzzz.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
import in.buzzzz.model.BuzzPreview;
import in.buzzzz.model.BuzzPreviewList;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.BuzzListParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.Utility;

public class BuzzListActivity extends BaseActivity implements ResultCallback<LocationSettingsResult> {
    public static final String TAG = "BuzzListActivity";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private int spinnerBugFistTimeLoad = 0;


    private String mFromScreen;
    private String latitude = "22.546548", longitude = "77.334874";
    private String mRadius = "5000";
    private String mInterestName = "Music";
    private String mInterestId;
    List<BuzzPreview> mBuzzPreviewList;
    private RecyclerView recyclerViewBuzz;
    private GoogleApiClient mGoogleApiClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private ProgressBar progressBar;

    List<String> mRadiusArrayList = new ArrayList<String>();
    List<String> mRadiusArrayToSend = new ArrayList<String>();
    private ArrayAdapter<String> dataAdapter;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;


    private FusedLocationProviderApi mFusedLocationProviderApi = LocationServices.FusedLocationApi;
    private Location mLocation;
    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Logger.i(TAG, "Location: " + location);
            if (location != null) {
                mLocation = location;
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                requestBuzzByInterest();
                Logger.i("onLocationChanged", "onLocationChanged");

                mFusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
            }
        }
    };

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Logger.i(TAG, "onConnected" + bundle);
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
        setContentView(R.layout.activity_buzzlist);
        mGoogleApiClient = buildGoogleApiClient();
        mGoogleApiClient.connect();
        mFromScreen = getIntent().getStringExtra(AppConstants.EXTRA_FROM);
        getViewsId();
        addItemOnSpinner();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getViewsId() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewBuzz = (RecyclerView) findViewById(R.id.recyclerview_buzzlist);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerViewBuzz.setLayoutManager(mLinearLayoutManager);
        recyclerViewBuzz.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

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

    private void requestBuzzByInterest() {
        progressBar.setVisibility(View.GONE);
        HashMap<String, String> params = new HashMap<>();

        ApiDetails.ACTION_NAME buzzBuzzByInterest = null;
        if (mFromScreen != null) {
            if (mFromScreen.equalsIgnoreCase(HomeScreenActivity.TAG)) {
                buzzBuzzByInterest = ApiDetails.ACTION_NAME.BUZZ_TRENDING;
            } else if (mFromScreen.equalsIgnoreCase(InterestActivity.TAG)) {
                buzzBuzzByInterest = ApiDetails.ACTION_NAME.BUZZ_BUZZ_BY_INTEREST;
                mInterestName = getIntent().getStringExtra(AppConstants.EXTRA_INTEREST_NAME);
                mInterestId = getIntent().getStringExtra(AppConstants.EXTRA_INTEREST_ID);

                params.put(ApiDetails.REQUEST_KEY_INTEREST, mInterestName);

            }

        }

        if (!latitude.isEmpty() && !longitude.isEmpty()) {
            params.put(ApiDetails.REQUEST_KEY_LATITUDE, String.valueOf(latitude));
            params.put(ApiDetails.REQUEST_KEY_LONGITUDE, String.valueOf(longitude));
        }
        params.put(ApiDetails.REQUEST_KEY_RADIUS, mRadius);
        Request request = new Request(buzzBuzzByInterest);
        request.setUrl(Api.BASE_URL_API + buzzBuzzByInterest.getActionName());
        request.setShowDialog(true);
        request.setDialogMessage(getString(R.string.progress_dialog_msg));
        request.setParamMap(params);
        request.setRequestType(Request.HttpRequestType.POST);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new BuzzListParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {
                if (model.getStatus() == 1) {
                    if (model instanceof BuzzPreviewList) {
                        BuzzPreviewList buzzPreviewList = (BuzzPreviewList) model;
                        mBuzzPreviewList = buzzPreviewList.getBuzzPreviews();

                        setData();
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

    private void setData() {
        BuzzAdapter interestAdapter = new BuzzAdapter(mActivity, mBuzzPreviewList,mLocation);
        recyclerViewBuzz.setAdapter(interestAdapter);
    }


    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(BuzzListActivity.this);
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
                    // Show the dialog by calling startResolutionForResult(), and spinnerBugFistTimeLoad the result
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
                        requestBuzzByInterest();

                        break;
                }
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buzzlist, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setAdapter(dataAdapter); // set the adapter to provide layout of rows and content
        spinner.setOnItemSelectedListener(onItemSelectedListener); // set the listener, to perform actions based on item selection
        dataAdapter.setDropDownViewResource(R.layout.drop_down_item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // add items into spinner dynamically
    public void addItemOnSpinner() {
        mRadiusArrayList.add("Radius:10 km");
        mRadiusArrayList.add("Radius:20 km");
        mRadiusArrayList.add("Radius:30 km");
        mRadiusArrayList.add("Radius:40 km");
        mRadiusArrayList.add("Radius:50 km");

        mRadiusArrayToSend.add("10000");
        mRadiusArrayToSend.add("20000");
        mRadiusArrayToSend.add("30000");
        mRadiusArrayToSend.add("40000");
        mRadiusArrayToSend.add("50000");

        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mRadiusArrayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRadius = mRadiusArrayToSend.get(position);
                spinnerBugFistTimeLoad = spinnerBugFistTimeLoad + 1;
                if (spinnerBugFistTimeLoad > 1) {
                    if (mLocation != null) {
                        latitude = String.valueOf(mLocation.getLatitude());
                        longitude = String.valueOf(mLocation.getLongitude());

                    }
                    Logger.i("spinner", "spinner");
                    requestBuzzByInterest();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }
}
