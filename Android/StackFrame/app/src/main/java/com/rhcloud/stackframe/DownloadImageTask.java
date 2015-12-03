package com.rhcloud.stackframe;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.rhcloud.stackframe.R;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    int index;
    LruCache<String, Bitmap> cache;

    public DownloadImageTask(ImageView bmImage, LruCache<String, Bitmap> cache) {
        this.bmImage = bmImage;
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
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        String avatarIndex= urls[1];
        if(avatarIndex != null && getBitmapFromMemCache(avatarIndex) != null)
        {
            Log.v("StackFrame-UI", "Image " + avatarIndex + " found in cache");
            return getBitmapFromMemCache(avatarIndex);
        }
        else
        {
            Log.v("StackFrame-UI", "No image in cache. Loading from server url: " + urldisplay + avatarIndex);
            Bitmap mIcon = BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher);
            try {
                InputStream in = new java.net.URL(urldisplay + avatarIndex).openStream();
                mIcon = BitmapFactory.decodeStream(in);
                addBitmapToMemoryCache(avatarIndex, mIcon);
                Log.v("StackFrame-UI", "Added image '" + avatarIndex + "' to cache" );
            } catch (Exception e) {
                Log.e("StackFrame-UI", e.getMessage());
                e.printStackTrace();
            }
            //avatarCache.add(index, mIcon11);
            return mIcon;

        }
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        bmImage.invalidate();
        Log.v("StackFrame-UI", "Set new image for view");
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            cache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        if(cache != null) return cache.get(key);
        else return null;
    }
}