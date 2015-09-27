package in.buzzzz.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.ig.crop.Crop;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import in.buzzzz.R;
import in.buzzzz.loader.APICaller;
import in.buzzzz.loader.LoaderCallback;
import in.buzzzz.model.BuzzPreview;
import in.buzzzz.model.CloudinaryDetail;
import in.buzzzz.model.Interest;
import in.buzzzz.model.InterestInfo;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.BuzzPreviewParser;
import in.buzzzz.parser.CloudinaryParser;
import in.buzzzz.parser.InterestParser;
import in.buzzzz.place.PlaceAutocompleteAdapter;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class CreateBuzzActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int CHOOSE_PHOTO = 1;
    private ImageView mImageViewBuzzPic;
    private EditText mEditTextBuzzTitle;
    private EditText mEditTextBuzzDesc;
    private TextView mTextViewSelectedInterests;
    private TextView mTextViewStartTime;
    private TextView mTextViewEndTime;
    private Button mButtonCreateBuzz;
    private CheckBox mCheckboxIsRsvp;
    private Spinner mSpinnerPeriod;
    private List<Interest> mInterestList;
    private AutoCompleteTextView mAutoCompleteTextViewInterest;
    private JSONArray jsonArrayInterest = new JSONArray();
    private JSONArray jsonArrayTag = new JSONArray();
    private String mStartDateTime = "", mEndDateTime = "";
    private String mImagePath = "";

    CharSequence mAddress = "";
    double mLatitude = 0.0;
    double mLongitude = 0.0;

    private static final String TAG = "CreateBuzzActivity";
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private TextView mTextViewVenue;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        setContentView(R.layout.activity_create_buzz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_create_buzz);
        linkViewsId();
        requestInterest();
        initViews();
    }

    private void initViews() {
        mAutocompleteView = (AutoCompleteTextView) findViewById(R.id.autocompletetextview_search);
        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
        mTextViewVenue = (TextView) findViewById(R.id.textview_venue);
        mTextViewVenue.setOnClickListener(mOnClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        mAutocompleteView.setAdapter(mAdapter);
    }

    private void linkViewsId() {
        mImageViewBuzzPic = (ImageView) findViewById(R.id.imageview_buzz_pic);
        mEditTextBuzzTitle = (EditText) findViewById(R.id.edittext_buzz_title);
        mEditTextBuzzDesc = (EditText) findViewById(R.id.edittext_buzz_desc);
        mButtonCreateBuzz = (Button) findViewById(R.id.button_create);
        mCheckboxIsRsvp = (CheckBox) findViewById(R.id.checkbox_is_rsvp);
        mSpinnerPeriod = (Spinner) findViewById(R.id.spinner_rsvp_options);
        mAutoCompleteTextViewInterest = (AutoCompleteTextView) findViewById(R.id.autocompletetextview_interest);
        mTextViewSelectedInterests = (TextView) findViewById(R.id.textview_selected_interests);
        mTextViewStartTime = (TextView) findViewById(R.id.textview_start_time);
        mTextViewEndTime = (TextView) findViewById(R.id.textview_end_time);
        mButtonCreateBuzz.setOnClickListener(mOnClickListener);
        mTextViewStartTime.setOnClickListener(mOnClickListener);
        mTextViewEndTime.setOnClickListener(mOnClickListener);
        mImageViewBuzzPic.setOnClickListener(mOnClickListener);
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
                        setDataAutoListAdapter();
                    }
                }
            }
        });
        if (!hasNetwork) {
            Utility.showToastMessage(mActivity, getString(R.string.no_network));
        }
    }

    private static final String KEY_INTEREST_ID = "key_interest_id";
    private static final String KEY_INTEREST_NAME = "key_interest_name";

    private void setDataAutoListAdapter() {
        List<HashMap<String, String>> countryList = new ArrayList<>();
        for (int i = 0; i < mInterestList.size(); i++) {
            HashMap<String, String> interestMap = new HashMap<>();
            Interest interest = mInterestList.get(i);
            interestMap.put(KEY_INTEREST_ID, interest.getId());
            interestMap.put(KEY_INTEREST_NAME, interest.getName());
            countryList.add(interestMap);
        }
        String[] from = {KEY_INTEREST_NAME};
        int[] to = {R.id.textview_custom_interest};

        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), countryList, R.layout.layout_custom_autocomplete, from, to);
        mAutoCompleteTextViewInterest.setAdapter(adapter);
        mAutoCompleteTextViewInterest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> interestMap = (HashMap<String, String>) parent.getAdapter().getItem(position);
                for (Interest interest : mInterestList) {
                    if (interest.getName().equals(interestMap.get(KEY_INTEREST_NAME))) {
                        jsonArrayInterest.put(interest.getName());
                        if (jsonArrayInterest.length() > 1)
                            mTextViewSelectedInterests.append(", ");
                        mTextViewSelectedInterests.append(interest.getName());
                        mAutoCompleteTextViewInterest.setText("");
                        break;
                    }
                }
            }
        });
    }

    private void requestCreatBuzz(String imageName) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiDetails.REQUEST_KEY_NAME, mEditTextBuzzTitle.getText().toString().trim());
        params.put(ApiDetails.REQUEST_KEY_IMAGE_NAME, imageName);
        params.put(ApiDetails.REQUEST_KEY_IS_RSVP, String.valueOf(mCheckboxIsRsvp.isChecked()));
        params.put(ApiDetails.REQUEST_KEY_LATITUDE, String.valueOf(mLatitude));
        params.put(ApiDetails.REQUEST_KEY_LONGITUDE, String.valueOf(mLongitude));
        params.put(ApiDetails.REQUEST_KEY_ADDRESS, String.valueOf(mAddress));
        params.put(ApiDetails.REQUEST_KEY_START_TIME, mStartDateTime);
        params.put(ApiDetails.REQUEST_KEY_END_TIME, mEndDateTime);
        params.put(ApiDetails.REQUEST_KEY_PERIOD, ApiDetails.PERIOD.ONCE.name());
        params.put(ApiDetails.REQUEST_KEY_TAGS, jsonArrayTag.toString()); // json array
        params.put(ApiDetails.REQUEST_KEY_INTERESTS, jsonArrayInterest.toString()); // json array
        Request request = new Request(ApiDetails.ACTION_NAME.CREATE_BUZZ);
        request.setParamMap(params);
        request.setShowDialog(true);
        request.setDialogMessage(getString(R.string.progress_dialog_msg));
        request.setUrl(Api.BASE_URL_API + ApiDetails.ACTION_NAME.CREATE_BUZZ.getActionName());
        request.setRequestType(Request.HttpRequestType.POST);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new BuzzPreviewParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {

                if (model.getStatus() == 1) {
                    if (model instanceof BuzzPreview) {
                        BuzzPreview buzzPreview = (BuzzPreview) model;
                        Intent intent = new Intent(mActivity, BuzzDetailActivity.class);
                        intent.putExtra(AppConstants.EXTRA_BUZZZZ_ID, buzzPreview.getBuzzId());
                        intent.putExtra(AppConstants.EXTRA_BUZZZZ_NAME, buzzPreview.getName());
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        if (!hasNetwork) {
            Utility.showToastMessage(mActivity, getString(R.string.no_network));
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_create:
                    validateAndCreateBuzz();
                    break;
                case R.id.textview_start_time:
                    showDateAndTimePicker(true);
                    break;
                case R.id.textview_end_time:
                    showDateAndTimePicker(false);
                    break;
                case R.id.textview_venue:
                    mAddress = "";
                    mLatitude = 0.0;
                    mLongitude = 0.0;
                    mTextViewVenue.setVisibility(View.GONE);
                    mAutocompleteView.setVisibility(View.VISIBLE);
                    break;
                case R.id.imageview_buzz_pic:
                    if (imageSelected) {
                        showImageChangeDialog();
                    } else {
                        choosePhoto();
                    }
                    break;
            }
        }
    };

    private void showImageChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Update Image");
        builder.setMessage("Do you want to change image for Buzzzz?");
        builder.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                choosePhoto();
            }
        });
        builder.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void validateAndCreateBuzz() {
        String buzzTitle = mEditTextBuzzTitle.getText().toString().trim();
        if (buzzTitle.isEmpty()) {
            mEditTextBuzzTitle.setError("Enter title");
        } else if (jsonArrayInterest.length() == 0) {
            mAutoCompleteTextViewInterest.setError("Select at least one interest");
        } else if (mStartDateTime.isEmpty()) {
            Utility.showToastMessage(mActivity, "Select a start time");
        } else if (mAddress.length() == 0 || mLatitude == 0.0 || mLongitude == 0.0) {
            mTextViewVenue.setVisibility(View.GONE);
            mAutocompleteView.setVisibility(View.VISIBLE);
            mAutocompleteView.setError("Select a venue");
        } else {
            if (mImagePath == null || mImagePath.isEmpty()) {
                requestCreatBuzz("");
            } else {
                uploadImageOnCloudinary(SharedPreference.getString(mActivity, AppConstants.PREF_KEY_USER_ID), mImagePath);
            }
        }
    }

    private void showDateAndTimePicker(final boolean isStartDateTime) {
        final View dialogView = View.inflate(mActivity, R.layout.layout_date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute(), 0);

                long startTime = calendar.getTimeInMillis();
                Date date = new Date(startTime);
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                if (isStartDateTime) {
                    mStartDateTime = formatter.format(date);
                    mTextViewStartTime.setText("Start Time: " + mStartDateTime);
                } else {
                    mEndDateTime = formatter.format(date);
                    mTextViewEndTime.setText("End Time: " + mEndDateTime);
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private static final int CAMERA_REQUEST = 1888;
    private boolean imageSelected = false;

    private void updateViewDimension() {
        Point point = Utility.getDisplayPoint(mActivity);
        ViewGroup.LayoutParams layoutParams = mImageViewBuzzPic.getLayoutParams();
        layoutParams.width = point.x;
        layoutParams.height = point.y;
        mImageViewBuzzPic.setLayoutParams(layoutParams);
    }

    private void choosePhoto() {
        Intent intent = new Intent(mActivity, CameraGalleryActivity.class);
        if (mImagePath != null) {
            intent.putExtra(AppConstants.EXTRA_IS_REMOVE_IMAGE, true);
        }
        startActivityForResult(intent, CHOOSE_PHOTO);
        overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    mImagePath = data.getStringExtra("imagePath");
                    if (mImagePath != null) {
                        beginCrop(Uri.fromFile(new File(mImagePath)));
                    } else {
                        Utility.showToastMessage(mActivity, getString(R.string.msg_choose_different));
                    }
                } else if (resultCode == CameraGalleryActivity.RESULT_REMOVED) {
                    mImagePath = null;
                    mImageViewBuzzPic.setImageBitmap(null);
                }
                break;
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    mImagePath = data.getStringExtra("imagePath");
                    if (mImagePath != null) {
                        beginCrop(Uri.fromFile(new File(mImagePath)));
                    } else {
                        Utility.showToastMessage(mActivity, getString(R.string.msg_choose_different));
                    }
                }
                break;
            case Crop.REQUEST_CROP:
                handleCrop(resultCode, data);
                break;
        }
    }

    private void beginCrop(Uri source) {
        Uri outputUri = null;
        try {
            outputUri = Uri.fromFile(Utility.createImageFile(mActivity));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Point point = Utility.getDisplayPoint(mActivity);
        int width = point.x;
        int height = point.y;
        Logger.i("point", "width:" + width + "height:" + height + "");
        new Crop(source).output(outputUri).withAspect(width, height).start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri croppedUri = Crop.getOutput(result);
            File outPutFile = new File(croppedUri.getPath());
            mImagePath = outPutFile.getAbsolutePath();
            updateViewDimension();
            setPic(mImagePath);
        } else if (resultCode == Crop.RESULT_ERROR) {
            //Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
            Utility.showToastMessage(mActivity, Crop.getError(result).getMessage());
        } else if (resultCode == RESULT_CANCELED) {
            mImagePath = null;
        }
    }

    private void setPic(String path) {
        mImageViewBuzzPic.setImageBitmap(Utility.getBitmap(path));
    }

    private void uploadImageOnCloudinary(String folderName, String imagePath) {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put(ApiDetails.REQUEST_KEY_CLOUDINARY_API_KEY, Api.CLOUDINARY_API_KEY);
        paramMap.put(ApiDetails.REQUEST_KEY_CLOUDINARY_API_SECRET, Api.CLOUDINARY_API_SECRET);
        paramMap.put(ApiDetails.REQUEST_KEY_CLOUDINARY_FOLDER, Api.CLOUDINARY_FOLDER + "/" + folderName);
        paramMap.put(ApiDetails.REQUEST_KEY_CLOUDINARY_CLOUD_NAME, Api.CLOUDINARY_CLOUD);
        paramMap.put(ApiDetails.REQUEST_KEY_CLOUDINARY_FORMAT, "JPG");
        paramMap.put(ApiDetails.REQUEST_KEY_CLOUDINARY_ACTION, ApiDetails.ACTION_NAME.UPLOAD.name());
        paramMap.put(ApiDetails.REQUEST_KEY_CLOUDINARY_IMAGE_PATH, imagePath);
        Request request = new Request(ApiDetails.ACTION_NAME.UPLOAD);
        request.setDialogMessage(getResources().getString(R.string.dialog_msg_upload_image));
        request.setParamMap(paramMap);
        request.setShowDialog(true);
        request.setUrl(Api.BASE_URL_API);
        request.setRequestType(Request.HttpRequestType.CLOUDINARY);
        CloudinaryParser cloudinaryParser = new CloudinaryParser();
        final LoaderCallback loaderCallback = new LoaderCallback(mActivity, cloudinaryParser);
        loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {
            @Override
            public void onComplete(Model model) {
                if (model instanceof CloudinaryDetail) {
                    CloudinaryDetail cloudinaryDetail = (CloudinaryDetail) model;
                    if (cloudinaryDetail.getPublicId() != null && !cloudinaryDetail.getPublicId().isEmpty()) {
                        String cloudinaryImagePath = cloudinaryDetail.getPublicId();
                        Logger.i("cloudinaryImagePath", cloudinaryImagePath);
                        requestCreatBuzz(cloudinaryImagePath);
                    }
                } else {
                    Utility.showToastMessage(mActivity, model.getMessage());
                }
            }
        });
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            mAddress = place.getAddress();
            mLatitude = place.getLatLng().latitude;
            mLongitude = place.getLatLng().longitude;

            // Format details of the place for display and show it in a TextView.
            mTextViewVenue.setText(place.getName() + "\n" + place.getAddress());
            mAutocompleteView.setVisibility(View.GONE);
            mAutocompleteView.setText("");
            mTextViewVenue.setVisibility(View.VISIBLE);
            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "DEV: Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
}