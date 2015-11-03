package net.site88.stackframe.stackframe;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.site88.stackframe.stackframe.ChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


public class Login extends AppCompatActivity {

    TextView title;
    Button login;
    EditText username;
    EditText password;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        title = (TextView) findViewById(R.id.title);
        login = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        if(title != null)
        {
            Typeface Orbitron = Typeface.createFromAsset(getAssets(), "fonts/Orbitron.ttf");
            title.setTypeface(Orbitron);
        }

        try {
            socket = IO.socket("http://nodejs-stackframe.nhcloud.com");
        } catch (Exception e) {}


        socket.on("register", socketRegister());
        socket.on("message", socketRegister());
        socket.connect();
        JSONObject registerjson = new JSONObject();
        try {
            registerjson.put("username", username.getText().toString());
            registerjson.put("password", password.getText().toString());
            registerjson.put("geoloc", "location");
        }
        catch (Exception e)
        {
            Toast.makeText(Login.this, "Error putting together registration data", Toast.LENGTH_SHORT).show();
            Log.e("StackFrame", e.getStackTrace().toString());
            e.printStackTrace();
        }
        socket.emit("register", registerjson);

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                //TODO: Add logic for some type of animation


                Toast.makeText(Login.this, "Attempting to log in...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private Emitter.Listener socketRegister()
    {
        Emitter.Listener onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
               Login.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String token;
                        String serverid;
                        try {
                            token = data.getString("token");
                            serverid = data.getString("serverid");
                            Toast.makeText(Login.this, "Received token '" + token + "' and serverid '" + serverid + "'.", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            return;
                        }
                    }
                });
            }
        };
        return onNewMessage;
    }
}





