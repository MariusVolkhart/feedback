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

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Feedback configuration
 */
public class Configuration {

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
    public final String fileProviderAuthority;
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
    public Configuration(
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
     * @param fileProviderAuthority the file provider authority.
     */
    public Configuration(String fileProviderAuthority) {
        this(fileProviderAuthority,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }
}
