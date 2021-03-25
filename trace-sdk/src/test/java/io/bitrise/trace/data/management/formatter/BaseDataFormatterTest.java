package io.bitrise.trace.data.management.formatter;

import org.junit.After;
import org.junit.Before;

import io.bitrise.trace.session.ApplicationSessionManager;

/**
 * Base DataFormatter for DataFormatter Tests.
 * This ensures the ApplicationSessionManager is started before which is required to be able to
 * format any data
 */
public class BaseDataFormatterTest {

    @Before
    public void setUpBeforeClass() {
        ApplicationSessionManager.getInstance().startSession();
    }

    @After
    public void tearDown() {
        ApplicationSessionManager.getInstance().stopSession();
    }
}
