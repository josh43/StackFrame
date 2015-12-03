package com.rhcloud.stackframe;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.LruCache;
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
    String socketid;
    String geoloc;
    String avatar;
    Handler handler;

    Intent chat;

    String token;
    int serverid;
    long lastMessage = new Date().getTime();

    LocalBroadcastManager broadcast;
    BroadcastReceiver mMessageReceiver;
    SharedPreferences loginInfo;
    SharedPreferences.Editor loginEditor;
    Bundle extra;
    int messageWait = 10;
    String lastMessageSender = "";
    String lastMessageText = "";
    //long lastMessage = 0;

    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        loginInfo = getApplicationContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        token = loginInfo.getString("token", "");
        loginEditor = loginInfo.edit();
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        extra = intent.getExtras();

        if(extra.getString("action") != null && extra.getString("action").equals("startup")) {
            handler = new Handler();
            try {
                String serverurl = StackFrameChat.this.getString(R.string.serverurl);
                if(serverurl == null || serverurl.equals(""))
                {
                    serverurl = "http://public-stackframe.rhcloud.com";
                    Log.d("StackFrame Backend", "Server url string came back null :(");
                }
                else Log.d("StackFrame Backend", "Using the url: " + serverurl);
                socket = IO.socket(serverurl);
                socket.on("register", onRegister);
                socket.on("login", onLogin);
                socket.on("message", onMessage);
                socket.on("ready", onConnect);
                socket.on("private", onPrivate);
                socket.on("profile", onProfile);
                socket.on("numberAvatars", onNumberAvatars);
                socket.connect();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error connecting socket to server", Toast.LENGTH_SHORT).show();
            }
        }

        else if(extra.getString("action").equals("login"))
        {
            username = extra.getString("username");
            password = extra.getString("password");
            geoloc = extra.getString("geoloc");
            login();
        }

        else if(extra.getString("action").equals("register"))
        {
            username = extra.getString("username");
            password = extra.getString("password");
            geoloc = extra.getString("geoloc");
            avatar = extra.getInt("avatar") + "";
            register();
        }
        else if(extra.getString("action").equals("numberAvatars"))
        {

            socket.emit("numberAvatars", 0);
        }
        else if(extra.getString("action").equals("profile"))
        {
            JSONObject output = new JSONObject();
            try
            {
                output.put("username", username);
                output.put("token", token);
                output.put("dest", extra.getString("index", ""));
                socket.emit("profile", output);
            }
            catch (Exception e)
            {
                Toast.makeText(StackFrameChat.this, "Message failed to send...", Toast.LENGTH_SHORT).show();
                Log.w("StackFrame-Backend", "Unable to construct JSON message");
                e.printStackTrace();
            }
        }
        else
        {
            Log.d("StackFrame-Backend", "Attempting to start service with invalid action: " + extra.getString("action"));
        }

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                if(intent.getAction() != null && intent.getAction().equals("outgoingMessage") && (new Date().getTime()) - lastMessage > 100) {
                    Log.d("StackFrame-Backend", "For sure got a unique outgoing message event");
                    lastMessage = new Date().getTime();

                    String message = intent.getStringExtra("message");
                    //Log.d("StackFrame-Backend", "Got message: " + message);
                    JSONObject output = new JSONObject();
                    try {
                        output.put("username", username);
                        output.put("token", token);
                        output.put("type", "text");
                        output.put("date", new Date().getTime());
                        output.put("text", message);
                        output.put("serverid", serverid);
                    } catch (Exception e) {
                        Toast.makeText(StackFrameChat.this, "Message failed to...", Toast.LENGTH_SHORT).show();
                        Log.w("StackFrame-Backend", "Unable to construct JSON message");
                    }
                    socket.emit("message", output);
                    Log.v("StackFrame Backend", "Sending message: " + message);
                }
                else
                {
                    Log.w("StackFrame-Backend", "Got outgoing message, but filtering (too soon from last send");
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("outgoingMessage"));

        return mStartMode;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(StackFrameChat.this, "Connected...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("connected");
                    intent.putExtra("action", "connected");
                    LocalBroadcastManager.getInstance(StackFrameChat.this).sendBroadcast(intent);
                    if (!loginInfo.getString("token", "").equals("")) {
                        username = loginInfo.getString("username", "");
                        password = loginInfo.getString("password", "");
                        geoloc = loginInfo.getString("geoloc", "");
                        token = loginInfo.getString("token", "");
                        Log.v("StackFrame-Backend", "Login info stored, attempting to login with existing authentication information: (Token: " + token + ")" );
                        reconnect();
                    } else {
                        Log.d("StackFrame-Backend", "No Login Information stored.");
                    }
                }
            });
        }
    };

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
             handler.post(new Runnable() {
                 @Override
                 public void run() {
                     JSONObject data = (JSONObject) args[0];
                     //String username;

                     try {
                         if (data.getString("token").equals("-1")) {
                             //Toast.makeText(StackFrameChat.this, "Invalid login information", Toast.LENGTH_SHORT).show();
                             Log.v("StackFrame-Backend", "Invalid login information. Received a -1 token value.");
                             Intent intent = new Intent("failedLogin");
                             intent.putExtra("action", "failedLogin");
                             LocalBroadcastManager.getInstance(StackFrameChat.this).sendBroadcast(intent);
                             return;
                         }

                         serverid = data.getInt("serverid");
                         token = data.getString("token");
                         socketid = data.getString("socket");
                         loginEditor.putInt("serverid", serverid);
                         loginEditor.putString("token", token);
                         loginEditor.putString("username", username);
                         loginEditor.putString("socket", socketid);
                         loginEditor.commit();
                     } catch (JSONException e) {
                         //Toast.makeText(StackFrameChat.this, "Something wrong with the registration I got...", Toast.LENGTH_SHORT).show();
                         Log.w("StackFrame-Backend", "!!Got Registration: " + data.toString());
                         Log.w("StackFrame-Backend", e.getStackTrace().toString());
                         e.printStackTrace();
                         Intent intent = new Intent("failedLogin");
                         intent.putExtra("action", "failedLogin");
                         LocalBroadcastManager.getInstance(StackFrameChat.this).sendBroadcast(intent);
                         return;
                     }
                     //Toast.makeText(StackFrameChat.this, "Got token: " + token, Toast.LENGTH_SHORT).show();
                     Log.d("StackFrame-Backend", "Got Registration: " + data.toString());
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

                        if(new Date().getTime() - lastMessage < messageWait && lastMessageSender.equals(username) && lastMessageText.equals(text))
                        {
                            Log.d("StackFrame-Backend", "Message duplicate detected. Ignoring.");
                            return;
                        }

                        lastMessageSender = username;
                        lastMessageText = text;
                    } catch (JSONException e) {
                        //Toast.makeText(StackFrameChat.this, "Something wrong with the message I got...", Toast.LENGTH_SHORT).show();
                        Log.d("StackFrame-Backend", "Something wrong with the message I got..." + args[0].toString());
                        return;
                    }
                    //Toast.makeText(StackFrameChat.this, "Got message: " + text, Toast.LENGTH_SHORT).show();
                    sendResult("message", data);
                }
            });
        }
    };

    private Emitter.Listener onPrivate = new Emitter.Listener() {
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
                        //Toast.makeText(StackFrameChat.this, "Something wrong with the message I got...", Toast.LENGTH_SHORT).show();
                        Log.d("StackFrame-Backend", "Something wrong with the message I got..." + args[0].toString());
                        return;
                    }
                    //Toast.makeText(StackFrameChat.this, "Got message: " + text, Toast.LENGTH_SHORT).show();
                    sendResult("private", data);
                }
            });
        }
    };

    private Emitter.Listener onRegister = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        if (data.getString("token").equals("-1"))
                        {
                            //Toast.makeText(StackFrameChat.this, "Invalid registration information", Toast.LENGTH_SHORT).show();
                            Log.v("StackFrame-Backend", "Invalid login information. Received a -1 token value.");
                            Intent intent = new Intent("failedRegister");
                            intent.putExtra("action", "failedRegister");
                            LocalBroadcastManager.getInstance(StackFrameChat.this).sendBroadcast(intent);
                            return;
                        }
                        else
                        {
                            loginEditor.putString("token", data.getString("token"));
                            token = data.getString("token");
                            loginEditor.commit();
                            Log.v("StackFrame-Backend", "Valid login. Updated user preferences (New token: " + token + ")");
                        }
                    } catch (JSONException e) {
                        Toast.makeText(StackFrameChat.this, "Something wrong with the message I got...", Toast.LENGTH_SHORT).show();
                        Log.d("StackFrame-Backend", "Something wrong with the message I got..." + args[0].toString());
                        Intent intent = new Intent("failedRegister");
                        intent.putExtra("action", "failedRegister");
                        LocalBroadcastManager.getInstance(StackFrameChat.this).sendBroadcast(intent);
                        return;
                    }
                    //Toast.makeText(StackFrameChat.this, "Got message: " + text, Toast.LENGTH_SHORT).show();

                    sendResult("register", data);
                    chat = new Intent(StackFrameChat.this, ChatActivity.class);
                    chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(chat);
                }
            });
        }
    };

    private Emitter.Listener onProfile = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(StackFrameChat.this, "Got profile!", Toast.LENGTH_SHORT).show();
                    JSONObject data = (JSONObject) args[0];
                    sendResult("profileLoaded", data);

                    Log.v("StackFrame-Backend", "Received profile from server: " + args[0]);
                }
            });
        }
    };

    private Emitter.Listener onNumberAvatars = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.v("StackFrame-Backend", "Received numberAvatars: " + args[0]);

                    updateNumberAvatars((int) args[0]);
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

    public void sendResult(String action, Object message) {
        Intent intent = new Intent("incomingMessage");
        intent.putExtra("action", action);
        if(message.getClass() == JSONObject.class)
        {
            intent.putExtra("message", message.toString());
        }
        else
        {
            intent.putExtra("message", (int) message);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void updateNumberAvatars(int  num)
    {
        Intent intent = new Intent("numberAvatars");
        intent.putExtra("action", "numberAvatars");
        intent.putExtra("message", num);
        LocalBroadcastManager.getInstance(StackFrameChat.this).sendBroadcast(intent);
    }

    private void login()
    {
        //Toast.makeText(this, "Sending Login Info to Server", Toast.LENGTH_SHORT).show();

        JSONObject registerjson = new JSONObject();
        try {
            registerjson.put("username", username);
            registerjson.put("password", password);
            registerjson.put("geoloc", geoloc);
        } catch (Exception e) {
            Toast.makeText(this, "Login Failed...", Toast.LENGTH_SHORT).show();
            Log.e("StackFrame-Backend", e.getStackTrace().toString());
            e.printStackTrace();
        }
        socket.emit("login", registerjson);
    }

    private void register()
    {
        //Toast.makeText(this, "Sending Registration Info to Server", Toast.LENGTH_SHORT).show();

        JSONObject registerjson = new JSONObject();
        try {
            registerjson.put("username", username);
            registerjson.put("password", password);
            registerjson.put("geoloc", geoloc);
            registerjson.put("avatar", avatar);
        } catch (Exception e) {
            Toast.makeText(this, "Registration Failed...", Toast.LENGTH_SHORT).show();
            //Log.e("StackFrame-Backend", e.getStackTrace().toString());
            e.printStackTrace();
        }
        Log.v("StackFrame-Backend", "Sending the following json array to register in StackFrame: " + registerjson.toString());
        socket.emit("register", registerjson);
    }

    private void reconnect()
    {
       //Toast.makeText(this, "Reconnecting to server", Toast.LENGTH_SHORT).show();

        JSONObject registerjson = new JSONObject();
        try {
            registerjson.put("username", username);
            registerjson.put("token", token);
            registerjson.put("geoloc", geoloc);
            registerjson.put("serverid", serverid);
        } catch (Exception e) {
            Toast.makeText(this, "Reconnect Failed...", Toast.LENGTH_SHORT).show();
            Log.e("StackFrame-Backend", "Unable to build register JSON data");
            e.printStackTrace();
        }
        Log.d("StackFrame-Backend", "Sending reconnect info now...");
        socket.emit("relogin", registerjson);
        //password = "password";
        //login();
    }
}
