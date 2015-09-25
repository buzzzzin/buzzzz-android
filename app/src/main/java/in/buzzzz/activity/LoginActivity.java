package in.buzzzz.activity;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import in.buzzzz.R;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.Utility;

/**
 * Created by Navkrishna on September 25, 2015
 */
public class LoginActivity extends BaseActivity {

    private LoginButton mFacebookLoginButton;
    private CallbackManager mFacebookCallbackManager;
    private boolean isFacebookSdkInitialized = false;
    private static List<String> READ_PERMISSIONS = Arrays.asList("public_profile", "email");
    private static final String TAG = "LoginActivity";

    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken fbAccessToken = loginResult.getAccessToken();
            Logger.i(TAG, "fb access token: " + fbAccessToken);
            GraphRequest request = GraphRequest.newMeRequest(fbAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    // Application code
                    if (response.getError() == null) {
                        Logger.i(TAG, "JSONObject: " + object);
                        Logger.i(TAG, "GraphResponse: " + response);
                        try {
                            String id = object.getString("id");
                            String email = object.optString("email");
                            String name = object.getString("name");
                            String gender = object.getString("gender");
                            if (email.isEmpty()) {
                                Utility.showToastMessage(mActivity, getString(R.string.error_no_email_facebook));
                            } else {
//                                loginRequest(imageUrl, email, ApiDetails.REGISTRATION_MEDIUM.FACEBOOK, loginResult.getAccessToken().getToken());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Utility.showToastMessage(mActivity, response.getError().getErrorUserMessage());
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            // App code
        }

        @Override
        public void onError(FacebookException exception) {
            // App code
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFacebook();
        setContentView(R.layout.activity_login);
        mFacebookLoginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        mFacebookLoginButton.setReadPermissions(READ_PERMISSIONS);

        // Callback registration
        mFacebookLoginButton.registerCallback(mFacebookCallbackManager, mFacebookCallback);
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                isFacebookSdkInitialized = true;
            }
        });
        mFacebookCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
