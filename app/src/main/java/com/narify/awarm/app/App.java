package com.narify.awarm.app;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class App extends Application {
    private static App mInstance;

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}