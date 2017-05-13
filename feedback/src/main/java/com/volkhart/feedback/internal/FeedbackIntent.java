package com.volkhart.feedback.internal;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import org.rm3l.maoni.common.model.Feedback;

public final class FeedbackIntent {
    private static final String FEEDBACK_ID = "id";
    private static final String CONTENT = "content";
    private static final String INCLUDE_SYSTEM_INFO = "sys_info";
    private static final String SCREENSHOT = "screenshot";
    private static final String LOGS = "logs";

    private static final String DEBUG = "DEBUG";
    private static final String FLAVOR = "FLAVOR";
    private static final String BUILD_TYPE = "BUILD_TYPE";

    public static Feedback parse(Activity activity, Intent intent) {
        boolean includeSystemInfo = intent.getBooleanExtra(INCLUDE_SYSTEM_INFO, false);
        Uri screenshotUri = intent.getParcelableExtra(SCREENSHOT);
        Uri logsUri = intent.getParcelableExtra(LOGS);

        Object buildConfigValue = ContextUtils.getBuildConfigValue(activity, DEBUG);
        Boolean isDebug = Boolean.FALSE;
        if (buildConfigValue instanceof Boolean) {
            isDebug = (Boolean) buildConfigValue;
        }

        buildConfigValue = ContextUtils.getBuildConfigValue(activity, FLAVOR);
        String buildFlavor = null;
        if (buildConfigValue != null) {
            buildFlavor = buildConfigValue.toString();
        }

        buildConfigValue = ContextUtils.getBuildConfigValue(activity, BUILD_TYPE);
        String buildType = null;
        if (buildConfigValue != null) {
            buildType = buildConfigValue.toString();
        }

        PackageInfo packageInfo;
        try {
            packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("If we can't find our own app, we're in trouble");
        }
        Feedback.App appInfo = new Feedback.App(
                activity.getClass().getCanonicalName(),
                isDebug,
                packageInfo.packageName,
                packageInfo.versionCode,
                buildFlavor,
                buildType,
                packageInfo.versionName);

        return new Feedback(
                intent.getStringExtra(FEEDBACK_ID),
                activity,
                appInfo,
                intent.getStringExtra(CONTENT),
                includeSystemInfo,
                screenshotUri,
                null,
                includeSystemInfo,
                logsUri,
                null
        );
    }

    static Intent of(String feedbackUniqueId, String contentText, boolean includeSystemInfo, Uri screenshotUri, Uri logsUri) {
        Intent intent = new Intent()
                .putExtra(FEEDBACK_ID, feedbackUniqueId)
                .putExtra(CONTENT, contentText)
                .putExtra(INCLUDE_SYSTEM_INFO, includeSystemInfo)
                .putExtra(SCREENSHOT, screenshotUri)
                .putExtra(LOGS, logsUri);
        return intent;
    }
}
