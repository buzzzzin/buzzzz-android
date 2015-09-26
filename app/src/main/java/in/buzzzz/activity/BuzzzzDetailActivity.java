package in.buzzzz.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

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
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.SharedPreference;

public class BuzzzzDetailActivity extends BaseActivity {
    private WebSocketClient mWebSocketClient;
    private EditText mEditText;

    private RecyclerView mRecyclerViewChat;
    List<ChatInfo> chatInfoList = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private String mChannelId = "demo";
    private String mSenderId = "-1";
    private String mSenderName = "Buzzzzer";

    private String mBuzzzzId;
    private String mBuzzzzName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzzzz_detail);
        mBuzzzzId = getIntent().getStringExtra(AppConstants.EXTRA_BUZZZZ_ID);
        mBuzzzzName = getIntent().getStringExtra(AppConstants.EXTRA_BUZZZZ_NAME);
        if (mBuzzzzId != null) {
            mSenderId = SharedPreference.getString(mActivity, AppConstants.PREF_KEY_USER_ID);
            mSenderName = SharedPreference.getString(mActivity, AppConstants.PREF_KEY_USER_NAME);
            getViewsId();
            connectWebSocket();
            requestBuzzzzDetail();
        } else {
            finish();
        }
    }

    private void getViewsId() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (mBuzzzzName != null) {
            collapsingToolbar.setTitle(mBuzzzzName);
        } else {
            collapsingToolbar.setTitle("Buzzzz name");
        }
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recyclerview_following);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerViewChat.setLayoutManager(mLinearLayoutManager);
        mEditText = (EditText) findViewById(R.id.message);
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
            uri = new URI(Api.CHAT_HOST_URL + Api.CHAT_CHANNEL + mChannelId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Logger.i("uri not valid", e.toString() + "");
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Logger.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseData(message);
                        Logger.i("onMessage", message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Logger.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Logger.i("Websocket", "Error " + e.getMessage());
                // Logger.i("webScoket", "In Error");
            }
        };

        mWebSocketClient.connect();
    }

    public void sendMessage(View view) {
        if (!mEditText.getText().toString().isEmpty()) {
            ChatInfo chatInfo = new ChatInfo();
            chatInfo.setMessage(mEditText.getText().toString());
            chatInfo.setSenderId(mSenderId);
            chatInfo.setSenderName(mSenderName);
            chatInfo.setImageUrl("https://www.google.co.in");
            JSONObject jsonObject = getChatJson(chatInfo, Api.CHAT_CHANNEL + mChannelId);
            mWebSocketClient.send(String.valueOf(jsonObject));
            Logger.i("send msg", jsonObject.toString());
            mEditText.setText("");
        }
    }

    private void parseData(String message) {
        JSONObject jsonObject;
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
        Logger.i("connection close", "connection close");
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

    private void requestBuzzzzDetail() {

    }
}
