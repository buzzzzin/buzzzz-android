package in.buzzzz.parser;

import org.json.JSONException;
import org.json.JSONObject;

import in.buzzzz.model.BuzzList;
import in.buzzzz.model.InterestInfo;
import in.buzzzz.model.Model;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Rajendra Singh on 26/9/15.
 */
public class HomeBuzzParser implements Parser<Model> {

    @Override
    public Model parse(JSONObject json) throws JSONException {
        InterestInfo interestInfo;
        BuzzList buzzList = new BuzzList();
        buzzList.setStatus(json.getInt(ApiDetails.RESPONSE_KEY_STATUS));
        buzzList.setMessage(json.getString(ApiDetails.RESPONSE_KEY_MESSAGE));
        if (buzzList.getStatus() == ApiDetails.STATUS_SUCCESS) {

            if (json.has(ApiDetails.RESPONSE_KEY_INTERESTS)) {
                InterestParser interestParser = new InterestParser();
                interestInfo = (InterestInfo) interestParser.parse(json);
                buzzList.setmInterestList(interestInfo.getInterestList());
            }
        }
        return buzzList;
    }
}
