package io.bitrise.trace.plugin.task;

import androidx.annotation.NonNull;
import com.android.build.gradle.api.BaseVariantOutput;
import io.bitrise.trace.plugin.configuration.BuildConfigurationManager;
import io.bitrise.trace.plugin.modifier.BuildHelper;
import io.bitrise.trace.plugin.modifier.ManifestHelper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.gradle.api.tasks.TaskAction;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Task that updates the AndroidManifest.xml file(s) with the required code changes.
 */
public class ManifestModifierTask extends BaseTraceVariantTask {

  /**
   * The action that will be performed when the task is run. Does the following:
   * <ul>
   * <li>Gets all the AndroidManifest.xml files for the given variant</li>
   * <li>Check if they already have a custom application set</li>
   * <li>If not the Trace Application is set</li>
   * </ul>
   *
   * @throws ParserConfigurationException if a DocumentBuilder cannot be created which
   *                                      satisfies the configuration
   *                                      requested.
   * @throws SAXException                 if any parse errors occur.
   * @throws IOException                  if any IO errors occur.
   * @throws TransformerException         if an unrecoverable error occurs during the course of
   *                                      the transformation.
   */
  @TaskAction
  public void updateManifests()
      throws ParserConfigurationException, SAXException, IOException, TransformerException {
    final BaseVariantOutput baseVariantOutput = getVariantOutput();
    if (baseVariantOutput == null) {
      return;
    }

    final List<File> androidManifests = new BuildHelper(logger).getManifestPaths(baseVariantOutput);

    for (@NonNull final File androidManifest : androidManifests) {
      final ManifestHelper manifestHelper = new ManifestHelper(androidManifest.getPath(), logger);
      addTraceApplicationToManifest(manifestHelper);
      addTraceServicesToApplication(manifestHelper);
      manifestHelper.commitManifestChanges();
      checkPermissions(manifestHelper);
    }
  }

  /**
   * Checks if the given AndroidManifest.xml already have a custom application set, if not the
   * Trace Application will be set.
   *
   * @param manifestHelper the {@link ManifestHelper} that holds the given AndroidManifest.xml.
   */
  private void addTraceApplicationToManifest(@NonNull final ManifestHelper manifestHelper) {
    if (!manifestHelper.isApplicationNamePresent()) {
      manifestHelper.setDefaultApplicationName();
    }
  }

  /**
   * Adds the entries for the MetricSender and the TraceSender services to the android manifests.
   *
   * @param manifestHelper the {@link ManifestHelper} that holds the given AndroidManifest.xml.
   */
  private void addTraceServicesToApplication(@NonNull final ManifestHelper manifestHelper) {
    final Element applicationElement = manifestHelper.getApplicationElement();
    if (applicationElement == null) {
      logger.info("No application element found in manifest file {}",
          manifestHelper.getAndroidManifestPath());
      return;
    }

    final Element metricServiceElement = manifestHelper.createMetricSenderServiceElement();
    manifestHelper.addElement(applicationElement, metricServiceElement);

    final Element traceServiceElement = manifestHelper.createTraceSenderServiceElement();
    manifestHelper.addElement(applicationElement, traceServiceElement);
  }

  /**
   * Check that all the required permissions for Data collection are present, if not the user
   * will be warned on the build console.
   *
   * @param manifestHelper the {@link ManifestHelper} that holds the given AndroidManifest.xml.
   * @see
   * <a href="https://developer.android.com/studio/build/manifest-merge">https://developer.android.com/studio/build/manifest-merge</a>
   */
  private void checkPermissions(@NonNull final ManifestHelper manifestHelper) {
    final BuildConfigurationManager buildConfigurationManager =
        BuildConfigurationManager.getInstance(getProject().getProjectDir().getAbsolutePath());
    final List<String> manifestPermissions = manifestHelper.getPermissions();
    final List<String> requiredPermissions = buildConfigurationManager.getRequiredPermissions();
    for (@NonNull final String requiredPermission : requiredPermissions) {
      if (!manifestPermissions.contains(requiredPermission)) {
        logger.warn(
            "{} is missing from {}, Trace plugin won't be able to gather some data. Please check "
                + "your AndroidManifest.xml(s) and make sure you do not remove these permissions.",
            requiredPermission, manifestHelper.getAndroidManifestPath());
      }
    }
  }
}
