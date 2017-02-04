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
package com.volkhart.feedback;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;

import com.volkhart.feedback.ui.MaoniActivity;
import com.volkhart.feedback.utils.ContextUtils;
import com.volkhart.feedback.utils.ViewUtils;

import org.rm3l.maoni.common.contract.Listener;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.volkhart.feedback.Feedback.CallbacksConfiguration.getInstance;
import static com.volkhart.feedback.ui.MaoniActivity.APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE;
import static com.volkhart.feedback.ui.MaoniActivity.APPLICATION_INFO_BUILD_CONFIG_DEBUG;
import static com.volkhart.feedback.ui.MaoniActivity.APPLICATION_INFO_BUILD_CONFIG_FLAVOR;
import static com.volkhart.feedback.ui.MaoniActivity.APPLICATION_INFO_PACKAGE_NAME;
import static com.volkhart.feedback.ui.MaoniActivity.APPLICATION_INFO_VERSION_CODE;
import static com.volkhart.feedback.ui.MaoniActivity.APPLICATION_INFO_VERSION_NAME;
import static com.volkhart.feedback.ui.MaoniActivity.CALLER_ACTIVITY;
import static com.volkhart.feedback.ui.MaoniActivity.CONTENT_ERROR_TEXT;
import static com.volkhart.feedback.ui.MaoniActivity.CONTENT_HINT;
import static com.volkhart.feedback.ui.MaoniActivity.EXTRA_LAYOUT;
import static com.volkhart.feedback.ui.MaoniActivity.FILE_PROVIDER_AUTHORITY;
import static com.volkhart.feedback.ui.MaoniActivity.INCLUDE_SYSTEM_INFO_TEXT;
import static com.volkhart.feedback.ui.MaoniActivity.SCREENSHOT_HINT;
import static com.volkhart.feedback.ui.MaoniActivity.SCREENSHOT_TOUCH_TO_PREVIEW_HINT;
import static com.volkhart.feedback.ui.MaoniActivity.THEME;
import static com.volkhart.feedback.ui.MaoniActivity.WINDOW_TITLE;

/**
 * Feedback configuration
 */
public class Feedback {

    private static final String LOG_TAG = Feedback.class.getSimpleName();

    private static final String DEBUG = "DEBUG";
    private static final String FLAVOR = "FLAVOR";
    private static final String BUILD_TYPE = "BUILD_TYPE";

    /**
     * The feedback window title
     */
    @Nullable
    public final CharSequence windowTitle;
    /**
     * The feedback form field error message to display to the user
     */
    @Nullable
    public final CharSequence contentErrorMessage;
    /**
     * The feedback form field hint message
     */
    @Nullable
    public final CharSequence feedbackContentHint;
    /**
     * Some text to display to the user, such as how the screenshot will be used by you,
     * and any links to your privacy policy
     */
    @Nullable
    public final CharSequence screenshotHint;
    /**
     * Text do display next to the "Include screenshot and logs" checkbox
     */
    @Nullable
    public final CharSequence includeSystemInfoText;
    /**
     * The "Touch to preview" text (displayed below the screenshot thumbnail).
     * Keep it short and to the point
     */
    @Nullable
    public final CharSequence touchToPreviewScreenshotText;
    /**
     * Extra layout resource.
     * Will be displayed between the feedback content field and the "Include screenshot" checkbox.
     */
    @LayoutRes
    @Nullable
    public final Integer extraLayout;
    @StyleRes
    @Nullable
    public final Integer theme;
    private final String fileProviderAuthority;
    private final AtomicBoolean mUsed = new AtomicBoolean(false);

