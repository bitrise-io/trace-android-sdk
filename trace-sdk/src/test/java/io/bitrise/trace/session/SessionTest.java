package io.bitrise.trace.session;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import io.bitrise.trace.data.resource.ResourceEntity;
import io.opencensus.proto.resource.v1.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * Unit tests for the {@link Session} objects. They will check that the ULID token creation works
 * as expected, to
 * create unique tokens.
 */
public class SessionTest {

  /**
   * When a {@link Session} is created the {@link io.azam.ulidj.ULID} of it should not be
   * {@code null}.
   */
  @Test
  public void newSession_createsNonNullUlid() {
    final Session session = new Session();
    final String ulid = session.getUlid();
    assertThat(ulid, is(notNullValue()));
  }

  /**
   * When multiple {@link Session}s are created the String value of the
   * {@link io.azam.ulidj.ULID} of it should be different.
   */
  @Test
  public void newSessions_createDifferentUlidStrings() {
    final Session session1 = new Session();
    final String ulid1 = session1.getUlid();

    final Session session2 = new Session();
    final String ulid2 = session2.getUlid();
    assertThat(ulid1, is(not(ulid2)));
  }

  @Test
  public void addResourceEntity() {
    ApplicationSessionManager.getInstance().startSession();

    final ResourceEntity resourceEntity1 = new ResourceEntity("label1", "value");
    final ResourceEntity resourceEntity1Updated = new ResourceEntity("label1", "updated");
    final ResourceEntity resourceEntity2 = new ResourceEntity("label2", "value2");

    final Session session = new Session();
    session.addResourceEntity(resourceEntity1);
    session.addResourceEntity(resourceEntity2);

    final List<ResourceEntity> expectedResourceEntities = new ArrayList<>();
    expectedResourceEntities.add(resourceEntity1);
    expectedResourceEntities.add(resourceEntity2);
    assertEquals(expectedResourceEntities, session.storedResources);

    // update one entity
    session.addResourceEntity(resourceEntity1Updated);
    final List<ResourceEntity> expectedResourceEntitiesUpdated = new ArrayList<>();
    expectedResourceEntitiesUpdated.add(resourceEntity1Updated);
    expectedResourceEntitiesUpdated.add(resourceEntity2);
    assertEquals(expectedResourceEntitiesUpdated, session.storedResources);

    ApplicationSessionManager.getInstance().stopSession();
  }

  @Test
  public void getResources() {
    final String sessionId = "sessionid";
    ApplicationSessionManager.getInstance().startSession();

    final Session session = new Session(sessionId);
    session.addResourceEntity(new ResourceEntity("label1", "value"));
    session.addResourceEntity(new ResourceEntity("label2", "value2"));

    final Resource resource = session.getResources();
    assertNotNull(resource);

    final Map<String, String> expectedLabelsMap = new HashMap<>();
    expectedLabelsMap.put("label1", "value");
    expectedLabelsMap.put("label2", "value2");
    expectedLabelsMap.put("app.platform", "android");
    expectedLabelsMap.put("app.session.id", sessionId);

    assertEquals(expectedLabelsMap, resource.getLabelsMap());

    ApplicationSessionManager.getInstance().stopSession();
  }

  @Test
  public void getResources_noStoredResources() {
    final Session session = new Session();
    assertNull(session.getResources());
  }
}