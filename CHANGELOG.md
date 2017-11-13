# Changelog

## Version 1.0.0 *(2017-11-13)*
- Target API 26

## Version 1.0.0-rc2 *(2017-05-14)*
- Auto-register the `FeedbackTree`
- Add a keyboard shortcut for initiating feedback
- Hide private library resources
- Provide an icon for the Feedback `MenuItem`

## Version 1.0.0-rc1 *(2017-02-05)*
All changes compared to Maoni 2.3.1

- Remove dependency on, usage of, and permissions for Logcat. Instead, use Timber to collect logs.
This is done through the `FeedbackTree`. Logs are not yet persisted across app launches.
- Logs will be sent with every feedback report.
- Full translation into German
- Custom views are maimed. They cannot participate in validation and cannot contribute custom values
to the feedback report for now.
- Move to using a fragment as the entry point to the library. Hopefully this makes integration a bit
easier.