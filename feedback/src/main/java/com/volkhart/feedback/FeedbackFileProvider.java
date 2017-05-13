package com.volkhart.feedback;

import android.annotation.SuppressLint;
import android.support.v4.content.FileProvider;

// It is up to the user of this library to register this provider in the Manifest. If we do it, then
// multiple apps that use this library will have conflicting providers and will prevent one another
// from being installed.
@SuppressLint("Registered")
public final class FeedbackFileProvider extends FileProvider {
}
