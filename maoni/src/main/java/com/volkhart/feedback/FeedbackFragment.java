package com.volkhart.feedback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.volkhart.feedback.internal.FeedbackIntent;
import com.volkhart.feedback.internal.MaoniActivity;
import com.volkhart.feedback.internal.ViewUtils;

import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.model.Feedback;

import java.io.File;

import static com.volkhart.feedback.internal.MaoniActivity.CONTENT_ERROR_TEXT;
import static com.volkhart.feedback.internal.MaoniActivity.CONTENT_HINT;
import static com.volkhart.feedback.internal.MaoniActivity.EXTRA_LAYOUT;
import static com.volkhart.feedback.internal.MaoniActivity.FILE_PROVIDER_AUTHORITY;
import static com.volkhart.feedback.internal.MaoniActivity.INCLUDE_SYSTEM_INFO_TEXT;
import static com.volkhart.feedback.internal.MaoniActivity.SCREENSHOT_HINT;
import static com.volkhart.feedback.internal.MaoniActivity.SCREENSHOT_TOUCH_TO_PREVIEW_HINT;
import static com.volkhart.feedback.internal.MaoniActivity.THEME;
import static com.volkhart.feedback.internal.MaoniActivity.WINDOW_TITLE;

public abstract class FeedbackFragment extends Fragment implements Listener {

    private static final int REQUEST_FEEDBACK = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_feedback, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_feedback) {
            Configuration configuration = getConfiguration();
            Intent intent = buildIntent(configuration);
            startActivityForResult(intent, REQUEST_FEEDBACK);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FEEDBACK) {
            if (resultCode == Activity.RESULT_OK) {
                Feedback feedback = FeedbackIntent.parse(getActivity(), data);
                onSendButtonClicked(feedback);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public final void onDismiss() {
        // Disabled in this library
    }

    protected abstract Configuration getConfiguration();

    private Intent buildIntent(Configuration configuration) {
        Activity context = getActivity();
        Intent intent = new Intent(context, MaoniActivity.class);

        intent.putExtra(FILE_PROVIDER_AUTHORITY, configuration.fileProviderAuthority);

        //Create screenshot file
        final File screenshotFile = new File(context.getFilesDir(), MaoniActivity.SCREENSHOT_PATH);
        ViewUtils.exportViewToFile(context.getWindow().getDecorView(), screenshotFile);

        if (configuration.theme != null) {
            intent.putExtra(THEME, configuration.theme);
        }

        if (configuration.windowTitle != null) {
            intent.putExtra(WINDOW_TITLE, configuration.windowTitle);
        }

        if (configuration.extraLayout != null) {
            intent.putExtra(EXTRA_LAYOUT, configuration.extraLayout);
        }

        if (configuration.feedbackContentHint != null) {
            intent.putExtra(CONTENT_HINT, configuration.feedbackContentHint);
        }

        if (configuration.contentErrorMessage != null) {
            intent.putExtra(CONTENT_ERROR_TEXT, configuration.contentErrorMessage);
        }

        if (configuration.screenshotHint != null) {
            intent.putExtra(SCREENSHOT_HINT, configuration.screenshotHint);
        }

        if (configuration.includeSystemInfoText != null) {
            intent.putExtra(INCLUDE_SYSTEM_INFO_TEXT, configuration.includeSystemInfoText);
        }

        if (configuration.touchToPreviewScreenshotText != null) {
            intent.putExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT, configuration.touchToPreviewScreenshotText);
        }
        return intent;
    }
}
