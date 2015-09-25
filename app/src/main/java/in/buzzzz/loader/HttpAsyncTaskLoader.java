package in.buzzzz.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.buzzzz.R;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.model.Response;
import in.buzzzz.parser.Parser;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

public class HttpAsyncTaskLoader extends AsyncTaskLoader<Model> {
    private Request request;
    private Parser parser;
    private Context context;
    private Response serverResponse;

    public HttpAsyncTaskLoader(Context context) {
        super(context);
    }

    public HttpAsyncTaskLoader(Context context, Request request, Parser parser) {
        super(context);
        this.context = context;
        this.request = request;
        this.parser = parser;
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public Model loadInBackground() {
        switch (request.getRequestType()) {
            case POST:
                serverResponse = Utility.doPost(context, request.getUrl(), getJsonParam(request.getParamMap(), true));
                break;
            case CLOUDINARY:
                // PARKED FEATURE FOR EDIT PROFILE
                break;
            case GET:
                break;
        }
        Model model = null;
        if (serverResponse != null) {
            model = parseResponse(serverResponse);
        }
        return model;
    }

    private Model parseResponse(Response serverResponse) {
        if (serverResponse.isError()) {
            Model model = new Model();
            model.setStatus(0);
            model.setMessage(serverResponse.getErrorMsg());
            model.setHttpStatusCode(serverResponse.getHttpStatusCode());
            return model;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(serverResponse.getResponse());
                Model model = parser.parse(jsonObject);
                return model;
            } catch (JSONException e) {
                e.printStackTrace();
                Logger.i("exception", e.toString());
                Model model = new Model();
                model.setStatus(0);
                model.setMessage(context.getString(R.string.msg_5xx));
                return model;
            }
        }
    }

    @Override
    public void deliverResult(Model data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(Model apps) {
        super.onCanceled(apps);
        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
    }

    /*
        convert a hashmap into a jSonObject(recusively).
        A hashmap object is converted into JSonObject
        A ArrayList<Hashmap> is converted into a jSonArray of JSonObjects
        To pass the jsonarray inside the jsonobject put the arraylist in hashmap.
     */

    public JSONObject getJsonParam(HashMap<String, ?> paramMap, boolean addKeys) {
        String temp;
        if (paramMap == null) {
            return null;
        }
        JSONObject jsonObject;
        if (addKeys) {
            jsonObject = getKeysJson();
        } else {
            jsonObject = new JSONObject();
        }
        for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
            try {

                if (entry.getValue() instanceof String) {
                    temp = (String) entry.getValue();
                    if (temp != null) {
                        if (temp.startsWith("[")
                                && temp.endsWith("]")) {
                            JSONArray jsonArray = new JSONArray(temp);
                            jsonObject.put(entry.getKey(), jsonArray);
                        } else {
                            jsonObject.put(entry.getKey(), temp);
                        }
                    }
                } else if (entry.getValue() instanceof HashMap) {
                    jsonObject.put(entry.getKey(), getJsonParam((HashMap) entry.getValue(), false));
                } else if (entry.getValue() instanceof ArrayList) {
                    temp = (String) entry.getKey();
                    ArrayList list = (ArrayList) entry.getValue();
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < list.size(); i++) {
                        jsonArray.put(getJsonParam((HashMap) list.get(i), false));
                    }
                    jsonObject.put(entry.getKey(), jsonArray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private JSONObject getKeysJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ApiDetails.REQUEST_KEY_SECRET_KEY, ApiDetails.SECRET_KEY);
            jsonObject.put(ApiDetails.REQUEST_KEY_ACCESS_TOKEN, SharedPreference.getString(context, AppConstants.PREF_KEY_AUTH_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(Model model) {
        model = null;
    }
}
