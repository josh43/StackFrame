package com.rhcloud.stackframe.navtest;

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

import org.json.JSONException;
import org.json.JSONObject;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.rhcloud.stackframe.navtest.R;


public class Login extends AppCompatActivity {

    TextView title;
    Button login;
    EditText username;
    EditText password;
    Socket socket;
    Intent loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(R.anim.abc_popup_enter, R.anim.abc_popup_enter);

        title = (TextView) findViewById(R.id.title);
        login = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        if(title != null)
        {
            //Typeface Orbitron = Typeface.createFromAsset(getAssets(), "fonts/Orbitron.ttf");
           //title.setTypeface(Orbitron);
        }

        loginService = new Intent(Login.this, StackFrameChat.class);
        loginService.putExtra("action", "startup");
        startService(loginService);


        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                //TODO: Add logic for some type of animation
                loginService.putExtra("action", "register");
                loginService.putExtra("username", username.getText().toString());
                loginService.putExtra("passowrd", password.getText().toString());
                loginService.putExtra("geoloc", "location");
                startService(loginService);
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopService(loginService);
        //overridePendingTransition(R.anim.abc_popup_enter, R.anim.abc_shrink_fade_out_from_bottom);
    }
}