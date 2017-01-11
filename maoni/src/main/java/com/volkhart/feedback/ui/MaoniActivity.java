/*
 * Copyright (c) 2016 Armel Soro
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.volkhart.feedback.ui;

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

import com.volkhart.feedback.Feedback.CallbacksConfiguration;
import com.volkhart.feedback.utils.LogcatUtils;
import com.volkhart.feedback.utils.ViewUtils;

import org.rm3l.maoni.R;
import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.model.Feedback;

import java.io.File;
import java.util.UUID;

/**
 * Maoni Activity
 */
public class MaoniActivity extends AppCompatActivity {

    public static final String APPLICATION_INFO_VERSION_CODE = "APPLICATION_INFO_VERSION_CODE";
    public static final String APPLICATION_INFO_VERSION_NAME = "APPLICATION_INFO_VERSION_NAME";
    public static final String APPLICATION_INFO_PACKAGE_NAME = "APPLICATION_INFO_PACKAGE_NAME";
    public static final String APPLICATION_INFO_BUILD_CONFIG_DEBUG =
            "APPLICATION_INFO_BUILD_CONFIG_DEBUG";
    public static final String APPLICATION_INFO_BUILD_CONFIG_FLAVOR =
            "APPLICATION_INFO_BUILD_CONFIG_FLAVOR";
    public static final String APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE =
            "APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE";
    public static final String WORKING_DIR = "WORKING_DIR";
    public static final String FILE_PROVIDER_AUTHORITY = "FILE_PROVIDER_AUTHORITY";
    public static final String THEME = "THEME";
    public static final String SCREENSHOT_FILE = "SCREENSHOT_FILE";
    public static final String CALLER_ACTIVITY = "CALLER_ACTIVITY";
    public static final String WINDOW_TITLE = "WINDOW_TITLE";
    public static final String SCREENSHOT_HINT = "SCREENSHOT_HINT";
    public static final String CONTENT_HINT = "CONTENT_HINT";
    public static final String CONTENT_ERROR_TEXT = "CONTENT_ERROR_TEXT";
    public static final String SCREENSHOT_TOUCH_TO_PREVIEW_HINT = "SCREENSHOT_PREVIEW_HINT";
    public static final String INCLUDE_SYSTEM_INFO_TEXT = "INCLUDE_SYSTEM_INFO_TEXT";
    public static final String EXTRA_LAYOUT = "EXTRA_LAYOUT";

    private static final String MAONI_LOGS_FILENAME = "maoni_logs.txt";

    @Nullable
    private TextInputLayout mContentInputLayout;

    @Nullable
    private EditText mContent;

    @Nullable
    private CheckBox mIncludeSystemInfo;

    @Nullable
    private CharSequence mScreenshotFilePath;

    @Nullable
    private CharSequence mContentErrorText;

    private File mWorkingDir;

    private String mFeedbackUniqueId;
    private Feedback.App mAppInfo;

    private Listener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();

        setTheme(intent.getIntExtra(THEME, R.style.Feedback_Theme));

        setContentView(R.layout.maoni_form_content);

        if (intent.hasExtra(WORKING_DIR)) {
            mWorkingDir = new File(intent.getStringExtra(WORKING_DIR));
        } else {
            mWorkingDir =  getCacheDir();
        }

        if (intent.hasExtra(EXTRA_LAYOUT)) {
            final View extraContentView = findViewById(R.id.maoni_content_extra);
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

        final CallbacksConfiguration maoniConfiguration = CallbacksConfiguration.getInstance();

        mListener = maoniConfiguration.getListener();

        if (intent.hasExtra(WINDOW_TITLE)) {
            setTitle(intent.getCharSequenceExtra(WINDOW_TITLE));
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }

        if (intent.hasExtra(SCREENSHOT_HINT)) {
            final CharSequence screenshotInformationalHint = intent.getCharSequenceExtra(SCREENSHOT_HINT);
            final TextView screenshotInformationalHintTv =
                    (TextView) findViewById(R.id.maoni_screenshot_informational_text);
            if (screenshotInformationalHintTv != null) {
                if (screenshotInformationalHint == null) {
                    screenshotInformationalHintTv.setVisibility(View.GONE);
                } else {
                    screenshotInformationalHintTv.setText(screenshotInformationalHint);
                }
            }
        }

        mContentInputLayout = (TextInputLayout) findViewById(R.id.maoni_content_input_layout);
        mContent = (EditText) findViewById(R.id.maoni_content);

        if (intent.hasExtra(CONTENT_HINT)) {
            final CharSequence contentHint = intent.getCharSequenceExtra(CONTENT_HINT);
            if (mContentInputLayout != null) {
                mContentInputLayout.setHint(contentHint);
            }
        }

        if (intent.hasExtra(CONTENT_ERROR_TEXT)) {
            mContentErrorText = intent.getCharSequenceExtra(CONTENT_ERROR_TEXT);
        } else {
            mContentErrorText = getString(R.string.maoni_validate_must_not_be_blank);
        }

        mIncludeSystemInfo = (CheckBox) findViewById(R.id.maoni_include_logs);
        if (mIncludeSystemInfo != null && intent.hasExtra(INCLUDE_SYSTEM_INFO_TEXT)) {
            mIncludeSystemInfo.setText(intent.getCharSequenceExtra(INCLUDE_SYSTEM_INFO_TEXT));
        }

        mScreenshotFilePath = intent.getCharSequenceExtra(SCREENSHOT_FILE);
        initScreenCaptureView(intent);

        mFeedbackUniqueId = UUID.randomUUID().toString();

        setAppRelatedInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initScreenCaptureView(getIntent());
    }