    /**
     * @param fileProviderAuthority        the file provider authority.
     * @param windowTitle                  the feedback window title
     * @param theme                        the theme to apply
     * @param feedbackContentHint          the feedback form field hint message
     * @param contentErrorMessage          the feedback form field error message to display to the user
     * @param extraLayout                  the extra layout resource.
     * @param includeSystemInfoText        the text do display next to the "Include screenshot and logs" checkbox
     * @param touchToPreviewScreenshotText the "Touch to preview" text
     * @param screenshotHint               the text to display to the user
     */
    public Feedback(
            String fileProviderAuthority,
            @Nullable final CharSequence windowTitle,
            @StyleRes @Nullable final Integer theme,
            @Nullable final CharSequence feedbackContentHint,
            @Nullable final CharSequence contentErrorMessage,
            @LayoutRes @Nullable final Integer extraLayout,
            @Nullable final CharSequence includeSystemInfoText,
            @Nullable final CharSequence touchToPreviewScreenshotText,
            @Nullable final CharSequence screenshotHint) {
        this.fileProviderAuthority = fileProviderAuthority;
        this.theme = theme;
        this.windowTitle = windowTitle;
        this.contentErrorMessage = contentErrorMessage;
        this.feedbackContentHint = feedbackContentHint;
        this.screenshotHint = screenshotHint;
        this.includeSystemInfoText = includeSystemInfoText;
        this.touchToPreviewScreenshotText = touchToPreviewScreenshotText;
        this.extraLayout = extraLayout;
    }

    /**
     * Start the Maoni Activity
     *
     * @param callerActivity the caller activity
     */
    public void start(@Nullable final Activity callerActivity) {

        if (mUsed.getAndSet(true)) {
            unregisterListener();
            throw new UnsupportedOperationException(
                    "Maoni instance cannot be reused to start a new activity. " +
                            "Please build a new Maoni instance.");
        }

        if (callerActivity == null) {
            Log.d(LOG_TAG, "Target activity is undefined");
            return;
        }

        final Intent maoniIntent = new Intent(callerActivity, MaoniActivity.class);

        //Set app-related info
        final PackageManager packageManager = callerActivity.getPackageManager();
        try {
            if (packageManager != null) {
                final PackageInfo packageInfo = packageManager
                        .getPackageInfo(callerActivity.getPackageName(), 0);
                if (packageInfo != null) {
                    maoniIntent.putExtra(APPLICATION_INFO_VERSION_CODE, packageInfo.versionCode);
                    maoniIntent.putExtra(APPLICATION_INFO_VERSION_NAME, packageInfo.versionName);
                    maoniIntent.putExtra(APPLICATION_INFO_PACKAGE_NAME, packageInfo.packageName);
                }
            }
        } catch (final PackageManager.NameNotFoundException nnfe) {
            //No worries
            nnfe.printStackTrace();
        }
        final Object buildConfigDebugValue = ContextUtils.getBuildConfigValue(callerActivity,
                DEBUG);
        if (buildConfigDebugValue != null && buildConfigDebugValue instanceof Boolean) {
            maoniIntent.putExtra(APPLICATION_INFO_BUILD_CONFIG_DEBUG,
                    (Boolean) buildConfigDebugValue);
        }
        final Object buildConfigFlavorValue = ContextUtils.getBuildConfigValue(callerActivity,
                FLAVOR);
        if (buildConfigFlavorValue != null) {
            maoniIntent.putExtra(APPLICATION_INFO_BUILD_CONFIG_FLAVOR,
                    buildConfigFlavorValue.toString());
        }
        final Object buildConfigBuildTypeValue = ContextUtils.getBuildConfigValue(callerActivity,
                BUILD_TYPE);
        if (buildConfigBuildTypeValue != null) {
            maoniIntent.putExtra(APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE,
                    buildConfigBuildTypeValue.toString());
        }

        maoniIntent.putExtra(FILE_PROVIDER_AUTHORITY, fileProviderAuthority);

        //Create screenshot file
        final File screenshotFile = new File(callerActivity.getFilesDir(), MaoniActivity.SCREENSHOT_PATH);
        ViewUtils.exportViewToFile(callerActivity.getWindow().getDecorView(), screenshotFile);

        maoniIntent.putExtra(CALLER_ACTIVITY, callerActivity.getClass().getCanonicalName());

        if (theme != null) {
            maoniIntent.putExtra(THEME, theme);
        }

        if (windowTitle != null) {
            maoniIntent.putExtra(WINDOW_TITLE, windowTitle);
        }

        if (extraLayout != null) {
            maoniIntent.putExtra(EXTRA_LAYOUT, extraLayout);
        }

        if (feedbackContentHint != null) {
            maoniIntent.putExtra(CONTENT_HINT, feedbackContentHint);
        }

        if (contentErrorMessage != null) {
            maoniIntent.putExtra(CONTENT_ERROR_TEXT, contentErrorMessage);
        }

        if (screenshotHint != null) {
            maoniIntent.putExtra(SCREENSHOT_HINT, screenshotHint);
        }

        if (includeSystemInfoText != null) {
            maoniIntent.putExtra(INCLUDE_SYSTEM_INFO_TEXT, includeSystemInfoText);
        }

        if (touchToPreviewScreenshotText != null) {
            maoniIntent.putExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT, touchToPreviewScreenshotText);
        }

