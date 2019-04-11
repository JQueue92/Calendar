package com.jqueue.formatlog;

import android.util.Log;

public class LogUtils {
    private static final boolean DEBUG = false;

    public static final void d(String tag,String msg){
        if(DEBUG) Log.d(tag,msg);
    }

    public static final void i(String tag,String msg){
        if(DEBUG) Log.i(tag, msg);
    }

    public static final void v(String tag,String msg){
        if(DEBUG) Log.d(tag, msg);
    }

}
