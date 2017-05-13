package com.volkhart.feedback.internal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.volkhart.feedback.R;

import java.io.File;
import java.util.UUID;

public class FeedbackActivity extends AppCompatActivity {

    public static final String FILE_PROVIDER_AUTHORITY = "FILE_PROVIDER_AUTHORITY";
    public static final String THEME = "THEME";
    public static final String WINDOW_TITLE = "WINDOW_TITLE";
    public static final String SCREENSHOT_HINT = "SCREENSHOT_HINT";
    public static final String CONTENT_HINT = "CONTENT_HINT";
    public static final String CONTENT_ERROR_TEXT = "CONTENT_ERROR_TEXT";
    public static final String SCREENSHOT_TOUCH_TO_PREVIEW_HINT = "SCREENSHOT_PREVIEW_HINT";
    public static final String INCLUDE_SYSTEM_INFO_TEXT = "INCLUDE_SYSTEM_INFO_TEXT";
    public static final String EXTRA_LAYOUT = "EXTRA_LAYOUT";
    public static final String SCREENSHOT_PATH = "feedback/screenshot.png";
    private static final String MAONI_LOGS_FILENAME = "feedback/logs.txt";

    @Nullable
    private TextInputLayout mContentInputLayout;

    @Nullable
    private EditText mContent;

    @Nullable
    private CheckBox mIncludeSystemInfo;

    @Nullable
    private CharSequence mContentErrorText;

    private String mFeedbackUniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();

        setTheme(intent.getIntExtra(THEME, R.style.Feedback_Theme));

        setContentView(R.layout.feedback_form_content);

        if (intent.hasExtra(EXTRA_LAYOUT)) {
            final View extraContentView = findViewById(R.id.feedback_content_extra);
            if (extraContentView instanceof LinearLayout) {
                final int extraLayout = intent.getIntExtra(EXTRA_LAYOUT, -1);
                if (extraLayout != -1) {
                    final LinearLayout extraContent = (LinearLayout) extraContentView;
                    extraContent.setVisibility(View.VISIBLE);
                    extraContent
                            .addView(getLayoutInflater().inflate(extraLayout, extraContent, false));
                }
            }
        }

