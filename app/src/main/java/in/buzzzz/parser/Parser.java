package in.buzzzz.parser;

import org.json.JSONException;
import org.json.JSONObject;

import in.buzzzz.model.Model;

/**
 * Created by Navkrishna on September 25, 2015
 */
public interface Parser<T extends Model> {
    T parse(JSONObject json) throws JSONException;
}
