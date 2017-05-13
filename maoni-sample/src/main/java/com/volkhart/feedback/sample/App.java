package com.volkhart.feedback.sample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.volkhart.feedback.FeedbackTree;

import timber.log.Timber;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(FeedbackTree.INSTANCE);
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
            Timber.plant(new Timber.DebugTree());
        }
    }
}
