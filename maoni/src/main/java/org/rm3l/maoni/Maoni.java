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
package org.rm3l.maoni;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;

import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.ui.MaoniActivity;
import org.rm3l.maoni.utils.ContextUtils;
import org.rm3l.maoni.utils.ViewUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.rm3l.maoni.Maoni.CallbacksConfiguration.getInstance;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_BUILD_CONFIG_DEBUG;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_BUILD_CONFIG_FLAVOR;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_PACKAGE_NAME;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_VERSION_CODE;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_VERSION_NAME;
import static org.rm3l.maoni.ui.MaoniActivity.CALLER_ACTIVITY;
import static org.rm3l.maoni.ui.MaoniActivity.CONTENT_ERROR_TEXT;
import static org.rm3l.maoni.ui.MaoniActivity.CONTENT_HINT;
import static org.rm3l.maoni.ui.MaoniActivity.EXTRA_LAYOUT;
import static org.rm3l.maoni.ui.MaoniActivity.FILE_PROVIDER_AUTHORITY;
import static org.rm3l.maoni.ui.MaoniActivity.INCLUDE_SYSTEM_INFO_TEXT;
import static org.rm3l.maoni.ui.MaoniActivity.SCREENSHOT_FILE;
import static org.rm3l.maoni.ui.MaoniActivity.SCREENSHOT_HINT;
import static org.rm3l.maoni.ui.MaoniActivity.SCREENSHOT_TOUCH_TO_PREVIEW_HINT;
import static org.rm3l.maoni.ui.MaoniActivity.THEME;
import static org.rm3l.maoni.ui.MaoniActivity.WINDOW_TITLE;
import static org.rm3l.maoni.ui.MaoniActivity.WORKING_DIR;

/**
 * Maoni configuration
 */
public class Maoni {

    private static final String LOG_TAG = Maoni.class.getSimpleName();

    private static final String MAONI_FEEDBACK_SCREENSHOT_FILENAME = "maoni_feedback_screenshot.png";

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
    private File maoniWorkingDir;

    private final AtomicBoolean mUsed = new AtomicBoolean(false);

