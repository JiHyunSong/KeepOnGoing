package com.secsm.keepongoing.Shared;


import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Helper class that is used to provide references to initialized RequestQueue(s) and ImageLoader(s)
 *
 * @author Ognyan Bankov
 */
public class MyVolley {
    private static MyVolley instance;
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    private static Context mContext;
    private MyVolley() {
// no instances
    }

    public static void init(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
//        ImprovedDiskBasedCache cache = new ImprovedDiskBasedCache(context.getCacheDir(), 16 * 1024 * 1024);
//
//        mRequestQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
//        mRequestQueue.start();
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
// Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
        Log.i("MyVolley", "cacheSize size : " + cacheSize);
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));
    }

    public static RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
//            mRequestQueue = Volley.newRequestQueue(context);
            ImprovedDiskBasedCache cache = new ImprovedDiskBasedCache(context.getCacheDir(), 16 * 1024 * 1024);
            mRequestQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
            mRequestQueue.start();
            return mRequestQueue;
//            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    /**
     * Returns instance of ImageLoader initialized with {@see FakeImageCache} which effectively means
     * that no memory caching is used. This is useful for images that you know that will be show
     * only once.
     *
     * @return
     */
    public static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }


}