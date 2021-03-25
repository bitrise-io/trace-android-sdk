package io.bitrise.trace.testapp.network;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.bitrise.trace.testapp.BaseUiTest;
import io.bitrise.trace.testapp.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.startsWith;

/**
 * Tests for {@link java.net.URLConnection} behaviour.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class URLConnectionUiTest extends BaseUiTest {

    @Rule
    public ActivityScenarioRule<UrlConnectionActivity> activityRule
            = new ActivityScenarioRule<>(UrlConnectionActivity.class);

    @Before
    public void setUp() {
        ActivityScenario.launch(UrlConnectionActivity.class);
        IdlingRegistry.getInstance().register(TestAppCountingIdlingResource.getInstance());
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(TestAppCountingIdlingResource.getInstance());
    }

    @Test
    public void URLConnection_httpRequest() {
        onView(withId(R.id.btn_connect_http)).check(matches(isClickable()));
        onView(withId(R.id.btn_connect_http)).perform(click());
        onView(withId(R.id.lbl_response_status_code)).check(matches(withText("301")));
        onView(withId(R.id.lbl_response_body)).check(matches(withText(startsWith("<html"))));
    }

    @Test
    public void URLConnection_httpsRequest() {
        onView(withId(R.id.btn_connect_https)).check(matches(isClickable()));
        onView(withId(R.id.btn_connect_https)).perform(click());
        onView(withId(R.id.lbl_response_status_code)).check(matches(withText("200")));
        onView(withId(R.id.lbl_response_body)).check(matches(withText(startsWith("{"))));
    }
}
