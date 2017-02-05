package com.volkhart.feedback;

import android.util.Log;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

public final class FeedbackTree extends Timber.DebugTree {

    public static final FeedbackTree INSTANCE = new FeedbackTree();
    public static int MESSAGES_TO_STORE = 100;
    private final List<String> logMessages = Collections.synchronizedList(new LinkedList<String>());

    private FeedbackTree() {
    }

    public void writeToStream(PrintWriter writer) {
        synchronized (logMessages) {
            for (String message : logMessages) {
                writer.println(message);
            }
        }

        // We add a line at the end to make sure the file gets create no matter what
        writer.println();
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        String displayLevel;
        switch (priority) {
            case Log.INFO:
                displayLevel = "I";
                break;
            case Log.WARN:
                displayLevel = "W";
                break;
            case Log.ERROR:
                displayLevel = "E";
                break;
            default:
                return;
        }
        String logMessage = String.format("%22s %s %s", tag, displayLevel,
                // Indent newlines to match the original indentation.
                message.replaceAll("\\n", "\n                         "));

        synchronized (logMessages) {
            logMessages.add(logMessage);
            if (logMessages.size() > MESSAGES_TO_STORE) {
                logMessages.remove(0);
            }
        }
    }
}
