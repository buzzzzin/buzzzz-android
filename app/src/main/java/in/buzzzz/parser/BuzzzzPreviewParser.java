package in.buzzzz.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.buzzzz.model.BuzzzzPreview;
import in.buzzzz.model.Model;
import in.buzzzz.utility.ApiDetails;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class BuzzzzPreviewParser implements Parser<Model> {

    @Override
    public Model parse(JSONObject json) throws JSONException {
        BuzzzzPreview buzzzzPreview = new BuzzzzPreview();
        buzzzzPreview.setStatus(json.getInt(ApiDetails.RESPONSE_KEY_STATUS));
        buzzzzPreview.setMessage(json.getString(ApiDetails.RESPONSE_KEY_MESSAGE));
        if (buzzzzPreview.getStatus() == ApiDetails.STATUS_SUCCESS) {
            JSONObject dataJsonObject = json.getJSONObject(ApiDetails.RESPONSE_KEY_DATA);
            buzzzzPreview.setBuzzId(dataJsonObject.getString(ApiDetails.RESPONSE_KEY_BUZZ_ID));
            buzzzzPreview.setName(dataJsonObject.getString(ApiDetails.RESPONSE_KEY_NAME));
            buzzzzPreview.setImageName(dataJsonObject.getString(ApiDetails.RESPONSE_KEY_IMAGE_NAME));
            buzzzzPreview.setIsRSVP(dataJsonObject.getBoolean(ApiDetails.RESPONSE_KEY_IS_RSVB));

            JSONArray interestJsonArray = dataJsonObject.getJSONArray(ApiDetails.RESPONSE_KEY_INTERESTS);
            List<String> interests = new ArrayList<>();
            for (int index = 0; interestJsonArray != null && index < interestJsonArray.length(); index++) {
                interests.add(interestJsonArray.getString(index));
            }
            buzzzzPreview.setInterests(interests);

            BuzzzzPreview.Location location = new BuzzzzPreview.Location();
            JSONObject locationJsonObject = dataJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_LOCATION);
            location.setCity(locationJsonObject.getString(ApiDetails.RESPONSE_KEY_CITY));
            location.setLatitude(locationJsonObject.getString(ApiDetails.RESPONSE_KEY_LATITUDE));
            location.setLongitude(locationJsonObject.getString(ApiDetails.RESPONSE_KEY_LONGITUDE));
            location.setAddress(locationJsonObject.getString(ApiDetails.RESPONSE_KEY_ADDRESS));
            location.setPincode(locationJsonObject.getString(ApiDetails.RESPONSE_KEY_PINCODE));
            buzzzzPreview.setLocation(location);

            BuzzzzPreview.Schedule schedule = new BuzzzzPreview.Schedule();
            JSONObject scheduleJsonObject = dataJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_SCHEDULE);
            schedule.setStartDate(scheduleJsonObject.getString(ApiDetails.RESPONSE_KEY_START_DATE));
            schedule.setEndDate(scheduleJsonObject.getString(ApiDetails.RESPONSE_KEY_END_DATE));
            schedule.setStartTime(scheduleJsonObject.getString(ApiDetails.RESPONSE_KEY_START_TIME));
            schedule.setEndTime(scheduleJsonObject.getString(ApiDetails.RESPONSE_KEY_END_TIME));
            buzzzzPreview.setSchedule(schedule);

            BuzzzzPreview.Stats stats = new BuzzzzPreview.Stats();
            JSONObject statsJsonObject = dataJsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_STATS);
            stats.setGoingCount(statsJsonObject.getString(ApiDetails.RESPONSE_KEY_GOING_COUNT));
            stats.setNotComingCount(statsJsonObject.getString(ApiDetails.RESPONSE_KEY_NOT_COMING_COUNT));
            stats.setMayBeCount(statsJsonObject.getString(ApiDetails.RESPONSE_KEY_MAY_BE_COUNT));
            buzzzzPreview.setStats(stats);
        }
        return buzzzzPreview;
    }
}
