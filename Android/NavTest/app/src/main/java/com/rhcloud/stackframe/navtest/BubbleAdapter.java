package com.rhcloud.stackframe.navtest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Desone on 11/12/2015.
 */
public class BubbleAdapter extends BaseAdapter {

    ArrayList<Message> list = new ArrayList<>();
    Context context;
    ArrayList<Bitmap> avatarCache;
    private static LayoutInflater inflater = null;

    public BubbleAdapter(Activity mainActivity, ArrayList<Message> messages)
    {
        list = messages;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        avatarCache = new ArrayList<Bitmap>();
    }

    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Bubble bubble = new Bubble();
        View row;
        row = inflater.inflate(R.layout.chat_bubble, null);
        bubble.text = (TextView) row.findViewById(R.id.text);
        bubble.date = (TextView) row.findViewById(R.id.date);
        bubble.layout = (LinearLayout) row.findViewById(R.id.chatlayout);
        bubble.avatar = (ImageView) row.findViewById(R.id.imageView);
        if(list.get(position).getUsername().equals("Application") || list.get(position).getUsername().equals("SERVER"))
        {
            bubble.avatar.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            new DownloadImageTask(bubble.avatar).execute("http://nodejs-stackframe.rhcloud.com/img" + list.get(position).getAvatar());
        }
        bubble.text.setText(list.get(position).getText());
        bubble.date.setText(cleanDate(list.get(position).getDate())); //Do date cleaning here
        bubble.layout.setMinimumWidth( parent.getWidth() );
        //bubble.avatar.setImageResource(); //Set image here!
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked a message by: " + list.get(position).getUsername(), Toast.LENGTH_SHORT).show();
                //TODO: Show user profile in dialog
            }
        });
        return row;
    }

    public String cleanDate(String prevDate)
    {
        String output = "";
        Long prevDateNum;
        long diff = 0;
        try {
            prevDateNum = Long.parseLong(prevDate);
            diff = (new Date().getTime()) - prevDateNum;
            if(diff < 1000) //Less than 1 second
                output = "0s";
            else if(diff < (60 * 1000)) //Less than 1 minute
                output = (diff / 1000) + "s";
            else if(diff < (60 * 60 * 1000)) //Less than 1 hour
                output = (diff / (60 * 1000)) + "m " + ((diff % (60 * 1000)) / 1000) + "s";
            else if(diff < (24 * 60 * 60 * 1000)) //Less than 1 day
                output = (diff % (60 * 60 * 1000)) + "h " + ((diff % (60 * 60 * 1000)) / (60 * 1000)) + "m " ;//+ (((diff % (60 * 60 * 1000)) % (60 * 1000)) / 1000) + "s";
            else if(diff > (24 * 60 * 60 * 1000)) //Less than 1 year
                output = (diff % (24 * 60 * 60 * 1000)) + "d " + ((diff % (24 * 60 * 60 * 1000)) % (60 * 60 * 1000)) + "h " ;//+ (((diff % (24 * 60 * 60 * 1000)) % (60 * 60 * 1000)) / (60 * 1000)) + "m " + ((((diff % (24 * 60 * 60 * 1000)) % (60 * 60 * 1000)) % (60 * 1000)) / 1000) + "s";
           else
                output = diff + "ms ago";
        } catch(Exception e)
        {
            Log.w("StackFrame UI", "Malformed date string, should be in ms since 1970 format: " + prevDate);
            output = "???";
        }
        return output;
    }

    public class Bubble
    {
        TextView text;
        TextView date;
        LinearLayout layout;
        ImageView avatar;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            /*int index = Integer.parseInt(urldisplay.substring(urldisplay.length() - 2, urldisplay.length() - 1));
            if(avatarCache.get(index) != null)
            {
                Log.v("StackFrame-UI", "Image " + index + " found in cache");
                return avatarCache.get(index);
            }
            else
            {
                Log.v("StackFrame-UI", "No image in cache. Loading from server.");
            }*/
            Bitmap mIcon11 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            //avatarCache.add(index, mIcon11);
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.invalidate();
            Log.v("StackFrame-UI", "Set new image for view");
        }
    }
}
