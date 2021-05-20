package io.bitrise.trace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should be used when the test requires a TestKit docker image to run. See root README.md for
 * more details about theTestKit.
 *
 * <p>To run the test without the TestKitTests, set the {@code android
 * .testInstrumentationRunnerArguments.notAnnotation} property. Example:
 * ./gradlew ":trace-sdk:connectedAndroidTest" "-Pandroid.testInstrumentationRunnerArguments
 * .notAnnotation=io.bitrise.trace.TestKitTest"
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestKitTest {
}
