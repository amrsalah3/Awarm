package com.narify.awarm.app;

import android.content.res.Resources;

public class AppResources {

    private static AppResources mInstance;

    private AppResources() {
    }

    private static synchronized AppResources getInstance() {
        if (mInstance == null) {
            mInstance = new AppResources();
        }
        return mInstance;
    }

    private static Resources getResources() {
        return App.getInstance().getResources();
    }

    public static synchronized Resources get() {
        return getInstance().getResources();
    }
}