        if (intent.hasExtra(WINDOW_TITLE)) {
            setTitle(intent.getCharSequenceExtra(WINDOW_TITLE));
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.feedback_ic_arrow_back_black_24dp);
        }

        if (intent.hasExtra(SCREENSHOT_HINT)) {
            final CharSequence screenshotInformationalHint = intent.getCharSequenceExtra(SCREENSHOT_HINT);
            final TextView screenshotInformationalHintTv =
                    (TextView) findViewById(R.id.feedback_screenshot_informational_text);
            if (screenshotInformationalHintTv != null) {
                if (screenshotInformationalHint == null) {
                    screenshotInformationalHintTv.setVisibility(View.GONE);
                } else {
                    screenshotInformationalHintTv.setText(screenshotInformationalHint);
                }
            }
        }

        mContentInputLayout = (TextInputLayout) findViewById(R.id.feedback_content_input_layout);
        mContent = (EditText) findViewById(R.id.feedback_content);

        if (intent.hasExtra(CONTENT_HINT)) {
            final CharSequence contentHint = intent.getCharSequenceExtra(CONTENT_HINT);
            if (mContentInputLayout != null) {
                mContentInputLayout.setHint(contentHint);
            }
        }

        if (intent.hasExtra(CONTENT_ERROR_TEXT)) {
            mContentErrorText = intent.getCharSequenceExtra(CONTENT_ERROR_TEXT);
        } else {
            mContentErrorText = getString(R.string.feedback_validate_must_not_be_blank);
        }

        mIncludeSystemInfo = (CheckBox) findViewById(R.id.feedback_include_logs);
        if (mIncludeSystemInfo != null && intent.hasExtra(INCLUDE_SYSTEM_INFO_TEXT)) {
            mIncludeSystemInfo.setText(intent.getCharSequenceExtra(INCLUDE_SYSTEM_INFO_TEXT));
        }

        initScreenCaptureView(intent);

        mFeedbackUniqueId = UUID.randomUUID().toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initScreenCaptureView(getIntent());
    }

    private void initScreenCaptureView(@NonNull final Intent intent) {
        final ImageButton screenshotThumb = (ImageButton)
                findViewById(R.id.feedback_screenshot);

        final TextView touchToPreviewTextView =
                (TextView) findViewById(R.id.feedback_screenshot_touch_to_preview);
        if (touchToPreviewTextView != null && intent.hasExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT)) {
            touchToPreviewTextView.setText(
                    intent.getCharSequenceExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT));
        }

        final View screenshotContentView = findViewById(R.id.feedback_include_screenshot_content);
        final File file = new File(getFilesDir(), SCREENSHOT_PATH);
        if (file.exists()) {
            if (mIncludeSystemInfo != null) {
                mIncludeSystemInfo.setVisibility(View.VISIBLE);
            }
            if (screenshotContentView != null) {
                screenshotContentView.setVisibility(View.VISIBLE);
            }
            if (screenshotThumb != null) {
                //Thumbnail - load with smaller resolution so as to reduce memory footprint
                screenshotThumb.setImageBitmap(
                        ViewUtils.decodeSampledBitmapFromFilePath(
                                file.getAbsolutePath(), 100, 100));
            }

            // Hook up clicks on the thumbnail views.
            if (screenshotThumb != null) {
                screenshotThumb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri screenshotUri = Uri.fromFile(file);
                        int theme = intent.getIntExtra(THEME, R.style.Feedback_Theme);
                        startActivity(ScreenshotEditorActivity.newIntent(FeedbackActivity.this, screenshotUri, theme));
                    }
                });
            }
        } else {
            if (mIncludeSystemInfo != null) {
                mIncludeSystemInfo.setVisibility(View.GONE);
            }
            if (screenshotContentView != null) {
                screenshotContentView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feedback_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // TODO Enable custom views to participate in validation
    private boolean validateForm() {
        if (mContent != null) {
            if (TextUtils.isEmpty(mContent.getText())) {
                if (mContentInputLayout != null) {
                    mContentInputLayout.setErrorEnabled(true);
                    mContentInputLayout.setError(mContentErrorText);
                }
                return false;
            } else {
                if (mContentInputLayout != null) {
                    mContentInputLayout.setErrorEnabled(false);
                }
            }
        }
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        } else if (itemId == R.id.feedback_send) {
            validateAndSubmitForm();
        }
        return true;
    }

    private void validateAndSubmitForm() {
        //Validate form
        if (this.validateForm()) {
            String contentText = "";
            if (mContent != null) {
                contentText = mContent.getText().toString();
            }

            final Intent intent = getIntent();

            String contentAuthority = intent.getStringExtra(FILE_PROVIDER_AUTHORITY);
            Uri screenshotUri;
            File screenshotFile;
            Uri logsUri = null;
            File logsFile = null;

            final boolean includeSystemInfo = mIncludeSystemInfo != null && mIncludeSystemInfo.isChecked();
            if (includeSystemInfo) {
                logsFile = new File(getFilesDir(), MAONI_LOGS_FILENAME);
                LogUtils.writeLogsToFile(logsFile);
            }

            screenshotFile = new File(getFilesDir(), SCREENSHOT_PATH);
            screenshotUri = FileProvider.getUriForFile(this, contentAuthority, screenshotFile);
            grantUriPermission(intent.getComponent().getPackageName(),
                    screenshotUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (logsFile != null) {
                logsUri = FileProvider.getUriForFile(this, contentAuthority, logsFile);
                grantUriPermission(intent.getComponent().getPackageName(),
                        logsUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            setResult(RESULT_OK, FeedbackIntent.of(
                    mFeedbackUniqueId,
                    contentText,
                    includeSystemInfo,
                    screenshotUri,
                    logsUri
            ));
            finish();
        } //else do nothing - this is up to the callback implementation
    }
}
