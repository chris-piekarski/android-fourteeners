package com.cpiekarski.fourteeners.utils;

import android.os.Build;
import android.util.Log;


public class SRLOG {
    // Enable verbose logs for non-user builds without relying on BuildConfig
    private static final boolean silent = !"user".equals(Build.TYPE);
    
    private static void log(String l, String tag, String msg) {
        if(l.equals("v") && silent) {
            Log.v(tag, msg);
        } else if (l.equals("d") && silent) {
            Log.d(tag, msg);
        } else if (l.equals("i")) {
            Log.i(tag, msg);
        } else if (l.equals("w")) {
            Log.w(tag, msg);
        } else if (l.equals("e")) {
            Log.e(tag, msg);
        } else if (l.equals("wtf")) {
            Log.wtf(tag, msg);
        }
    }
    
    public static void v(String TAG, String msg) {
        log("v", TAG, msg);
    }
    
    public static void d(String TAG, String msg) {
        log("d", TAG, msg);
    }
    
    public static void i(String TAG, String msg) {
        log("i", TAG, msg);
    }
    
    public static void w(String TAG, String msg) {
        log("w", TAG, msg);
    }
    
    public static void e(String TAG, String msg) {
        log("e", TAG, msg);
    }
    
    public static void wtf(String TAG, String msg) {
        log("wtf", TAG, msg);
    }
}
