package com.cognitio.aisat;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by anshu on 10/10/16.
 */
public class VolleySingleton {
    private static VolleySingleton inst = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mimageloader;
    public Context context;
    private VolleySingleton(Context context){

        mRequestQueue = Volley.newRequestQueue(context);
        mimageloader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private LruCache<String,Bitmap> cache = new LruCache<>((int)Runtime.getRuntime().maxMemory()/1024/8);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url,bitmap);

            }
        });

    }

    public static VolleySingleton getinstance(Context context){
        if(inst == null)
        {
            inst = new VolleySingleton(context);
        }
        return inst;
    }

    public RequestQueue getrequestqueue()
    {
        return mRequestQueue;
    }
    public ImageLoader getimageloader()
    {
        return mimageloader;
    }
}
