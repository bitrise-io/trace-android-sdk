package io.bitrise.trace.plugin.modifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.gradle.api.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.bitrise.trace.plugin.TraceConstants;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

/**
 * Contains all the test cases that are related to {@link ManifestHelper}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ManifestHelperTest {

    /**
     * The name of the application in simple_android_manifest_1.xml.
     */
    private static final String APPLICATION_NAME_MANIFEST_1 = ".MyApplication";

    /**
     * The name with extension of the first AndroidManifest.xml for testing.
     */
    private static final String MANIFEST_FILE_NAME_0 = "simple_android_manifest_0.xml";

    /**
     * The name with extension of the second AndroidManifest.xml for testing.
     */
    private static final String MANIFEST_FILE_NAME_1 = "simple_android_manifest_1.xml";

    /**
     * The Android permission for Internet.
     */
    private static final String PERMISSION_INTERNET = "android.permission.INTERNET";

    /**
     * The Android permission for accessing the network state.
     */
    private static final String PERMISSION_ACCESS_NETWORK_STATE = "android.permission" +
            ".ACCESS_NETWORK_STATE";

    /**
     * Contains the name of the AndroidManifest.xml-s.
     */
    private String[] manifestFileNames = new String[]{
            MANIFEST_FILE_NAME_0,
            MANIFEST_FILE_NAME_1
    };

    /**
     * Contains the {@link ManifestHelper}s for the tests.
     */
    private ManifestHelper[] manifestHelpers;

    /**
     * A mocked {@link Logger} instance for the {@link #manifestHelpers}.
     */
    private Logger mockLogger;

    @Before
    public void setUp() {
        this.mockLogger = mock(Logger.class);
        initManifestHelpers();
    }

    /**
     * Gets the {@link Document} of the given AndroidManifest.xml from the test resources.
     *
     * @param name the name of the manifest file.
     * @return the Document, or {@code null} if Exception happened.
     */
    @Nullable
    private Document getManifestDocument(@NonNull final String name) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder;
        final String path = String.format("src/test/resources/%1$s", name);
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new File(path));
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Initializes the {@link #manifestHelpers} member from the manifests found in
     * {@link #manifestFileNames}.
     */
    private void initManifestHelpers() {
        final int numberOfManifests = manifestFileNames.length;
        manifestHelpers = new ManifestHelper[numberOfManifests];
        for (int i = 0; i < numberOfManifests; i++) {
            manifestHelpers[i] = new ManifestHelper(getManifestDocument(manifestFileNames[i]),
                    mockLogger);
        }
    }

    /**
     * In a manifest where the application name is not set, we should not find it.
     */
    @Test
    public void isApplicationNamePresent_0() {
        final boolean actualResult = manifestHelpers[0].isApplicationNamePresent();
        assertThat(actualResult, is(false));
    }

    /**
     * In a manifest where the application name is set, we should find it.
     */
    @Test
    public void isApplicationNamePresent_1() {
        final boolean actualResult = manifestHelpers[1].isApplicationNamePresent();
        assertThat(actualResult, is(true));
    }

    /**
     * When there is no application name set, the result should be empty for
     * {@link ManifestHelper#getApplicationName()}.
     */
    @Test
    public void getApplicationName_0() {
        final String actualResult = manifestHelpers[0].getApplicationName();
        final String expectedResult = "";
        assertThat(actualResult, is(expectedResult));
    }

    /**
     * When there is application name set, the result should be the value of the name for
     * {@link ManifestHelper#getApplicationName()}.
     */
    @Test
    public void getApplicationName_1() {
        final String actualResult = manifestHelpers[1].getApplicationName();
        assertThat(actualResult, is(APPLICATION_NAME_MANIFEST_1));
    }

    /**
     * When there is no application name set, after setting it the result should be the value we
     * set.
     */
    @Test
    public void setApplicationName_0() {
        final ManifestHelper manifestHelper = manifestHelpers[0];
        manifestHelper.setDefaultApplicationName();
        final String actualResult = manifestHelper.getApplicationName();
        final String expectedResult = TraceConstants.TRACE_APPLICATION_CLASS_FULL_NAME;
        assertThat(actualResult, is(expectedResult));
    }

    /**
     * When there is application name set, after setting it the result should be the value we set.
     */
    @Test
    public void setApplicationName_1() {
        final ManifestHelper manifestHelper = manifestHelpers[1];
        manifestHelper.setDefaultApplicationName();
        final String actualResult = manifestHelper.getApplicationName();
        final String expectedResult = TraceConstants.TRACE_APPLICATION_CLASS_FULL_NAME;
        assertThat(actualResult, is(expectedResult));
    }

    /**
     * When no permissions are set in the AndroidManifest.xml, the result should be an empty list.
     */
    @Test
    public void getPermissions_0() {
        final List<String> actualResult = manifestHelpers[0].getPermissions();
        assertThat(actualResult.isEmpty(), is(true));
    }

    /**
     * We should get the permissions listed in the AndroidManifest.xml.
     */
    @Test
    public void getPermissions_1() {
        final List<String> actualResult = manifestHelpers[1].getPermissions();
        final String[] expectedResult = new String[]{
                PERMISSION_INTERNET,
                PERMISSION_ACCESS_NETWORK_STATE};
        assertThat(actualResult, containsInAnyOrder(expectedResult));
    }
}