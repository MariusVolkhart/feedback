package com.volkhart.feedback.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Utilities for manipulating {@code Context}.
 */
final class ContextUtils {

    private ContextUtils() {
        throw new AssertionError("No instances");
    }

    /**
     * Gets a field from the project's BuildConfig. This is useful when, for example, flavors
     * are used at the project level to set custom fields.
     * <p/>
     * Workaround inspired from http://goo.gl/gKQqkC
     *
     * @param context   Used to find the correct file
     * @param fieldName The name of the field to access
     * @return The value of the field, or {@literal null} if the field is not found.
     */
    @Nullable
    static Object getBuildConfigValue(@NonNull final Context context,
                                      @NonNull final String fieldName) {
        try {
            return Class
                    .forName(String.format("%s.BuildConfig", context.getPackageName()))
                    .getField(fieldName)
                    .get(null);
        } catch (final Exception e) {
            //No worries
            e.printStackTrace();
            return null;
        }
    }
}
