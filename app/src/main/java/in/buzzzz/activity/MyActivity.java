package in.buzzzz.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import in.buzzzz.R;
import in.buzzzz.utility.ChatJSONMessage;

public class MyActivity extends FragmentActivity {

	private static final String START = "START";
	private static final String CHAT = "CHAT";
	private static final String TYPING = "TYPING";
	private static final String CHAT_HISTORY = "CHAT_HISTORY";
	private static final String CHAT_CLOSE = "CHAT_CLOSE";
	private static final String START_CHAT_WITH_DEALER = "START_CHAT_WITH_DEALER";
	private static final String START_DEALER = "START_DEALER";
	private static final String DEALER_CHAT_LIST = "DEALER_CHAT_LIST";
	private static final String DEALER_CHAT = "DEALER_CHAT";
	private static final String DEALER_UNAVAILABLE = "DEALER_UNAVAILABLE";
	private static final String END_USER = "enduser";

	private WebSocketClient mWebSocketClient;

	static TextView txtviewTyping;
	private EditText editText;

	private static LinearLayout linearLytChat;

	private String senderChatConfigId = null, recieverChatConfigId = null,
			chatType, senderName = "", recieverName = "";
	private ChatJSONMessage chatJSONMessage;

	private LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		senderName = getIntent().getStringExtra("name");
		chatJSONMessage = new ChatJSONMessage();
		getViewsId();
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		connectWebSocket();

	}

	private void getViewsId() {

		linearLytChat = (LinearLayout) findViewById(R.id.linear_lyt_chat);
		txtviewTyping = (TextView) findViewById(R.id.typing);
		editText = (EditText) findViewById(R.id.message);

		TextWatcher watch = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {

				Log.i("afterTextChanged", "afterTextChanged");

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				Log.i("beforeTextChanged", "beforeTextChanged");
			}

			@Override
			public void onTextChanged(CharSequence s, int a, int b, int c) {
				Log.i("onTextChanged", "onTextChanged");

				if (s.length() > 0)
					sendTypingEvent(s);

			}

			private void sendTypingEvent(CharSequence s) {

				JSONObject mainJsonObject = chatJSONMessage.getChatJSONMessage(
						senderChatConfigId, senderName, END_USER,
						recieverChatConfigId, recieverName, senderName
								+ " is typing...", TYPING);

				mWebSocketClient.send(mainJsonObject.toString());
				Log.e("my msg", mainJsonObject.toString());
			}

		};

//		editText.addTextChangedListener(watch);

	}



	private void connectWebSocket() {
		URI uri;
		try {
uri = new URI(
					"ws://chatapi.cardekho.com:8080/CarDekhoChat/chatwebsocket");

			uri = new URI(
					"ws://10.1.14.158:8080/buzz/abc");
			// uri = new URI("ws://10.0.2.2:8080/");

			// ws://chatapi.cardekho.com:8080/CarDekhoChat/chatwebsocket
			// ws://localhost:8080
			// ws://10.0.2.2:8080/

		} catch (URISyntaxException e) {
			e.printStackTrace();

			Log.e("uri not valid", e.toString() + "");

			return;
		}

		// mWebSocketClient = new WebSocketClient(uri) {

		mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {
				Log.e("Websocket", "Opened");
				mWebSocketClient.send("hello testing");


			}

			@Override
			public void onMessage(String s) {
				final String message = s;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						parseData(message);
						Log.e("onMessage", message);
						// textView.setText(textView.getText() + "\n" +
						// message);
					}

				});
			}

			@Override
			public void onClose(int i, String s, boolean b) {
				Log.i("Websocket", "Closed " + s);
			}

			@Override
			public void onError(Exception e) {
				Log.i("Websocket", "Error " + e.getMessage());
				// Log.e("webScoket", "In Error");
			}

		};

		mWebSocketClient.connect();

	}

	public void sendMessage(View view) {

		String type = "";
		Log.e("recieverChatConfigId", recieverChatConfigId + "");
		if (recieverChatConfigId != null) {

			type = CHAT;
		} else {

			type = START;
		}

		JSONObject mainJsonObject = chatJSONMessage.getChatJSONMessage(
				senderChatConfigId, senderName, END_USER, recieverChatConfigId,
				recieverName, editText.getText().toString(), type);

		mWebSocketClient.send(mainJsonObject.toString());
		Log.e("send msg", mainJsonObject.toString());

		View inflatedView = inflater.inflate(R.layout.sender_row, null);

		TextView textViewChat = (TextView) inflatedView
				.findViewById(R.id.txtview_chat);
		textViewChat.setText(editText.getText().toString());
		linearLytChat.addView(inflatedView);
		editText.setText("");
	}

	private void parseData(String message) {
		// {"data":{"message":"Welcome to carDekho. Our expert will shortly join you.","sender":{"chatconfigid":352,"name":"CarDekho-Admin"},"chatconfigid":352,"receiver":{"chatconfigid":"","name":""}},"type":"START"}

try {
			JSONObject jsonObject = new JSONObject(message);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			String msg = "";

			if (dataJsonObject.has("message"))
				msg = dataJsonObject.getString("message");

			JSONObject senderJsonObject = dataJsonObject
					.getJSONObject("sender");
			senderChatConfigId = senderJsonObject.getString("chatconfigid");

			JSONObject recieverJsonObject = dataJsonObject
					.getJSONObject("receiver");
			recieverChatConfigId = recieverJsonObject.getString("chatconfigid");

			chatType = jsonObject.getString("type");

			if (chatType.equalsIgnoreCase("TYPING")) {
				txtviewTyping.setVisibility(View.VISIBLE);
				txtviewTyping.setText(msg);
			} else {
				txtviewTyping.setVisibility(View.GONE);
				// textView.setText(textView.getText() + "\n \n" + msg);

				View inflatedView = inflater.inflate(R.layout.receiver_row,
						null);

				TextView textViewChat = (TextView) inflatedView
						.findViewById(R.id.txtview_chat);

				if (msg != null && !msg.equalsIgnoreCase("")) {
					textViewChat.setText(msg);
					linearLytChat.addView(inflatedView);
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		View inflatedView = inflater.inflate(R.layout.receiver_row,
				null);

		TextView textViewChat = (TextView) inflatedView
				.findViewById(R.id.txtview_chat);

		if (message != null && !message.equalsIgnoreCase("")) {
			textViewChat.setText(message);
			linearLytChat.addView(inflatedView);
		}

	}

	public void close() {

		// {"data":{"sender":{"chatconfigid":"243","name":"cardekho268"},"chatconfigid":"243","SupportRoomName":""},"type":"CHAT_CLOSE"}

		JSONObject jsonObjectMain = new JSONObject();

		try {
			jsonObjectMain.put("type", CHAT_CLOSE);
			JSONObject dataJSONJsonObject = new JSONObject();
			JSONObject senderJSONObject = new JSONObject();

			dataJSONJsonObject.put("SupportRoomName", "");

			if (senderChatConfigId != null) {
				senderJSONObject.put("chatconfigid", senderChatConfigId);

			}
			senderJSONObject.put("name", senderName);
			dataJSONJsonObject.put("sender", senderJSONObject);

			jsonObjectMain.put("data", dataJSONJsonObject);

			String msg = jsonObjectMain.toString();
			mWebSocketClient.send(msg);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mWebSocketClient.close();
		Log.e("connection close", "connection close");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
//		close();
		mWebSocketClient.close();
	}

}
