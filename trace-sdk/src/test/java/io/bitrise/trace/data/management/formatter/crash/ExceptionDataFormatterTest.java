package io.bitrise.trace.data.management.formatter.crash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import io.bitrise.trace.data.dto.CrashData;
import io.bitrise.trace.data.dto.CrashReport;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ExceptionDataFormatter}.
 */
public class ExceptionDataFormatterTest {

  private final String expectedThrowableString = "java.lang.Throwable\n" +
      "\tat io.bitrise.trace.data.management.formatter.crash.ExceptionDataFormatterTest" +
      ".formatCrashData_noMessageInThrowable(ExceptionDataFormatterTest.java:59)\n" +
      "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
      "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke" +
      "(NativeMethodAccessorImpl.java:62)\n" +
      "\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke" +
      "(DelegatingMethodAccessorImpl.java:43)\n" +
      "\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\n" +
      "\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:59)" +
      "\n" +
      "\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)\n" +
      "\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:56)\n" +
      "\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)\n" +
      "\tat org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)\n" +
      "\tat org.junit.runners.BlockJUnit4ClassRunner$1.evaluate(BlockJUnit4ClassRunner.java:100)" +
      "\n" +
      "\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:366)\n" +
      "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:103)\n" +
      "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:63)\n" +
      "\tat org.junit.runners.ParentRunner$4.run(ParentRunner.java:331)\n" +
      "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:79)\n" +
      "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:329)\n" +
      "\tat org.junit.runners.ParentRunner.access$100(ParentRunner.java:66)\n" +
      "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:293)\n" +
      "\tat org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)\n" +
      "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:413)\n" +
      "\tat org.junit.runner.JUnitCore.run(JUnitCore.java:137)\n" +
      "\tat com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner" +
      ".java:69)\n" +
      "\tat com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner" +
      ".java:33)\n" +
      "\tat com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:220)\n" +
      "\tat com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:53)\n";

  @Test
  public void formatCrashData_noMessageInThrowable() {
    final Map<Thread, StackTraceElement[]> allStackTraces = new HashMap<>();
    final Thread mockThread = Mockito.mock(Thread.class);
    allStackTraces.put(mockThread, CrashFormatterTestProvider.createStackTraceElements());
    final CrashData input = new CrashData(new Throwable(), allStackTraces);

    when(mockThread.getId()).thenReturn(12345L);

    final CrashReport result = ExceptionDataFormatter.formatCrashData(input);

    assertNotNull(result);

    final Map<String, String> expectedThreads = new HashMap<>();
    expectedThreads.put("12345", CrashFormatterTestProvider.expectedStringifiedStackTrace);

    assertEquals(result.getThreads(), expectedThreads);
    assertEquals(expectedThrowableString, result.getThrowable());
  }



}
