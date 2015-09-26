package in.buzzzz.utility;

import org.json.JSONObject;

/**
 * Created by Navkrishna on September 25, 2015
 */
public final class ApiDetails {

    public enum ACTION_NAME {
        LOGIN("/auth/login"),
        NONE("");

        private final String value;

        ACTION_NAME(String value) {
            this.value = value;
        }

        public String getActionName() {
            return this.value;
        }

        public static ACTION_NAME getActionName(String period) {
            for (ACTION_NAME actionName : values()) {
                if (actionName.getActionName().equals(period)) {
                    return actionName;
                }
            }
            return NONE;
        }
    }

    public enum MEDIUM {
        FACEBOOK, GPLUS, MANUAL
    }

    public enum GENDER {
        MALE, FEMALE, NOT_SET
    }

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 0;

    public static  final String CHAT_HOST_URL="ws://10.1.14.158:9091";
    public static  final String CHAT_CHANNEL="/buzz/chat/";
    public static final String SECRET_KEY = "myAppKey";

    // HEADER KEYS
    public static final String HEADER_SECRET_KEY = "Secret-Key";
    public static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";

    // REQUEST KEYS
//    Common keys
    public static final String REQUEST_KEY_SECRET_KEY = "secretKey";
    public static final String REQUEST_KEY_ACCESS_TOKEN = "accessToken";

    //    Login keys
    public static final String REQUEST_KEY_NAME = "name";
    public static final String REQUEST_KEY_GENDER = "gender";
    public static final String REQUEST_KEY_EMAIL = "email";
    public static final String REQUEST_KEY_MEDIUM_ID = "mediumId";
    public static final String REQUEST_KEY_MEDIUM_TYPE = "mediumType";

    //    RESPONSE KEYS
    public static final String RESPONSE_KEY_MESSAGE = "message";
    public static final String RESPONSE_KEY_STATUS = "status";
    public static final String RESPONSE_KEY_DATA = "data";

    //    Login Keys
    public static final String RESPONSE_KEY_USER = "user";
    public static final String RESPONSE_KEY_ID = "id";
    public static final String RESPONSE_KEY_NAME = "name";
    public static final String RESPONSE_KEY_GENDER = "gender";
    public static final String RESPONSE_KEY_EMAIL = "email";
    public static final String RESPONSE_KEY_MEDIUM = "medium";
    public static final String RESPONSE_KEY_MEDIUM_ID = "mediumId";
    public static final String RESPONSE_KEY_MEDIUM_TYPE = "mediumType";
    public static final String RESPONSE_KEY_AUTH_TOKEN = "authToken";
    public static final String RESPONSE_KEY_HAS_INTERESTS = "hasInterests";
    //    REQUEST KEYS
    public static final String REQUEST_KEY_ACCESS_TOKEN = "access";


    public static final String REQUEST_KEY_DESTINATION = "destination";
    public static final String REQUEST_KEY_SENDER_ID = "senderId";
    public static final String REQUEST_KEY_SENDER_NAME = "senderName";
    public static final String REQUEST_KEY_MESSAGE = "message";
    public static final String REQUEST_KEY_IMAGE_URL= "imageUrl";
    public static final String REQUEST_KEY_DATA = "data";

}
