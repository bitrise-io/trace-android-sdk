package io.bitrise.trace.plugin.modifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.plugin.TraceConstants;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.gradle.api.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Responsible for modifying the AndroidManifest.xml.
 */
public class ManifestHelper {

  /**
   * The element name for the application in the AndroidManifest.xml.
   */
  private static final String APPLICATION_ELEMENT = "application";

  /**
   * The element name for the manifest in the AndroidManifest.xml.
   */
  private static final String MANIFEST_ELEMENT = "manifest";

  /**
   * The attribute name for the package in the AndroidManifest.xml.
   */
  private static final String PACKAGE_ATTRIBUTE = "package";

  /**
   * The element name for the service in the AndroidManifest.xml.
   */
  private static final String SERVICE_ELEMENT = "service";

  /**
   * The attribute name in the AndroidManifest.xml for a given element.
   */
  private static final String NAME_ATTRIBUTE = "android:name";

  /**
   * The attribute permission in the AndroidManifest.xml for a given element.
   */
  private static final String PERMISSION_ATTRIBUTE = "android:permission";

  /**
   * The element name for the uses-permission in the AndroidManifest.xml.
   */
  private static final String USES_PERMISSION_ELEMENT = "uses-permission";

  /**
   * The value for the BIND_JOB_SERVICE permission.
   */
  private static final String BIND_JOB_SERVICE_PERMISSION = "android.permission.BIND_JOB_SERVICE";

  /**
   * The AndroidManifest.xml parsed as a {@link Document}.
   */
  @NonNull
  private final Document androidManifest;

  /**
   * Logger instance for providing logging functions.
   */
  @NonNull
  private final Logger logger;

  /**
   * The androidManifestPath of the AndroidManifest.xml.
   */
  @Nullable
  private final String androidManifestPath;

  /**
   * Constructor for class. Creates a ManifestHelper to easily parse and modify the given
   * AndroidManifest.xml.
   *
   * @param androidManifest the AndroidManifest parsed as a {@link Document} to work with.
   * @param logger          for providing the logging functions.
   */
  public ManifestHelper(@NonNull final Document androidManifest, @NonNull final Logger logger) {
    this.logger = logger;
    this.androidManifestPath = null;
    this.androidManifest = androidManifest;
  }

  /**
   * Constructor for class. Creates the ManifestHelper and automatically searches for the
   * AndroidManifest.xml to work with.
   *
   * @param androidManifestPath the androidManifestPath of the AndroidManifest.xml.
   * @param logger              for providing the logging functions.
   * @throws IOException                  if any IO errors occur.
   * @throws SAXException                 if any parse errors occur.
   * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies
   *                                      the configuration requested.
   */
  public ManifestHelper(@NonNull final String androidManifestPath, @NonNull final Logger logger)
      throws IOException, SAXException, ParserConfigurationException {
    this.logger = logger;
    this.androidManifestPath = androidManifestPath;
    this.androidManifest = openAndroidManifest();
  }

  /**
   * Opens the AndroidManifest.xml from the default location and parses it as a {@link Document}.
   *
   * @return the manifest as a parsed Document.
   * @throws IOException                  if any IO errors occur.
   * @throws SAXException                 if any parse errors occur.
   * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies
   *                                      the configuration requested.
   */
  @NonNull
  private Document openAndroidManifest() throws IOException,
                                                SAXException, ParserConfigurationException {
    if (androidManifestPath == null) {
      throw new IOException("No path set for AndroidManifest.xml.");
    }
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    logger.debug("Opening AndroidManifest.xml at androidManifestPath {}", androidManifestPath);
    return builder.parse(new File(androidManifestPath));
  }

  /**
   * Checks if the application name attribute is presented in the {@link #androidManifest} or not.
   *
   * @return {@code true} if the application name attribute is present, {@code false} otherwise.
   */
  public boolean isApplicationNamePresent() {
    final Element applicationElement = getApplicationElement();
    if (applicationElement == null) {
      return false;
    }
    return applicationElement.getAttribute(NAME_ATTRIBUTE).length() > 0;
  }

