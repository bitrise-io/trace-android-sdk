package io.bitrise.trace.plugin.modifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import com.android.builder.internal.ClassFieldImpl;
import com.android.builder.model.ClassField;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

/**
 * Unit tests for {@link BuildHelper}.
 */
public class BuildHelperTest {

  private static BuildHelper buildHelper;

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Rule
  public TestName testName = new TestName();

  @BeforeClass
  public static void setupClass() {
    final Logger logger = Logging.getLogger(BuildHelper.class.getName());
    buildHelper = new BuildHelper(logger);
  }

  @Before
  public void setup() {
    assertTrue(getTestDir().mkdirs());
  }

  private File getTestDir() {
    return new File(getTestDirPath());
  }

  @NonNull
  private String getTestDirPath() {
    return tempFolder.getRoot().getPath() + "/" + testName + "/";
  }

  @NonNull
  private String getDummyFileName(final int index) {
    return String.format("dummy_%s.txt", index);
  }

  @NonNull
  private String getDummyDirName(final int index) {
    return String.format("dummy_%s/", index);
  }

  // region collectFilesRecursively tests
  @Test
  public void collectFilesRecursively_emptyInput_OutputShouldBeEmpty() {
    final List<File> actual = buildHelper.collectFilesRecursively(Collections.emptySet());
    final List<File> expected = new ArrayList<>();
    assertThat(actual, containsInAnyOrder(expected.toArray(new File[0])));
  }

  @Test
  public void collectFilesRecursively_SingleFile_FileAsInput() throws IOException {
    final File singleFile = new File(getTestDirPath() + getDummyFileName(0));
    assertTrue(singleFile.createNewFile());

    final List<File> actual =
        buildHelper.collectFilesRecursively(Collections.singleton(singleFile));
    final List<File> expected = new ArrayList<File>() {
      {
        add(singleFile);
      }
    };

    assertThat(actual, containsInAnyOrder(expected.toArray(new File[0])));
  }

  @Test
  public void collectFilesRecursively_SingleFile_DirectoryAsInput() throws IOException {
    final File singleFile = new File(getTestDirPath() + getDummyFileName(0));
    assertTrue(singleFile.createNewFile());

    final List<File> actual =
        buildHelper.collectFilesRecursively(Collections.singleton(getTestDir()));
    final List<File> expected = new ArrayList<File>() {
      {
        add(singleFile);
      }
    };

    assertThat(actual, containsInAnyOrder(expected.toArray(new File[0])));
  }

  @Test
  public void collectFilesRecursively_MultipleFile() throws IOException {
    final File file0 = new File(getTestDirPath() + getDummyFileName(0));
    final File file1 = new File(getTestDirPath() + getDummyFileName(1));
    assertTrue(file0.createNewFile());
    assertTrue(file1.createNewFile());

    final List<File> actual =
        buildHelper.collectFilesRecursively(Collections.singleton(getTestDir()));
    final List<File> expected = new ArrayList<File>() {
      {
        add(file0);
        add(file1);
      }
    };

    assertThat(actual, containsInAnyOrder(expected.toArray(new File[0])));
  }

  @Test
  public void collectFilesRecursively_NestedFiles() throws IOException {
    final File dir0 = new File(getTestDirPath() + getDummyDirName(0));
    final File dir1 = new File(getTestDirPath() + getDummyDirName(1));
    assertTrue(dir0.mkdirs());
    assertTrue(dir1.mkdirs());

    final File file0 = new File(dir0, getDummyFileName(0));
    final File file1 = new File(dir1, getDummyFileName(1));
    assertTrue(file0.createNewFile());
    assertTrue(file1.createNewFile());

    final List<File> actual =
        buildHelper.collectFilesRecursively(Collections.singleton(getTestDir()));
    final List<File> expected = new ArrayList<File>() {
      {
        add(file0);
        add(file1);
      }
    };

    assertThat(actual, containsInAnyOrder(expected.toArray(new File[0])));
  }

  @Test
  public void collectFilesRecursively_ComplexStructure() throws IOException {
    final File dir0 = new File(getTestDirPath() + getDummyDirName(0));
    final File dir1 = new File(getTestDirPath() + getDummyDirName(1));
    final File dir2 = new File(dir0.getPath() + getDummyDirName(2));
    final File dir3 = new File(dir1.getPath() + getDummyDirName(3));
    assertTrue(dir0.mkdirs());
    assertTrue(dir1.mkdirs());
    assertTrue(dir2.mkdirs());
    assertTrue(dir3.mkdirs());

    final File file0 = new File(dir0, getDummyFileName(0));
    final File file1 = new File(dir1, getDummyFileName(1));
    final File file2 = new File(dir2, getDummyFileName(2));
    assertTrue(file0.createNewFile());
    assertTrue(file1.createNewFile());
    assertTrue(file2.createNewFile());

    final List<File> actual =
        buildHelper.collectFilesRecursively(Collections.singleton(getTestDir()));
    final List<File> expected = new ArrayList<File>() {
      {
        add(file0);
        add(file1);
        add(file2);
      }
    };

    assertThat(actual, containsInAnyOrder(expected.toArray(new File[0])));
  }
  //endregion

  // region createStringClassField
  @Test
  public void createBuildConfigStringClassField_ShouldCreateExpectedFormat() {
    final ClassField actual = BuildHelper.createBuildConfigStringClassField("someKey", "someValue");
    final ClassField expected = new ClassFieldImpl("String", "someKey", "\"someValue\"");
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void createResValueStringClassField_ShouldCreateExpectedFormat() {
    final ClassField actual = BuildHelper.createResValueStringClassField("someKey", "someValue");
    final ClassField expected = new ClassFieldImpl("string", "someKey", "someValue");
    assertThat(actual, equalTo(expected));
  }
  // endregion
}