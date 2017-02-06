package com.volkhart.feedback.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.volkhart.feedback.Configuration;
import com.volkhart.feedback.FeedbackFragment;

import org.rm3l.maoni.common.model.Feedback;
import org.rm3l.maoni.email.MaoniEmailListener;

public final class ListenerFragment extends FeedbackFragment {

    public static final String TAG = ListenerFragment.class.getSimpleName();
    private MaoniEmailListener listener;

    public static ListenerFragment newInstance() {
        return new ListenerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = new MaoniEmailListener(getActivity(),
                "text/html",
                "Feedback for Feedback Sample App (" +
                        BuildConfig.APPLICATION_ID + ":" +
                        BuildConfig.VERSION_NAME + ")",
                null,
                null,
                new String[]{"apps+maoni@rm3l.org"},
                null,
                new String[]{"apps+maoni_sample@rm3l.org"});
    }

    @Override
    protected Configuration getConfiguration() {
        String authority = BuildConfig.APPLICATION_ID + ".feedback";
        return new Configuration(
                authority,
                "Feedback", //Set to an empty string to clear it,
                null,
                "[Custom hint] Write your feedback here",
                "Custom error message",
                R.layout.my_feedback_activity_extra_content,
                "[Custom text] Include system logs",
                "Touch To Preview",
                "Custom Screenshot hint: Lorem Ipsum Dolor Sit Amet..."
        );
    }

    @Override
    public boolean onSendButtonClicked(Feedback feedback) {
        feedback.put("CustomKey", "CustomValue");
        return listener.onSendButtonClicked(feedback);
    }
}
