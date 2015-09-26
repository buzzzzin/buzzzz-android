package in.buzzzz.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by Navkrishna on September 25, 2015
 */
public class BaseActivity extends AppCompatActivity {
    public Activity mActivity = this;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
