///******************************************************************************
// * Copyright 2011 Tavendo GmbH
// * <p/>
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p/>
// * http://www.apache.org/licenses/LICENSE-2.0
// * <p/>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ******************************************************************************/
//
//
//package in.buzzzz.activity;
//
//
//import android.app.Activity;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.Date;
//
//import in.buzzzz.R;
//import in.buzzzz.wamp.Wamp;
//import in.buzzzz.wamp.WampConnection;
//
//
//public class WampActivity extends Activity {
//
//
//    @SuppressWarnings("unused")
//    private static final String TAG = "de.tavendo.autobahn.simplepubsub";
//
//
//    private static final String PREFS_NAME = "AutobahnAndroidSimplePubSub";
//
//
//    private SharedPreferences mSettings;
//
//
//    private static EditText mHostname;
//    private static EditText mPort;
//    private static TextView mStatusline;
//    private static Button mStart;
//
//
//    private final Wamp mConnection = new WampConnection();
//
//
//    private void alert(String message) {
//        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//
//    private void loadPrefs() {
//
//
//        mHostname.setText(mSettings.getString("hostname", ""));
//        mPort.setText(mSettings.getString("port", "9000"));
//    }
//
//
//    private void savePrefs() {
//
//
//        SharedPreferences.Editor editor = mSettings.edit();
//        editor.putString("hostname", mHostname.getText().toString());
//        editor.putString("port", mPort.getText().toString());
//        editor.commit();
//    }
//
//
//    private void setButtonConnect() {
//        mStart.setText("Connect");
//        mStart.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                test();
//            }
//        });
//    }
//
//
//    private void setButtonDisconnect() {
//        mStart.setText("Disconnect");
//        mStart.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                mConnection.disconnect();
//            }
//        });
//    }
//
//
//    /**
//     * We want PubSub events delivered to us in JSON payload to be automatically
//     * converted to this domain POJO. We specify this class later when we subscribe.
//     */
//    private static class MyEvent1 {
//
//
//        public int num;
//        public String name;
//        public boolean flag;
//        public Date created;
//        public double rand;
//
//
//        @Override
//        public String toString() {
//            return "{name: " + name +
//                    ", created: " + created +
//                    ", num: " + num +
//                    ", rand: " + rand +
//                    ", flag:" + flag + "}";
//        }
//    }
//
//
//    private void test() {
//
//
////        final String wsuri = "ws://" + mHostname.getText() + ":" + mPort.getText();
//
//        final String wsuri = "ws://10.1.14.158:8080/publish";
//
//
//        mStatusline.setText("Connecting to\n" + wsuri + " ..");
//
//
//        setButtonDisconnect();
//
//
//        // we establish a connection by giving the WebSockets URL of the server
//        // and the handler for open/close events
//        mConnection.connect(wsuri, new Wamp.ConnectionHandler() {
//
//
//            @Override
//            public void onOpen() {
//
//
//                // The connection was successfully established. we set the status
//                // and save the host/port as Android application preference for next time.
//                mStatusline.setText("Connected to\n" + wsuri);
//                savePrefs();
//
//
//                // We establish a prefix to use for writing URIs using shorthand CURIE notation.
//                mConnection.prefix("event", "http://10.1.14.158:8080/publish");
//
//
//                // We subscribe to a topic by giving the topic URI, the type we want events
//                // to be converted to, and the event handler we want to have fired.
//
//                mConnection.subscribe("event:channel/football", MyEvent1.class, new Wamp.EventHandler() {
//
//
//                    @Override
//                    public void onEvent(String topicUri, Object event) {
//
//
//                        // when we get an event, we safely can cast to the type we specified previously
//                        MyEvent1 evt = (MyEvent1) event;
//
//
//                        alert("Event received : " + evt.toString());
//                    }
//                });
//                mConnection.publish("event:channel/football", MyEvent1.class);
//            }
//
//
//            @Override
//            public void onClose(int code, String reason) {
//
//
//                // The connection was closed. Set the status line, show a message box,
//                // and set the button to allow to connect again.
//                mStatusline.setText("Connection closed.");
//                alert(reason);
//                setButtonConnect();
//            }
//        });
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wamp);
//
//
//        mHostname = (EditText) findViewById(R.id.hostname);
//        mPort = (EditText) findViewById(R.id.port);
//        mStatusline = (TextView) findViewById(R.id.statusline);
//        mStart = (Button) findViewById(R.id.start);
//
//
//        mSettings = getSharedPreferences(PREFS_NAME, 0);
//        loadPrefs();
//
//
//        setButtonConnect();
//    }
//}
