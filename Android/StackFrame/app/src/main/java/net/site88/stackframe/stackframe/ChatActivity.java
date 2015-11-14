package net.site88.stackframe.stackframe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

import com.github.nkzawa.emitter.Emitter;
import com.pubnub.api.*;
import org.json.*;


public class ChatActivity extends ActionBarActivity {

    ArrayList<Message> chat = new ArrayList<Message>();
    //ArrayAdapter arrayAdapter;
    BubbleAdapter arrayAdapter;
    ListView chatView;
    EditText message;
    Button send;
    boolean newMessage = false;
    BroadcastReceiver mMessageReceiver;
    LocalBroadcastManager broadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatView = (ListView) findViewById(R.id.chatView);

        chat.add(new Message("Application", "None", "text", "none", "Welcome to StackFrame!", "0"));

        message = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String output =  message.getText().toString();
                message.setText("");
                sendResult(output);
            }
        });

        arrayAdapter = new BubbleAdapter(this, chat);
                /*= new ArrayAdapter<String>(
                ChatActivity.this,
                android.R.layout.simple_expandable_list_item_1,
                chat );*/

        chatView.setAdapter(arrayAdapter);

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                String message = intent.getStringExtra("message");
                try {
                    JSONObject data = new JSONObject(message);
                    chat.add(new Message(data.getString("username"), data.getString("token"), data.getString("type"), data.getString("date"), data.getString("text"), data.getString("serverid")));
                    arrayAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(ChatActivity.this, "Something wrong with the message data", Toast.LENGTH_SHORT).show();
                }
                Log.d("StackFrame-UI", "Got message: " + message);
                //Toast.makeText(ChatActivity.this, "Got a message: " + message, Toast.LENGTH_SHORT).show();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("incomingMessage"));
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public void sendResult(String message) {
        Intent intent = new Intent("outgoingMessage");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}