package in.buzzzz.parser;

import org.json.JSONException;
import org.json.JSONObject;

import in.buzzzz.model.CloudinaryDetail;
import in.buzzzz.model.Model;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.Logger;

/**
 * Created by Navkrishna on September 27, 2015
 */
public class CloudinaryParser implements Parser<Model> {
    @Override
    public Model parse(JSONObject json) throws JSONException {
        CloudinaryDetail detail = new CloudinaryDetail();
        Logger.i("json in parser", json.toString());
        if (json.has(ApiDetails.RESPONSE_KEY_CLOUDINARY_URL) && json.has(ApiDetails.RESPONSE_KEY_CLOUDINARY_PUBLIC_ID) && json.has(ApiDetails.RESPONSE_KEY_CLOUDINARY_FORMAT)) {
            detail.setImageUrl(json.getString(ApiDetails.RESPONSE_KEY_CLOUDINARY_URL));
            detail.setPublicId(json.getString(ApiDetails.RESPONSE_KEY_CLOUDINARY_PUBLIC_ID));
            detail.setFormat(json.getString(ApiDetails.RESPONSE_KEY_CLOUDINARY_FORMAT));
        }
        return detail;
    }
}