package com.volkhart.feedback.sample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
            Timber.plant(new Timber.DebugTree());
        }
    }
}
