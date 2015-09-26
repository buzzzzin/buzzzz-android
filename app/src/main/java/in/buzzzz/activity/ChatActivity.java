package in.buzzzz.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.List;

import in.buzzzz.R;
import in.buzzzz.adapter.ChatAdapter;
import in.buzzzz.model.ChatInfo;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.ChatJSONMessage;

public class ChatActivity extends BaseActivity {
    private WebSocketClient mWebSocketClient;
    private EditText editText;

    private String senderChatConfigId = null, recieverChatConfigId = null,
            chatType, senderName = "", recieverName = "";
    private ChatJSONMessage chatJSONMessage;

    private RecyclerView mRecyclerViewChat;
    List<ChatInfo> chatInfoList = new ArrayList<ChatInfo>();
    private ChatAdapter chatAdapter;

    private String mChannelId = "demo";
    private String mSenderId = "1234";
    private String mSenderName = "Rajendra";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__chat);
        senderName = getIntent().getStringExtra("name");
        chatJSONMessage = new ChatJSONMessage();
        getViewsId();
        connectWebSocket();

    }

    private void getViewsId() {

//        linearLytChat = (LinearLayout) findViewById(R.id.linear_lyt_chat);
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recyclerview_following);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerViewChat.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }
        });

      /*  List<ChatInfo> chatInfoList = new ArrayList<ChatInfo>();
        for (int i = 0; i < 30; i++) {
            ChatInfo chatInfo = new ChatInfo();
            if (i % 2 == 0) {
                chatInfo.setOwnMessage(true);
            }
            chatInfo.setMessage("Hello this is " + i);
            chatInfoList.add(chatInfo);

        }*/


//		txtviewTyping = (TextView) findViewById(R.id.typing);
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

            /*    JSONObject mainJsonObject = chatJSONMessage.getChatJSONMessage(
                        senderChatConfigId, senderName, END_USER,
                        recieverChatConfigId, recieverName, senderName
                                + " is typing...", TYPING);

                mWebSocketClient.send(mainJsonObject.toString());
                Log.e("my msg", mainJsonObject.toString());*/
            }

        };

//		editText.addTextChangedListener(watch);

    }


    private void setDataInChatAdapter() {
        if (chatAdapter == null) {
            chatAdapter = new ChatAdapter(mActivity, chatInfoList);
            mRecyclerViewChat.setAdapter(chatAdapter);
        } else {
            mRecyclerViewChat.getAdapter().notifyDataSetChanged();
        }
        mRecyclerViewChat.getLayoutManager().smoothScrollToPosition(mRecyclerViewChat, null, chatAdapter.getItemCount() - 1);


    }

    private void connectWebSocket() {
        URI uri;
        try {
          /*  uri = new URI(
                    "ws://chatapi.cardekho.com:8080/CarDekhoChat/chatwebsocket");*/

            uri = new URI(ApiDetails.CHAT_HOST_URL + ApiDetails.CHAT_CHANNEL +
                    mChannelId);


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
//                mWebSocketClient.send("hello testing");


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

        if (!editText.getText().toString().isEmpty()) {
            ChatInfo chatInfo = new ChatInfo();
            chatInfo.setMessage(editText.getText().toString());
            chatInfo.setSenderId(mSenderId);
            chatInfo.setSenderName(mSenderName);
            chatInfo.setImageUrl("https://www.google.co.in");
            JSONObject jsonObject = getChatJson(chatInfo, ApiDetails.CHAT_CHANNEL + mChannelId);
            mWebSocketClient.send(String.valueOf(jsonObject));
            Log.e("send msg", jsonObject.toString());
            editText.setText("");

        }

    }

    private void parseData(String message) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
            ChatInfo chatInfo = new ChatInfo();
            chatInfo.setMessage(jsonObject.getString(ApiDetails.REQUEST_KEY_MESSAGE));
            chatInfo.setImageUrl(jsonObject.getString(ApiDetails.REQUEST_KEY_IMAGE_URL));
            chatInfo.setSenderId(jsonObject.getString(ApiDetails.REQUEST_KEY_SENDER_ID));
            chatInfo.setSenderName(jsonObject.getString(ApiDetails.REQUEST_KEY_SENDER_NAME));
            if (mSenderId.equals(chatInfo.getSenderId())) {
                chatInfo.setOwnMessage(true);
            }
            chatInfoList.add(chatInfo);
            setDataInChatAdapter();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        mWebSocketClient.close();
        Log.e("connection close", "connection close");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        close();
    }


    private JSONObject getChatJson(ChatInfo chatInfo, String destination) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ApiDetails.REQUEST_KEY_DESTINATION, destination);

            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put(ApiDetails.REQUEST_KEY_SENDER_ID, chatInfo.getSenderId());
            jsonObjectData.put(ApiDetails.REQUEST_KEY_SENDER_NAME, chatInfo.getSenderName());
            jsonObjectData.put(ApiDetails.REQUEST_KEY_MESSAGE, chatInfo.getMessage());
            jsonObjectData.put(ApiDetails.REQUEST_KEY_IMAGE_URL, chatInfo.getImageUrl());

            jsonObject.put("data", jsonObjectData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