    private void initScreenCaptureView(@NonNull final Intent intent) {
        final ImageButton screenshotThumb = (ImageButton)
                findViewById(R.id.maoni_screenshot);

        final TextView touchToPreviewTextView =
                (TextView) findViewById(R.id.maoni_screenshot_touch_to_preview);
        if (touchToPreviewTextView != null && intent.hasExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT)) {
            touchToPreviewTextView.setText(
                    intent.getCharSequenceExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT));
        }

        final View screenshotContentView = findViewById(R.id.maoni_include_screenshot_content);
        if (!TextUtils.isEmpty(mScreenshotFilePath)) {
            final File file = new File(mScreenshotFilePath.toString());
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
                            startActivity(ScreenshotEditorActivity.newIntent(MaoniActivity.this, screenshotUri, theme));
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
    protected void onDestroy() {
        CallbacksConfiguration.getInstance().reset();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maoni_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setAppRelatedInfo() {

        final Intent intent = getIntent();
        final CharSequence callerActivity = intent.getCharSequenceExtra(CALLER_ACTIVITY);
        mAppInfo = new Feedback.App(
                callerActivity != null ? callerActivity : getClass().getSimpleName(),
                intent.hasExtra(APPLICATION_INFO_BUILD_CONFIG_DEBUG) ?
                        intent.getBooleanExtra(APPLICATION_INFO_BUILD_CONFIG_DEBUG, false) : null,
                intent.getStringExtra(APPLICATION_INFO_PACKAGE_NAME),
                intent.getIntExtra(APPLICATION_INFO_VERSION_CODE, -1),
                intent.getStringExtra(APPLICATION_INFO_BUILD_CONFIG_FLAVOR),
                intent.getStringExtra(APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE),
                intent.hasExtra(APPLICATION_INFO_VERSION_NAME) ?
                        intent.getStringExtra(APPLICATION_INFO_VERSION_NAME) : null);
    }

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
        } else if (itemId == R.id.maoni_feedback_send) {
            validateAndSubmitForm();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mListener != null) {
            mListener.onDismiss();
        }
        super.onBackPressed();
    }

    private void validateAndSubmitForm() {
        //Validate form
        if (this.validateForm()) {
            //TODO Check that device is actually connected to the internet prior to going any further
            String contentText = "";
            if (mContent != null) {
                contentText = mContent.getText().toString();
            }

            final Intent intent = getIntent();

            Uri screenshotUri = null;
            File screenshotFile = null;
            Uri logsUri = null;
            File logsFile = null;

            final boolean includeSystemInfo = mIncludeSystemInfo != null && mIncludeSystemInfo.isChecked();
            if (includeSystemInfo) {
                logsFile = new File(
                        mWorkingDir,
                        MAONI_LOGS_FILENAME);
                LogcatUtils.getLogsToFile(logsFile);
            }

            if (intent.hasExtra(FILE_PROVIDER_AUTHORITY)) {
                final String fileProviderAuthority = intent.getStringExtra(FILE_PROVIDER_AUTHORITY);
                if (mScreenshotFilePath != null) {
                    screenshotFile = new File(mScreenshotFilePath.toString());
                    screenshotUri = FileProvider
                            .getUriForFile(this, fileProviderAuthority, screenshotFile);
                    grantUriPermission(intent.getComponent().getPackageName(),
                            screenshotUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                if (logsFile != null) {
                    logsUri = FileProvider
                            .getUriForFile(this, fileProviderAuthority, logsFile);
                    grantUriPermission(intent.getComponent().getPackageName(),
                            logsUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

            //Construct the feedback object and call the actual implementation
            final Feedback feedback =
                    new Feedback(mFeedbackUniqueId,
                            this,
                            mAppInfo,
                            contentText,
                            includeSystemInfo,
                            screenshotUri,
                            screenshotFile,
                            includeSystemInfo,
                            logsUri,
                            logsFile);
            if (mListener != null) {
                if (mListener.onSendButtonClicked(feedback)) {
                    finish();
                } // else do *not* finish the activity
            } else {
                finish();
            }
        } //else do nothing - this is up to the callback implementation
    }

}
