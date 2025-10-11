package com.cpiekarski.fourteeners.utils

import android.util.Log
import com.cpiekarski.fourteeners.BuildConfig

object SRLOG {
    // Make google happy by not polluting logcat in production
    private val silent = BuildConfig.DEBUG // "user".equals(Build.TYPE)
    
    private fun log(l: String, tag: String, msg: String) {
        when (l) {
            "v" -> if (silent) Log.v(tag, msg)
            "d" -> if (silent) Log.d(tag, msg)
            "i" -> Log.i(tag, msg)
            "w" -> Log.w(tag, msg)
            "e" -> Log.e(tag, msg)
            "wtf" -> Log.wtf(tag, msg)
        }
    }
    
    @JvmStatic
    fun v(tag: String, msg: String) {
        log("v", tag, msg)
    }
    
    @JvmStatic
    fun d(tag: String, msg: String) {
        log("d", tag, msg)
    }
    
    @JvmStatic
    fun i(tag: String, msg: String) {
        log("i", tag, msg)
    }
    
    @JvmStatic
    fun w(tag: String, msg: String) {
        log("w", tag, msg)
    }
    
    @JvmStatic
    fun e(tag: String, msg: String) {
        log("e", tag, msg)
    }
    
    fun wtf(tag: String, msg: String) {
        log("wtf", tag, msg)
    }
}