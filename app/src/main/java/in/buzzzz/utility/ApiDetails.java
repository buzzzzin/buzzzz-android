package in.buzzzz.utility;

/**
 * Created by Navkrishna on September 25, 2015
 */
public final class ApiDetails {

    public enum ACTION_NAME {
        LOGIN("/auth/login"),
        INTEREST("/interest/list"),
        HOME_BUZZ("/v1/home/buzz"),
        PREVIEW("/buzz/preview/"),
        RSVP("/buzz/rsvp"),
        CREATE_BUZZ("/buzz/save"),
        UPLOAD("UPLOAD"),
        NONE("");

        private final String value;

        ACTION_NAME(String value) {
            this.value = value;
        }

        public String getActionName() {
            return this.value;
        }

        public static ACTION_NAME getActionName(String value) {
            for (ACTION_NAME actionName : values()) {
                if (actionName.getActionName().equals(value)) {
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

    public enum RSVP {
        YES("YES"), NO("NO"), MAY_BE("MAY_BE"), NONE("");
        private final String value;

        RSVP(String value) {
            this.value = value;
        }

        public String getRsvp() {
            return this.value;
        }

        public static RSVP getRsvpName(String value) {
            for (RSVP rsvp : values()) {
                if (rsvp.getRsvp().equals(value)) {
                    return rsvp;
                }
            }
            return NONE;
        }
    }

    public enum PERIOD {
        ONCE,
        DAILY,
        MONTHLY,
        WEEKLY,
        CUSTOM
    }

    public enum MESSAGE_TYPE {
        CHAT, NOTIFICATION
    }

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 0;

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
    public static final String REQUEST_KEY_LATITUDE = "latitude";
    public static final String REQUEST_KEY_LONGITUDE = "longitude";
    public static final String REQUEST_KEY_RADIUS = "radius";
    //    RSVP keys
    public static final String REQUEST_KEY_STATUS = "status";
    public static final String REQUEST_KEY_BUZZ_ID = "buzzId";
    //    Create Buzz
    public static final String REQUEST_KEY_IMAGE_NAME = "imageName";
    public static final String REQUEST_KEY_IS_RSVP = "isRSVP";
    public static final String REQUEST_KEY_ADDRESS = "address";
    public static final String REQUEST_KEY_START_TIME = "startTime";
    public static final String REQUEST_KEY_END_TIME = "endTime";
    public static final String REQUEST_KEY_PERIOD = "period";
    public static final String REQUEST_KEY_TAGS = "tags";
    public static final String REQUEST_KEY_INTERESTS = "interests";

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
    public static final String RESPONSE_KEY_IMAGE = "image";
    public static final String RESPONSE_KEY_IS_SUBSCRIBED = "isSubscribed";

    //    Buzzzz Preview
    public static final String RESPONSE_KEY_BUZZS = "buzzs";
    public static final String RESPONSE_KEY_BUZZ_ID = "buzzId";
    public static final String RESPONSE_KEY_IMAGE_NAME = "imageName";
    public static final String RESPONSE_KEY_IS_RSVB = "isRSVP";
    public static final String RESPONSE_KEY_RSVB_STATUS = "rsvpStatus";
    public static final String RESPONSE_KEY_INTERESTS = "interests";
    public static final String RESPONSE_KEY_LOCATION = "location";
    public static final String RESPONSE_KEY_CITY = "city";
    public static final String RESPONSE_KEY_LATITUDE = "latitude";
    public static final String RESPONSE_KEY_LONGITUDE = "longitude";
    public static final String RESPONSE_KEY_ADDRESS = "address";
    public static final String RESPONSE_KEY_PINCODE = "pincode";
    public static final String RESPONSE_KEY_SCHEDULE = "schedule";
    public static final String RESPONSE_KEY_START_DATE = "startDate";
    public static final String RESPONSE_KEY_END_DATE = "endDate";
    public static final String RESPONSE_KEY_START_TIME = "startTime";
    public static final String RESPONSE_KEY_END_TIME = "endTime";
    public static final String RESPONSE_KEY_PERIOD = "period";
    public static final String RESPONSE_KEY_STATS = "stats";
    public static final String RESPONSE_KEY_GOING_COUNT = "goingCount";
    public static final String RESPONSE_KEY_NOT_COMING_COUNT = "notComingCount";
    public static final String RESPONSE_KEY_MAY_BE_COUNT = "mayBeCount";

    //    REQUEST KEYS
    public static final String REQUEST_KEY_DESTINATION = "destination";
    public static final String REQUEST_KEY_SENDER_ID = "senderId";
    public static final String REQUEST_KEY_SENDER_NAME = "senderName";
    public static final String REQUEST_KEY_MESSAGE = "message";
    public static final String REQUEST_KEY_IMAGE_URL = "imageUrl";
    public static final String REQUEST_KEY_DATA = "data";
    public static final String REQUEST_KEY_TOKEN = "token";
    public static final String REQUEST_KEY_TYPE = "type";

    //    Cloudinary keys
    public static final String REQUEST_KEY_CLOUDINARY_API_KEY = "api_key";
    public static final String REQUEST_KEY_CLOUDINARY_API_SECRET = "api_secret";
    public static final String REQUEST_KEY_CLOUDINARY_FOLDER = "folder";
    public static final String REQUEST_KEY_CLOUDINARY_CLOUD_NAME = "cloud_name";
    public static final String REQUEST_KEY_CLOUDINARY_FORMAT = "format";
    public static final String REQUEST_KEY_CLOUDINARY_ACTION = "action";
    public static final String REQUEST_KEY_CLOUDINARY_IMAGE_PATH = "imagePath";
    // Cloudinary response keypublic static final
    public static final String RESPONSE_KEY_CLOUDINARY_URL = "url";
    public static final String RESPONSE_KEY_CLOUDINARY_PUBLIC_ID = "public_id";
    public static final String RESPONSE_KEY_CLOUDINARY_FORMAT = "format";
}
