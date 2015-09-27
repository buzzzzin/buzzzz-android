package in.buzzzz.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.buzzzz.R;
import in.buzzzz.adapter.ChatAdapter;
import in.buzzzz.loader.APICaller;
import in.buzzzz.loader.LoaderCallback;
import in.buzzzz.model.BuzzPreview;
import in.buzzzz.model.ChatInfo;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.BuzzPreviewParser;
import in.buzzzz.parser.MessageParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

public class BuzzDetailActivity extends BaseActivity {
    private static final String TAG = "BuzzDetailActivity";
    private WebSocketClient mWebSocketClient;
    private EditText mEditText;
    private TextView mTextViewRsvbMessage;
    private TextView mTextViewVenue;
    private TextView mTextViewStart;
    private TextView mTextViewResponse;
    private TextView mButtonYes, mButtonNo, mButtonMayBe;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private RelativeLayout mRelativeLayoutIsRsvp;
    private ImageView mImageViewBuzzPic;

    private RecyclerView mRecyclerViewChat;
    List<ChatInfo> chatInfoList = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private String mChannelId = "demo";
    private String mSenderId = "-1";
    private String mSenderName = "Buzzzzer";

    private String mBuzzzzId;
    private String mBuzzzzName;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_yes:
                    sendRsvbResponse(ApiDetails.RSVP.YES);
                    break;
                case R.id.button_no:
                    sendRsvbResponse(ApiDetails.RSVP.NO);
                    break;
                case R.id.button_maybe:
                    sendRsvbResponse(ApiDetails.RSVP.MAY_BE);
                    break;

