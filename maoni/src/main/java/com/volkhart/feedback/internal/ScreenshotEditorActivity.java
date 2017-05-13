package com.volkhart.feedback.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;

import com.volkhart.feedback.R;

public final class ScreenshotEditorActivity extends AppCompatActivity implements ScreenshotEditorFragment.Listener {

    public static Intent newIntent(Context context, Uri screenshotUri, @StyleRes int theme) {
        Intent intent = new Intent(context, ScreenshotEditorActivity.class);
        intent.putExtra(ScreenshotEditorFragment.ARG_SCREENSHOT_URI, screenshotUri);
        intent.putExtra(FeedbackActivity.THEME, theme);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getIntent().getIntExtra(FeedbackActivity.THEME, R.style.Feedback_Theme));
        if (savedInstanceState == null) {
            Uri screenshotUri = getIntent().getParcelableExtra(ScreenshotEditorFragment.ARG_SCREENSHOT_URI);
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, ScreenshotEditorFragment.newInstance(screenshotUri), ScreenshotEditorFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onDoneWithScreenshotEditing() {
        finish();
    }
}
