package in.buzzzz.parser;

import org.json.JSONException;
import org.json.JSONObject;

import in.buzzzz.model.Model;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class MessageParser implements Parser<Model> {
    @Override
    public Model parse(JSONObject json) throws JSONException {
        Model model = new Model();
        model.setStatus(json.getInt(ApiDetails.RESPONSE_KEY_STATUS));
        model.setMessage(json.getString(ApiDetails.RESPONSE_KEY_MESSAGE));
        return model;
    }
}
