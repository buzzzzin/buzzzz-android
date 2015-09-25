package in.buzzzz.model;

import java.util.HashMap;

import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.Logger;

/**
 * Created by Navkrishna on September 25, 2015
 */
public class Request extends Model {

    public enum HttpRequestType {
        GET, POST, CLOUDINARY
    }

    private String dialogMessage;
    private boolean showDialog = true;
    private boolean isDialogCancelable = true;
    private String url;
    private final int id;
    private HashMap<String, String> paramMap;
    private HttpRequestType requestType;

    private static final String TAG = "Request";

    public Request(ApiDetails.ACTION_NAME actionName) {
        Logger.i(TAG, "Ordinal: " + actionName.ordinal());
        id = actionName.ordinal();
    }

    public boolean isDialogCancelable() {
        return isDialogCancelable;
    }

    public void setIsDialogCancelable(boolean isDialogCancelable) {
        this.isDialogCancelable = isDialogCancelable;
    }

    public int getId() {
        return id;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(HashMap<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public HttpRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(HttpRequestType requestType) {
        this.requestType = requestType;
    }
}
