package in.buzzzz.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import in.buzzzz.R;

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
    }
}