                case R.id.imageview_navigation:
                    if (mBuzzPreview != null && mBuzzPreview.getLocation() != null && !mBuzzPreview.getLocation().getLatitude().isEmpty()) {
                        Utility.navigateTo(mActivity, mBuzzPreview.getLocation().getLatitude(), mBuzzPreview.getLocation().getLongitude());

                    } else {
                        Utility.showToastMessage(mActivity, "No Location infromation available.");
                    }
                    break;

            }
        }
    };
    private ImageButton imageViewNavigation;
    private BuzzPreview mBuzzPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzzzz_detail);
        mBuzzzzId = getIntent().getStringExtra(AppConstants.EXTRA_BUZZZZ_ID);
        mChannelId = mBuzzzzId;
        mBuzzzzName = getIntent().getStringExtra(AppConstants.EXTRA_BUZZZZ_NAME);
        if (mBuzzzzId != null) {
            mSenderId = SharedPreference.getString(mActivity, AppConstants.PREF_KEY_USER_ID);
            mSenderName = SharedPreference.getString(mActivity, AppConstants.PREF_KEY_USER_NAME);
            getViewsId();
            connectWebSocket();
            requestBuzzDetail();
        } else {
            finish();
        }
    }

    private void getViewsId() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (mBuzzzzName != null) {
            mCollapsingToolbar.setTitle(mBuzzzzName);
        } else {
            mCollapsingToolbar.setTitle("Buzzzz name");
        }
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recyclerview_following);
        mImageViewBuzzPic = (ImageView) findViewById(R.id.imageview_buzz_pic);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerViewChat.setLayoutManager(mLinearLayoutManager);
        mEditText = (EditText) findViewById(R.id.message);
        mTextViewRsvbMessage = (TextView) findViewById(R.id.textview_rsvb_message);
        mTextViewVenue = (TextView) findViewById(R.id.textview_venue);
        mTextViewStart = (TextView) findViewById(R.id.textview_start);
        mTextViewResponse = (TextView) findViewById(R.id.textview_response);
        mRelativeLayoutIsRsvp = (RelativeLayout) findViewById(R.id.relativelayout_is_rsvp);
        mButtonYes = (TextView) findViewById(R.id.button_yes);
        mButtonNo = (TextView) findViewById(R.id.button_no);
        mButtonMayBe = (TextView) findViewById(R.id.button_maybe);
        imageViewNavigation = (ImageButton) findViewById(R.id.imageview_navigation);


        mButtonYes.setOnClickListener(mOnClickListener);
        mButtonNo.setOnClickListener(mOnClickListener);
        mButtonMayBe.setOnClickListener(mOnClickListener);
        imageViewNavigation.setOnClickListener(mOnClickListener);
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
            uri = new URI(Api.CHAT_HOST_URL + Api.CHAT_CHANNEL_BUZZ + mChannelId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Logger.i("uri not valid", e.toString() + "");
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Logger.i("Websocket", "Opened");
                if (SharedPreference.getBoolean(mActivity, AppConstants.PREF_KEY_IS_LOGGED_IN)) {
                    String welcomeText = "I am here...";
//                    joinExitMessage(welcomeText);
                } else {
                    String text = "Login to join Buzzzz...";
//                    joinExitMessage(text);
                }
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
            }
        };

        mWebSocketClient.connect();
    }

    /**
     * will be called if user joins or leaves chat
     */
    private void joinExitMessage(String message) {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setMessage(message);
        chatInfo.setSenderId(mSenderId);
        chatInfo.setSenderName(mSenderName);
        String imageName = Api.BASE_URL_CLOUDINARY_SOCIAL
                + SharedPreference.getString(mActivity, AppConstants.PREF_KEY_MEDIUM_TYPE).toLowerCase()
                + "/"
                + SharedPreference.getString(mActivity, AppConstants.PREF_KEY_MEDIUM_ID);
        chatInfo.setImageUrl(imageName);
        JSONObject jsonObject = getChatJson(chatInfo, Api.CHAT_CHANNEL_BUZZ + mChannelId);
        mWebSocketClient.send(String.valueOf(jsonObject));
        Logger.i("send msg", jsonObject.toString());
    }

    public void sendMessage(View view) {
        if (!mEditText.getText().toString().isEmpty()) {
            ChatInfo chatInfo = new ChatInfo();
            chatInfo.setMessage(mEditText.getText().toString());
            chatInfo.setSenderId(mSenderId);
            chatInfo.setSenderName(mSenderName);
            String imageName = Api.BASE_URL_CLOUDINARY_SOCIAL
                    + SharedPreference.getString(mActivity, AppConstants.PREF_KEY_MEDIUM_TYPE).toLowerCase()
                    + "/q_50,r_100,h_50,w_50/"
                    + SharedPreference.getString(mActivity, AppConstants.PREF_KEY_MEDIUM_ID)
                    + ".png";
            chatInfo.setImageUrl(imageName);
            JSONObject jsonObject = getChatJson(chatInfo, Api.CHAT_CHANNEL_BUZZ + mChannelId);
            mWebSocketClient.send(String.valueOf(jsonObject));
            Logger.i("send msg", jsonObject.toString());
            mEditText.setText("");
        }
    }

    private void parseData(String message) {
        JSONObject jsonObject;
        try {
            JSONObject djsonObject = new JSONObject(message);
            jsonObject = djsonObject.getJSONObject(ApiDetails.RESPONSE_KEY_DATA);
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
//            jsonObject.put(ApiDetails.REQUEST_KEY_DESTINATION, destination);
            jsonObject.put(ApiDetails.REQUEST_KEY_TYPE, ApiDetails.MESSAGE_TYPE.CHAT);
            jsonObject.put(ApiDetails.REQUEST_KEY_TOKEN, SharedPreference.getString(mActivity, AppConstants.PREF_KEY_AUTH_TOKEN));

            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put(ApiDetails.REQUEST_KEY_SENDER_ID, chatInfo.getSenderId());
            jsonObjectData.put(ApiDetails.REQUEST_KEY_SENDER_NAME, chatInfo.getSenderName());
            jsonObjectData.put(ApiDetails.REQUEST_KEY_MESSAGE, chatInfo.getMessage());
            jsonObjectData.put(ApiDetails.REQUEST_KEY_IMAGE_URL, chatInfo.getImageUrl());
            jsonObjectData.put(ApiDetails.REQUEST_KEY_RECEIVER_ID, SharedPreference.getString(mActivity, AppConstants.PREF_KEY_USER_ID));

            jsonObject.put("data", jsonObjectData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void requestBuzzDetail() {
        Request request = new Request(ApiDetails.ACTION_NAME.PREVIEW);
        request.setUrl(Api.BASE_URL_API + ApiDetails.ACTION_NAME.PREVIEW.getActionName() + mBuzzzzId);
        request.setDialogMessage(getString(R.string.progress_dialog_msg));
        request.setShowDialog(true);
        request.setRequestType(Request.HttpRequestType.GET);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new BuzzPreviewParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {
                Logger.i(TAG, "model: " + model);
                if (model.getStatus() == ApiDetails.STATUS_SUCCESS) {
                    if (model instanceof BuzzPreview) {

                        mBuzzPreview = (BuzzPreview) model;
                        displayBuzzPreview((BuzzPreview) model);
                    }
                } else {
                    Utility.showToastMessage(mActivity, model.getMessage());
                }
            }
        });
        if (!hasNetwork) {
            Utility.showToastMessage(mActivity, getString(R.string.no_network));
        }
    }

    private void sendRsvbResponse(final ApiDetails.RSVP rsvp) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiDetails.REQUEST_KEY_STATUS, rsvp.name());
        params.put(ApiDetails.REQUEST_KEY_BUZZ_ID, mBuzzzzId);

        Request request = new Request(ApiDetails.ACTION_NAME.RSVP);
        request.setParamMap(params);
        request.setShowDialog(false);
        request.setUrl(Api.BASE_URL_API + ApiDetails.ACTION_NAME.RSVP.getActionName());
        request.setRequestType(Request.HttpRequestType.POST);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new MessageParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {
                Logger.i(TAG, "model: " + model);
                if (model.getStatus() == ApiDetails.STATUS_SUCCESS) {
                    updateRsvbButton(rsvp);
                } else {
                    Utility.showToastMessage(mActivity, model.getMessage());
                }
            }
        });
        if (!hasNetwork) {
            Utility.showToastMessage(mActivity, getString(R.string.no_network));
        }
    }

    private void updateRsvbButton(ApiDetails.RSVP rsvp) {
        String rsvpMessage;
        switch (rsvp) {
            case YES:
                rsvpMessage = "You are going!!";
                mTextViewRsvbMessage.setText(rsvpMessage);
                mButtonYes.setTextColor(getResources().getColor(R.color.primary));
                mButtonNo.setTextColor(getResources().getColor(R.color.accent));
                mButtonMayBe.setTextColor(getResources().getColor(R.color.accent));
                break;
            case NO:
                rsvpMessage = "You will be missed!!";
                mTextViewRsvbMessage.setText(rsvpMessage);
                mButtonNo.setTextColor(getResources().getColor(R.color.primary));
                mButtonYes.setTextColor(getResources().getColor(R.color.accent));
                mButtonMayBe.setTextColor(getResources().getColor(R.color.accent));
                break;
            case MAY_BE:
                rsvpMessage = "Let's wait!!";
                mTextViewRsvbMessage.setText(rsvpMessage);
                mButtonMayBe.setTextColor(getResources().getColor(R.color.primary));
                mButtonYes.setTextColor(getResources().getColor(R.color.accent));
                mButtonNo.setTextColor(getResources().getColor(R.color.accent));
                break;
        }
    }

    private void displayBuzzPreview(BuzzPreview buzzPreview) {
        mCollapsingToolbar.setTitle(buzzPreview.getName());
        if (buzzPreview.isRSVP()) {
//            mTextViewRsvbMessage.setText("You are going!!");
            BuzzPreview.Stats stats = buzzPreview.getStats();
            String response = String.format("%s Going | %s Not going | %s May be", stats.getGoingCount(), stats.getNotComingCount(), stats.getMayBeCount());
            mTextViewResponse.setText(response);
        } else {
            mRelativeLayoutIsRsvp.setVisibility(View.GONE);
            mTextViewResponse.setVisibility(View.GONE);
        }
        mTextViewVenue.setText(buzzPreview.getLocation().getAddress());
        mTextViewStart.setText(buzzPreview.getSchedule().getStartTime());

        updateRsvbButton(buzzPreview.getRsvp());
        Utility.setImageFromUrl(Api.BASE_URL_CLOUDINARY_BUZZZZ + buzzPreview.getImageName(), mImageViewBuzzPic);
    }
}
