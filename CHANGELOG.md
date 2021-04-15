Change Log
==========

trace-android-sdk public beta versions
--------------------------------------
**Note:** these versions of the *trace-android-sdk* are stored in a public repo, but should be 
considered still as beta.

### trace-sdk - 0.2.0 - 2021-04-15
* feat: **Support Android version 21:** Update minSdkVersion to 21 for trace-sdk and trace-test-application

### trace-sdk - 0.1.1 - 2021-04-09
* fix: **Update metric endpoint:** Update metric endpoint to the updated endpoint.
* fix: **remove key description from startup latency metric:** Remove description from the label key in startup latency metric.

### trace-gradle-plugin - 0.1.0 - 2021-03-24
* feat: **Initial public release**

### trace-sdk - 0.1.0 - 2021-03-29
* feat: **Initial public release**
* fix: **SDK will send the version name in user agent request headers:** 
Updated SDK to send the version name in the header of every request.


trace-android-sdk private beta versions
---------------------------------------
**Note:** these early versions of the *trace-android-sdk* do not have tags and the source code was 
stored in a private repo.

### trace-gradle-plugin - 0.0.6 - 2021-03-09
* fix: **Transform exception:** 
When a given resolved dependency File points to a directory,
trace-gradle-plugin failed to read it, because it was treated as a File.
Fixed this issue, to handle directories too.

### trace-sdk - 0.0.6 - 2021-02-11
* fix: **Send fragment sessions:** 
Ensure fragment lifecycle events are recorded and sent.
* fix: **Update test cases:** 
Update test cases of MetricSenderInstrumentedTest and
TraceSenderInstrumentedTest to pass.
* fix: **Minor fix:** 
The value of the token is added to the configuration, not the ID of it.
* fix: **Missing token issue:** 
Token is now passed in the build via a resource value. Starting from
version 4.1.0 of com.android.tools:gradle build configuration values
were not passed.
* fix: **Remove device country collection:** 
Remove device country from collection as the backend populates this.
* fix: **Update accuracy of App CPU measurement:** 
Application CPU measurements are more accurate now.
* fix: **Hardcoded CLK_TCK:** 
Different CPUs will provide different settings for CLK_TCK, current
method is inaccurate, hardcoded default 100.
* fix: **Filter invalid values:** 
Negative values will be filtered for ApplicationCpuUsageDataCollector.
* feat: **Updated ApplicationCpuUsageDataCollector:** 
Number of ticks in a second became calculated, not hardcoded.
* feat: **Updated App CPU usage collection:** 
App CPU usage collection can be done now over API level 26 too.

### trace-gradle-plugin - 0.0.5 - 2021-02-10
* fix: **Minor fix:** 
The value of the token is added to the configuration, not the ID of it.
* fix: **Missing token issue:** 
Token is now passed in the build via a resource value. Starting from
version 4.1.0 of com.android.tools:gradle build configuration values
were not passed.

### trace-gradle-plugin - 0.0.4 - 2021-02-06
* fix: **Simplify ManifestProcessorTask usage:** 
Code now uses search instead of the API methods to get the manifest
outputs. This is needed, as different versions of com.android
.tools:gradle have different API, which leads to compile issues when
different versions are used. Changed BuildUtils to BuildHelper to be
able to log inner states.
* fix: **Build issue of trace-gradle-plugin:** 
Build with trace-gradle-plugin failed, because Signature of
ManifestProcessorTask.getBundleManifestOutputDirectory() changed in
com.android.tools:gradle with version 3.6.0, return value changed from
File to DirectoryProperty. Fixed this issue.
* feat: **Add VerifyTraceTask:** 
Added VerifyTraceTask, that will be added to the apps that use the
plugin. It's purpose to verify if all required component is correctly
setup.

### trace-sdk - 0.0.5 - 2021-02-05
* fix: **Send http.url with network spans:** 
Fix network request spans working correctly.
* fix: **sending ApplicationStartUp metric:** 
Fix sending ApplicationStartUp data.
* fix: **URLConnection requests being reported twice:** 
Fix UrlConnection requests being reported twice.

### trace-sdk - 0.0.4 - 2021-01-28
* fix: **Debug flag:** 
Fix the debug build flag in TraceSdk.
* fix: **Traces not being sent:** 
Ensure trace requests are saved and sent as expected.
* feat: **debug log messages:** 
Add more debug logging.
* feat: **Update info logs:** 
Add info logs when the SDK is initialising and has successfully
* feat: **Update error logs:** 
Update error logging around the trace-config.json file.
* feat: **Update warning log messages:** 
Update warning log messages to contain location (class and method names)

### trace-gradle-plugin - 0.0.3 - 2021-01-28
* feat: **Update error logs:** 
Update error logging around the trace-config.json file.
* feat: **Update warning log messages:** 
Update warning log messages to contain location (class and method names)

### trace-sdk - 0.0.3 - 2021-01-05
* fix: **sdk release includes aar:** 
Ensure the .aar file is uploaded with a release.
* fix: **gradle release script:** 
Update the release gradle scripts to enable the release to succeed.

### trace-gradle-plugin - 0.0.2 - 2021-01-04
* **Initial release to maven central** 

### trace-sdk - 0.0.2 - 2020-12-17
* **Initial release to maven central** 
