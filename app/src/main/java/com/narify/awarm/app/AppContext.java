package com.narify.awarm.app;

import android.content.Context;

public class AppContext {

    private static AppContext mInstance;

    private AppContext() {
    }

    private static synchronized AppContext getInstance() {
        if (mInstance == null) {
            mInstance = new AppContext();
        }
        return mInstance;
    }

    private static Context getContext() {
        return App.getInstance();
    }

    public static synchronized Context get() {
        return getInstance().getContext();
    }
}
