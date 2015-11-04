package in.buzzzz.parser;

import org.json.JSONException;
import org.json.JSONObject;

import in.buzzzz.model.Config;
import in.buzzzz.model.Model;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Navkrishna on November 04, 2015
 */
public class ConfigParser implements Parser<Model> {
    @Override
    public Model parse(JSONObject json) throws JSONException {
        Config config = new Config();
        Config.Url url = new Config.Url();
        JSONObject urlJsonObject = json.getJSONObject(ApiDetails.RESPONSE_KEY_URL);
        url.setApi(urlJsonObject.getString(ApiDetails.RESPONSE_KEY_API));
        url.setChat(urlJsonObject.getString(ApiDetails.RESPONSE_KEY_CHAT));
        config.setUrl(url);
        return config;
    }
}
