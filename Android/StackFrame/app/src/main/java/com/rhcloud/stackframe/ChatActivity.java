package com.rhcloud.stackframe;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;


public class ChatActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    ArrayList<Message> chat = new ArrayList<Message>();
    //ArrayAdapter arrayAdapter;
    BubbleAdapter arrayAdapter;
    ListView chatView;
    EditText message;
    Button send;
    boolean newMessage = false;
    BroadcastReceiver mMessageReceiver;
    LocalBroadcastManager broadcast;
    SharedPreferences loginInfo;
    SharedPreferences.Editor loginEditor;
    ArrayList<Bitmap> avatarCache;
    //LruCache<String, Bitmap> cache;
    DownloadImageTask imageDownloader;
    Intent loginService;
    LruCache<String, Bitmap> cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_drawer);

        avatarCache = new ArrayList<Bitmap>();
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

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        chatView = (ListView) findViewById(R.id.chatView);

        chat.add(new Message("Application", "None", "text", (new Date()).getTime() + "", "Welcome to StackFrame!", "0", "1"));

        loginInfo = getApplication().getSharedPreferences("loginInfo", MODE_PRIVATE);
        loginEditor = loginInfo.edit();

        loginService = new Intent(this, StackFrameChat.class);

        message = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String output =  message.getText().toString();
                message.setText("");
                sendResult(output);
                Log.d("StackFrame UI", "Received send button press, initiating message send.");
            }
        });

        arrayAdapter = new BubbleAdapter(this, chat, cache);

        chatView.setAdapter(arrayAdapter);

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent

                if(intent.getStringExtra("action").equals("message")) {
                    String message = intent.getStringExtra("message");
                    try {
                        JSONObject data = new JSONObject(message);
                        chat.add(new Message(data.getString("username"), data.getString("token"), data.getString("type"), data.getString("date"), data.getString("text"), data.getString("serverid"), data.getString("avatar")));
                        arrayAdapter.notifyDataSetChanged();
                        if (chatView.getLastVisiblePosition() == chat.size() - 2) {
                            chatView.smoothScrollToPosition(chat.size() - 1);
                        } else {
                            Log.d("StackFrame-Backend", "Position of listview is not at the end, not scrolling to bottom: " + chatView.getLastVisiblePosition() + "/" + chat.size());
                        }
                    } catch (Exception e) {
                        //Toast.makeText(ChatActivity.this, "Something wrong with the message data", Toast.LENGTH_SHORT).show();
                        Log.w("StackFrame UI", "Something wrong with message data: " + message);
                        e.printStackTrace();
                    }
                    Log.d("StackFrame-UI", "Got message: " + message);
                    //Toast.makeText(ChatActivity.this, "Got a message: " + message, Toast.LENGTH_SHORT).show();
                }
                else if(intent.getStringExtra("action").equals("private"))
                {
                    String message = intent.getStringExtra("message");
                    try {
                        JSONObject data = new JSONObject(message);
                        chat.add(new Message(data.getString("username"), data.getString("token"), data.getString("type"), data.getString("date"), "PRIVATE: " + data.getString("text"), data.getString("serverid"), data.getString("avatar")));
                        arrayAdapter.notifyDataSetChanged();
                        if (chatView.getLastVisiblePosition() == chat.size() - 2) {
                            chatView.smoothScrollToPosition(chat.size() - 1);
                        } else {
                            Log.d("StackFrame-Backend", "Position of listview is not at the end, not scrolling to bottom: " + chatView.getLastVisiblePosition() + "/" + chat.size());
                        }
                    } catch (Exception e) {
                        //Toast.makeText(ChatActivity.this, "Something wrong with the message data", Toast.LENGTH_SHORT).show();
                        Log.w("StackFrame UI", "Something wrong with message data: " + message);
                        e.printStackTrace();
                    }
                    Log.d("StackFrame-UI", "Got message: " + message);
                    //Toast.makeText(ChatActivity.this, "Got a message: " + message, Toast.LENGTH_SHORT).show();
                }
                else if(intent.getStringExtra("action").equals("profileLoaded"))
                {
                    //Toast.makeText(ChatActivity.this, "Got Profile!", Toast.LENGTH_SHORT).show();
                    Log.v("StackFrame-UI", "Received profile: " + intent.getStringExtra("message"));

                    String avatar = "";
                    String username = "";
                    String score = "";
                    try
                    {
                        JSONObject profileData = new JSONObject(intent.getStringExtra("message"));
                        avatar = profileData.getString("avatar");
                        username = profileData.getString("username");
                        score = profileData.getString("score");
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    Dialog profile = new Dialog(ChatActivity.this);
                    profile.setContentView(R.layout.profile_dialog);
                    try
                    {
                        ImageView avatarView = (ImageView)  profile.findViewById(R.id.avatarView);
                        avatarView.setImageResource(R.mipmap.ic_launcher);
                        String[] temp = new String[2];
                        temp[0] = context.getString(R.string.serverurl) + "/img";
                        temp[1] = avatar;
                        imageDownloader = new DownloadImageTask(avatarView, cache);
                        imageDownloader.execute(temp);
                        TextView usernameView = (TextView) profile.findViewById(R.id.usernameView);
                        TextView scoreView = (TextView) profile.findViewById(R.id.scoreView);
                        usernameView.setText(username);
                        scoreView.setText("Score: " + score);
                    }
                    catch (Exception e)
                    {
                        Log.w("StackFrame-UI", "Unable to load profile image!");
                        e.printStackTrace();
                    }
                    profile.setTitle("Profile:");
                    profile.show();
                }
            }
        };
        //Log.d("StackFrame UI", "View height (listview): " + chatView.getHeight());
        //Log.d("StackFrame UI", "View height (container): " + findViewById(R.id.container).getHeight());
        //Log.d("StackFrame UI", "View height (layout): " + findViewById(R.id.drawer_layout).getHeight());
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("incomingMessage"));

        //chatView.setMinimumHeight(findViewById(R.id.drawer_layout).getHeight() - getSupportActionBar().getHeight());
        //Log.d("StackFrame UI", "View height (chatview): " + (findViewById(R.id.drawer_layout).getHeight() - getSupportActionBar().getHeight()));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

        switch(position)
        {
            case 0: showProfile();
                break;
            case 1: showSettings();
                break;
            case 2: logOut();
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                //mTitle = getString(R.string.title_section1);
                //Toast.makeText(ChatActivity.this, "Showing Profile...", Toast.LENGTH_SHORT).show();
                //showProfile();
                break;
            case 2:
                //mTitle = getString(R.string.title_section2);
                //showSettings();
                break;
            case 3:
                //mTitle = getString(R.string.title_section3);
                //logOut();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
    protected void onDestroy()
    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        stopService(loginService);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ChatActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void sendResult(String message) {
        Intent intent = new Intent("outgoingMessage");
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d("StackFrame UI", "Message sent to backend.");
    }

    private void showProfile()
    {
        if(loginService != null) {
            loginService.putExtra("action", "profile");
            loginService.putExtra("index", loginInfo.getString("username", ""));
            startService(loginService);
        }
    }

    private void showSettings()
    {

    }

    public void logOut() {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        loginEditor.putString("serverid", "");
        loginEditor.putString("username", "");
        loginEditor.putString("token", "");
        loginEditor.putString("geoloc", "");
        loginEditor.commit();

        Intent login = new Intent(this, Login.class);
        //login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(login);
    }
}
