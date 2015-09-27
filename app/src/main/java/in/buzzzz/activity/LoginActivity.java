package in.buzzzz.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import in.buzzzz.R;
import in.buzzzz.loader.APICaller;
import in.buzzzz.loader.LoaderCallback;
import in.buzzzz.model.Login;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.LoginParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

/**
 * Created by Navkrishna on September 25, 2015
 */
public class LoginActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 1001;
    private LoginButton mFacebookLoginButton;
    private SignInButton mSignInButton;
    private CallbackManager mFacebookCallbackManager;
    private boolean isFacebookSdkInitialized = false;
    private static List<String> READ_PERMISSIONS = Arrays.asList("public_profile", "email");
    private static final String TAG = "LoginActivity";

    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Logger.i(TAG, "onConnected:" + bundle);
            mShouldResolve = false;

            getGPlusProfileInfo();
        }

        @Override
        public void onConnectionSuspended(int cause) {

        }
    };
    GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            if (!mIsResolving && mShouldResolve) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(mActivity, RC_SIGN_IN);
                        mIsResolving = true;
                    } catch (IntentSender.SendIntentException e) {
                        Logger.e(TAG, "Could not resolve ConnectionResult.", e);
                        mIsResolving = false;
                        mGoogleApiClient.connect();
                    }
                } else {
                    // Could not resolve the connection result, show the user an
                    // error dialog.
//                    showErrorDialog(connectionResult);
                }
            } else {
                // Show the signed-out UI
//                showSignedOutUI();
            }
        }
    };
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sign_in_button_google_plus:
                    onSignInClicked();
                    break;
            }
        }
    };

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
                            String mediumId = object.getString("id");
                            String email = object.optString("email");
                            String name = object.getString("name");
                            String gender = object.getString("gender");
                            if (email.isEmpty()) {
                                Utility.showToastMessage(mActivity, getString(R.string.error_no_email_facebook));
                            } else {
                                requestLogin(name, Utility.getGender(gender),
                                        email, mediumId, ApiDetails.MEDIUM.FACEBOOK);
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
            Utility.showToastMessage(mActivity, "Login cancelled");
        }

        @Override
        public void onError(FacebookException exception) {
            Utility.showToastMessage(mActivity, exception.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFacebook();
        setContentView(R.layout.activity_login);
        initGoogleClient();
        linkView();
    }

    private void linkView() {
        mFacebookLoginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button_google_plus);

        /*ViewGroup.LayoutParams layoutParamsGPlus = mFacebookLoginButton.getLayoutParams();
        mSignInButton.setLayoutParams(layoutParamsGPlus);*/

        mFacebookLoginButton.setReadPermissions(READ_PERMISSIONS);
        mFacebookLoginButton.registerCallback(mFacebookCallbackManager, mFacebookCallback);

        mSignInButton.setOnClickListener(mOnClickListener);

        ImageView imageView = (ImageView) findViewById(R.id.imageview_splash);

        Point point = Utility.getDisplayPoint(mActivity);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = (int) (point.x / 1.25);
        layoutParams.height = (int) (point.x / 1.25);
        imageView.setLayoutParams(layoutParams);
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

    private void initGoogleClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    private void getGPlusProfileInfo() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String googleDisplayName = currentPerson.getDisplayName();
                String googlePersonEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Logger.i(TAG, "Gender: " + currentPerson.getGender());
                Logger.i(TAG, "Name: " + googleDisplayName + ", plusProfile: "
                        + ", email: " + googlePersonEmail);
                requestLogin(googleDisplayName,
                        Utility.getGender(currentPerson.getGender()),
                        googlePersonEmail,
                        currentPerson.getId(),
                        ApiDetails.MEDIUM.GPLUS);
            } else {
                Utility.showToastMessage(mActivity, "Person information is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
//        mStatus.setText(R.string.signing_in);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        Logger.i(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void requestLogin(String name, ApiDetails.GENDER gender, String email, String mediumId, ApiDetails.MEDIUM medium) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiDetails.REQUEST_KEY_NAME, name);
        params.put(ApiDetails.REQUEST_KEY_GENDER, String.valueOf(gender));
        params.put(ApiDetails.REQUEST_KEY_EMAIL, email);
        params.put(ApiDetails.REQUEST_KEY_MEDIUM_ID, mediumId);
        params.put(ApiDetails.REQUEST_KEY_MEDIUM_TYPE, String.valueOf(medium));

        Request request = new Request(ApiDetails.ACTION_NAME.LOGIN);
        request.setUrl(Api.BASE_URL_API + ApiDetails.ACTION_NAME.LOGIN.getActionName());
        request.setShowDialog(true);
        request.setDialogMessage(getString(R.string.msg_login_progress));
        request.setParamMap(params);
        request.setRequestType(Request.HttpRequestType.POST);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new LoginParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {
                if (model.getStatus() == ApiDetails.STATUS_SUCCESS) {
                    if (model instanceof Login) {
                        Login login = (Login) model;
                        SharedPreference.saveLoginInfo(mActivity, (Login) model);
                        if (login.hasInterests()) {
                            showHomeScreen();
                        } else {
                            showInterestScreen();
                        }
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

    private void showHomeScreen() {
        Intent intent = new Intent(mActivity, HomeScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void showInterestScreen() {
        Intent intent = new Intent(mActivity, InterestActivity.class);
        startActivity(intent);
        finish();

    }
}
