package in.buzzzz.utility;

import org.json.JSONObject;

/**
 * Created by Navkrishna on September 25, 2015
 */
public final class ApiDetails {

    public enum ACTION_NAME {
        GET, POST
    }

    public static  final String CHAT_HOST_URL="ws://10.1.14.158:9091";
    public static  final String CHAT_CHANNEL="/buzz/chat/";
    public static final String SECRET_KEY = "myAppKey";

    // HEADER KEYS
    public static final String HEADER_SECRET_KEY = "SecretKey";

    //    REQUEST KEYS
    public static final String REQUEST_KEY_ACCESS_TOKEN = "access";


    public static final String REQUEST_KEY_DESTINATION = "destination";
    public static final String REQUEST_KEY_SENDER_ID = "senderId";
    public static final String REQUEST_KEY_SENDER_NAME = "senderName";
    public static final String REQUEST_KEY_MESSAGE = "message";
    public static final String REQUEST_KEY_IMAGE_URL= "imageUrl";
    public static final String REQUEST_KEY_DATA = "data";

}
