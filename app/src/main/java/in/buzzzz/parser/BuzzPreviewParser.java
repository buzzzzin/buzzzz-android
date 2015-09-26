package in.buzzzz.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.buzzzz.model.BuzzPreview;
import in.buzzzz.model.Model;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class BuzzPreviewParser implements Parser<Model> {
    BuzzPreview buzzPreview = new BuzzPreview();

    @Override
    public Model parse(JSONObject json) throws JSONException {
        buzzPreview.setStatus(json.getInt(ApiDetails.RESPONSE_KEY_STATUS));
        buzzPreview.setMessage(json.getString(ApiDetails.RESPONSE_KEY_MESSAGE));
        if (buzzPreview.getStatus() == ApiDetails.STATUS_SUCCESS) {
            JSONObject dataJsonObject = json.getJSONObject(ApiDetails.RESPONSE_KEY_DATA);
            buzzParser(dataJsonObject);
        }
        return buzzPreview;
    }

    public Model buzzParser(JSONObject buzzJsonObject) throws JSONException {
        buzzPreview.setBuzzId(buzzJsonObject.getString(ApiDetails.RESPONSE_KEY_BUZZ_ID));
        buzzPreview.setName(buzzJsonObject.getString(ApiDetails.RESPONSE_KEY_NAME));
        buzzPreview.setImageName(buzzJsonObject.getString(ApiDetails.RESPONSE_KEY_IMAGE_NAME));
        buzzPreview.setIsRSVP(buzzJsonObject.getBoolean(ApiDetails.RESPONSE_KEY_IS_RSVB));

        JSONArray interestJsonArray = buzzJsonObject.getJSONArray(ApiDetails.RESPONSE_KEY_INTERESTS);
        List<String> interests = new ArrayList<>();
        for (int index = 0; interestJsonArray != null && index < interestJsonArray.length(); index++) {
            interests.add(interestJsonArray.getString(index));
        }
        buzzPreview.setInterests(interests);

        buzzPreview.setRsvp(ApiDetails.RSVP.getRsvpName(buzzJsonObject.getString(ApiDetails.RESPONSE_KEY_RSVB_STATUS)));

        BuzzPreview.Location location = new BuzzPreview.Location();
        JSONObject locationJsonObject = buzzJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_LOCATION);
        location.setCity(locationJsonObject.getString(ApiDetails.RESPONSE_KEY_CITY));
        location.setLatitude(locationJsonObject.getString(ApiDetails.RESPONSE_KEY_LATITUDE));
        location.setLongitude(locationJsonObject.getString(ApiDetails.RESPONSE_KEY_LONGITUDE));
        location.setAddress(locationJsonObject.getString(ApiDetails.RESPONSE_KEY_ADDRESS));
        location.setPincode(locationJsonObject.getString(ApiDetails.RESPONSE_KEY_PINCODE));
        buzzPreview.setLocation(location);

        BuzzPreview.Schedule schedule = new BuzzPreview.Schedule();
        JSONObject scheduleJsonObject = buzzJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_SCHEDULE);
        schedule.setStartDate(scheduleJsonObject.getString(ApiDetails.RESPONSE_KEY_START_DATE));
        schedule.setEndDate(scheduleJsonObject.getString(ApiDetails.RESPONSE_KEY_END_DATE));
        schedule.setStartTime(scheduleJsonObject.getString(ApiDetails.RESPONSE_KEY_START_TIME));
        schedule.setEndTime(scheduleJsonObject.getString(ApiDetails.RESPONSE_KEY_END_TIME));
        buzzPreview.setSchedule(schedule);

        BuzzPreview.Stats stats = new BuzzPreview.Stats();
        JSONObject statsJsonObject = buzzJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_STATS);
        stats.setGoingCount(statsJsonObject.getString(ApiDetails.RESPONSE_KEY_GOING_COUNT));
        stats.setNotComingCount(statsJsonObject.getString(ApiDetails.RESPONSE_KEY_NOT_COMING_COUNT));
        stats.setMayBeCount(statsJsonObject.getString(ApiDetails.RESPONSE_KEY_MAY_BE_COUNT));
        buzzPreview.setStats(stats);
        return buzzPreview;
    }
}
