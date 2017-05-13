# Feedback
_Feedback_ is an Android library that aims to replicate the Google feedback mechanism circa start of 2017.
_Feedback_ tries to be opinionated about the UI and attempts to provide a great UX on all form factors.

_Feedback_ captures a screenshot which the user can markup, as well as the system logs, and provides
them for easy upload.

## Using in your application
### The Basics
[![Maven Central](https://img.shields.io/maven-central/v/com.volkhart.feedback/feedback.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.volkhart.feedback%22%20AND%20a%3A%22feedback%22)

Add the dependency to your `build.gradle`:

```groovy
  compile ('com.volkhart.feedback:feedback:<current_version>')
```

Add the following to your `AndroidManifest.xml`. This is used to safely share the screenshot.
```xml
<provider
    android:name="com.volkhart.feedback.FeedbackFileProvider"
    android:authorities="${applicationId}.feedback"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/feedback_filepaths" />
</provider>
```

Subclass `FeedbackFragment`...
```java
public class MyFeedbackFragment extends com.volkhart.feedback.FeedbackFragment {
    public static final String TAG = "MyFeedbackFragment";
    
    @Override
    protected Configuration getConfiguration() {
        String authority = BuildConfig.APPLICATION_ID + ".feedback";
        return new Configuration(authority);
    }

    @Override
    public boolean onSendButtonClicked(Feedback feedback) {
        return false; // TODO implement
    }
}
```

... and add it to your `Activity`.
```java
public class MyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new MyFeedbackFragment(), MyFeedbackFragment.TAG)
                    .commit();
        }
        // Other stuff...
    }
}
```

### Uploading
You'll want to call your upload mechanism in `onSendButtonClicked(Feedback)` of your `FeedbackFragment`
subclass. There are several components available that implement uploading to popular destinations; they
are listed [here](http://maoni.rm3l.org/) (search the page for `maoni-` ). Alternatively you can handle
the uploading yourself.

## Alternatives
If you're looking for more customizability, check out the parent project, [maoni](http://maoni.rm3l.org/).
