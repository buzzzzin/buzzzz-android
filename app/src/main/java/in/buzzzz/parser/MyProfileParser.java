package in.buzzzz.parser;

import org.json.JSONException;
import org.json.JSONObject;

import in.buzzzz.model.BuzzList;
import in.buzzzz.model.Model;
import in.buzzzz.model.MyProfile;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class MyProfileParser implements Parser<Model> {
    @Override
    public Model parse(JSONObject json) throws JSONException {
        MyProfile myProfile = new MyProfile();
        myProfile.setStatus(json.getInt(ApiDetails.RESPONSE_KEY_STATUS));
        myProfile.setMessage(json.getString(ApiDetails.RESPONSE_KEY_MESSAGE));
        if (myProfile.getStatus() == ApiDetails.STATUS_SUCCESS) {
            JSONObject dataJsonObject = json.getJSONObject(ApiDetails.RESPONSE_KEY_DATA);
            JSONObject userJsonObject = dataJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_USER);
            myProfile.setId(userJsonObject.getString(ApiDetails.RESPONSE_KEY_ID));
            myProfile.setName(userJsonObject.getString(ApiDetails.RESPONSE_KEY_NAME));
            myProfile.setGender(ApiDetails.GENDER.valueOf(userJsonObject.getString(ApiDetails.RESPONSE_KEY_GENDER)));
            myProfile.setEmail(userJsonObject.getString(ApiDetails.RESPONSE_KEY_EMAIL));
            myProfile.setMobile(userJsonObject.getString(ApiDetails.RESPONSE_KEY_MOBILE));
            myProfile.setCountry(userJsonObject.getString(ApiDetails.RESPONSE_KEY_COUNTRY));

            JSONObject mediumJsonObject = userJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_MEDIUM);
            MyProfile.Medium medium = new MyProfile.Medium();
            medium.setMediumId(mediumJsonObject.getString(ApiDetails.RESPONSE_KEY_MEDIUM_ID));
            medium.setMediumType(mediumJsonObject.getString(ApiDetails.RESPONSE_KEY_MEDIUM_TYPE));
            myProfile.setMedium(medium);

            JSONObject statsJsonObject = dataJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_STATS);
            MyProfile.Stats stats = new MyProfile.Stats();
            stats.setBuzzCount(statsJsonObject.getString(ApiDetails.RESPONSE_KEY_BUZZ_COUNT));
            stats.setInterestCount(statsJsonObject.getString(ApiDetails.RESPONSE_KEY_INTEREST_COUNT));
            myProfile.setStats(stats);

            BuzzList buzzList = (BuzzList) new HomeBuzzParser().parse(json);
            myProfile.setInterests(buzzList.getInterestList());
            myProfile.setBuzzs(buzzList.getBuzzPreviewList());
        }
        return myProfile;
    }
}
