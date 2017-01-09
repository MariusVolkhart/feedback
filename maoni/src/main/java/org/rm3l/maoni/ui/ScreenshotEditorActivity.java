package org.rm3l.maoni.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public final class ScreenshotEditorActivity extends AppCompatActivity implements ScreenshotEditorFragment.Listener {

    public static Intent newIntent(Context context, Uri screenshotUri) {
        Intent intent = new Intent(context, ScreenshotEditorActivity.class);
        intent.putExtra(ScreenshotEditorFragment.ARG_SCREENSHOT_URI, screenshotUri);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
