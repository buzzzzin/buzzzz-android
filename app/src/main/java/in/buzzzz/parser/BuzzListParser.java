package in.buzzzz.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.buzzzz.model.BuzzPreview;
import in.buzzzz.model.BuzzPreviewList;
import in.buzzzz.model.Model;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class BuzzListParser implements Parser<Model> {
    @Override
    public Model parse(JSONObject json) throws JSONException {
        BuzzPreviewList buzzPreviewList = new BuzzPreviewList();
        buzzPreviewList.setStatus(json.getInt(ApiDetails.RESPONSE_KEY_STATUS));
        buzzPreviewList.setMessage(json.getString(ApiDetails.RESPONSE_KEY_MESSAGE));
        List<BuzzPreview> buzzPreviews = new ArrayList<>();
        if (buzzPreviewList.getStatus() == ApiDetails.STATUS_SUCCESS) {
            JSONObject dataJsonObject = json.getJSONObject(ApiDetails.RESPONSE_KEY_DATA);
            JSONArray buzzsJsonArray = dataJsonObject.getJSONArray(ApiDetails.RESPONSE_KEY_BUZZS);
            for (int index = 0; buzzsJsonArray != null && index < buzzsJsonArray.length(); index++) {
                BuzzPreview buzzPreview = (BuzzPreview) new BuzzPreviewParser().buzzParser(buzzsJsonArray.getJSONObject(index));
                buzzPreviews.add(buzzPreview);
            }
        }
        buzzPreviewList.setBuzzPreviews(buzzPreviews);
        return buzzPreviewList;
    }
}
