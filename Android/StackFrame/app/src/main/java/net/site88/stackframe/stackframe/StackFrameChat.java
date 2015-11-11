package net.site88.stackframe.stackframe;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.IBinder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Stack;

/**
 * Created by Desone on 11/2/2015.
 */
public class StackFrameChat extends Service
{
    /** indicates how to behave if the service is killed */
    int mStartMode;

    Socket socket;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    String username;
    String password;
    String geoloc;
    Handler handler;

    Intent chat;

    String token;
    String serverid;

    LocalBroadcastManager broadcast;
    BroadcastReceiver mMessageReceiver;

    /** Called when the service is being created. */
    @Override
    public void onCreate() {

    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extra = intent.getExtras();

        if(extra.getString("action") != null && extra.getString("action").equals("startup")) {
            handler = new Handler();
            try {
                socket = IO.socket("http://nodejs-stackframe.rhcloud.com");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error connecting socket to server", Toast.LENGTH_SHORT).show();
            }
        }

        else if(extra.getString("action") != null && extra.getString("action").equals("register")) {
            Toast.makeText(this, "Sending Registration Info to Server", Toast.LENGTH_SHORT).show();
            username = extra.getString("username");
            password = extra.getString("password");
            geoloc = extra.getString("geoloc");

            socket.on("register", onRegister);
            socket.on("message", onMessage);
            socket.connect();

            JSONObject registerjson = new JSONObject();
            try {
                registerjson.put("username", username);
                registerjson.put("password", password);
                registerjson.put("geoloc", geoloc);
            } catch (Exception e) {
                Toast.makeText(this, "Error putting together registration data", Toast.LENGTH_SHORT).show();
                Log.e("StackFrame", e.getStackTrace().toString());
                e.printStackTrace();
            }
            socket.emit("register", registerjson);
        }
        else
        {
            Toast.makeText(this, "Attempting to start service with invalid action: " + extra.getString("action"), Toast.LENGTH_SHORT).show();
        }

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                String message = intent.getStringExtra("message");
                Log.d("StackFrame-Backend", "Got message: " + message);
                JSONObject output = new JSONObject();
                try {
                    output.put("username", username);
                    output.put("token", token);
                    output.put("type", "text");
                    output.put("date", new Date().toString());
                    output.put("text", message);
                    output.put("serverid", serverid);
                } catch (Exception e)
                {
                    Toast.makeText(StackFrameChat.this, "Unable to construct message", Toast.LENGTH_SHORT).show();
                }
                socket.emit("message", output);
                //Toast.makeText(ChatActivity.this, "Got a message: " + message, Toast.LENGTH_SHORT).show();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("outgoingMessage"));

        return mStartMode;
    }

    private Emitter.Listener onRegister = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
             handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    //String username;

                    try {
                        serverid = data.getString("serverid");
                        token = data.getString("token");
                    } catch (JSONException e) {
                        Toast.makeText(StackFrameChat.this, "Something wrong with the registration I got...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(StackFrameChat.this, "Got token: " + token, Toast.LENGTH_SHORT).show();

                    chat = new Intent(StackFrameChat.this, ChatActivity.class);
                    chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(chat);

                }
            });
        }
    };

    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String text = "";
                    try {
                        username = data.getString("username");
                        text = data.getString("text");
                    } catch (JSONException e) {
                        Toast.makeText(StackFrameChat.this, "Something wrong with the message I got...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Toast.makeText(StackFrameChat.this, "Got message: " + text, Toast.LENGTH_SHORT).show();
                    sendResult(data);
                }
            });
        }
    };

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        socket.disconnect();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendResult(JSONObject message) {
        Intent intent = new Intent("incomingMessage");
        // You can also include some extra data.
        intent.putExtra("message", message.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
