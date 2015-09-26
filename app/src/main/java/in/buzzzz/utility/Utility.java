package in.buzzzz.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import in.buzzzz.R;
import in.buzzzz.model.Response;

/**
 * Created by Navkrishna on September 25, 2015
 */
public final class Utility {

    private static final int timeoutConnection = 60 * 1000;

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
    }

    public static Response doPost(Context context, String url, JSONObject jsonObject) {
        Logger.i("url", url);
        Response response = new Response();
        if (!isNetworkAvailable(context)) {
            String error = context.getResources().getString(R.string.no_network);
            response.setError(true);
            response.setErrorMsg(error);
            return response;
        }
        try {
            String encodeUrl = encodeURL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(encodeUrl).openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Accept-Language", getLocaleLanguageTag(context));
            if (jsonObject.has(ApiDetails.REQUEST_KEY_ACCESS_TOKEN) && !jsonObject.getString(ApiDetails.REQUEST_KEY_ACCESS_TOKEN).isEmpty()) {
                urlConnection.setRequestProperty(ApiDetails.HEADER_X_AUTH_TOKEN, jsonObject.getString(ApiDetails.REQUEST_KEY_ACCESS_TOKEN));
                Logger.i(ApiDetails.HEADER_X_AUTH_TOKEN, jsonObject.getString(ApiDetails.REQUEST_KEY_ACCESS_TOKEN));
            }
            jsonObject.remove(ApiDetails.REQUEST_KEY_ACCESS_TOKEN);
            if (jsonObject.has(ApiDetails.REQUEST_KEY_SECRET_KEY) && !jsonObject.getString(ApiDetails.REQUEST_KEY_SECRET_KEY).isEmpty()) {
                urlConnection.setRequestProperty(ApiDetails.HEADER_SECRET_KEY, jsonObject.getString(ApiDetails.REQUEST_KEY_SECRET_KEY));
                Logger.i(ApiDetails.HEADER_SECRET_KEY, jsonObject.getString(ApiDetails.REQUEST_KEY_SECRET_KEY));
            }
            jsonObject.remove(ApiDetails.REQUEST_KEY_SECRET_KEY);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(timeoutConnection);
            Logger.i("API Url::", url + " " + jsonObject.toString());
            OutputStream os = urlConnection.getOutputStream();
            os.write(jsonObject.toString().getBytes());
            os.flush();
            generateResponse(context, response, urlConnection);
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            getDefaultResponse(response, context.getString(R.string.msg_5xx));
        }
        Logger.i(response.getResponse());
        return response;
    }

    public static Response doGet(Context context, String url) {
        Logger.i("url", url);
        Response response = new Response();
        if (!isNetworkAvailable(context)) {
            String error = context.getResources().getString(R.string.no_network);
            response.setError(true);
            response.setErrorMsg(error);
            return response;
        }
        try {
            String encodeUrl = encodeURL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(encodeUrl).openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Accept-Language", getLocaleLanguageTag(context));

            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(timeoutConnection);
            generateResponse(context, response, urlConnection);
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            getDefaultResponse(response, context.getString(R.string.msg_5xx));
        }
        Logger.i("print response", response.getResponse());
        return response;
    }

    public static String getLocaleLanguageTag(Context context) {
        String languageTag = "en";
        Locale locale = context.getResources().getConfiguration().locale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (locale != null) {
            languageTag = locale.toString().replaceAll("_", "-");
        }
        return languageTag;
    }

    private static void generateResponse(Context context, Response response, HttpURLConnection urlConnection) throws IOException {
        final int responseCode = urlConnection.getResponseCode();
        Logger.i("Response Code: " + responseCode);
        response.setHttpStatusCode(responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String result = readStream(urlConnection.getInputStream());
            response.setError(false);
            response.setResponse(result);
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            getDefaultResponse(response, context.getString(R.string.msg_401));
        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            getDefaultResponse(response, context.getString(R.string.msg_404));
        } else if (responseCode >= 400 && responseCode < 500) {
            getDefaultResponse(response, context.getString(R.string.msg_4xx));
        } else {
            getDefaultResponse(response, context.getString(R.string.msg_5xx));
        }
    }

    /*
    *
    * Below Method is used for the utility of the get and post method.
     */
    private static String readStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        reader.close();
        return out.toString();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (connMgr != null) {
                if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED
                        || connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTING) {
                    return true;
                } else if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                        || connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTING) {
                    return true;
                } else
                    return false;
            } else {
                return false;
            }
        } catch (Exception e) {

        }
        return false;
    }

    private static String encodeURL(String url) {
        return url.replaceAll(" ", "%20");
    }

    private static Response getDefaultResponse(Response response, String message) {
        response.setError(true);
        response.setErrorMsg(message);

        return response;
    }

    /**
     * From Facebook
     *
     * @param gender gender in String
     * @return GENDER
     */
    public static ApiDetails.GENDER getGender(String gender) {
        if (gender != null) {
            if ("male".equalsIgnoreCase(gender)) {
                return ApiDetails.GENDER.MALE;
            } else if ("female".equalsIgnoreCase(gender)) {
                return ApiDetails.GENDER.FEMALE;
            } else {
                return ApiDetails.GENDER.NOT_SET;
            }
        }
        return ApiDetails.GENDER.NOT_SET;
    }

    public static ApiDetails.GENDER getGender(int gender) {
        switch (gender) {
            case 0:
                return ApiDetails.GENDER.MALE;
            case 1:
                return ApiDetails.GENDER.FEMALE;
            default:
                return ApiDetails.GENDER.NOT_SET;
        }
    }
}
