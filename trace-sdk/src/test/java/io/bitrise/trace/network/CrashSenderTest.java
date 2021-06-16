package io.bitrise.trace.network;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.data.management.formatter.crash.CrashFormatterTestProvider;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.storage.TraceDataStorage;
import io.bitrise.trace.data.storage.TraceDatabase;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Unit tests for {@link CrashSender}.
 */
public class CrashSenderTest {

  final Context mockContext = Mockito.mock(Context.class);
  private static final DataStorage mockDataStorage = mock(DataStorage.class);
  final File mockCacheDirectory = Mockito.mock(File.class);


  @Test
  public void convertCrashReportToJson() {
    final CrashSender crashSender = new CrashSender(createCrashReport(), mockCacheDirectory,
        mockDataStorage);

    when(mockDataStorage.getAllResources()).thenReturn(new ArrayList<ResourceEntity>());

    String json = crashSender.convertCrashReportToJson();

    assertEquals("{\"throwable\":\"class.method1(file:1)\\nclass.method2(filename:2)\\nclass" +
        ".method3(filename:3)\",\"threads\":{\"1234\":\"class1a.method1(file1a:1)\\nclass2a" +
        ".method2(filename2a:2)\\nclass3a.method3(filename3a:3)\",\"2345\":\"class1b.method1" +
        "(file1b:1)\\nclass2b.method2(filename2b:2)\\nclass3b.method3(filename3b:3)\"}}", json);

  }

  private CrashReport createCrashReport() {

    final String throwableStackTrace = "class.method1(file:1)\n"
        + "class.method2(filename:2)\n"
        + "class.method3(filename:3)";

     final String thread1StackTrace = "class1a.method1(file1a:1)\n"
        + "class2a.method2(filename2a:2)\n"
        + "class3a.method3(filename3a:3)";

    final String thread2StackTrace = "class1b.method1(file1b:1)\n"
        + "class2b.method2(filename2b:2)\n"
        + "class3b.method3(filename3b:3)";


    final Map<String, String> threads = new HashMap<>();
    threads.put("1234", thread1StackTrace);
    threads.put("2345", thread2StackTrace);

    return new CrashReport(throwableStackTrace, threads);
  }

}
