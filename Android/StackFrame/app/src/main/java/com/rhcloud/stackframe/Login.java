package com.rhcloud.stackframe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.rhcloud.stackframe.R;

import java.io.InputStream;
import java.util.ArrayList;


public class Login extends AppCompatActivity {

    TextView title;
    Button loginView;
    Button registerView;
    EditText usernameView;
    EditText passwordView;
    ProgressBar loadingBar;
    ProgressBar connectingProgressbar;
    Socket socket;
    Intent loginService;
    int loginWidth;
    int shortAnimation = 100;
    Dialog imageSelect;
    ArrayList<ImageView> avatars;
    //ArrayList<Bitmap> avatarCache;
    int rowSize = 2;
    LruCache<String, Bitmap> cache;
    SharedPreferences loginInfo;
    SharedPreferences.Editor loginEditor;
    UIAnimator loginAnimator;
    UIAnimator registerAnimator;
    UIAnimator usernameAnimator;
    UIAnimator passwordAnimator;
    UIAnimator loadingbarAnimator;
    UIAnimator connectionAnimator;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(R.anim.abc_popup_enter, R.anim.abc_popup_enter);

        avatars = new ArrayList<ImageView>();
        //avatarCache = new ArrayList<Bitmap>();

        loginInfo = getApplication().getSharedPreferences("loginInfo", MODE_PRIVATE);
        loginEditor = loginInfo.edit();

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        cache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap bitmap)
            {
                return bitmap.getByteCount() / 1024;
            }
        };

        setTitle("");
        this.setTitleColor(getResources().getColor(R.color.fontColor));

        title = (TextView) findViewById(R.id.title);
        loginView = (Button) findViewById(R.id.login);
        registerView = (Button) findViewById(R.id.register);
        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        loadingBar = (ProgressBar) findViewById(R.id.progressBar);
        connectingProgressbar = (ProgressBar) findViewById(R.id.connectingProgressbar);

        if(title != null)
        {
            Typeface Orbitron = Typeface.createFromAsset(getAssets(), "fonts/Orbitron.ttf");
            title.setTypeface(Orbitron);
        }

        loginInfo = getApplicationContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        if (!loginInfo.getString("token", "").equals("")) {
            loginView.setVisibility(View.GONE);
            registerView.setVisibility(View.GONE);
            usernameView.setVisibility(View.GONE);
            passwordView.setVisibility(View.GONE);
            loadingBar.setVisibility(View.VISIBLE);
        }

        loginService = new Intent(Login.this, StackFrameChat.class);
        loginService.putExtra("action", "startup");
        startService(loginService);

        loginAnimator = new UIAnimator(loginView);
        registerAnimator = new UIAnimator(registerView);
        usernameAnimator = new UIAnimator(usernameView);
        passwordAnimator = new UIAnimator(passwordView);
        loadingbarAnimator = new UIAnimator(loadingBar);
        connectionAnimator = new UIAnimator(connectingProgressbar);


        loginView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                loginWidth = usernameView.getWidth();
                //usernameView.setText("");
                //passwordView.setText("");

                Runnable progressbarCallback = new Runnable() {
                    @Override
                    public void run() {
                        loadingbarAnimator.growView(shortAnimation, UIAnimator.Position.CENTER, true,  null);
                    }
                };

                loginAnimator.shrinkView(shortAnimation, UIAnimator.Position.RIGHT, true, progressbarCallback);
                registerAnimator.shrinkView(shortAnimation, UIAnimator.Position.LEFT, true, null);
                usernameAnimator.shrinkView(shortAnimation, UIAnimator.Position.CENTER, true, null);
                passwordAnimator.shrinkView(shortAnimation, UIAnimator.Position.CENTER, true, null);


                loginService.putExtra("action", "login");
                loginService.putExtra("username", usernameView.getText().toString());
                loginService.putExtra("password", passwordView.getText().toString());
                loginService.putExtra("geoloc", "location");
                startService(loginService);

                //Toast.makeText(Login.this, "Attempting to log in...", Toast.LENGTH_SHORT).show();
            }
        });

        registerView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                loginWidth = usernameView.getWidth();
                //usernameView.setText("");
                //passwordView.setText("");

                loginService.putExtra("action", "numberAvatars");
                startService(loginService);
                //loginShrink.start();
                //loginService.putExtra("action", "register");
                //loginService.putExtra("username", usernameView.getText().toString());
                //loginService.putExtra("password", passwordView.getText().toString());
                //loginService.putExtra("geoloc", "location");
            }
        });

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getStringExtra("action") != null && intent.getStringExtra("action").equals("growFields")) {
                    growFields();
                }
                else if(intent.getStringExtra("action").equals("numberAvatars"))
                {
                    //Toast.makeText(Login.this, "Time to load all " + ((int) intent.getIntExtra("message", 0)) + " avatars", Toast.LENGTH_SHORT).show();
                    Log.v("StackFrame-UI", "Time to load all " + ((int) intent.getIntExtra("message", 0)) + " avatars");
                    inflateImageSelect(((int) intent.getIntExtra("message", 0)));
                }
                else if(intent.getStringExtra("action").equals("failedRegister"))
                {
                    Toast.makeText(Login.this, "User Exists...", Toast.LENGTH_SHORT).show();
                    Log.v("StackFrame-UI", "Error creating user, suspect that it already exists");
                    imageSelect.dismiss();
                    growFields();
                    //inflateImageSelect(((int) intent.getIntExtra("message", 0)));
                }
                else if(intent.getStringExtra("action").equals("register"))
                {
                    Toast.makeText(Login.this, "Profile created!", Toast.LENGTH_SHORT).show();
                    Log.v("StackFrame-UI", "New profile created");
                    imageSelect.dismiss();
                    //inflateImageSelect(((int) intent.getIntExtra("message", 0)));
                }
                else if(intent.getStringExtra("action").equals("connected"))
                {
                    Runnable buttonCallback = new Runnable() {
                        @Override
                        public void run() {
                            loginAnimator.growView(shortAnimation, UIAnimator.Position.RIGHT, true, null);
                            registerAnimator.growView(shortAnimation, UIAnimator.Position.LEFT, true, null);
                        }
                    };
                    connected = true;
                    connectionAnimator.shrinkView(shortAnimation, UIAnimator.Position.CENTER, true, buttonCallback);
                }
                else if(intent.getStringExtra("action").equals("failedLogin"))
                {
                    //Toast.makeText(Login.this, "Invalid login...", Toast.LENGTH_SHORT).show();
                    Log.v("StackFrame-UI", "Error logging in");
                    //imageSelect.dismiss();
                    growFields();
                    //inflateImageSelect(((int) intent.getIntExtra("message", 0)));
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("failedLogin"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("failedRegister"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("numberAvatars"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("connected"));
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
            logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(connected)
        {
            loginView.setVisibility(View.VISIBLE);
            registerView.setVisibility(View.VISIBLE);
            usernameView.setVisibility(View.VISIBLE);
            passwordView.setVisibility(View.VISIBLE);
            loadingBar.setVisibility(View.GONE);
            connectingProgressbar.setVisibility(View.GONE);
        }
        else
        {
            //if(loginService.)
            //loginService.putExtra("action", "startup");
            //stopService(loginService);
            //startService(loginService);

            if(isMyServiceRunning(loginService.getClass()))
            {
                Toast.makeText(Login.this, "Service verified running", Toast.LENGTH_SHORT).show();
            }

            loginView.setVisibility(View.GONE);
            registerView.setVisibility(View.GONE);
            usernameView.setVisibility(View.VISIBLE);
            passwordView.setVisibility(View.VISIBLE);
            loadingBar.setVisibility(View.GONE);
            connectingProgressbar.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopService(loginService);
        //overridePendingTransition(R.anim.abc_popup_enter, R.anim.abc_shrink_fade_out_from_bottom);
    }

    private void growFields()
    {
        loadingBar.setVisibility(View.GONE);
        usernameView.setScaleX(1f);
        loginView.setScaleX(1f);
        registerView.setScaleX(1f);
        passwordView.setScaleX(1f);
        loadingBar.setVisibility(View.GONE);
        usernameView.setVisibility(View.VISIBLE);
        loginView.setVisibility(View.VISIBLE);
        registerView.setVisibility(View.VISIBLE);
        passwordView.setVisibility(View.VISIBLE);
    }

    private void inflateImageSelect(int count)
    {
        if(imageSelect == null)
        {
            imageSelect = new Dialog(this);
            imageSelect.setContentView(R.layout.avatar_select_dialog);
            imageSelect.setTitle("Select Avatar...");

            TableLayout base = (TableLayout) imageSelect.findViewById(R.id.dialogBase);
            TableRow row = new TableRow(this);

            Log.v("StackFrame-UI", "Dialog empty, creating new.");
            for (int i = 1; i < count; i++) {
                if (i % rowSize == 1) {
                    row = new TableRow(this);
                }

                final ImageView tempView = new ImageView(this);
                tempView.setImageResource(R.mipmap.ic_launcher);
                String[] temp = new String[2];
                temp[0] = getString(R.string.serverurl) + "/img";
                temp[1] = i + "";
                new DownloadImageTask(tempView, cache).execute(temp);
                tempView.setVisibility(View.VISIBLE);
                tempView.setMaxWidth(base.getWidth() / rowSize);
                tempView.setMinimumHeight(base.getWidth() / rowSize);
                tempView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loginService.putExtra("action", "register");
                        loginService.putExtra("username", usernameView.getText().toString());
                        loginService.putExtra("password", passwordView.getText().toString());
                        loginService.putExtra("geoloc", "location");
                        loginService.putExtra("avatar", avatars.indexOf(tempView) + 1);
                        startService(loginService);
                    }
                });
                avatars.add(tempView);
                row.addView(tempView);
                base.invalidate();

                if (i % rowSize == 0) {
                    base.addView(row);
                }
                Log.v("StackFrame-UI", "Loaded item: " + i);
            }

            if (count % 2 == 1) {
                base.addView(row);
            }
        }
        else
        {
            Log.v("StackFrame-UI", "View already populated. Reusing.");
            imageSelect.dismiss();
        }

        imageSelect.show();
    }

    public void logOut() {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        loginEditor.putString("serverid", "");
        loginEditor.putString("username", "");
        loginEditor.putString("token", "");
        loginEditor.putString("geoloc", "");
        loginEditor.commit();

        Intent login = getIntent();
        finish();
        startActivity(login);
    }

    private boolean isMyServiceRunning( Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if(serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
}