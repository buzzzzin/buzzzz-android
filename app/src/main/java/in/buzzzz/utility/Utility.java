package in.buzzzz.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
            urlConnection.setRequestProperty(ApiDetails.HEADER_X_AUTH_TOKEN, SharedPreference.getString(context, AppConstants.PREF_KEY_AUTH_TOKEN));
            jsonObject.remove(ApiDetails.REQUEST_KEY_ACCESS_TOKEN);
            urlConnection.setRequestProperty(ApiDetails.HEADER_SECRET_KEY, ApiDetails.SECRET_KEY);
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
            urlConnection.setRequestProperty(ApiDetails.HEADER_X_AUTH_TOKEN, SharedPreference.getString(context, AppConstants.PREF_KEY_AUTH_TOKEN));
            urlConnection.setRequestProperty(ApiDetails.HEADER_SECRET_KEY, ApiDetails.SECRET_KEY);

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

    public static Response uploadOnCloudinary(Context context, HashMap<String, String> config, String imagePath) {
        Response response = new Response();
        Cloudinary cloudinary = new Cloudinary(config);
        try {
            String serverResponse = "";
            // Upload image from url e.g facebook, google+
            if (imagePath.startsWith("http")) {
                serverResponse = String.valueOf(cloudinary.uploader().upload(imagePath, config));
            }
            // upload image from SD Card
            else {
                Logger.i("imagePath", imagePath + "");
                Logger.i("config", config.toString());
                File file = new File(imagePath);
                InputStream inputStream = new FileInputStream(file);
                serverResponse = String.valueOf(cloudinary.uploader().upload(inputStream, config));
            }
            Logger.i("serverResponse", serverResponse + "");
            response.setError(false);
            response.setResponse(serverResponse);
        } catch (Exception exception) {
            exception.printStackTrace();
            response.setError(true);
            response.setErrorMsg(context.getString(R.string.msg_image_not_uploaded));
        }
        return response;
    }

    public static Response destroyImage(Context context, HashMap<String, String> config) {
        Response response = new Response();
        Cloudinary cloudinary = new Cloudinary(config);
        try {
            String serverResponse = "";
            String publicID = config.get("public_id");
            Logger.i("publicID", publicID + "");
            config.remove("public_id");
            config.put("timestamp", Long.valueOf(System.currentTimeMillis() / 1000L).toString());
            Logger.i("config", config.toString());
            serverResponse = String.valueOf(cloudinary.uploader().destroy(publicID, config));
            response.setError(false);
            response.setResponse(serverResponse);
        } catch (Exception exception) {
            exception.printStackTrace();
            response.setError(true);
            response.setErrorMsg(context.getString(R.string.msg_image_not_deleted));
        }
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

    public static void setImageFromUrl(String imageUrl, ImageView imageView, int placeholderResId) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(placeholderResId)
                .showImageOnFail(placeholderResId)
                .showImageOnLoading(placeholderResId)
                .resetViewBeforeLoading(true).cacheInMemory(true).build();
        ImageLoader.getInstance().displayImage(imageUrl, imageView, options, null);
    }

    public static Point getDisplayPoint(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static boolean isLocationProviderEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean network_enabled = false;
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gps_enabled || network_enabled;
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File albumF = getAlbumDir(context);
        File imageF = File.createTempFile(imageFileName, "JPG", albumF);
        return imageF;
    }

    private static File getAlbumDir(Context context) {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new BaseAlbumDirFactory().getAlbumStorageDir(getAlbumName(context));
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Logger.i("not mounted", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Logger.i("not mounted", "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    private static String getAlbumName(Context context) {
        return context.getString(R.string.app_name);
    }

    public static Bitmap getBitmap(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Logger.i("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            // rotating bitmap
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
