package net.site88.stackframe.stackframe;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

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

    String token;
    String serverid;

    /** Called when the service is being created. */
    @Override
    public void onCreate() {

    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extra = intent.getExtras();
        username =  extra.getString("username");
        password = extra.getString("password");
        geoloc = extra.getString("geoloc");

        try {
            socket = IO.socket("http://nodejs-stackframe.rhcloud.com");
        } catch (Exception e) {}


        socket.on("register", socketRegister());
        socket.on("message", receiveMessage());
        socket.connect();

        JSONObject registerjson = new JSONObject();
        try {
            registerjson.put("username", username);
            registerjson.put("password", password);
            registerjson.put("geoloc", geoloc);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error putting together registration data", Toast.LENGTH_SHORT).show();
            Log.e("StackFrame", e.getStackTrace().toString());
            e.printStackTrace();
        }
        socket.emit("register", registerjson);


        return mStartMode;
    }

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

    public Emitter.Listener socketRegister()
    {
        Emitter.Listener onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
               new Runnable() {
                   @Override
                   public void run() {
                       JSONObject data = (JSONObject) args[0];
                       String token;
                       String serverid;
                       try {
                           token = data.getString("token");
                           serverid = data.getString("serverid");
                           Toast.makeText(StackFrameChat.this, "Received token '" + token + "' and serverid '" + serverid + "'.", Toast.LENGTH_SHORT).show();

                           Intent chatroom = new Intent(StackFrameChat.this, ChatActivity.class);
                       } catch (JSONException e) {
                           return;
                       }
                   }
               };
            }
        };
        return onNewMessage;
    }

    public Emitter.Listener receiveMessage()
    {
        Emitter.Listener onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String user;
                        String text;
                        try {
                            user = data.getString("username");
                            text = data.getString("text");
                            Toast.makeText(StackFrameChat.this, "Received message [" + user + "]: " + text, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            return;
                        }
                    }
                };
            }
        };
        return onNewMessage;
    }
}
