package in.buzzzz.loader;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.os.Bundle;

import java.net.HttpURLConnection;

import in.buzzzz.activity.SplashActivity;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.Parser;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

/**
 * Created by rajendra on 17/9/14.
 */
public class LoaderCallback implements LoaderManager.LoaderCallbacks<Model> {
    private Request request;
    private Activity activity;
    DialogInterface.OnCancelListener mOnCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            activity.getLoaderManager().destroyLoader(request.getId());
        }
    };
    private Parser parser;
    private APICaller apiCaller;
    private ProgressDialog pd;

    public LoaderCallback(Activity activity, Parser parser) {
        this.activity = activity;
        this.parser = parser;
    }

    public void setServerResponse(APICaller apiCaller) {
        this.apiCaller = apiCaller;
    }

    @Override
    public void onLoaderReset(Loader<Model> modelLoader) {
        modelLoader = null;
    }

    @Override
    public void onLoadFinished(Loader<Model> modelLoader, Model model) {
        dismissDialog();
        activity.getLoaderManager().destroyLoader(request.getId());
        onResponseFromServer(model);
    }

    public void onResponseFromServer(Model model) {
        // storing is account verified in shared preferences
        if (!SharedPreference.getBoolean(activity, AppConstants.PREF_KEY_IS_ACCOUNT_VERIFIED))
            SharedPreference.setBoolean(activity, AppConstants.PREF_KEY_IS_ACCOUNT_VERIFIED, model.isAccountVerified());
        if (model.getHttpStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            showMsg(model.getMessage());
            SharedPreference.clearLoggedInInfo(activity);
            Intent startOverIntent = new Intent(activity, SplashActivity.class);
            startOverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//          startOverIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
            activity.startActivity(startOverIntent);
        }
        apiCaller.onComplete(model);
    }

    @Override
    public Loader<Model> onCreateLoader(int i, Bundle bundle) {
        boolean showDialog = bundle.getBoolean("showDialog", true);
        if (showDialog) {
            // To prevent crash when activity state is finished and app tries to show dialog
            if (activity != null && !activity.isFinishing())
                showDialog(activity);
        }
        return new HttpAsyncTaskLoader(activity, request, parser);
    }

    public final boolean requestToServer(Request request) {
        /**
         * Checking the network Here. That network connection is available or not.
         */
        if (!hasConnectivity(activity)) {
            return false;
        }
        this.request = request;
        Bundle bundle = new Bundle();
        bundle.putBoolean("showDialog", request.isShowDialog());
        activity.getLoaderManager().initLoader(request.getId(), bundle, this);
        return true;
    }

    public boolean hasConnectivity(Context context) {
        boolean rc = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                    && cm.getActiveNetworkInfo().isConnected()) {
                rc = true;
            }
        }
        return rc;
    }

    public void showMsg(String msg) {
        Utility.showToastMessage(activity, msg);
    }

    void showDialog(Context context) {
        if (context == null)
            return;
        if (pd != null) {
            pd.dismiss();
        }
        pd = new ProgressDialog(context);
        pd.setMessage(request.getDialogMessage());
        pd.setCancelable(request.isDialogCancelable());
        pd.setIndeterminate(true);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(mOnCancelListener);
        pd.show();
    }

    void dismissDialog() {
        if (pd != null) {
            pd.dismiss();
        }
    }
}
