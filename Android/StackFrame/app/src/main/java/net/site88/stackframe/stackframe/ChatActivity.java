package net.site88.stackframe.stackframe;

import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

import com.pubnub.api.*;
import org.json.*;


public class ChatActivity extends ActionBarActivity {

    ArrayList<String> chat = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    ListView chatView;
    boolean newMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatView = (ListView) findViewById(R.id.chatView);

        Pubnub pubnub = new Pubnub("pub-c-1a6a9ba9-b6b2-45aa-8dbe-7f6b398fdf14", "sub-c-b5dbfc4e-6252-11e5-8a6a-02ee2ddab7fe");
        chat.add("Welcome to Stackframe!");

        arrayAdapter = new ArrayAdapter<String>(
                ChatActivity.this,
                android.R.layout.simple_list_item_1,
                chat );

        chatView.setAdapter(arrayAdapter);



        /* Subscribe to the demo_tutorial channel */
        try {
            pubnub.subscribe("demo_tutorial", new Callback() {
                public void successCallback(String channel, Object message) {
                    try {chat.add(((JSONObject) message).getString("text"));}
                    catch (Exception e) { e.printStackTrace(); }
                    Log.v("StackFrame", "Recieved a message from PubNub.");
                    newMessage = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayAdapter.notifyDataSetChanged();
                            if(chatView.getLastVisiblePosition() == (chat.size() - 2))
                            {
                                chatView.smoothScrollToPosition(chat.size() - 1);
                            }
                            Log.v("StackFame", chatView.getLastVisiblePosition() + "");
                        }
                    });
                    Log.v("StackFrame", "Updating Listview");
                   // Looper.prepare();
                    //Toast.makeText(ChatActivity.this, "Successfully Connected To Pubnub", Toast.LENGTH_SHORT).show();
                }

                public void errorCallback(String channel, PubnubError error) {
                   Looper.prepare();
                    Toast.makeText(ChatActivity.this, "Unable to connect to PubNub", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
