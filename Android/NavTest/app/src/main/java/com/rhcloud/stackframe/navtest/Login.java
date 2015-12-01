package com.rhcloud.stackframe.navtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import com.rhcloud.stackframe.navtest.R;

import java.io.InputStream;
import java.util.ArrayList;


public class Login extends AppCompatActivity {

    TextView title;
    Button loginView;
    Button registerView;
    EditText usernameView;
    EditText passwordView;
    ProgressBar loadingBar;
    Socket socket;
    Intent loginService;
    SharedPreferences loginInfo;
    int loginWidth;
    int shortAnimation = 100;
    Dialog imageSelect;
    ArrayList<ImageView> avatars;
    int rowSize = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(R.anim.abc_popup_enter, R.anim.abc_popup_enter);

        avatars = new ArrayList<ImageView>();

        setTitle("");
        this.setTitleColor(getResources().getColor(R.color.fontColor));

        title = (TextView) findViewById(R.id.title);
        loginView = (Button) findViewById(R.id.login);
        registerView = (Button) findViewById(R.id.register);
        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        loadingBar = (ProgressBar) findViewById(R.id.progressBar);

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


        loginView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                loginWidth = usernameView.getWidth();
                //usernameView.setText("");
                //passwordView.setText("");

                ValueAnimator loginShrink = ObjectAnimator.ofFloat(0f, 1f);
                loginShrink.setDuration(shortAnimation);
                loginView.setPivotX(loginView.getWidth());
                registerView.setPivotX(0f);
                loginShrink.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        usernameView.setScaleX(1f - animation.getAnimatedFraction());
                        loginView.setScaleX(1f - animation.getAnimatedFraction());
                        registerView.setScaleX(1f - animation.getAnimatedFraction());
                        passwordView.setScaleX(1f - animation.getAnimatedFraction());
                        usernameView.invalidate();
                        loginView.invalidate();
                        registerView.invalidate();
                        passwordView.invalidate();
                    }
                });
                loginShrink.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        loginView.setVisibility(View.GONE);
                        registerView.setVisibility(View.GONE);
                        usernameView.setVisibility(View.GONE);
                        passwordView.setVisibility(View.GONE);
                        loadingBar.setVisibility(View.VISIBLE);
                        ValueAnimator loadingGrow = ObjectAnimator.ofFloat(0f, 1f);
                        loadingGrow.setDuration(shortAnimation);
                        loginView.setPivotX(loginView.getWidth());
                        registerView.setPivotX(0f);
                        loadingGrow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                loadingBar.setScaleX(animation.getAnimatedFraction());
                                loadingBar.setScaleY(animation.getAnimatedFraction());
                                loadingBar.invalidate();
                            }
                        });
                        loadingGrow.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                loginView.setVisibility(View.GONE);
                                registerView.setVisibility(View.GONE);
                                usernameView.setVisibility(View.GONE);
                                passwordView.setVisibility(View.GONE);
                                loadingBar.setVisibility(View.VISIBLE);
                                startService(loginService);
                            }
                        });
                        loadingGrow.start();

                        startService(loginService);
                    }
                });
                loginShrink.start();
                loginService.putExtra("action", "login");
                loginService.putExtra("username", usernameView.getText().toString());
                loginService.putExtra("password", passwordView.getText().toString());
                loginService.putExtra("geoloc", "location");

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

                ValueAnimator loginShrink = ObjectAnimator.ofFloat(0f, 1f);
                loginShrink.setDuration(shortAnimation);
                loginView.setPivotX(loginView.getWidth());
                registerView.setPivotX(0f);
                loginShrink.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        usernameView.setScaleX(1f - animation.getAnimatedFraction());
                        loginView.setScaleX(1f - animation.getAnimatedFraction());
                        registerView.setScaleX(1f - animation.getAnimatedFraction());
                        passwordView.setScaleX(1f - animation.getAnimatedFraction());
                        usernameView.invalidate();
                        loginView.invalidate();
                        registerView.invalidate();
                        passwordView.invalidate();
                    }
                });
                loginShrink.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        loginView.setVisibility(View.GONE);
                        registerView.setVisibility(View.GONE);
                        usernameView.setVisibility(View.GONE);
                        passwordView.setVisibility(View.GONE);
                        loadingBar.setVisibility(View.VISIBLE);
                        ValueAnimator loadingGrow = ObjectAnimator.ofFloat(0f, 1f);
                        loadingGrow.setDuration(shortAnimation);
                        loginView.setPivotX(loginView.getWidth());
                        registerView.setPivotX(0f);
                        loadingGrow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                loadingBar.setScaleX(animation.getAnimatedFraction());
                                loadingBar.setScaleY(animation.getAnimatedFraction());
                                loadingBar.invalidate();
                            }
                        });
                        loadingGrow.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                loginView.setVisibility(View.GONE);
                                registerView.setVisibility(View.GONE);
                                usernameView.setVisibility(View.GONE);
                                passwordView.setVisibility(View.GONE);
                                loadingBar.setVisibility(View.VISIBLE);
                                //startService(loginService);
                            }
                        });
                        loadingGrow.start();
                        loginService.putExtra("action", "numberAvatars");
                        startService(loginService);
                    }
                });
                loginShrink.start();
                loginService.putExtra("action", "register");
                loginService.putExtra("username", usernameView.getText().toString());
                loginService.putExtra("password", passwordView.getText().toString());
                loginService.putExtra("geoloc", "location");
            }
        });

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getStringExtra("action") != null && intent.getStringExtra("action").equals("growFields")) {
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
                else if(intent.getStringExtra("action").equals("numberAvatars"))
                {
                    Toast.makeText(Login.this, "Time to load all " + ((int) intent.getIntExtra("message", 0)) + " avatars", Toast.LENGTH_SHORT).show();
                    Log.v("StackFrame-UI", "Time to load all " + ((int) intent.getIntExtra("message", 0)) + " avatars");
                    inflateImageSelect(((int) intent.getIntExtra("message", 0)));
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("failedLogin"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("numberAvatars"));
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

    private void inflateImageSelect(int count)
    {
        imageSelect = new Dialog(this);
        imageSelect.setContentView(R.layout.avatar_select_dialog);
        imageSelect.setTitle("Select Avatar...");

        TableLayout base = (TableLayout) imageSelect.findViewById(R.id.dialogBase);
        TableRow row = new TableRow(this);

        for(int i = 1; i <= count; i++)
        {
            if(i % rowSize == 1)
            {
                row = new TableRow(this);
            }

            final ImageView tempView = new ImageView(this);
            tempView.setImageResource(R.mipmap.ic_launcher);
            new DownloadImageTask(tempView).execute("http://nodejs-stackframe.rhcloud.com/img" + i);
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

            if(i % rowSize == 0)
            {
                base.addView(row);
            }
            Log.v("StackFrame-UI", "Loaded item: " + i);
        }

        if(count % 2 == 1)
        {
            base.addView(row);
        }

        imageSelect.show();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.invalidate();
            Log.v("StackFrame-UI", "Set new image for view");
        }
    }
}