  /**
   * Gets the package name from the android manifest file.
   *
   * @return the package name, or {@code null} when not found.
   */
  @NonNull
  public String getPackageName() {
    final NodeList manifestElementList = androidManifest.getElementsByTagName(MANIFEST_ELEMENT);
    if (manifestElementList.getLength() == 0) {
      logger.debug("No application element found in manifest file.");
      throw new IllegalStateException("Could not get the package name, this should not happen!");
    }
    final Node manifestNode = manifestElementList.item(0);
    if (Node.ELEMENT_NODE == manifestNode.getNodeType()) {
      return ((Element) manifestNode).getAttribute(PACKAGE_ATTRIBUTE);
    }
    throw new IllegalStateException("Could not get the package name, this should not happen!");
  }

  /**
   * Gets the application {@link Element} from the {@link #androidManifest}.
   *
   * @return the application Element.
   */
  @Nullable
  public Element getApplicationElement() {
    final NodeList applicationElementList =
        androidManifest.getElementsByTagName(APPLICATION_ELEMENT);
    if (applicationElementList.getLength() == 0) {
      logger.debug("No application element found in manifest file.");
      return null;
    }
    final Node applicationNode = applicationElementList.item(0);
    if (Node.ELEMENT_NODE == applicationNode.getNodeType()) {
      return (Element) applicationNode;
    }
    logger.debug("Application is not element, type is {}", applicationNode.getNodeType());
    return null;
  }

  /**
   * Adds an {@link Element} to the xml file.
   *
   * @param parent the parent Element of the Element to add.
   * @param child  the child Element to add.
   */
  public void addElement(@NonNull final Element parent, @NonNull final Element child) {
    final NodeList applicationElementList =
        androidManifest.getElementsByTagName(parent.getTagName());
    if (applicationElementList.getLength() == 0) {
      logger.debug("No {} element found in manifest file.", parent.getTagName());
      throw new IllegalArgumentException("No such element: " + parent.getTagName());
    }
    parent.appendChild(child);
  }

  /**
   * Creates an {@link Element} to the xml file.
   *
   * @param name the name of the Element which will be created.
   * @return the created Element.
   */
  @NonNull
  public Element createElement(@NonNull final String name) {
    return androidManifest.createElement(name);
  }

  /**
   * Creates an {@link Attr} to the xml file.
   *
   * @param name  the name of the Attr which will be created.
   * @param value the value of the given Attr which will be created.
   * @return the created Attr.
   */
  @NonNull
  public Attr createAttribute(@NonNull final String name, @NonNull final String value) {
    Attr attr = androidManifest.createAttribute(name);
    attr.setValue(value);
    return attr;
  }

  /**
   * Adds an Application name attribute with the given value. If not present, the attribute is
   * added.
   */
  public void setDefaultApplicationName() {
    final Element applicationElement = getApplicationElement();
    if (applicationElement == null) {
      return;
    }
    applicationElement.setAttribute(NAME_ATTRIBUTE,
        TraceConstants.TRACE_APPLICATION_CLASS_FULL_NAME);
    logger.info("Added application {} to AndroidManifest.",
        TraceConstants.TRACE_APPLICATION_CLASS_NAME);
  }

  /**
   * Gets the Application name attribute to the given value.
   *
   * @return the value of the name attribute or empty String if not the attribute is not presented.
   */
  @NonNull
  public String getApplicationName() {
    final Element applicationElement = getApplicationElement();
    return applicationElement == null ? "" : applicationElement.getAttribute(NAME_ATTRIBUTE);
  }

  /**
   * Creates an {@link Element} to the xml file, with the required values for the MetricSender
   * Service.
   *
   * @return the created Element.
   */
  @NonNull
  public Element createMetricSenderServiceElement() {
    final Element metricServiceElement = createElement(SERVICE_ELEMENT);
    final Attr metricServiceNameAttr = createAttribute(NAME_ATTRIBUTE,
        TraceConstants.TRACE_METRIC_SENDER_CLASS_FULL_NAME);
    metricServiceElement.setAttributeNode(metricServiceNameAttr);
    final Attr metricServicePermissionAttr = createAttribute(PERMISSION_ATTRIBUTE,
        BIND_JOB_SERVICE_PERMISSION);
    metricServiceElement.setAttributeNode(metricServicePermissionAttr);
    return metricServiceElement;
  }

