package com.volkhart.feedback.internal;

import android.support.annotation.NonNull;

import com.volkhart.feedback.FeedbackTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import timber.log.Timber;

/**
 * Utilities for manipulating the App logs
 */
final class LogUtils {

    private LogUtils() {
        throw new UnsupportedOperationException("Not instantiable");
    }

    static void writeLogsToFile(@NonNull final File outputFile) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, false)));
            FeedbackTree.INSTANCE.writeToStream(out);
        } catch (final IOException ioe) {
            Timber.e(ioe, "Unable to write logs to file");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
