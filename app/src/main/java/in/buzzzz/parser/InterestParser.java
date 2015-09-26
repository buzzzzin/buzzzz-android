package in.buzzzz.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.buzzzz.model.Interest;
import in.buzzzz.model.InterestInfo;
import in.buzzzz.model.Model;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Rajendra Singh on 26/9/15.
 */
public class InterestParser implements Parser<Model> {
    @Override
    public Model parse(JSONObject json) throws JSONException {
        InterestInfo interestInfo = new InterestInfo();
        interestInfo.setStatus(json.getInt(ApiDetails.RESPONSE_KEY_STATUS));
        interestInfo.setMessage(json.getString(ApiDetails.RESPONSE_KEY_MESSAGE));
        if (interestInfo.getStatus() == ApiDetails.STATUS_SUCCESS) {
            JSONObject dataJSONObject = json.getJSONObject(ApiDetails.RESPONSE_KEY_DATA);
            JSONArray interestsJSONArray = dataJSONObject.getJSONArray(ApiDetails.RESPONSE_KEY_INTERESTS);
            List<Interest> interestArrayList = new ArrayList<Interest>();
            for (int i = 0; i < interestsJSONArray.length(); i++) {
                Interest interest = new Interest();
                JSONObject jsonObject = interestsJSONArray.getJSONObject(i);
                interest.setImageName(jsonObject.getString(ApiDetails.RESPONSE_KEY_IMAGE));
                interest.setId(jsonObject.getString(ApiDetails.RESPONSE_KEY_ID));
                interest.setIsSubscribed(jsonObject.getString(ApiDetails.RESPONSE_KEY_IS_SUBSCRIBED));
                interest.setName(jsonObject.getString(ApiDetails.RESPONSE_KEY_NAME));
                interestArrayList.add(interest);
            }
            interestInfo.setInterestList(interestArrayList);
        }

        return interestInfo;
    }
}