  /**
   * Creates an {@link Element} to the xml file, with the required values for the TraceSender
   * Service.
   *
   * @return the created Element.
   */
  @NonNull
  public Element createTraceSenderServiceElement() {
    final Element traceServiceElement = createElement(SERVICE_ELEMENT);
    final Attr traceServiceNameAttr = createAttribute(NAME_ATTRIBUTE,
        TraceConstants.TRACE_TRACE_SENDER_CLASS_FULL_NAME);
    traceServiceElement.setAttributeNode(traceServiceNameAttr);
    final Attr traceServicePermissionAttr = createAttribute(PERMISSION_ATTRIBUTE,
        BIND_JOB_SERVICE_PERMISSION);
    traceServiceElement.setAttributeNode(traceServicePermissionAttr);
    return traceServiceElement;
  }

  /**
   * Gets the list of permissions from the AndroidManifest.xml file.
   *
   * @return the list of permissions.
   */
  @NonNull
  public List<String> getPermissions() {
    final NodeList permissionElementList =
        androidManifest.getElementsByTagName(USES_PERMISSION_ELEMENT);
    final int nodeListSize = permissionElementList.getLength();
    if (nodeListSize == 0) {
      logger.debug("No uses-permission element found in manifest file.");
      return Collections.emptyList();
    }
    final List<String> permissions = new ArrayList<>();
    for (int i = 0; i < nodeListSize; i++) {
      final Node permissionNode = permissionElementList.item(i);
      if (Node.ELEMENT_NODE == permissionNode.getNodeType()) {
        permissions.add(((Element) permissionNode).getAttribute(NAME_ATTRIBUTE));
        continue;
      }
      logger.debug("Application is not element, type is {}", permissionNode.getNodeType());
    }
    return permissions;
  }

  /**
   * Commits the changes to the AndroidManifest.xml on {@link #androidManifestPath} and reopens
   * it.
   *
   * @throws TransformerException         if an unrecoverable error occurs during the course of
   *                                      the transformation.
   * @throws IOException                  if the path is {@code null}.
   * @throws SAXException                 if any parse errors occur.
   * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies
   *                                      the configuration requested.
   */
  public void applyManifestChanges() throws TransformerException, IOException, SAXException,
                                            ParserConfigurationException {
    commitManifestChanges(androidManifest);
    openAndroidManifest();
  }

  /**
   * Commits the changes to the AndroidManifest.xml on {@link #androidManifestPath}.
   *
   * @throws TransformerException if an unrecoverable error occurs during the course of the
   *                              transformation.
   * @throws IOException          if the path is {@code null}.
   */
  public void commitManifestChanges() throws TransformerException, IOException {
    commitManifestChanges(androidManifest);
  }

  /**
   * Commits the changes to the AndroidManifest.xml on {@link #androidManifestPath}.
   *
   * @param newAndroidManifest the new document.
   * @throws TransformerException if an unrecoverable error occurs during the course of the
   *                              transformation.
   * @throws IOException          if the path is {@code null}.
   */
  public void commitManifestChanges(@NonNull final Document newAndroidManifest)
      throws TransformerException, IOException {
    if (androidManifestPath == null) {
      throw new IOException("No path set for AndroidManifest.xml.");
    }
    final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    final Transformer transformer = transformerFactory.newTransformer();
    final DOMSource domSource = new DOMSource(newAndroidManifest);

    final StreamResult streamResult = new StreamResult(new File(androidManifestPath));
    transformer.transform(domSource, streamResult);
    logger.info("Updated AndroidManifest.xml at path {}.", androidManifestPath);
  }

  /**
   * Gets the {@link #androidManifest}.
   *
   * @return the AndroidManifest.xml.
   */
  @NonNull
  public Document getAndroidManifest() {
    return androidManifest;
  }

  /**
   * Gets the path of the AndroidManifest document.
   *
   * @return the path, or {@code null} when {@link #ManifestHelper(Document, Logger)}
   *     constructor was used.
   */
  @Nullable
  public String getAndroidManifestPath() {
    return androidManifestPath;
  }
}