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
import android.graphics.Typeface;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(R.anim.abc_popup_enter, R.anim.abc_popup_enter);

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
                        //TEMP CODE
                        inflateImageSelect();
                        //
                        //startService(loginService);
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

               /* ValueAnimator loginGrow = ObjectAnimator.ofFloat(0f, 1f);
                loginGrow.setDuration(shortAnimation);
                loginGrow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        loadingBar.setScaleX(animation.getAnimatedFraction());
                        loadingBar.setScaleY(animation.getAnimatedFraction());
                        loadingBar.invalidate();
                    }
                });
                loginGrow.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        loginView.setVisibility(View.VISIBLE);
                        registerView.setVisibility(View.VISIBLE);
                        usernameView.setVisibility(View.VISIBLE);
                        passwordView.setVisibility(View.VISIBLE);
                        loadingBar.setVisibility(View.GONE);
                        ValueAnimator loadingGrow = ObjectAnimator.ofFloat(0f, 1f);
                        loadingGrow.setDuration(shortAnimation);
                        loginView.setPivotX(loginView.getWidth());
                        registerView.setPivotX(0f);
                        loadingGrow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                usernameView.setScaleX(animation.getAnimatedFraction());
                                loginView.setScaleX(animation.getAnimatedFraction());
                                registerView.setScaleX(animation.getAnimatedFraction());
                                passwordView.setScaleX(animation.getAnimatedFraction());
                                usernameView.invalidate();
                                loginView.invalidate();
                                registerView.invalidate();
                                passwordView.invalidate();
                            }
                        });
                        loadingGrow.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                            }
                        });
                        loadingGrow.start();
                    }
                });*/


            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("failedLogin"));
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

    private void inflateImageSelect()
    {
        final Dialog imageSelect = new Dialog(this);
        imageSelect.setContentView(R.layout.avatar_select_dialog);
        imageSelect.setTitle("Select Avatar...");

        //socket.emit("numberAvatars", null);
        //TODO: Get the number of available avatars
        ImageView singleView = new ImageView(this);
        singleView.setImageResource(R.drawable.male228);
        singleView.setVisibility(View.VISIBLE);
        ScrollView scrollView = (ScrollView) imageSelect.findViewById(R.id.scrollView);
        singleView.setMinimumWidth(scrollView.getWidth()/2);
        singleView.setMinimumHeight(scrollView.getWidth()/2);
        scrollView.addView(singleView);
        scrollView.invalidate();

        imageSelect.show();
    }
}