    /**
     * Constructor
     * @param fileProviderAuthority        the file provider authority.
     *                                     If {@literal null}, file sharing will not be available
     * @param maoniWorkingDir                the working directory for Maoni.
     *                                       Will default to the caller activity cache directory if none was specified.
     *                                       This is where screenshots are typically stored.
     * @param windowTitle                  the feedback window title
     * @param theme                        the theme to apply
     * @param feedbackContentHint          the feedback form field hint message
     * @param contentErrorMessage          the feedback form field error message to display to the user
     * @param extraLayout                  the extra layout resource.
     * @param includeSystemInfoText        the text do display next to the "Include screenshot and logs" checkbox
     * @param touchToPreviewScreenshotText the "Touch to preview" text
     * @param screenshotHint               the text to display to the user
     */
    public Maoni(
            @Nullable String fileProviderAuthority,
            @Nullable final File maoniWorkingDir,
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
        this.maoniWorkingDir = maoniWorkingDir;
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

        maoniIntent.putExtra(WORKING_DIR,
                maoniWorkingDir != null ?
                        maoniWorkingDir : callerActivity.getCacheDir().getAbsolutePath());

        //Create screenshot file
        final File screenshotFile = new File(
                maoniWorkingDir != null ? maoniWorkingDir : callerActivity.getCacheDir(),
                MAONI_FEEDBACK_SCREENSHOT_FILENAME);
        ViewUtils.exportViewToFile(callerActivity,
                callerActivity.getWindow().getDecorView(), screenshotFile);
        maoniIntent.putExtra(SCREENSHOT_FILE, screenshotFile.getAbsolutePath());

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


    public Maoni unregisterListener() {
        getInstance().setListener(null);
        return this;
    }

    /**
     * Maoni Builder
     */
    public static class Builder {

        @Nullable
        private final String fileProviderAuthority;
        @StyleRes
        @Nullable
        public Integer theme;
        @Nullable
        private File maoniWorkingDir;
        @Nullable
        private CharSequence windowTitle;
        @Nullable
        private CharSequence windowSubTitle;
        @ColorRes
        @Nullable
        private Integer windowTitleTextColor;
        @ColorRes
        @Nullable
        private Integer windowSubTitleTextColor;
        @Nullable
        private CharSequence contentErrorMessage;
        @Nullable
        private CharSequence feedbackContentHint;
        @Nullable
        private CharSequence screenshotHint;
        @DrawableRes
        @Nullable
        private Integer header;
        @Nullable
        private CharSequence includeScreenshotText;
        @Nullable
        private CharSequence includeSystemInfoText;
        @Nullable
        private CharSequence touchToPreviewScreenshotText;
        @LayoutRes
        @Nullable
        private Integer extraLayout;

        /**
         * Constructor
         *
         * @param fileProviderAuthority the file provider authority.
         *                              If {@literal null}, screenshot file sharing will not be available
         */
        public Builder(@Nullable final String fileProviderAuthority) {
            this.fileProviderAuthority = fileProviderAuthority;
        }

        @Nullable
        public File getMaoniWorkingDir() {
            return maoniWorkingDir;
        }

        public Builder withMaoniWorkingDir(@Nullable File maoniWorkingDir) {
            this.maoniWorkingDir = maoniWorkingDir;
            return this;
        }

        @Nullable
        public Integer getTheme() {
            return theme;
        }

        public Builder withTheme(@StyleRes @Nullable Integer theme) {
            this.theme = theme;
            return this;
        }

        @Nullable
        public CharSequence getWindowTitle() {
            return windowTitle;
        }

        public Builder withWindowTitle(@Nullable CharSequence windowTitle) {
            this.windowTitle = windowTitle;
            return this;
        }

        @Nullable
        public CharSequence getWindowSubTitle() {
            return windowSubTitle;
        }

        public Builder withWindowSubTitle(@Nullable CharSequence windowSubTitle) {
            this.windowSubTitle = windowSubTitle;
            return this;
        }

        @Nullable
        public Integer getWindowTitleTextColor() {
            return windowTitleTextColor;
        }

        public Builder withWindowTitleTextColor(@ColorRes @Nullable Integer windowTitleTextColor) {
            this.windowTitleTextColor = windowTitleTextColor;
            return this;
        }

        @Nullable
        public Integer getWindowSubTitleTextColor() {
            return windowSubTitleTextColor;
        }

        public Builder withWindowSubTitleTextColor(@ColorRes @Nullable Integer windowSubTitleTextColor) {
            this.windowSubTitleTextColor = windowSubTitleTextColor;
            return this;
        }

        @Nullable
        public Integer getExtraLayout() {
            return extraLayout;
        }

        public Builder withExtraLayout(@LayoutRes @Nullable Integer extraLayout) {
            this.extraLayout = extraLayout;
            return this;
        }

        @Nullable
        public CharSequence getFeedbackContentHint() {
            return feedbackContentHint;
        }

        public Builder withFeedbackContentHint(@Nullable CharSequence feedbackContentHint) {
            this.feedbackContentHint = feedbackContentHint;
            return this;
        }

        @Nullable
        public CharSequence getIncludeSystemInfoText() {
            return includeSystemInfoText;
        }

        public Builder withIncludeSystemInfoText(@Nullable CharSequence includeSystemInfoText) {
            this.includeSystemInfoText = includeSystemInfoText;
            return this;
        }

        @Nullable
        public CharSequence getTouchToPreviewScreenshotText() {
            return touchToPreviewScreenshotText;
        }

        public Builder withTouchToPreviewScreenshotText(@Nullable CharSequence touchToPreviewScreenshotText) {
            this.touchToPreviewScreenshotText = touchToPreviewScreenshotText;
            return this;
        }

        @Nullable
        public CharSequence getContentErrorMessage() {
            return contentErrorMessage;
        }

        public Builder withContentErrorMessage(@Nullable CharSequence contentErrorMessage) {
            this.contentErrorMessage = contentErrorMessage;
            return this;
        }

        @DrawableRes
        @Nullable
        public Integer getHeader() {
            return header;
        }

        public Builder withHeader(@Nullable Integer header) {
            this.header = header;
            return this;
        }

        @Nullable
        public CharSequence getScreenshotHint() {
            return screenshotHint;
        }

        /**
         * @param screenshotHint if {@code null}, the screenshot hint view will be hidden
         */
        public Builder withScreenshotHint(@Nullable CharSequence screenshotHint) {
            this.screenshotHint = screenshotHint;
            return this;
        }

        public Builder withListener(@Nullable final Listener listener) {
            getInstance().setListener(listener);
            return this;
        }

        public Maoni build() {
            return new Maoni(
                    fileProviderAuthority,
                    maoniWorkingDir,
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
