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

package org.rm3l.maoni.sample.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.volkhart.feedback.Feedback;

import org.rm3l.maoni.sample.BuildConfig;
import org.rm3l.maoni.sample.R;
import org.rm3l.maoni.sample.feedback.MyHandlerForMaoni;

import timber.log.Timber;

public class MaoniSampleMainActivity extends AppCompatActivity {

    private static final String MY_FILE_PROVIDER_AUTHORITY =
            (BuildConfig.APPLICATION_ID + ".feedback");

    private Feedback feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");
        setContentView(R.layout.activity_maoni_sample_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.maoni_app_name);
            setSupportActionBar(toolbar);
        }

        final MyHandlerForMaoni handlerForMaoni = new MyHandlerForMaoni(this);
        final Feedback.Builder builder = new Feedback.Builder(MY_FILE_PROVIDER_AUTHORITY, handlerForMaoni)
                .withWindowTitle("Feedback") //Set to an empty string to clear it
                .withExtraLayout(R.layout.my_feedback_activity_extra_content)
                .withFeedbackContentHint("[Custom hint] Write your feedback here")
                .withIncludeSystemInfoText("[Custom text] Include system logs")
                .withTouchToPreviewScreenshotText("Touch To Preview")
                .withContentErrorMessage("Custom error message")
                .withScreenshotHint("Custom test: Lorem Ipsum Dolor Sit Amet...");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // MaoniActivity de-registers handlers, listeners and validators upon activity destroy,
                    // so we need to re-register it again by reconstructing a new Maoni instance.
                    //Also, Maoni.start(...) cannot be called twice,
                    // but we are reusing the Builder to construct a new instance along with its handler.
                    feedback = builder.build();
                    feedback.start(MaoniSampleMainActivity.this);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        Timber.i("onDestroy");
        //Clear strong references used in Maoni, by de-registering any handlers, listeners and validators
        if (feedback != null) {
            feedback.unregisterListener();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Timber.i("onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_maoni_sample, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.i("onOptionsItemSelected: %d", item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_about:
                new LibsBuilder()
                        //Pass the fields of your application to the lib so it can find all external lib information
                        .withFields(R.string.class.getFields())
                        .withActivityTitle(getString(R.string.action_about))
                        //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        //start the activity
                        .start(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
