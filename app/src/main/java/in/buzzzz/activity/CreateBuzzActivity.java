package in.buzzzz.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

import org.json.JSONArray;

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
import in.buzzzz.model.Interest;
import in.buzzzz.model.InterestInfo;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.InterestParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.Utility;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class CreateBuzzActivity extends BaseActivity {

    private ImageView mImageViewBuzzPic;
    private EditText mEditTextBuzzTitle;
    private EditText mEditTextBuzzDesc;
    private TextView mTextViewBuzzVenue;
    private TextView mTextViewSelectedInterests;
    private TextView mTextViewStartTime;
    private TextView mTextViewEndTime;
    private Button mButtonCreateBuzz;
    private CheckBox mCheckboxIsRsvp;
    private Spinner mSpinnerPeriod;
    private List<Interest> mInterestList;
    private AutoCompleteTextView mAutoCompleteTextViewInterest;
    private JSONArray jsonArrayInterst = new JSONArray();
    private String mStartDateTime, mEndDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_buzz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_create_buzz);
        linkViewsId();
        requestInterest();
    }

    private void linkViewsId() {
        mImageViewBuzzPic = (ImageView) findViewById(R.id.imageview_buzz_pic);
        mEditTextBuzzTitle = (EditText) findViewById(R.id.edittext_buzz_title);
        mEditTextBuzzDesc = (EditText) findViewById(R.id.edittext_buzz_desc);
        mTextViewBuzzVenue = (TextView) findViewById(R.id.textview_choose_venue);
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
        List<HashMap<String, String>> countryList = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < mInterestList.size(); i++) {
            HashMap<String, String> interestMap = new HashMap<String, String>();
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
                        jsonArrayInterst.put(interest.getName());
                        if (jsonArrayInterst.length() > 1)
                            mTextViewSelectedInterests.append(", ");
                        mTextViewSelectedInterests.append(interest.getName());
                        mAutoCompleteTextViewInterest.setText("");
                        break;
                    }
                }
            }
        });
    }

    private void requestCreatBuzz() {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiDetails.REQUEST_KEY_NAME, mEditTextBuzzTitle.getText().toString().trim());
        params.put(ApiDetails.REQUEST_KEY_IMAGE_NAME, "");
        params.put(ApiDetails.REQUEST_KEY_IS_RSVP, String.valueOf(mCheckboxIsRsvp.isChecked()));
        params.put(ApiDetails.REQUEST_KEY_LATITUDE, "");
        params.put(ApiDetails.REQUEST_KEY_LONGITUDE, "");
        params.put(ApiDetails.REQUEST_KEY_ADDRESS, "");
        params.put(ApiDetails.REQUEST_KEY_START_TIME, "");
        params.put(ApiDetails.REQUEST_KEY_END_TIME, "");
        params.put(ApiDetails.REQUEST_KEY_PERIOD, ApiDetails.PERIOD.ONCE.name());
        params.put(ApiDetails.REQUEST_KEY_TAGS, ""); // json array
        params.put(ApiDetails.REQUEST_KEY_INTERESTS, jsonArrayInterst.toString()); // json array
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
            }
        }
    };

    private void validateAndCreateBuzz() {
        String buzzTitle = mEditTextBuzzTitle.getText().toString().trim();
        if (buzzTitle.isEmpty()) {
            mEditTextBuzzTitle.setError("Enter title");
        } else if (jsonArrayInterst.length() == 0) {
            mAutoCompleteTextViewInterest.setError("Select at least one interest");
        } else {
            requestCreatBuzz();
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
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                if (isStartDateTime) {
                    mStartDateTime = formatter.format(date);
                    mTextViewStartTime.setText(mStartDateTime);
                } else {
                    mEndDateTime = formatter.format(date);
                    mTextViewEndTime.setText(mEndDateTime);
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }
}