package in.buzzzz.parser;

import org.json.JSONException;
import org.json.JSONObject;

import in.buzzzz.model.Login;
import in.buzzzz.model.Model;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class LoginParser implements Parser<Model> {
    @Override
    public Model parse(JSONObject json) throws JSONException {
        Login login = new Login();
        login.setStatus(json.getInt(ApiDetails.RESPONSE_KEY_STATUS));
        login.setMessage(json.getString(ApiDetails.RESPONSE_KEY_MESSAGE));
        if (login.getStatus() == ApiDetails.STATUS_SUCCESS) {
            JSONObject dataJsonObject = json.getJSONObject(ApiDetails.RESPONSE_KEY_DATA);
            login.setHasInterests(dataJsonObject.getBoolean(ApiDetails.RESPONSE_KEY_HAS_INTERESTS));
            login.setAuthToken(dataJsonObject.getString(ApiDetails.RESPONSE_KEY_AUTH_TOKEN));

            JSONObject userJsonObject = dataJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_USER);
            login.setId(userJsonObject.getString(ApiDetails.RESPONSE_KEY_ID));
            login.setName(userJsonObject.getString(ApiDetails.RESPONSE_KEY_NAME));
            login.setGender(userJsonObject.getString(ApiDetails.RESPONSE_KEY_GENDER));
            login.setEmail(userJsonObject.getString(ApiDetails.RESPONSE_KEY_EMAIL));

            JSONObject mediumJsonObject = userJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_MEDIUM);
            login.setMediumType(mediumJsonObject.getString(ApiDetails.RESPONSE_KEY_MEDIUM_TYPE));
            login.setMediumId(mediumJsonObject.getString(ApiDetails.RESPONSE_KEY_MEDIUM_ID));
        }

        return login;
    }
}
