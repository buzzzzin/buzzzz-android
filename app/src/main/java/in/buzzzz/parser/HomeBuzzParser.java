package in.buzzzz.parser;

import org.json.JSONException;
import org.json.JSONObject;

import in.buzzzz.model.BuzzList;
import in.buzzzz.model.BuzzPreviewList;
import in.buzzzz.model.InterestInfo;
import in.buzzzz.model.Model;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Rajendra Singh on 26/9/15.
 */
public class HomeBuzzParser implements Parser<Model> {

    @Override
    public Model parse(JSONObject json) throws JSONException {

        BuzzList buzzList = new BuzzList();
        buzzList.setStatus(json.getInt(ApiDetails.RESPONSE_KEY_STATUS));
        buzzList.setMessage(json.getString(ApiDetails.RESPONSE_KEY_MESSAGE));
        if (buzzList.getStatus() == ApiDetails.STATUS_SUCCESS) {

            JSONObject dataJsonObject = json.getJSONObject(ApiDetails.RESPONSE_KEY_DATA);

            if (dataJsonObject.has(ApiDetails.RESPONSE_KEY_INTERESTS)) {
                InterestParser interestParser = new InterestParser();
                InterestInfo interestInfo = (InterestInfo) interestParser.parse(json);
                buzzList.setInterestList(interestInfo.getInterestList());
            }

            if (dataJsonObject.has(ApiDetails.RESPONSE_KEY_BUZZS)) {

                BuzzListParser buzzListParser = new BuzzListParser();
                BuzzPreviewList buzzPreview = (BuzzPreviewList) buzzListParser.parse(json);

                buzzList.setBuzzPreviewList(buzzPreview.getBuzzPreviews());

            }
        }
        return buzzList;
    }
}
