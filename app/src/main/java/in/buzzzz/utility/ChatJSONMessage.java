package in.buzzzz.utility;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatJSONMessage {



	public JSONObject getChatJSONMessage(String senderChatConfigId,
			String senderName, String SenderType, String recieverChatConfigId,
			String recieverName, String message, String type) {

		JSONObject jsonObjectMain = new JSONObject();
		try {

			JSONObject dataJSONJsonObject = new JSONObject();
			JSONObject senderJSONObject = new JSONObject();

			if (recieverChatConfigId != null
					&& !recieverChatConfigId.equalsIgnoreCase("")) {
				senderJSONObject.put("chatconfigid", recieverChatConfigId);
				jsonObjectMain.put("type", type);
			} else {
				senderJSONObject.put("chatconfigid", "");
				jsonObjectMain.put("type", type);
			}

			senderJSONObject.put("name", senderName);
			senderJSONObject.put("sendertype", SenderType);
			dataJSONJsonObject.put("sender", senderJSONObject);

			JSONObject recieverJSONObject = new JSONObject();

			if (senderChatConfigId != null) {
				recieverJSONObject.put("chatconfigid", senderChatConfigId);
			} else {
				recieverJSONObject.put("chatconfigid", "");
			}

			recieverJSONObject.put("name", "");

			dataJSONJsonObject.put("receiver", recieverJSONObject);

			jsonObjectMain.put("data", dataJSONJsonObject);
			dataJSONJsonObject.put("message", message);
			dataJSONJsonObject.put("SupportRoomName", "");

			jsonObjectMain.put("data", dataJSONJsonObject);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObjectMain;

	}
}