        callerActivity.startActivity(maoniIntent);
    }


    public Feedback unregisterListener() {
        getInstance().setListener(null);
        return this;
    }

    /**
     * Maoni Builder
     */
    public static class Builder {
        private final String fileProviderAuthority;
        @StyleRes
        @Nullable
        public Integer theme;
        @Nullable
        private CharSequence windowTitle;
        @Nullable
        private CharSequence contentErrorMessage;
        @Nullable
        private CharSequence feedbackContentHint;
        @Nullable
        private CharSequence screenshotHint;
        @Nullable
        private CharSequence includeSystemInfoText;
        @Nullable
        private CharSequence touchToPreviewScreenshotText;
        @LayoutRes
        @Nullable
        private Integer extraLayout;

        /**
         * @param listener Required to be able to process feedback
         */
        public Builder(String fileProviderAuthority, Listener listener) {
            if (fileProviderAuthority == null) {
                throw new NullPointerException("fileProviderAuthority may not be null");
            }
            this.fileProviderAuthority = fileProviderAuthority;
            if (listener == null) {
                throw new NullPointerException("listener may not be null");
            }
            getInstance().setListener(listener);
        }

        public Builder withTheme(@StyleRes @Nullable Integer theme) {
            this.theme = theme;
            return this;
        }

        public Builder withWindowTitle(@Nullable CharSequence windowTitle) {
            this.windowTitle = windowTitle;
            return this;
        }

        public Builder withExtraLayout(@LayoutRes @Nullable Integer extraLayout) {
            this.extraLayout = extraLayout;
            return this;
        }

        public Builder withFeedbackContentHint(@Nullable CharSequence feedbackContentHint) {
            this.feedbackContentHint = feedbackContentHint;
            return this;
        }

        public Builder withIncludeSystemInfoText(@Nullable CharSequence includeSystemInfoText) {
            this.includeSystemInfoText = includeSystemInfoText;
            return this;
        }

        public Builder withTouchToPreviewScreenshotText(@Nullable CharSequence touchToPreviewScreenshotText) {
            this.touchToPreviewScreenshotText = touchToPreviewScreenshotText;
            return this;
        }

        public Builder withContentErrorMessage(@Nullable CharSequence contentErrorMessage) {
            this.contentErrorMessage = contentErrorMessage;
            return this;
        }

        /**
         * @param screenshotHint if {@code null}, the screenshot hint view will be hidden
         */
        public Builder withScreenshotHint(@Nullable CharSequence screenshotHint) {
            this.screenshotHint = screenshotHint;
            return this;
        }

        public Feedback build() {
            return new Feedback(
                    fileProviderAuthority,
                    windowTitle,
                    theme,
                    feedbackContentHint,
                    contentErrorMessage,
                    extraLayout,
                    includeSystemInfoText,
                    touchToPreviewScreenshotText,
                    screenshotHint);
        }
    }

    /**
     * Callbacks Configuration for Maoni
     */
    public static class CallbacksConfiguration {

        @Nullable
        private static CallbacksConfiguration SINGLETON = null;

        @Nullable
        private Listener listener;

        private CallbacksConfiguration() {
        }

        @NonNull
        public static CallbacksConfiguration getInstance() {
            if (SINGLETON == null) {
                SINGLETON = new CallbacksConfiguration();
            }
            return SINGLETON;
        }

        @Nullable
        public Listener getListener() {
            return listener;
        }

        @NonNull
        public CallbacksConfiguration setListener(@Nullable final Listener listener) {
            this.listener = listener;
            return this;
        }

        public CallbacksConfiguration reset() {
            return setListener(null);
        }
    }

}
