package com.volkhart.feedback.sample.feedback;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.volkhart.feedback.sample.BuildConfig;
import com.volkhart.feedback.sample.R;

import org.rm3l.maoni.common.contract.Handler;
import org.rm3l.maoni.common.model.Feedback;
import org.rm3l.maoni.email.MaoniEmailListener;

/**
 * {@link MaoniEmailListener} is a Maoni listener class allowing to send emails.
 * It comes as an external contrib, which can be included to your {@literal build.gradle},
 * as follows:
 * <p/>
 * <pre>
 *     <code>
 *         dependencies {
 *             //...
 *             compile 'org.rm3l:maoni-email:<versionToReplace>'
 *         }
 *     </code>
 * </pre>
 * <p/>
 * Anyways, you are free to just implement {@link Handler} and provide your own implementation.
 */
public class MyHandlerForMaoni extends MaoniEmailListener implements Handler {

    private final Context mContext;
    private TextInputLayout mEmailInputLayout;
    private EditText mEmail;
    private EditText mExtraEditText;
    private RadioGroup mExtraRadioGroup;

    public MyHandlerForMaoni(Context context) {
        this(context,
                "text/html",
                "Feedback for Maoni Sample App (" +
                        BuildConfig.APPLICATION_ID + ":" +
                        BuildConfig.VERSION_NAME + ")",
                null,
                null,
                new String[]{"apps+maoni@rm3l.org"},
                null,
                new String[]{"apps+maoni_sample@rm3l.org"});
    }

    private MyHandlerForMaoni(Context context,
                              String mimeType,
                              String subject,
                              String bodyHeader,
                              String bodyFooter,
                              String[] toAddresses,
                              String[] ccAddresses,
                              String[] bccAddresses) {
        super(context,
                mimeType, subject, bodyHeader, bodyFooter,
                toAddresses, ccAddresses, bccAddresses);
        this.mContext = context;
    }

    @Override
    public void onDismiss() {
        Toast.makeText(mContext, "Activity Dismissed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSendButtonClicked(@NonNull Feedback feedback) {

        // Depending on your use case, you may add specific data in the feedback object returned,
        // and manipulate it accordingly
//        feedback.put("Email", mEmail.getText());
//        feedback.put("My Extra Edit Text", mExtraEditText.getText());
//
//        final String myExtraRadioGroupChecked;
//        switch (mExtraRadioGroup.getCheckedRadioButtonId()) {
//            case R.id.extra_rg1:
//                myExtraRadioGroupChecked = "RG 1";
//                break;
//            case R.id.extra_rg2:
//                myExtraRadioGroupChecked = "RG 2";
//                break;
//            case R.id.extra_rg3:
//                myExtraRadioGroupChecked = "RG 3";
//                break;
//            default:
//                myExtraRadioGroupChecked = null;
//                break;
//        }
//        feedback.put("My Extra Radio Group",
//                myExtraRadioGroupChecked != null ? myExtraRadioGroupChecked : "???");

        //Forward to the Email Listener for opening up the "Send Email" Intent
        return super.onSendButtonClicked(feedback);
    }

    @Override
    public boolean validateForm(@NonNull View rootView) {
        if (mEmail != null) {
            if (TextUtils.isEmpty(mEmail.getText())) {
                if (mEmailInputLayout != null) {
                    mEmailInputLayout.setErrorEnabled(true);
                    mEmailInputLayout.setError(mContext.getString(R.string.feedback_validate_must_not_be_blank));
                }
                return false;
            } else {
                if (mEmailInputLayout != null) {
                    mEmailInputLayout.setErrorEnabled(false);
                }
            }
        }
        return true;
    }

    @Override
    public void onCreate(@NonNull View rootView, @Nullable final Bundle savedInstanceState) {
        // Disabled in this library
    }
}
