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

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {

    TextView title;
    Button login;
    EditText username;
    EditText password;

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

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                //TODO: Add logic for some type of animation

                //TODO: Add logic for an HTTP request to HTTP://stackframe.site88.net/authenticate.php
                // Instantiate the RequestQueue
// Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = "HTTP://stackframe.site88.net/authenticate.php";

// Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                try
                                {
                                    JSONObject auth = new JSONObject(response);
                                    Log.v("Stackframe", response);
                                    if(auth.getInt("success") == 1)
                                    {
                                        Toast.makeText(Login.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();

                                        //TODO: Parse and save login info for future use!!

                                        Intent intent = new Intent(Login.this, ChatActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(Login.this, "Invalid Login Info:", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(Login.this, username.getText().toString(), Toast.LENGTH_SHORT).show();
                                        username.setText("");
                                        password.setText("");
                                    }
                                }
                                catch(Exception e)
                                {
                                    Toast.makeText(Login.this, "Unable to read data received from network! :(", Toast.LENGTH_SHORT).show();
                                }

                                //Toast.makeText(Login.this, "Response is: ", Toast.LENGTH_SHORT).show();



                                //mTextView.setText("Response is: "+ response.substring(0,500));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "Unable to connect...", Toast.LENGTH_SHORT).show();
                        //mTextView.setText("That didn't work!");
                    }
                })
                //@Override
                {protected Map<String, String> getParams () {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", username.getText().toString());
                        params.put("password", password.getText().toString());
                        return params;
                    }
                };
// Add the request to the RequestQueue.
                queue.add(stringRequest);
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
}



