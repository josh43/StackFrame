package net.site88.stackframe.stackframe;

import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

import com.pubnub.api.*;
import org.json.*;


public class ChatActivity extends ActionBarActivity {

    ArrayList<String> chat = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    ListView chatView;
    EditText message;
    Button send;
    boolean newMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatView = (ListView) findViewById(R.id.chatView);

        final Pubnub pubnub = new Pubnub("pub-c-1a6a9ba9-b6b2-45aa-8dbe-7f6b398fdf14", "sub-c-b5dbfc4e-6252-11e5-8a6a-02ee2ddab7fe");
        chat.add("Welcome to StackFrame!");

        message = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();

                try {
                    data.put("text", message.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pubnub.publish("demo_tutorial", data, new Callback() {});
            }
        });

        arrayAdapter = new ArrayAdapter<String>(
                ChatActivity.this,
                android.R.layout.simple_list_item_1,
                chat );

        chatView.setAdapter(arrayAdapter);




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
