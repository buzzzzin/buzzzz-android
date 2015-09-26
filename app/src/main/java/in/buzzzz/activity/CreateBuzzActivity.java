package in.buzzzz.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import in.buzzzz.R;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class CreateBuzzActivity extends BaseActivity {

    private ImageView mImageViewBuzzPic;
    private EditText mEditTextBuzzTitle;
    private EditText mEditTextBuzzDesc;
    private TextView mTextViewBuzzVenue;
    private Button mButtonCreateBuzz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_buzz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_create_buzz);
        linkViewsId();
    }

    private void linkViewsId() {
        mImageViewBuzzPic = (ImageView) findViewById(R.id.imageview_buzz_pic);
        mEditTextBuzzTitle = (EditText) findViewById(R.id.edittext_buzz_title);
        mEditTextBuzzDesc = (EditText) findViewById(R.id.edittext_buzz_desc);
        mTextViewBuzzVenue = (TextView) findViewById(R.id.textview_choose_venue);
        mButtonCreateBuzz = (Button) findViewById(R.id.button_create);
        mButtonCreateBuzz.setOnClickListener(mOnClickListener);
    }

    private void requestCreatBuzz() {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiDetails.REQUEST_KEY_NAME, mEditTextBuzzTitle.getText().toString().trim());
        params.put(ApiDetails.REQUEST_KEY_IMAGE_NAME, "");
        params.put(ApiDetails.REQUEST_KEY_IS_RSVP, "");
        params.put(ApiDetails.REQUEST_KEY_LATITUDE, "");
        params.put(ApiDetails.REQUEST_KEY_LONGITUDE, "");
        params.put(ApiDetails.REQUEST_KEY_ADDRESS, "");
        params.put(ApiDetails.REQUEST_KEY_START_TIME, "");
        params.put(ApiDetails.REQUEST_KEY_END_TIME, "");
        params.put(ApiDetails.REQUEST_KEY_PERIOD, "");
        params.put(ApiDetails.REQUEST_KEY_TAGS, ""); // json array
        params.put(ApiDetails.REQUEST_KEY_INTERESTS, ""); // json array
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String buzzTitle = mEditTextBuzzTitle.getText().toString().trim();
            String buzzDesc = mEditTextBuzzDesc.getText().toString().trim();
            if (!buzzTitle.isEmpty() && !buzzDesc.isEmpty()) {
                requestCreatBuzz();
            }
        }
    };
}